package io.github.stevezhang123.gunwood.mixin.client.embeddium;

import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask", remap = false)
public abstract class EmbeddiumChunkBuilderMeshingTaskMixin {
    @Unique
    private final BlockPos.MutableBlockPos gunwood$blockPos = new BlockPos.MutableBlockPos();

    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/world/WorldSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"
            ),
            remap = false,
            require = 0
    )
    private BlockState gunwood$hidePaintedBlockFromEmbeddiumMesh(@Coerce Object slice, int x, int y, int z) {
        BlockState state = ((EmbeddiumWorldSliceAccessor) slice).gunwood$getBlockState(x, y, z);
        BlockPos pos = this.gunwood$blockPos.set(x, y, z);

        if (GunwoodCommonConfig.ENABLE_EMBEDDIUM_COMPAT.get()
                && GunwoodClientRenderRules.shouldSkipBlockRender((BlockAndTintGetter) slice, pos, state)) {
            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }
}
