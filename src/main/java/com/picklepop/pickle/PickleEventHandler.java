package com.picklepop.pickle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PickleEventHandler {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void pickupItem(EntityItemPickupEvent event) {
//        LOGGER.info("Item picked up!");
    }

    @SubscribeEvent
    public void breakItem(BlockEvent.BreakEvent event) {
        LOGGER.info("Broke a block!");

        if (event.getState().getBlock() == Blocks.DIAMOND_ORE) {

        }

        if (event.getState().getBlock() == Blocks.COBBLESTONE) {
//            World world = (World) event.getWorld();

        }

//        BlockPos pos = event.getPos();
//
//        // Get the reference to the block we want to place
//        Block blk = Blocks.FURNACE;
//        // Make a position.
//        BlockPos pos0 = new BlockPos(pos.getX()+1, (pos.getY()+1) , pos.getZ());
//
//        World world = (World) event.getWorld();
//
//        LOGGER.info("got the world : " + world.toString());
//
//        // Get the default state(basically metadata 0)
//        BlockState state0=blk.defaultBlockState();
//
//        LOGGER.info("state : " + state0.toString());
//        LOGGER.info("pos : " + pos0.toString());
//
//        // set the block
//        world.setBlock(pos0, state0, 3);
    }

//    @SubscribeEvent
//    public void onLootTableLoad(LootTableLoadEvent event) {
//        LOGGER.info("Loading loot table ############################################################")
//        LootTable table = event.getTable();
//        LOGGER.info(table.toString());
//    }

//    @SubscribeEvent(priority= EventPriority.NORMAL, receiveCanceled=true)
//    public void onEvent(LivingDropsEvent event)
//    {
//        if (event.getEntity().getType() instanceof EntityType.SHEEP)
//        {
//            // DEBUG
//            System.out.println("EntitySheep drops event");
//            event.getDrops().clear();
//            ItemStack itemStackToDrop = new ItemStack(Items.APPLE, 5);
//            event.getDrops().add(new EntityItem(event.entity.worldObj, event.entity.posX,
//                    event.entity.posY, event.entity.posZ, itemStackToDrop));
//            event.getDrops().add(new Entit(event.entity.worldObj, event.entity.posX,
//                    event.entity.posY, event.entity.posZ, itemStackToDrop));
//
//        }
//    }
}
