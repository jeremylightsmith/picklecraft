package com.picklepop.pickle;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
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
