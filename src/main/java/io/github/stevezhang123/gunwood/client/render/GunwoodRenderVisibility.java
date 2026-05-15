package io.github.stevezhang123.gunwood.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class GunwoodRenderVisibility {
    private GunwoodRenderVisibility() {
    }

    public static boolean shouldHideBlockPos(BlockPos pos) {
        return GunwoodClientRenderRules.shouldSkipPaintedPos(pos);
    }

    public static boolean shouldHideBlockEntity(BlockEntity blockEntity) {
        return GunwoodClientRenderRules.shouldSkipBlockEntity(blockEntity);
    }
}
