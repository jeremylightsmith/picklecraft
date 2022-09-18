package com.picklepop.pickle;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraftforge.event.CommandEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.stream.Stream;

public class JSONWriter {
    public JSONObject commandEventToJson(CommandEvent event) {
        String command = event.getParseResults().getReader().getString();
        JSONObject json = new JSONObject();
        json.put("type", "command_event");
        json.put("command", command);

        Entity entity = event.getParseResults().getContext().getSource().getEntity();
        if (entity instanceof Player) {
            json.put("player", playerToJson((Player) entity));
        }
        return json;
    }

    public JSONObject playerMoveEventToJson(Player player) {
        JSONObject json = new JSONObject();
        json.put("type", "player_move_event");
        json.put("player", playerToJson(player));
        return json;
    }

    public JSONObject playerToJson(Player player) {
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
        json.put("type", blockState.getBlock().getName().toString());
        if (blockState.getValues().size() > 0) {
            JSONObject propsJson = new JSONObject();
            blockState.getValues().forEach((property, comparable) -> {
                propsJson.put(property.getName(), comparable.toString());
            });
            json.put("properties", propsJson);
        }
        return json;
    }

    public JSONArray positionToJson(Vec3 v) {
        return arrayToJson(v.x, v.y, v.z);
    }

    public JSONArray positionToJson(Vec3i v) {
        return arrayToJson(v.getX(), v.getY(), v.getZ());
    }

    public JSONArray rotationToJson(Vec2 v) {
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
