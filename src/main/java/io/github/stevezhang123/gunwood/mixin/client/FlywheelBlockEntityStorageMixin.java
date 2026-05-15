package io.github.stevezhang123.gunwood.mixin.client;

import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.engine_room.flywheel.impl.visualization.storage.BlockEntityStorage", remap = false)
public abstract class FlywheelBlockEntityStorageMixin {
    @Inject(
            method = "willAccept(Lnet/minecraft/world/level/block/entity/BlockEntity;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void gunwood$skipHiddenPaintedBlockEntityVisual(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
        if (GunwoodCommonConfig.enableCreateCompat() && GunwoodClientRenderRules.shouldSkipBlockEntity(blockEntity)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "createRaw(Ldev/engine_room/flywheel/api/visualization/VisualizationContext;Lnet/minecraft/world/level/block/entity/BlockEntity;F)Ldev/engine_room/flywheel/api/visual/BlockEntityVisual;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void gunwood$skipRecreatedHiddenPaintedBlockEntityVisual(
            @Coerce Object visualizationContext,
            BlockEntity blockEntity,
            float partialTick,
            CallbackInfoReturnable<Object> cir
    ) {
        if (GunwoodCommonConfig.enableCreateCompat() && GunwoodClientRenderRules.shouldSkipBlockEntity(blockEntity)) {
            cir.setReturnValue(null);
        }
    }
}
