package io.github.stevezhang123.gunwood.mixin.client;

import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public abstract class VanillaChunkRebuildTaskMixin {
    @Redirect(method = "compile", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState gunwood$hidePaintedBlockFromChunkMesh(RenderChunkRegion region, BlockPos pos) {
        BlockState state = region.getBlockState(pos);
        return GunwoodClientRenderRules.shouldSkipBlockRender(region, pos, state) ? Blocks.AIR.defaultBlockState() : state;
    }
}
