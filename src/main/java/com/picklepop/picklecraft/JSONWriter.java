package com.picklepop.picklecraft;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraftforge.event.CommandEvent;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.stream.Stream;

public class JSONWriter {
    public JsonObjectBuilder commandEventToJson(CommandEvent event) {
        String command = event.getParseResults().getReader().getString();
        JsonObjectBuilder json = Json.createObjectBuilder()
                .add("type", "command_event")
                .add("command", command);

        Entity entity = event.getParseResults().getContext().getSource().getEntity();
        if (entity instanceof Player) {
            json.add("player", playerToJson((Player) entity));
        }
        return json;
    }

    public JsonObjectBuilder playerMoveEventToJson(Player player) {
        return Json.createObjectBuilder()
                .add("type", "player_move_event")
                .add("player", playerToJson(player));
    }

    public JsonObjectBuilder playerToJson(Player player) {
        return livingEntityToJson(player);
    }

    public JsonObjectBuilder livingEntityToJson(LivingEntity entity) {
        return Json.createObjectBuilder()
                .add("id", entity.getId())
                .add("name", entity.getName().getString())
                .add("position", positionToJson(entity.position()))
                .add("rotation", rotationToJson(entity.getRotationVector()));
    }

    public JsonObjectBuilder blockStateToJson(BlockState blockState) {
        JsonObjectBuilder json = Json.createObjectBuilder()
                .add("type", blockState.getBlock().getName().toString());

        if (blockState.getValues().size() > 0) {
            JsonObjectBuilder propsJson = Json.createObjectBuilder();
            blockState.getValues().forEach((property, comparable) -> {
                propsJson.add(property.getName(), comparable.toString());
            });
            json.add("properties", propsJson);
        }

        return json;
    }

    public JsonArrayBuilder positionToJson(Vec3 v) {
        return Json.createArrayBuilder()
                .add(v.x).add(v.y).add(v.z);
    }

    public JsonArrayBuilder positionToJson(Vec3i v) {
        return Json.createArrayBuilder()
                .add(v.getX()).add(v.getY()).add(v.getZ());
    }

    public JsonArrayBuilder rotationToJson(Vec2 v) {
        return Json.createArrayBuilder()
                .add(v.x).add(v.y);
    }

    public JsonObjectBuilder statusToJson(String status) {
        return Json.createObjectBuilder()
                .add("status", status);
    }

    public JsonArrayBuilder streamToArray(Stream<JsonObjectBuilder> stream) {
        JsonArrayBuilder array = Json.createArrayBuilder();
        stream.forEach(array::add);
        return array;
    }
}
