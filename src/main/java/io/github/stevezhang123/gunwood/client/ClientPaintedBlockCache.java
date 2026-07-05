package io.github.stevezhang123.gunwood.client;

import io.github.stevezhang123.gunwood.compat.create.GunwoodFlywheelCompat;
import io.github.stevezhang123.gunwood.compat.create.GunwoodCreateContraptionCompat;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClientPaintedBlockCache {
    private static final LongSet PAINTED_BLOCKS = new LongOpenHashSet();
    private static final Long2ObjectMap<LongSet> PAINTED_BLOCKS_BY_CHUNK = new Long2ObjectOpenHashMap<>();

    private ClientPaintedBlockCache() {
    }

    public static void replaceAll(Collection<BlockPos> positions) {
        LongSet newPositions = new LongOpenHashSet();
        positions.forEach(pos -> newPositions.add(pos.asLong()));
        replaceAll(newPositions);
    }

    public static void replaceAll(long[] positions) {
        LongSet newPositions = new LongOpenHashSet(positions);
        replaceAll(newPositions);
    }

    public static void replaceAll(LongCollection positions) {
        LongSet oldPositions = new LongOpenHashSet(PAINTED_BLOCKS);
        LongSet newPositions = new LongOpenHashSet(positions);

        PAINTED_BLOCKS.clear();
        PAINTED_BLOCKS_BY_CHUNK.clear();
        newPositions.forEach((long pos) -> addToCache(pos));

        newPositions.forEach((long pos) -> {
            if (!oldPositions.contains(pos)) {
                BlockPos blockPos = BlockPos.of(pos);
                markRenderDirty(pos);
                refreshFlywheelVisual(blockPos, true);
            }
        });
        oldPositions.forEach((long pos) -> {
            if (!newPositions.contains(pos)) {
                BlockPos blockPos = BlockPos.of(pos);
                markRenderDirty(pos);
                refreshFlywheelVisual(blockPos, false);
            }
        });
        refreshAllFlywheelVisuals();
        refreshAllContraptionVisuals();
    }

    public static void add(BlockPos pos) {
        add(pos.asLong());
    }

    public static void add(long pos) {
        if (addToCache(pos)) {
            BlockPos blockPos = BlockPos.of(pos);
            markRenderDirty(pos);
            refreshFlywheelVisual(blockPos, true);
            refreshContraptionVisual(blockPos);
        }
    }

    public static void addAll(Collection<BlockPos> positions) {
        LongArrayList positionLongs = new LongArrayList(positions.size());
        for (BlockPos pos : positions) {
            positionLongs.add(pos.asLong());
        }
        addAll(positionLongs);
    }

    public static void addAll(long[] positions) {
        addAll(new LongArrayList(positions));
    }

    public static void addAll(LongCollection positions) {
        LongList changedPositions = new LongArrayList();
        positions.forEach((long pos) -> {
            if (addToCache(pos)) {
                changedPositions.add(pos);
            }
        });

        if (!changedPositions.isEmpty()) {
            markRenderDirty(changedPositions);
            changedPositions.forEach((long pos) -> refreshFlywheelVisual(BlockPos.of(pos), true));
            refreshContraptionVisuals(toBlockPosList(changedPositions));
        }
    }

    public static void remove(BlockPos pos) {
        remove(pos.asLong());
    }

    public static void remove(long pos) {
        if (PAINTED_BLOCKS.remove(pos)) {
            removeFromChunkIndex(pos);
            BlockPos blockPos = BlockPos.of(pos);
            markRenderDirty(pos);
            refreshFlywheelVisual(blockPos, false);
            refreshContraptionVisual(blockPos);
        }
    }

    public static void removeAll(Collection<BlockPos> positions) {
        LongArrayList positionLongs = new LongArrayList(positions.size());
        for (BlockPos pos : positions) {
            positionLongs.add(pos.asLong());
        }
        removeAll(positionLongs);
    }

    public static void removeAll(long[] positions) {
        removeAll(new LongArrayList(positions));
    }

    public static void removeAll(LongCollection positions) {
        LongList changedPositions = new LongArrayList();
        positions.forEach((long pos) -> {
            if (PAINTED_BLOCKS.remove(pos)) {
                removeFromChunkIndex(pos);
                changedPositions.add(pos);
            }
        });

        if (!changedPositions.isEmpty()) {
            markRenderDirty(changedPositions);
            changedPositions.forEach((long pos) -> refreshFlywheelVisual(BlockPos.of(pos), false));
            refreshContraptionVisuals(toBlockPosList(changedPositions));
        }
    }

    public static boolean contains(BlockPos pos) {
        return contains(pos.asLong());
    }

    public static boolean contains(long pos) {
        return PAINTED_BLOCKS.contains(pos);
    }

    public static Set<BlockPos> positions() {
        Set<BlockPos> positions = new HashSet<>();
        PAINTED_BLOCKS.forEach((long pos) -> positions.add(BlockPos.of(pos)));
        return Set.copyOf(positions);
    }

    public static long[] positionLongs() {
        return PAINTED_BLOCKS.toLongArray();
    }

    public static List<BlockPos> positionsNear(BlockPos center, int range, int limit) {
        List<BlockPos> positions = new ArrayList<>();
        int minChunkX = (center.getX() - range) >> 4;
        int maxChunkX = (center.getX() + range) >> 4;
        int minChunkZ = (center.getZ() - range) >> 4;
        int maxChunkZ = (center.getZ() + range) >> 4;
        double rangeSqr = (double) range * range;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                LongSet chunkPositions = PAINTED_BLOCKS_BY_CHUNK.get(ChunkPos.asLong(chunkX, chunkZ));
                if (chunkPositions == null) {
                    continue;
                }

                for (long posLong : chunkPositions) {
                    BlockPos pos = BlockPos.of(posLong);
                    if (pos.distSqr(center) <= rangeSqr) {
                        positions.add(pos);
                        if (positions.size() >= limit) {
                            return positions;
                        }
                    }
                }
            }
        }

        return positions;
    }

    public static void refreshAllRendering() {
        PAINTED_BLOCKS.forEach((long pos) -> markRenderDirty(pos));
        refreshAllFlywheelVisuals();
        refreshAllContraptionVisuals();
    }

    private static void markRenderDirty(BlockPos pos) {
        markRenderDirty(pos.asLong());
    }

    private static void markRenderDirty(long posLong) {
        BlockPos pos = BlockPos.of(posLong);
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && GunwoodCommonConfig.refreshLightOnPaintChange()) {
            updateSkyLightSources(posLong);
            BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                    .forEach(lightPos -> minecraft.level.getLightEngine().checkBlock(lightPos.immutable()));
            minecraft.level.getLightEngine().runLightUpdates();
        }

        if (minecraft.levelRenderer != null) {
            try {
                minecraft.levelRenderer.setBlocksDirty(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
            } catch (NullPointerException ignored) {
                // The level renderer can outlive its view area briefly while leaving a world.
            }
        }
    }

    private static void markRenderDirty(Collection<BlockPos> positions) {
        LongArrayList positionLongs = new LongArrayList(positions.size());
        for (BlockPos pos : positions) {
            positionLongs.add(pos.asLong());
        }
        markRenderDirty(positionLongs);
    }

    private static void markRenderDirty(LongCollection positions) {
        Minecraft minecraft = Minecraft.getInstance();
        if (positions.isEmpty()) {
            return;
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        LongSet lightPositions = new LongOpenHashSet();

        for (long posLong : positions) {
            BlockPos pos = BlockPos.of(posLong);
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
            updateSkyLightSources(posLong);
            BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                    .forEach(lightPos -> lightPositions.add(lightPos.asLong()));
        }

        if (minecraft.level != null && GunwoodCommonConfig.refreshLightOnPaintChange()) {
            lightPositions.forEach((long lightPos) -> minecraft.level.getLightEngine().checkBlock(BlockPos.of(lightPos)));
            minecraft.level.getLightEngine().runLightUpdates();
        }

        if (minecraft.levelRenderer != null) {
            try {
                minecraft.levelRenderer.setBlocksDirty(minX - 1, minY - 1, minZ - 1, maxX + 1, maxY + 1, maxZ + 1);
            } catch (NullPointerException ignored) {
                // The level renderer can outlive its view area briefly while leaving a world.
            }
        }
    }

    private static long chunkKey(long pos) {
        return ChunkPos.asLong(BlockPos.getX(pos) >> 4, BlockPos.getZ(pos) >> 4);
    }

    private static boolean addToCache(long pos) {
        if (PAINTED_BLOCKS.add(pos)) {
            PAINTED_BLOCKS_BY_CHUNK.computeIfAbsent(chunkKey(pos), key -> new LongOpenHashSet()).add(pos);
            return true;
        }
        return false;
    }

    private static void updateSkyLightSources(long posLong) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            BlockPos pos = BlockPos.of(posLong);
            if (!minecraft.level.hasChunkAt(pos)) {
                return;
            }
            LevelChunk chunk = minecraft.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
            chunk.getSkyLightSources().update(chunk, pos.getX() & 15, pos.getY(), pos.getZ() & 15);
        }
    }

    private static void removeFromChunkIndex(long pos) {
        long chunkKey = chunkKey(pos);
        LongSet chunkPositions = PAINTED_BLOCKS_BY_CHUNK.get(chunkKey);
        if (chunkPositions != null) {
            chunkPositions.remove(pos);
            if (chunkPositions.isEmpty()) {
                PAINTED_BLOCKS_BY_CHUNK.remove(chunkKey);
            }
        }
    }

    private static List<BlockPos> toBlockPosList(LongCollection positions) {
        List<BlockPos> blockPositions = new ArrayList<>(positions.size());
        positions.forEach((long pos) -> blockPositions.add(BlockPos.of(pos)));
        return blockPositions;
    }

    private static void refreshFlywheelVisual(BlockPos pos, boolean painted) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        if (painted) {
            GunwoodFlywheelCompat.onPaintedBlockAdded(minecraft.level, pos);
        } else {
            GunwoodFlywheelCompat.onPaintedBlockRemoved(minecraft.level, pos);
        }
    }

    private static void refreshAllFlywheelVisuals() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            GunwoodFlywheelCompat.refreshAllPaintedBlocks(minecraft.level);
        }
    }

    private static void refreshContraptionVisual(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            GunwoodCreateContraptionCompat.refreshPaintedBlock(minecraft.level, pos);
        }
    }

    private static void refreshContraptionVisuals(Collection<BlockPos> positions) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            GunwoodCreateContraptionCompat.refreshPaintedBlocks(minecraft.level, positions);
        }
    }

    private static void refreshAllContraptionVisuals() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            GunwoodCreateContraptionCompat.refreshAllContraptions(minecraft.level);
        }
    }
}
