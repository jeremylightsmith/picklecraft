package com.picklepop.pickle;

import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// this is a class that makes it impossible to actually play minecraft. this is for little ones that are
// trying to concentrate and work on their mods :)
//
// you can lift it temporarily from the api, or just comment it out if you actually want to play
public class TheBoot {
    private static final Logger LOGGER = LogManager.getLogger();
    private long liftUntil = 0;

    @SubscribeEvent
    public void pickupItem(EntityItemPickupEvent event) {
        if (isLifted()) return;

        LOGGER.info("Cancelling item pick up");
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void breakItem(BlockEvent.BreakEvent event) {
        if (isLifted()) return;

        LOGGER.info("Cancelling break item");
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void attackEntity(AttackEntityEvent event) {
        if (isLifted()) return;

        LOGGER.info("Cancelling attack entity");
        event.setCanceled(true);
    }

    public void liftBoot(int seconds) {
        this.liftUntil = System.currentTimeMillis() + seconds * 1000;
    }

    private boolean isLifted() {
        return System.currentTimeMillis() < liftUntil;
    }
}
