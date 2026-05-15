package io.github.stevezhang123.gunwood.mixin;

import io.github.stevezhang123.gunwood.paint.PaintedBlockLightTransparency;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSkyLightSources.class)
public abstract class ChunkSkyLightSourcesMixin {
    @Inject(method = "isEdgeOccluded", at = @At("HEAD"), cancellable = true)
    private static void gunwood$paintedBlocksDoNotBlockSkySourceColumns(
            BlockGetter level,
            BlockPos pos1,
            BlockState state1,
            BlockPos pos2,
            BlockState state2,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (PaintedBlockLightTransparency.isPainted(level, pos1) || PaintedBlockLightTransparency.isPainted(level, pos2)) {
            cir.setReturnValue(false);
        }
    }
}
