package com.picklepop.pickle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;

public class WebServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private MinecraftServer server;

    public WebServer(MinecraftServer server) {
        this.server = server;
    }

    public void start() throws Exception {
        LOGGER.info("starting server...");
        HttpServer server = HttpServer.create(new InetSocketAddress(3200), 0);
        server.createContext("/", new MyHandler(this.server));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        private MinecraftServer server;

        public MyHandler(MinecraftServer server) {
            this.server = server;
        }

        public void handle(HttpExchange t) throws IOException {
            String path = String.format("%s %s", t.getRequestMethod(), t.getRequestURI());

            System.out.println("path = " + path);
            LOGGER.info("patttth = " + path);

            String responseBody = "";

            switch (path) {
                case "POST /blocks/new":
                    newBlock(readStream(t.getRequestBody()));
                    break;

                case "GET /players":
                    responseBody = getPlayers();
                    break;
            }

            byte[] responseBodyBytes = responseBody.getBytes();

            t.sendResponseHeaders(200, responseBodyBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(responseBodyBytes);
            os.close();
        }

        private void newBlock(String stuff) {
            System.out.println("new block: " + stuff);
        }

        private String getPlayers() {
            ServerWorld world = this.server.overworld();
            List<ServerPlayerEntity> players = world.players();

            ServerPlayerEntity player = world.getRandomPlayer();

            return player.toString();
        }

        private String readStream(InputStream stream) throws IOException {
            InputStreamReader isReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(isReader);
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        }
    }
}
