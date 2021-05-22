package com.picklepop.pickle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PickleEventHandler {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void pickupItem(EntityItemPickupEvent event) {
        LOGGER.info("Item picked up!");
    }

    @SubscribeEvent
    public void breakItem(BlockEvent.BreakEvent event) {
        LOGGER.info("Broke a block!");

        BlockPos pos = event.getPos();

        // Get the reference to the block we want to place
        Block blk = Blocks.FURNACE;
        // Make a position.
        BlockPos pos0 = new BlockPos(pos.getX()+1, (pos.getY()+1) , pos.getZ());

        World world = (World) event.getWorld();

        LOGGER.info("got the world : " + world.toString());

        // Get the default state(basically metadata 0)
        BlockState state0=blk.defaultBlockState();

        LOGGER.info("state : " + state0.toString());
        LOGGER.info("pos : " + pos0.toString());

        // set the block
        world.setBlock(pos0, state0, 3);
    }
}
