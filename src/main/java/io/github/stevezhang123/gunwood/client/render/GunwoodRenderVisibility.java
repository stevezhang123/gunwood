package io.github.stevezhang123.gunwood.client.render;

import io.github.stevezhang123.gunwood.client.ClientPaintedBlockCache;
import io.github.stevezhang123.gunwood.client.ClientPaintedBlockVisibility;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class GunwoodRenderVisibility {
    private GunwoodRenderVisibility() {
    }

    public static boolean shouldHideBlockPos(BlockPos pos) {
        return Minecraft.getInstance().player != null
                && ClientPaintedBlockCache.contains(pos)
                && !ClientPaintedBlockVisibility.hasGlasses();
    }

    public static boolean shouldHideBlockEntity(BlockEntity blockEntity) {
        return blockEntity != null && shouldHideBlockPos(blockEntity.getBlockPos());
    }
}
