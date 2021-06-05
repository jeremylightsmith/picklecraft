package com.picklepop.pickle;

import com.mojang.brigadier.ParseResults;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.CommandEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Stream;

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

    public JSONObject blockStateToJson(BlockState blockState) {
        JSONObject json = new JSONObject();
        json.put("type", blockState.getBlock().getRegistryName().toString());
        if (blockState.getValues().size() > 0) {
            JSONObject propsJson = new JSONObject();
            blockState.getValues().forEach((property, comparable) -> {
                propsJson.put(property.getName(), comparable.toString());
            });
            json.put("properties", propsJson);
        }
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

    public JSONArray streamToArray(Stream<JSONObject> stream) {
        JSONArray array = new JSONArray();
        stream.forEach(array::add);
        return array;
    }
}
