package com.picklepop.pickle;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.BlockPos;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import java.util.stream.Stream;

class Controller {
    private final WorldFacade model;
    private final TheBoot boot;
    private final JSONWriter json = new JSONWriter();

    public Controller(MinecraftServer server, TheBoot boot) {
        this.model = new WorldFacade(server);
        this.boot = boot;
    }

    public JSONAware getNearbyEntities(Params params) {
        ServerPlayer player = model.getPlayer(params.getString("playerName"));
        int range = params.getInt("range");

        return json.streamToArray(
                model.getNearbyEntities(player, range).stream().map(json::livingEntityToJson));
    }

    public JSONAware placeBlock(Params params) {
        model.placeBlock(params.getString("type"), params.getBlockPos("position"));
        return json.statusToJson("OK");
    }

    public JSONAware placeBlocks(Params params) {
        model.placeBlocks(params.getString("type"),
                params.getBlockPos("fromPosition"),
                params.getBlockPos("toPosition"));
        return json.statusToJson("OK");
    }

    public JSONAware getBlocks(Params params) {
        Stream<BlockState> blocks = model.getBlocks(
                params.getBlockPos("fromPosition"),
                params.getBlockPos("toPosition"));
        return json.streamToArray(blocks.map(json::blockStateToJson));
    }

    public JSONAware getPlayers(Params params) {
        return json.streamToArray(
                model.getPlayers().stream().map(json::playerToJson));
    }

    public JSONAware getPlayer(Params params) {
        ServerPlayer player = model.getPlayer(params.getString("name"));
        return player != null ? json.playerToJson(player) : new JSONObject();
    }

    public JSONAware spawnEntity(Params params) {
        model.spawnEntity(params.getString("type"), params.getVec3("position"));
        return json.statusToJson("OK");
    }

    public JSONAware liftBoot(Params params) {
        this.boot.liftBoot(30);
        return json.statusToJson("OK");
    }

    public JSONAware setDayTime(Params params) {
        model.setDayTime(params.getString("time"));
        return json.statusToJson("OK");
    }
}
