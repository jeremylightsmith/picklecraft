package com.picklepop.pickle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;
// import org.json.simple.*;


public class WebServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private MinecraftServer server;

    public WebServer(MinecraftServer server) {
        this.server = server;
    }

    public void start() throws Exception {
        LOGGER.info("starting server...");
        HttpServer server = HttpServer.create(new InetSocketAddress(3200), 0);
        server.createContext("/", new Controller(this.server));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class Controller implements HttpHandler {
        private WorldFacade model;
        private final JSONWriter json = new JSONWriter();

        public Controller(MinecraftServer server) {
            this.model = new WorldFacade(server);
        }

        public void handle(HttpExchange t) throws IOException {
            String path = String.format("%s %s", t.getRequestMethod(), t.getRequestURI());
            try {
                String response = "";
                Params params = Params.parse(t.getRequestBody());
                switch (path) {
                    case "POST /place_block":
                        response = placeBlock(params);
                        break;
                    case "GET /players":
                        response = getPlayers();
                        break;
                    case "POST /nearby_entities":
                        response = getNearbyEntities(params);
                        break;
                    case "POST /player":
                        response = getPlayer(params);
                        break;
                    default:
                        System.out.println("Not found " + path);
                        renderText(t, "Not found", 404);
                        return;
                }
                System.out.println(path + ": " + params.toString());
                renderText(t, response, 200);

            } catch (Exception e) {
                System.out.println("Exception " + path);
                e.printStackTrace();
                renderText(t, e.getMessage(), 500);
            }
        }

        private void renderText(HttpExchange t, String body, int status) throws IOException {
            byte[] responseBodyBytes = body.getBytes();
            t.sendResponseHeaders(status, responseBodyBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(responseBodyBytes);
            os.close();
        }

        private String getNearbyEntities(Params params) {
            ServerPlayerEntity player = model.getPlayer(params.getString("player_name"));
            int range = params.getInt("range");

            return json.livingEntities(model.getNearbyEntities(player, range)).toJSONString();
        }

        private String placeBlock(Params params) {
            BlockPos pos = new BlockPos(
                    params.getInt("x"),
                    params.getInt("y"),
                    params.getInt("z")
            );

            model.placeBlock(params.getString("type"), pos);

            return "OK";
        }

        private String getPlayers() {
            return json.players(model.getPlayers()).toJSONString();
        }

        private String getPlayer(Params params) {
            ServerPlayerEntity player = model.getPlayer(params.getString("name"));
            return player != null ? json.player(player).toJSONString() : new JSONObject().toJSONString();
        }
    }
}
