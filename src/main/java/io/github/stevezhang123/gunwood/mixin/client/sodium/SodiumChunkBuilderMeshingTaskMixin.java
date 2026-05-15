package io.github.stevezhang123.gunwood.mixin.client.sodium;

import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask", remap = false)
public abstract class SodiumChunkBuilderMeshingTaskMixin {
    @Unique
    private final BlockPos.MutableBlockPos gunwood$blockPos = new BlockPos.MutableBlockPos();

    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"
            ),
            remap = false,
            require = 0
    )
    private BlockState gunwood$hidePaintedBlockFromSodiumMesh(@Coerce Object slice, int x, int y, int z) {
        BlockState state = ((SodiumLevelSliceAccessor) slice).gunwood$getBlockState(x, y, z);
        BlockPos pos = this.gunwood$blockPos.set(x, y, z);

        if (GunwoodClientRenderRules.shouldSkipBlockRender((BlockAndTintGetter) slice, pos, state)) {
            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }
}
