package io.github.stevezhang123.gunwood.client;

import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public final class ClientPaintedBlockVisibility {
    private ClientPaintedBlockVisibility() {
    }

    public static boolean shouldHideOrdinaryBlockModel(BlockState state, BlockPos pos) {
        // BakedModel hiding stage. BlockEntityRenderer fallback is handled separately.
        return state.getRenderShape() == RenderShape.MODEL
                && GunwoodClientRenderRules.shouldSkipPaintedPos(pos);
    }

    public static boolean hasGlasses() {
        return GunwoodClientRenderRules.isWearingGlasses();
    }
}
