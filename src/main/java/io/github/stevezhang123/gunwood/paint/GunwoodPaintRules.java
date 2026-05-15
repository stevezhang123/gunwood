package io.github.stevezhang123.gunwood.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public final class GunwoodPaintRules {
    private GunwoodPaintRules() {
    }

    public static boolean canPaint(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        return !state.isAir()
                && level.mayInteract(player, pos)
                && !PaintedBlockManager.contains(level, pos);
    }

    public static boolean canScrape(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        return level.mayInteract(player, pos)
                && PaintedBlockManager.contains(level, pos);
    }
}
