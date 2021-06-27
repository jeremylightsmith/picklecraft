package com.picklepop.pickle;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


public class SocketServer extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();
    private MinecraftServer server;
    private List<BlockingQueue<String>> commandListeners = new LinkedList<>();
    private TheBoot boot;
    private Map<String, PlayerState> playerStates = new HashMap<>();

    static class PlayerState {
        public String name;
        public Vector3i position;
    }

    public SocketServer(MinecraftServer server, TheBoot boot) {
        this.server = server;
        this.boot = boot;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ServerWorld getWorld() {
        return server.overworld();
    }

    public List<BlockingQueue<String>> getCommandListeners() {
        return commandListeners;
    }

    public void run () {
        LOGGER.info("starting server...");
        ServerSocket socket = null;

        try {
            MinecraftForge.EVENT_BUS.register(this);
            socket = new ServerSocket(3200);
            socket.setReuseAddress(true);

            while (true) {
                Socket client = socket.accept();
                System.out.println("New client connected: " + client.getInetAddress().getHostAddress());

                new Thread(new MyConnectionHandler(this, client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MinecraftForge.EVENT_BUS.unregister(this);
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SubscribeEvent
    public void executeCommand(CommandEvent event) {
        fireEvent(new JSONWriter().commandEventToJson(event));
    }

    @SubscribeEvent
    public void playerUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            String name = player.getName().getContents();

            Vector3i pos = new Vector3i(player.position().x, player.position().y, player.position().z);
            PlayerState state = playerStates.get(name);
            if (state == null) {
                state = new PlayerState();
                state.name = name;
                state.position = pos;
                playerStates.put(name, state);
            } else if (!state.position.equals(pos)) {
                state.position = pos;
            } else {
                return;
            }

            fireEvent(new JSONWriter().playerMoveEventToJson(player));
        }
    }

    private void fireEvent(JSONObject event) {
//        LOGGER.info("event: " + event.toJSONString());
        commandListeners.forEach(listener -> {
            listener.add(event.toJSONString());
        });
    }

    static class MyConnectionHandler implements Runnable {
        private final Socket socket;
        private final SocketServer server;

        public MyConnectionHandler(SocketServer server, Socket socket) {
            this.server = server;
            this.socket = socket;
        }

        public void run() {

            BlockingQueue<String> messageQueue = new LinkedBlockingDeque<>();
            this.server.getCommandListeners().add(messageQueue);

            new Thread(new MyRequestHandler(this.server, this.socket, messageQueue)).start();

            PrintWriter out = null;
            BufferedReader in = null;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);

                while(true) {
                    try {
                        String message = messageQueue.take();
                        System.out.println("sending: " + message);
                        out.println(message);
                    } catch (InterruptedException e) {
                        System.out.println("interrupted");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                LOGGER.info("stopping listening for ...");
                this.server.getCommandListeners().remove(messageQueue);

                try {
                    if (out != null) out.close();
//                    if (in != null) in.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static class MyRequestHandler implements Runnable {
        private final Socket socket;
        private final SocketServer server;
        private final BlockingQueue<String> messageQueue;
        private final Controller controller;

        public MyRequestHandler(SocketServer server, Socket socket, BlockingQueue<String> messageQueue) {
            this.server = server;
            this.socket = socket;
            this.messageQueue = messageQueue;
            this.controller = new Controller(server.server, server.boot);
        }

        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while(true) {
                    String message = in.readLine();
                    if( message == null ) {
                        System.out.println("disconnected");
                        break;
                    }
                    System.out.println("received: " + message);
                    JSONObject json = new JSONObject();
                    try {
                        json = handleMessage(message, json);
                    } catch (Exception e) {
                        System.out.println("Exception:");
                        e.printStackTrace();
                        json.put("status", "ERROR");
                        json.put("error", e.getMessage());
                    } finally {
                        this.messageQueue.add(json.toJSONString() + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                LOGGER.info("stopping listening for ...");

                try {
                    if (in != null) in.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        JSONObject handleMessage(String message, JSONObject json) throws Exception {
            Params request;

            try {
                request = Params.parse(message);
                System.out.println("recompiled: " + request.toString());
            } catch (ParseException e) {
                System.out.println("JSOM parse error");
                json.put("status", "ERROR");
                json.put("error", "JSON parse error: " + e.getMessage());
                return json;
            }

            String methodName = request.getString("method");
            System.out.println("method: " + methodName);
            Method method;

            try {
                method = Controller.class.getMethod(methodName, Params.class);
            } catch (NoSuchMethodException e) {
                System.out.println("Not found");
                json.put("status", "ERROR");
                json.put("error", "Not found: " + request.toString());
                return json;
            }

            try {
                JSONAware result = (JSONAware) method.invoke(this.controller, request);
                json.put("status", "OK");
                json.put("result", result);
                return json;
            } catch (InvocationTargetException e) {
                System.out.println("Exception:");
                e.getTargetException().printStackTrace();
                json.put("status", "ERROR");
                json.put("error", e.getTargetException().getMessage());
                return json;
            }
        }
    }

}
