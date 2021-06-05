package com.picklepop.pickle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
// import org.json.simple.*;


public class WebServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Controller controller;

    public WebServer(MinecraftServer server, TheBoot boot) {
        controller = new Controller(server, boot);
    }

    public void start() throws Exception {
        LOGGER.info("starting server...");
        HttpServer server = HttpServer.create(new InetSocketAddress(3200), 0);
        server.createContext("/", new RequestHandler(controller));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class RequestHandler implements HttpHandler {
        private final Controller controller;

        public RequestHandler(Controller controller) {
            this.controller = controller;
        }

        public void handle(HttpExchange t) throws IOException {
            if (!t.getRequestMethod().equals("POST") || !t.getRequestURI().toString().equals("/rpc")) {
                renderText(t, "Not found, try POST /rpc", 404);
                return;
            }

            Params params = Params.parse(t.getRequestBody());
            System.out.println(params);

            try {
                Method method = Controller.class.getMethod(params.getString("method"), Params.class);
                JSONAware json = (JSONAware) method.invoke(controller, params);
                renderText(t, json.toJSONString(), 200);

            } catch (NoSuchMethodException e) {
                System.out.println("Not found");
                renderError(t, "Not found: " + params.toString(), 404);

            } catch (InvocationTargetException e) {
                System.out.println("Exception:");
                e.getTargetException().printStackTrace();
                renderError(t, e.getTargetException().getMessage(), 500);

            } catch (Exception e) {
                System.out.println("Exception:");
                e.printStackTrace();
                renderError(t, e.getMessage(), 500);
            }
        }

        private void renderError(HttpExchange t, String error, int status) throws IOException {
            JSONObject json = new JSONObject();
            json.put("error", error);
            renderText(t, json.toJSONString(), status);
        }

        private void renderText(HttpExchange t, String body, int status) throws IOException {
            byte[] responseBodyBytes = body.getBytes();
            t.sendResponseHeaders(status, responseBodyBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(responseBodyBytes);
            os.close();
        }
    }
}
