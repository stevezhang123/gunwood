package io.github.stevezhang123.gunwood.mixin;

import io.github.stevezhang123.gunwood.paint.PaintedBlockLightTransparency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightEngine.class)
public abstract class LightEngineMixin {
    @Shadow
    @Final
    protected LightChunkGetter chunkSource;

    @Inject(method = "getOpacity", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksHaveAirLikeOpacity(BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (PaintedBlockLightTransparency.isPainted(this.chunkSource.getLevel(), pos)) {
            cir.setReturnValue(1);
        }
    }

    @Inject(method = "getState", at = @At("HEAD"), cancellable = true)
    private void gunwood$lightEngineSeesPaintedBlocksAsAir(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (PaintedBlockLightTransparency.isPainted(this.chunkSource.getLevel(), pos)) {
            cir.setReturnValue(Blocks.AIR.defaultBlockState());
        }
    }

    @Inject(
            method = "getOcclusionShape(Lnet/minecraft/world/level/block/state/BlockState;JLnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gunwood$paintedBlocksHaveEmptyLightEngineShape(
            BlockState state,
            long pos,
            Direction direction,
            CallbackInfoReturnable<VoxelShape> cir
    ) {
        if (PaintedBlockLightTransparency.isPainted(this.chunkSource.getLevel(), BlockPos.of(pos))) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(
            method = "getOcclusionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void gunwood$paintedBlocksHaveEmptyStaticLightEngineShape(
            BlockGetter level,
            BlockPos pos,
            BlockState state,
            Direction direction,
            CallbackInfoReturnable<VoxelShape> cir
    ) {
        if (PaintedBlockLightTransparency.isPainted(level, pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(method = "getLightBlockInto", at = @At("HEAD"), cancellable = true)
    private static void gunwood$paintedBlocksDoNotFaceOccludeLight(
            BlockGetter level,
            BlockState state1,
            BlockPos pos1,
            BlockState state2,
            BlockPos pos2,
            Direction direction,
            int defaultReturnValue,
            CallbackInfoReturnable<Integer> cir
    ) {
        boolean painted1 = PaintedBlockLightTransparency.isPainted(level, pos1);
        boolean painted2 = PaintedBlockLightTransparency.isPainted(level, pos2);
        if (painted1 || painted2) {
            VoxelShape shape1 = painted1 ? Shapes.empty() : LightEngine.getOcclusionShape(level, pos1, state1, direction);
            VoxelShape shape2 = painted2 ? Shapes.empty() : LightEngine.getOcclusionShape(level, pos2, state2, direction.getOpposite());
            cir.setReturnValue(Shapes.mergedFaceOccludes(shape1, shape2, direction) ? 16 : defaultReturnValue);
        }
    }
}
