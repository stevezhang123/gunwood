package io.github.stevezhang123.gunwood.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PaintedBlockManager {
    private PaintedBlockManager() {
    }

    public static void add(ServerLevel level, BlockPos pos) {
        if (PaintedBlockSavedData.get(level).add(pos)) {
            refreshLight(level, pos);
        }
    }

    public static void addAll(ServerLevel level, Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = PaintedBlockSavedData.get(level).addAll(positions);
        refreshLight(level, changedPositions);
    }

    public static void remove(ServerLevel level, BlockPos pos) {
        if (PaintedBlockSavedData.get(level).remove(pos)) {
            refreshLight(level, pos);
        }
    }

    public static void removeAll(ServerLevel level, Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = PaintedBlockSavedData.get(level).removeAll(positions);
        refreshLight(level, changedPositions);
    }

    public static boolean contains(ServerLevel level, BlockPos pos) {
        return PaintedBlockSavedData.get(level).contains(pos);
    }

    public static Set<BlockPos> getAll(ServerLevel level) {
        return PaintedBlockSavedData.get(level).getAll();
    }

    public static void refreshLight(ServerLevel level, BlockPos pos) {
        notifyLightRelevantBlockChange(level, pos);
        updateSkyLightSources(level, pos);
        BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                .forEach(lightPos -> level.getLightEngine().checkBlock(lightPos.immutable()));
    }

    private static void refreshLight(ServerLevel level, Collection<BlockPos> positions) {
        if (positions.isEmpty()) {
            return;
        }

        Set<BlockPos> lightPositions = new HashSet<>();
        positions.forEach(pos -> {
            notifyLightRelevantBlockChange(level, pos);
            updateSkyLightSources(level, pos);
            BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                    .forEach(lightPos -> lightPositions.add(lightPos.immutable()));
        });
        lightPositions.forEach(level.getLightEngine()::checkBlock);
    }

    public static void refreshAllLight(ServerLevel level) {
        refreshLight(level, new ArrayList<>(getAll(level)));
    }

    private static void notifyLightRelevantBlockChange(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, state, 2);
    }

    private static void updateSkyLightSources(ServerLevel level, BlockPos pos) {
        level.getChunk(pos).getSkyLightSources().update(level.getChunk(pos), pos.getX() & 15, pos.getY(), pos.getZ() & 15);
    }
}
