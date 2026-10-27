package com.example.knifeanim.client.mixin;

import com.example.knifeanim.client.KnifeAnimClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ACHTUNG: Klassen-/Methodennamen und Signatur fuer 26.3 muessen gegen die
 * dekompilierte Klasse geprueft werden (./gradlew genSources). Bei Abweichung
 * hier anpassen.
 */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonRendererMixin {

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void knifeanim$applyInspect(AbstractClientPlayer player, float partialTick, float pitch,
                                        InteractionHand hand, float swingProgress, ItemStack stack,
                                        float equipProgress, PoseStack poseStack,
                                        SubmitNodeCollector collector, int light, CallbackInfo ci) {
        if (hand != InteractionHand.MAIN_HAND || !KnifeAnimClient.isKnife(stack)) return;

        float t = KnifeAnimClient.progress();
        if (t < 0f) return;

        float arc = (float) Math.sin(Math.PI * t);          // 0 -> 1 -> 0
        float e = t * t * (3f - 2f * t);                    // smoothstep

        // Messer leicht anheben und nach vorne holen
        poseStack.translate(0.0f, 0.12f * arc, -0.10f * arc);
        // Drehung um die Klingenachse (Achse ggf. tauschen: XP / YP / ZP)
        poseStack.mulPose(Axis.ZP.rotationDegrees(360f * e));
        // leichtes Kippen, damit es wie ein Inspect aussieht
        poseStack.mulPose(Axis.XP.rotationDegrees(-30f * arc));
        poseStack.mulPose(Axis.YP.rotationDegrees(20f * arc));
    }
}
