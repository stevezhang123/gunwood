package io.github.stevezhang123.gunwood.mixin.client;

import io.github.stevezhang123.gunwood.client.ClientPaintedBlockVisibility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void gunwood$renderFaceBesideHiddenPaintedBlock(
            BlockState state,
            BlockGetter level,
            BlockPos offset,
            Direction face,
            BlockPos pos,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (ClientPaintedBlockVisibility.shouldHideOrdinaryBlockModel(level.getBlockState(pos), pos)) {
            cir.setReturnValue(true);
        }
    }
}
