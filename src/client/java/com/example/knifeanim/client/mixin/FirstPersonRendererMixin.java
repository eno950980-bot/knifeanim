package com.example.knifeanim.client.mixin;

import com.example.knifeanim.client.KnifeAnimClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

        float arc = (float) Math.sin(Math.PI * t);
        float e = t * t * (3f - 2f * t);

        poseStack.translate(0.0f, 0.12f * arc, -0.10f * arc);

        poseStack.mulPose(new Matrix4f()
                .rotateZ((float) Math.toRadians(360f * e))
                .rotateX((float) Math.toRadians(-30f * arc))
                .rotateY((float) Math.toRadians(20f * arc)));
    }
}
