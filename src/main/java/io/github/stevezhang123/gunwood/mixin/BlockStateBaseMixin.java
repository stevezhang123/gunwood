package io.github.stevezhang123.gunwood.mixin;

import io.github.stevezhang123.gunwood.paint.PaintedBlockLightTransparency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksDoNotBlockLight(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (PaintedBlockLightTransparency.isPainted(level, pos)) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksPropagateSkylight(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (PaintedBlockLightTransparency.isPainted(level, pos)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksUseEmptyLightOcclusionShape(BlockGetter level, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (PaintedBlockLightTransparency.isPainted(level, pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(method = "getFaceOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksUseEmptyFaceOcclusionShape(
            BlockGetter level,
            BlockPos pos,
            Direction direction,
            CallbackInfoReturnable<VoxelShape> cir
    ) {
        if (PaintedBlockLightTransparency.isPainted(level, pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksUseTransparentShadeBrightness(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (PaintedBlockLightTransparency.isPainted(level, pos)) {
            cir.setReturnValue(1.0F);
        }
    }
}
