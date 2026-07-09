package io.github.stevezhang123.gunwood.mixin.client;

import com.mojang.logging.LogUtils;
import com.mojang.blaze3d.vertex.VertexSorting;
import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import org.slf4j.Logger;

@Mixin(SectionCompiler.class)
public abstract class SectionCompilerMixin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Redirect(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState gunwood$hidePaintedBlockFromVanillaChunkMesh(
            RenderChunkRegion region,
            BlockPos pos,
            SectionPos sectionPos,
            RenderChunkRegion compileRegion,
            VertexSorting vertexSorting,
            SectionBufferBuilderPack sectionBufferBuilderPack,
            List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers
    ) {
        BlockState state = region.getBlockState(pos);
        if (GunwoodClientRenderRules.shouldSkipBlockRender(region, pos, state)) {
            debugPaintPath("vanilla_chunk_mesh_skip pos={} {} {} renderVisibilityShouldSkip=true", pos.getX(), pos.getY(), pos.getZ());
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    private static void debugPaintPath(String message, Object... args) {
        if (GunwoodCommonConfig.debugPaintingPath()) {
            LOGGER.info("[Gunwood paint debug] " + message, args);
        }
    }
}
