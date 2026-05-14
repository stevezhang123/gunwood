package io.github.stevezhang123.gunwood.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Set;

public final class PaintedBlockManager {
    private PaintedBlockManager() {
    }

    public static void add(ServerLevel level, BlockPos pos) {
        PaintedBlockSavedData.get(level).add(pos);
        refreshLight(level, pos);
    }

    public static void remove(ServerLevel level, BlockPos pos) {
        PaintedBlockSavedData.get(level).remove(pos);
        refreshLight(level, pos);
    }

    public static boolean contains(ServerLevel level, BlockPos pos) {
        return PaintedBlockSavedData.get(level).contains(pos);
    }

    public static Set<BlockPos> getAll(ServerLevel level) {
        return PaintedBlockSavedData.get(level).getAll();
    }

    public static void refreshLight(ServerLevel level, BlockPos pos) {
        BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                .forEach(lightPos -> level.getLightEngine().checkBlock(lightPos.immutable()));
    }

    public static void refreshAllLight(ServerLevel level) {
        getAll(level).forEach(pos -> refreshLight(level, pos));
    }
}
