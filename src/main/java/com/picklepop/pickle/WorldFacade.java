package com.picklepop.pickle;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

class WorldFacade {
    private MinecraftServer server;
    private static final Logger LOGGER = LogManager.getLogger();

    public WorldFacade(MinecraftServer server) {
        this.server = server;
    }

    public ServerLevel getWorld() {
        return this.server.overworld();
    }

    public void placeBlock(String type, BlockPos pos) {
        getWorld().setBlock(pos, getBlock(type).defaultBlockState(), 3);
    }

    public void placeBlocks(String type, BlockPos from, BlockPos to) {
        ServerLevel world = getWorld();
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
        return getWorld().getBlockStates(new AABB(from, to));
    }

    public Block getBlock(String type) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(type));
        if (block != null)
            return block;
        else
            throw new RuntimeException("Couldn't find block: " + type);
//        IForgeRegistry<Block> registry = GameRegistry.findRegistry(Block.class);
//        Block block = registry.getValue(new ResourceLocation(type));
//        if (block != null)
//            return block;
//        else
//            throw new RuntimeException("Couldn't find block: " + type);
    }

    public EntityType<?> getEntityType(String type) {
        Optional<EntityType<?>> entityType = EntityType.byString(type);
        if (entityType.isPresent())
            return entityType.get();
        else
            throw new RuntimeException("Couldn't find entity type: " + type);
    }

    public List<ServerPlayer> getPlayers() {
        return getWorld().players();
    }

    public ServerPlayer getPlayer(String name) {
        List<ServerPlayer> players = getWorld().getPlayers(player -> player.getName().getString().equals(name));

        if (players.size() == 0) {
            List<String> names = new ArrayList<>();
            getWorld().getPlayers(p -> true).forEach(p -> names.add(p.getName().getString()));
            throw new RuntimeException("Can't find player: " + name + ". Server only has players: " +
                    String.join(", ", names));
        }

        return players.get(0);
    }

    public void moveEntity(Player player, Vec3 pos) {
        LOGGER.info("Player " + player.getName().getContents() + " -> " + pos);
        player.travel(pos);
    }

    public void spawnEntity(String type, Vec3 pos) {
        ServerLevel world = getWorld();
        Entity entity = getEntityType(type).create(world);
        entity.setPos(pos.x, pos.y, pos.z);
        world.addFreshEntity(entity);
    }

    public List<LivingEntity> getNearbyEntities(ServerPlayer player, int range) {
        return getWorld().getNearbyEntities(LivingEntity.class,
                TargetingConditions.forNonCombat().range(20),
                player,
                new AABB(
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
