package io.github.stevezhang123.gunwood.mixin.client;

import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.engine_room.flywheel.impl.visualization.VisualManagerImpl", remap = false)
public abstract class FlywheelVisualManagerImplMixin {
    @Inject(method = "queueAdd(Ljava/lang/Object;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void gunwood$skipHiddenBlockEntityVisualAdd(@Coerce Object object, CallbackInfo ci) {
        if (object instanceof BlockEntity blockEntity && GunwoodClientRenderRules.shouldSkipBlockEntity(blockEntity)) {
            ci.cancel();
        }
    }

    @Inject(method = "queueUpdate(Ljava/lang/Object;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void gunwood$skipHiddenBlockEntityVisualUpdate(@Coerce Object object, CallbackInfo ci) {
        if (object instanceof BlockEntity blockEntity && GunwoodClientRenderRules.shouldSkipBlockEntity(blockEntity)) {
            ci.cancel();
        }
    }
}
