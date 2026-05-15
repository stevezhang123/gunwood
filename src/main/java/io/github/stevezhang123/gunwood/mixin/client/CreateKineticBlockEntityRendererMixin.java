package io.github.stevezhang123.gunwood.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer", remap = false)
public abstract class CreateKineticBlockEntityRendererMixin {
    @Inject(
            method = "renderSafe(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void gunwood$skipHiddenKineticRenderer(
            @Coerce Object blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            CallbackInfo ci
    ) {
        if (blockEntity instanceof BlockEntity be && GunwoodClientRenderRules.shouldSkipCreateKineticRender(be)) {
            ci.cancel();
        }
    }
}
