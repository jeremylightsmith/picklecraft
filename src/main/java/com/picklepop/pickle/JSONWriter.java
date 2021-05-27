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
    public JSONObject commandEvent(CommandEvent event) {
        String command = event.getParseResults().getReader().getString();
        JSONObject json = new JSONObject();
        json.put("event", "command_event");
        json.put("command", command);

        Entity entity = event.getParseResults().getContext().getSource().getEntity();
        if (entity instanceof ServerPlayerEntity) {
            json.put("player", player((ServerPlayerEntity) entity));
        }
        return json;
    }

    public JSONArray players(List<ServerPlayerEntity> players) {
        JSONArray json = new JSONArray();
        for (ServerPlayerEntity player : players) {
            json.add(player(player));
        }
        return json;
    }

    public JSONArray livingEntities(List<LivingEntity> entities) {
        JSONArray json = new JSONArray();
        for (LivingEntity entity : entities) {
            json.add(livingEntity(entity));
        }
        return json;
    }

    public JSONObject player(ServerPlayerEntity player) {
        JSONObject json = livingEntity(player);
        return json;
    }

    public JSONObject livingEntity(LivingEntity entity) {
        JSONObject json = new JSONObject();
        json.put("id", entity.getId());
        json.put("name", entity.getName().getString());
        json.put("position", position(entity.position()));
        json.put("rotation", rotation(entity.getRotationVector()));
        return json;
    }

    public JSONArray position(Vector3d v) {
        return array(v.x, v.y, v.z);
    }

    public JSONArray rotation(Vector2f v) {
        return array(v.x, v.y);
    }

    public JSONArray array(Object a, Object b, Object c) {
        JSONArray json = new JSONArray();
        json.add(a);
        json.add(b);
        json.add(c);
        return json;
    }

    public JSONArray array(Object a, Object b) {
        JSONArray json = new JSONArray();
        json.add(a);
        json.add(b);
        return json;
    }
}
