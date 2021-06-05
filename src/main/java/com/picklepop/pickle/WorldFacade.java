package com.picklepop.pickle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class WorldFacade {
    private MinecraftServer server;

    public WorldFacade(MinecraftServer server) {
        this.server = server;
    }

    public ServerWorld getWorld() {
        return this.server.overworld();
    }

    public void placeBlock(String type, BlockPos pos) {
        getWorld().setBlock(pos, getBlock(type).defaultBlockState(), 3);
    }

    public void placeBlocks(String type, BlockPos from, BlockPos to) {
        ServerWorld world = getWorld();
        BlockState blockState = getBlock(type).defaultBlockState();
        int x1 = Math.min(from.getX(), to.getX());
        int x2 = Math.max(from.getX(), to.getX());
        int y1 = Math.min(from.getY(), to.getY());
        int y2 = Math.max(from.getY(), to.getY());
        int z1 = Math.min(from.getZ(), to.getZ());
        int z2 = Math.max(from.getZ(), to.getZ());

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    world.setBlock(new BlockPos(x, y, z), blockState, 3);
                }
            }
        }
    }

    public Stream<BlockState> getBlocks(BlockPos from, BlockPos to) {
        return getWorld().getBlockStates(new AxisAlignedBB(from, to));
    }

    public Block getBlock(String type) {
        IForgeRegistry<Block> registry = GameRegistry.findRegistry(Block.class);
        Block block = registry.getValue(new ResourceLocation(type));
        if (block != null)
            return block;
        else
            throw new RuntimeException("Couldn't find block: " + type);
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
