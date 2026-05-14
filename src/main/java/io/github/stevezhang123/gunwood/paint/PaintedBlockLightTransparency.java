package io.github.stevezhang123.gunwood.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;

public final class PaintedBlockLightTransparency {
    private PaintedBlockLightTransparency() {
    }

    public static boolean isPainted(ServerLevel level, BlockPos pos) {
        return PaintedBlockManager.contains(level, pos);
    }

    public static boolean isPainted(BlockGetter level, BlockPos pos) {
        return level instanceof ServerLevel serverLevel && isPainted(serverLevel, pos);
    }
}
