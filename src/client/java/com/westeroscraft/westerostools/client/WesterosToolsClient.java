package com.westeroscraft.westerostools.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import com.westeroscraft.westerostools.item.ClientClickTracker;

public class WesterosToolsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Track end-of-tick mouse button state so the interaction callbacks can
        // tell a fresh press from vanilla's re-fires while the button is held.
        ClientTickEvents.END_CLIENT_TICK.register(mc ->
            ClientClickTracker.endTick(mc.options.keyAttack.isDown(), mc.options.keyUse.isDown()));
    }
}
