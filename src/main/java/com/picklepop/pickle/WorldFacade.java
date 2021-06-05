package com.picklepop.pickle;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

class WorldFacade {
    private MinecraftServer server;

    public WorldFacade(MinecraftServer server) {
        this.server = server;
    }

    public ServerWorld getWorld() {
        return this.server.overworld();
    }

    public void placeBlock(String type, BlockPos position) {
        getWorld().setBlock(position, getBlock(type).defaultBlockState(), 3);
    }

    public Block getBlock(String type) {
        switch (type) {
            case "COBBLESTONE":
                return Blocks.COBBLESTONE;
            case "IRON_ORE":
                return Blocks.IRON_ORE;
            case "GOLD_ORE":
                return Blocks.GOLD_ORE;
            case "NETHERITE_BLOCK":
                return Blocks.NETHERITE_BLOCK;
            case "WATER":
                return Blocks.WATER;
            case "ACACIA_WOOD":
                return Blocks.ACACIA_WOOD;
            case "DIAMOND_BLOCK":
                return Blocks.DIAMOND_BLOCK;
            case "GLASS":
                return Blocks.GLASS;
            default:
                return Blocks.AIR;
        }
    }

    public List<ServerPlayerEntity> getPlayers() {
        return getWorld().players();
    }

    public ServerPlayerEntity getPlayer(String name) {
        List<ServerPlayerEntity> players = getWorld().getPlayers(player -> player.getName().getString().equals(name));

        if (players.size() == 0) {
            List<String> names = new ArrayList<>();
            getWorld().getPlayers(p -> true).forEach(p -> names.add(p.getName().getString()));
            throw new RuntimeException("Can't find player: " + name + ". Server only has players: " +
                    String.join(", ", names));
        }

        return players.get(0);
    }

    public List<LivingEntity> getNearbyEntities(ServerPlayerEntity player, int range) {
        return getWorld().getNearbyEntities(LivingEntity.class,
                new EntityPredicate().range(20),
                player,
                new AxisAlignedBB(
                        player.position().x - range,
                        player.position().y - range,
                        player.position().z - range,
                        player.position().x + range,
                        player.position().y + range,
                        player.position().z + range));
    }

    public void setDayTime(String time) {
        getWorld().setDayTime(getTimeFromName(time));
    }

    private long getTimeFromName(String time) {
        switch (time) {
            case "day":
                return 1000;
            case "night":
                return 13000;
            case "noon":
                return 6000;
            case "midnight":
                return 18000;
            default:
                return 1000;
        }
    }
}
