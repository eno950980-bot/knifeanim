package com.example.knifeanim.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

public class KnifeAnimClient implements ClientModInitializer {

    // Dauer der Inspect-Animation in Millisekunden
    private static final long DURATION_MS = 1200;

    private static KeyMapping inspectKey;
    private static long startMs = -1;

    @Override
    public void onInitializeClient() {
        inspectKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.knifeanim.inspect",
                InputConstants.KEY_R,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("knifeanim", "main"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (inspectKey.consumeClick()) {
                if (mc.player != null && isKnife(mc.player.getMainHandItem())) {
                    startMs = System.currentTimeMillis();
                }
            }
            // Animation abbrechen, wenn das Messer nicht mehr in der Hand ist
            if (mc.player == null || !isKnife(mc.player.getMainHandItem())) {
                startMs = -1;
            }
        });
    }

    /** Hier festlegen, was als "Messer" zaehlt. Standard: jedes Schwert. */
    public static boolean isKnife(ItemStack stack) {
        return stack.is(ItemTags.SWORDS);
        // Alternative: nur bestimmter Name, z.B.
        // return stack.getHoverName().getString().contains("Karambit");
    }

    /** Fortschritt 0..1, oder -1 wenn keine Animation laeuft. */
    public static float progress() {
        if (startMs < 0) return -1f;
        float t = (System.currentTimeMillis() - startMs) / (float) DURATION_MS;
        if (t >= 1f) {
            startMs = -1;
            return -1f;
        }
        return t;
    }
}
