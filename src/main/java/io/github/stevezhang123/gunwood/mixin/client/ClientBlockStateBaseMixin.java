package io.github.stevezhang123.gunwood.mixin.client;

import io.github.stevezhang123.gunwood.client.GunwoodClientLightRules;
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
public abstract class ClientBlockStateBaseMixin {
    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksDoNotBlockClientLight(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (GunwoodClientLightRules.isPainted(level, pos)) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksPropagateClientSkylight(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (GunwoodClientLightRules.isPainted(level, pos)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksUseEmptyClientLightOcclusionShape(BlockGetter level, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (GunwoodClientLightRules.isPainted(level, pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(method = "getFaceOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksUseEmptyClientFaceOcclusionShape(
            BlockGetter level,
            BlockPos pos,
            Direction direction,
            CallbackInfoReturnable<VoxelShape> cir
    ) {
        if (GunwoodClientLightRules.isPainted(level, pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksUseTransparentClientShadeBrightness(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (GunwoodClientLightRules.isPainted(level, pos)) {
            cir.setReturnValue(1.0F);
        }
    }

    @Inject(method = "isViewBlocking", at = @At("HEAD"), cancellable = true)
    private void gunwood$paintedBlocksDoNotBlockClientView(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (GunwoodClientLightRules.isPainted(level, pos)) {
            cir.setReturnValue(false);
        }
    }
}
