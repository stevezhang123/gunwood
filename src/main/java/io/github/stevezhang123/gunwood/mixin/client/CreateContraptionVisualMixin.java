package io.github.stevezhang123.gunwood.mixin.client;

import io.github.stevezhang123.gunwood.compat.create.GunwoodContraptionRenderRules;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.simibubi.create.content.contraptions.render.ContraptionVisual", remap = false)
public abstract class CreateContraptionVisualMixin {
    @Inject(method = "setupVisualizer", at = @At("HEAD"), cancellable = true, require = 0)
    private void gunwood$skipHiddenContraptionBlockEntityVisual(BlockEntity blockEntity, float partialTicks, CallbackInfo ci) {
        if (GunwoodContraptionRenderRules.shouldSkipContraptionBlockEntity(this, blockEntity)) {
            ci.cancel();
        }
    }

    @Inject(method = "setupActor", at = @At("HEAD"), cancellable = true, require = 0)
    private void gunwood$skipHiddenContraptionActorVisual(@Coerce Object actor, @Coerce Object renderLevel, CallbackInfo ci) {
        if (GunwoodContraptionRenderRules.shouldSkipContraptionActor(this, actor)) {
            ci.cancel();
        }
    }
}
