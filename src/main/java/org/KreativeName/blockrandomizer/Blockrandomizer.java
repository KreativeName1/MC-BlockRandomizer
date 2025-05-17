package org.KreativeName.blockrandomizer;

import net.fabricmc.api.ModInitializer;

public class Blockrandomizer implements ModInitializer {

    @Override
    public void onInitialize() {
        // This code runs when Minecraft is starting up, before the client and server
        // have been initialized.
        // You can use this for any setup code that doesn't depend on either the client
        // or server.
        System.out.println("Blockrandomizer is initializing!");
    }
}
