package org.KreativeName.blockrandomizer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.BlockItem;
import net.minecraft.network.chat.Component;

import net.minecraft.world.phys.HitResult;
import net.minecraft.util.RandomSource;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class BlockrandomizerClient implements ClientModInitializer {
    private boolean wasRightClicking = false;
    private boolean wasHoldingBlockItem = false;
    private final RandomSource random = RandomSource.create();

    private boolean randomizationEnabled = false;

    private KeyMapping toggleKey;
    private boolean wasToggleKeyPressed = false;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blockrandomizer.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.blockrandomizer"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isToggleKeyPressed = toggleKey.isDown();
            if (isToggleKeyPressed && !wasToggleKeyPressed) {
                randomizationEnabled = !randomizationEnabled;

                if (client.player != null) {
                    Component message = Component.translatable("message.blockrandomizer.title").append(" ").append(
                            randomizationEnabled
                                    ? Component.translatable("message.blockrandomizer.enabled").withStyle(style -> style.withColor(0x55FF55))
                                    : Component.translatable("message.blockrandomizer.disabled").withStyle(style -> style.withColor(0xFF5555))
                    );
                    client.player.displayClientMessage(message, true);
                }
            }
            wasToggleKeyPressed = isToggleKeyPressed;

            if (randomizationEnabled) {
                checkBlockPlacement(client);
            }
        });
    }

    private void checkBlockPlacement(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) return;

        boolean isRightClicking = client.options.keyUse.isDown();
        boolean isHoldingBlockItem = player.getMainHandItem().getItem() instanceof BlockItem;
        HitResult hitResult = client.hitResult;
        boolean isLookingAtBlock = hitResult != null && hitResult.getType() == HitResult.Type.BLOCK;

        if (isLookingAtBlock && isHoldingBlockItem && isRightClicking) {
            if (!wasRightClicking || random.nextFloat() < 0.2f) {
                randomizeHotbarSelection(player);
            }
        }
    }

    private void randomizeHotbarSelection(LocalPlayer player) {
        int hotbarSize = 9;
        boolean isNotBlock = true;
        int randomSlot = -1;

        while (isNotBlock)
        {
            randomSlot = random.nextInt(hotbarSize);
            if (player.getInventory().getItem(randomSlot).getItem() instanceof BlockItem) {
                isNotBlock = false;
            }
        }

        player.getInventory().setSelectedSlot(randomSlot);
    }
}