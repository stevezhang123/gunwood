package io.github.stevezhang123.gunwood.paint;

import com.mojang.logging.LogUtils;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Set;

public final class PaintedBlockManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static int skippedLightRefreshWarnings;

    private PaintedBlockManager() {
    }

    public static void add(ServerLevel level, BlockPos pos) {
        if (PaintedBlockSavedData.get(level).add(pos)) {
            refreshLight(level, pos.asLong());
        }
    }

    public static void add(ServerLevel level, long pos) {
        if (PaintedBlockSavedData.get(level).add(pos)) {
            refreshLight(level, pos);
        }
    }

    public static void addAll(ServerLevel level, Collection<BlockPos> positions) {
        LongList changedPositions = PaintedBlockSavedData.get(level).addAll(positions);
        refreshLight(level, changedPositions);
    }

    public static LongList addAllLongs(ServerLevel level, LongCollection positions) {
        LongList changedPositions = PaintedBlockSavedData.get(level).addAllLongs(positions);
        refreshLight(level, changedPositions);
        return changedPositions;
    }

    public static void remove(ServerLevel level, BlockPos pos) {
        if (PaintedBlockSavedData.get(level).remove(pos)) {
            refreshLight(level, pos.asLong());
        }
    }

    public static void remove(ServerLevel level, long pos) {
        if (PaintedBlockSavedData.get(level).remove(pos)) {
            refreshLight(level, pos);
        }
    }

    public static void removeAll(ServerLevel level, Collection<BlockPos> positions) {
        LongList changedPositions = PaintedBlockSavedData.get(level).removeAll(positions);
        refreshLight(level, changedPositions);
    }

    public static LongList removeAllLongs(ServerLevel level, LongCollection positions) {
        LongList changedPositions = PaintedBlockSavedData.get(level).removeAllLongs(positions);
        refreshLight(level, changedPositions);
        return changedPositions;
    }

    public static boolean contains(ServerLevel level, BlockPos pos) {
        return contains(level, pos.asLong());
    }

    public static boolean contains(ServerLevel level, long pos) {
        return PaintedBlockSavedData.get(level).contains(pos);
    }

    public static Set<BlockPos> getAll(ServerLevel level) {
        return PaintedBlockSavedData.get(level).getAll();
    }

    public static LongSet getAllLongs(ServerLevel level) {
        return PaintedBlockSavedData.get(level).getAllLongs();
    }

    public static long[] getAllLongArray(ServerLevel level) {
        return PaintedBlockSavedData.get(level).getAllLongArray();
    }

    public static void refreshLight(ServerLevel level, BlockPos pos) {
        refreshLight(level, pos.asLong());
    }

    public static void refreshLight(ServerLevel level, long pos) {
        if (!GunwoodCommonConfig.refreshLightOnPaintChange()) {
            return;
        }

        if (!isSafeLightRefreshTarget(level, pos)) {
            return;
        }

        try {
            BlockPos blockPos = BlockPos.of(pos);
            notifyLightRelevantBlockChange(level, blockPos);
            updateSkyLightSources(level, blockPos);
            BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))
                    .forEach(lightPos -> checkLightBlock(level, lightPos.asLong()));
        } catch (RuntimeException exception) {
            handleLightRefreshException(pos, exception);
        }
    }

    private static void refreshLight(ServerLevel level, LongCollection positions) {
        if (positions.isEmpty() || !GunwoodCommonConfig.refreshLightOnPaintChange()) {
            return;
        }

        LongSet lightPositions = new LongOpenHashSet();
        positions.forEach((long pos) -> {
            if (!isSafeLightRefreshTarget(level, pos)) {
                return;
            }

            try {
                BlockPos blockPos = BlockPos.of(pos);
                notifyLightRelevantBlockChange(level, blockPos);
                updateSkyLightSources(level, blockPos);
                BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))
                        .forEach(lightPos -> {
                            long lightPosLong = lightPos.asLong();
                            if (isSafeLightRefreshTarget(level, lightPosLong)) {
                                lightPositions.add(lightPosLong);
                            }
                        });
            } catch (RuntimeException exception) {
                handleLightRefreshException(pos, exception);
            }
        });
        lightPositions.forEach((long lightPos) -> checkLightBlock(level, lightPos));
    }

    public static void refreshAllLight(ServerLevel level) {
        refreshLight(level, new LongArrayList(getAllLongs(level)));
    }

    private static void notifyLightRelevantBlockChange(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, state, 2);
    }

    private static void updateSkyLightSources(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        chunk.getSkyLightSources().update(chunk, pos.getX() & 15, pos.getY(), pos.getZ() & 15);
    }

    private static boolean isSafeLightRefreshTarget(ServerLevel level, long pos) {
        if (level == null) {
            return false;
        }

        BlockPos blockPos = BlockPos.of(pos);
        if (!level.isInWorldBounds(blockPos)) {
            return false;
        }

        try {
            return level.hasChunkAt(blockPos);
        } catch (RuntimeException exception) {
            handleLightRefreshException(pos, exception);
            return false;
        }
    }

    private static void checkLightBlock(ServerLevel level, long pos) {
        try {
            level.getLightEngine().checkBlock(BlockPos.of(pos));
        } catch (RuntimeException exception) {
            handleLightRefreshException(pos, exception);
        }
    }

    private static void handleLightRefreshException(long pos, RuntimeException exception) {
        if (!GunwoodCommonConfig.skipLightRefreshInSablePhysicalBodies()) {
            throw exception;
        }
        warnSkippedLightRefresh(pos, exception);
    }

    private static void warnSkippedLightRefresh(long pos, RuntimeException exception) {
        if (skippedLightRefreshWarnings < 5) {
            BlockPos blockPos = BlockPos.of(pos);
            LOGGER.warn(
                    "Skipping Gunwood light refresh at {} {} {} because the target is not safely refreshable: {}",
                    blockPos.getX(),
                    blockPos.getY(),
                    blockPos.getZ(),
                    exception.toString()
            );
            skippedLightRefreshWarnings++;
        } else {
            LOGGER.debug("Skipping Gunwood light refresh at packed position {}", pos, exception);
        }
    }
}
