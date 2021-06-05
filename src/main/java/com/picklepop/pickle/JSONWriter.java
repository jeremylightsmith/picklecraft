package com.picklepop.pickle;

import com.mojang.brigadier.ParseResults;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.CommandEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class JSONWriter {
    public JSONObject commandEventToJson(CommandEvent event) {
        String command = event.getParseResults().getReader().getString();
        JSONObject json = new JSONObject();
        json.put("event", "command_event");
        json.put("command", command);

        Entity entity = event.getParseResults().getContext().getSource().getEntity();
        if (entity instanceof ServerPlayerEntity) {
            json.put("player", playerToJson((ServerPlayerEntity) entity));
        }
        return json;
    }

    public JSONArray playersToJson(List<ServerPlayerEntity> players) {
        JSONArray json = new JSONArray();
        for (ServerPlayerEntity player : players) {
            json.add(this.playerToJson(player));
        }
        return json;
    }

    public JSONArray livingEntitiesToJson(List<LivingEntity> entities) {
        JSONArray json = new JSONArray();
        for (LivingEntity entity : entities) {
            json.add(livingEntityToJson(entity));
        }
        return json;
    }

    public JSONObject playerToJson(ServerPlayerEntity player) {
        JSONObject json = livingEntityToJson(player);
        return json;
    }

    public JSONObject livingEntityToJson(LivingEntity entity) {
        JSONObject json = new JSONObject();
        json.put("id", entity.getId());
        json.put("name", entity.getName().getString());
        json.put("position", positionToJson(entity.position()));
        json.put("rotation", rotationToJson(entity.getRotationVector()));
        return json;
    }

    public JSONArray positionToJson(Vector3d v) {
        return arrayToJson(v.x, v.y, v.z);
    }

    public JSONArray rotationToJson(Vector2f v) {
        return arrayToJson(v.x, v.y);
    }

    public JSONArray arrayToJson(Object a, Object b, Object c) {
        JSONArray json = new JSONArray();
        json.add(a);
        json.add(b);
        json.add(c);
        return json;
    }

    public JSONArray arrayToJson(Object a, Object b) {
        JSONArray json = new JSONArray();
        json.add(a);
        json.add(b);
        return json;
    }

    public JSONObject statusToJson(String status) {
        JSONObject json = new JSONObject();
        json.put("status", status);
        return json;
    }
}
