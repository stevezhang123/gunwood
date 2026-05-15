package io.github.stevezhang123.gunwood.paint;

import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public final class GunwoodPaintRules {
    private GunwoodPaintRules() {
    }

    public static boolean canPaint(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        return canPaint(level, pos, state, player, GunwoodCommonConfig.REQUIRE_BUILD_PERMISSION_FOR_BATCH_OPERATIONS.get());
    }

    public static boolean canPaint(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player, boolean checkPermission) {
        return !state.isAir()
                && (!checkPermission || level.mayInteract(player, pos))
                && !PaintedBlockManager.contains(level, pos);
    }

    public static boolean canScrape(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        return canScrape(level, pos, state, player, GunwoodCommonConfig.REQUIRE_BUILD_PERMISSION_FOR_BATCH_OPERATIONS.get());
    }

    public static boolean canScrape(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player, boolean checkPermission) {
        return (!checkPermission || level.mayInteract(player, pos))
                && PaintedBlockManager.contains(level, pos);
    }
}
