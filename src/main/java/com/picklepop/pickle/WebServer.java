package com.picklepop.pickle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
// import org.json.simple.*;
import org.json.*;

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
            String response = "";
            try {
                String path = String.format("%s %s", t.getRequestMethod(), t.getRequestURI());

                switch (path) {
                    case "POST /blocks/new":
                        response = placeBlock(parseParams(t.getRequestBody()));
                        break;

                    case "GET /players":
                        response = getPlayers();
                        break;
                }
            } catch(Exception e) {
                e.printStackTrace();
                renderText(t, e.getMessage(), 500);
                return;
            }
            renderText(t, response, 200);
        }

        private void renderText(HttpExchange t, String body, int status) throws IOException {
            byte[] responseBodyBytes = body.getBytes();
            t.sendResponseHeaders(status, responseBodyBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(responseBodyBytes);
            os.close();
        }

        private String placeBlock(Map params) {
            System.out.println("placeMap(type: "+params.get("type")+", ("+params.get("x")+", "+params.get("y")+", "+params.get("z")+")");

            Block block = getBlock((String) params.get("type"));
            BlockPos pos = new BlockPos(
                    ((Number) params.get("x")).intValue(),
                    ((Number) params.get("y")).intValue(),
                    ((Number) params.get("z")).intValue()
            );

            ServerWorld world = this.server.overworld();

            world.setBlock(pos, block.defaultBlockState(), 3);

            return "OK";
        }

        private Block getBlock(String type) {
            switch(type) {
                case "COBBLESTONE":
                    return Blocks.COBBLESTONE;
                case "IRON_ORE":
                    return Blocks.IRON_ORE;
                case "GOLD_ORE":
                    return Blocks.GOLD_ORE;
                case "NETHERITE_BLOCK":
                    return Blocks.NETHERITE_BLOCK;
                case "WATER":
                    return Blocks.WATER;
                case "ACACIA_WOOD":
                    return Blocks.ACACIA_WOOD;
                default:
                    return Blocks.AIR;
            }
        }

        private String getPlayers() {
            ServerWorld world = this.server.overworld();
            List<ServerPlayerEntity> players = world.players();

            JSONArray jPlayers = new JSONArray();
            for (ServerPlayerEntity player : players) {
                JSONArray jPosition = new JSONArray();
                jPosition.add(player.position().x);
                jPosition.add(player.position().y);
                jPosition.add(player.position().z);

                JSONObject jPlayer = new JSONObject();
                jPlayer.put("id", player.getId());
                jPlayer.put("name", player.getName().getString());
                jPlayer.put("position", jPosition);
                jPlayers.add(jPlayer);
            }

            return jPlayers.toString();
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

        private Map parseParams(InputStream stream) throws IOException {
            try {
                return (JSONObject) new JSONParser().parse(readStream(stream));
            } catch (ParseException e) {
                throw new IOException(e);
            }
        }
    }
}
