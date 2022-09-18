package com.picklepop.picklecraft;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import javax.json.Json;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import java.util.stream.Stream;

class Controller {
    private final WorldFacade model;
    private final TheBoot boot;
    private final JSONWriter json = new JSONWriter();

    public Controller(MinecraftServer server, TheBoot boot) {
        this.model = new WorldFacade(server);
        this.boot = boot;
    }

    public JsonStructure getNearbyEntities(Params params) {
        ServerPlayer player = model.getPlayer(params.getString("playerName"));
        int range = params.getInt("range");

        return json.streamToArray(
                model.getNearbyEntities(player, range).stream().map(json::livingEntityToJson)).build();
    }

    public JsonStructure placeBlock(Params params) {
        model.placeBlock(params.getString("type"), params.getBlockPos("position"));
        return json.statusToJson("OK").build();
    }

    public JsonStructure placeBlocks(Params params) {
        model.placeBlocks(params.getString("type"),
                params.getBlockPos("fromPosition"),
                params.getBlockPos("toPosition"));
        return json.statusToJson("OK").build();
    }

    public JsonStructure getBlocks(Params params) {
        Stream<BlockState> blocks = model.getBlocks(
                params.getBlockPos("fromPosition"),
                params.getBlockPos("toPosition"));
        return json.streamToArray(blocks.map(json::blockStateToJson)).build();
    }

    public JsonStructure getPlayers(Params params) {
        return json.streamToArray(
                model.getPlayers().stream().map(json::playerToJson)).build();
    }

    public JsonStructure getPlayer(Params params) {
        ServerPlayer player = model.getPlayer(params.getString("name"));
        return player != null ? json.playerToJson(player).build() : Json.createObjectBuilder().build();
    }

    public JsonStructure spawnEntity(Params params) {
        model.spawnEntity(params.getString("type"), params.getVec3("position"));
        return json.statusToJson("OK").build();
    }

    public JsonStructure liftBoot(Params params) {
        this.boot.liftBoot(30);
        return json.statusToJson("OK").build();
    }

    public JsonStructure setDayTime(Params params) {
        model.setDayTime(params.getString("time"));
        return json.statusToJson("OK").build();
    }
}
