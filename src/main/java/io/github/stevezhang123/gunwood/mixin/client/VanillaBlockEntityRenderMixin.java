package io.github.stevezhang123.gunwood.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class VanillaBlockEntityRenderMixin {
    @Inject(
            method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private <E extends BlockEntity> void gunwood$hidePaintedBlockEntity(
            E blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            CallbackInfo ci
    ) {
        if (GunwoodClientRenderRules.shouldSkipBlockEntity(blockEntity)) {
            ci.cancel();
        }
    }
}
