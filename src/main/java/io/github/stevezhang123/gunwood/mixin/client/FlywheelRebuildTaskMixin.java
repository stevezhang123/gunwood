package io.github.stevezhang123.gunwood.mixin.client;

import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.chunk.SectionCompiler", priority = 1100)
public abstract class FlywheelRebuildTaskMixin {
    @Inject(
            method = "handleBlockEntity(Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void gunwood$skipHiddenPaintedBlockEntityInRebuild(@Coerce Object compileResults, BlockEntity blockEntity, CallbackInfo ci) {
        if (GunwoodClientRenderRules.shouldSkipBlockEntity(blockEntity)) {
            ci.cancel();
        }
    }
}
