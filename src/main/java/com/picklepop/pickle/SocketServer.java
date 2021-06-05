package com.picklepop.pickle;

import com.mojang.brigadier.ParseResults;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
// import org.json.simple.*;


public class SocketServer extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();
    private MinecraftServer server;
    private List<BlockingQueue<String>> commandListeners = new LinkedList<>();

    public SocketServer(MinecraftServer server) {
        this.server = server;
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
            socket = new ServerSocket(3201);
            socket.setReuseAddress(true);

            while (true) {
                Socket client = socket.accept();
                System.out.println("New client connected: " + client.getInetAddress().getHostAddress());

                new Thread(new MyHandler(this, client)).start();
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
        JSONObject json = new JSONWriter().commandEventToJson(event);
        LOGGER.info("command: " + json.toJSONString());

        commandListeners.forEach(listener -> {
            listener.add(json.toJSONString());
        });
    }

    static class MyHandler implements Runnable {
        private final Socket socket;
        private final SocketServer server;

        public MyHandler(SocketServer server, Socket socket) {
            this.server = server;
            this.socket = socket;
        }

        public void run() {
            BlockingQueue<String> messageQueue = new LinkedBlockingDeque<>();
            this.server.getCommandListeners().add(messageQueue);

            PrintWriter out = null;
            BufferedReader in = null;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
                    if (in != null) in.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
