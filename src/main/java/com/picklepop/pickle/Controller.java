package com.picklepop.pickle;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

class Controller {
    private final WorldFacade model;
    private final TheBoot boot;
    private final JSONWriter json = new JSONWriter();

    public Controller(MinecraftServer server, TheBoot boot) {
        this.model = new WorldFacade(server);
        this.boot = boot;
    }

    public JSONAware getNearbyEntities(Params params) {
        ServerPlayerEntity player = model.getPlayer(params.getString("player_name"));
        int range = params.getInt("range");

        return json.livingEntitiesToJson(model.getNearbyEntities(player, range));
    }

    public JSONAware placeBlock(Params params) {
        BlockPos pos = new BlockPos(
                params.getInt("x"),
                params.getInt("y"),
                params.getInt("z")
        );

        model.placeBlock(params.getString("type"), pos);

        return json.statusToJson("OK");
    }

    public JSONAware getPlayers(Params params) {
        return json.playersToJson(model.getPlayers());
    }

    public JSONAware getPlayer(Params params) {
        ServerPlayerEntity player = model.getPlayer(params.getString("name"));
        return player != null ? json.playerToJson(player) : new JSONObject();
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
