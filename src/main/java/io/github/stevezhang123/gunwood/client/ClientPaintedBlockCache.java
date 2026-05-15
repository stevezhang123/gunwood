package io.github.stevezhang123.gunwood.client;

import io.github.stevezhang123.gunwood.compat.create.GunwoodFlywheelCompat;
import io.github.stevezhang123.gunwood.compat.create.GunwoodCreateContraptionCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ClientPaintedBlockCache {
    private static final Set<BlockPos> PAINTED_BLOCKS = new HashSet<>();
    private static final Map<Long, Set<BlockPos>> PAINTED_BLOCKS_BY_CHUNK = new HashMap<>();

    private ClientPaintedBlockCache() {
    }

    public static void replaceAll(Collection<BlockPos> positions) {
        Set<BlockPos> oldPositions = Set.copyOf(PAINTED_BLOCKS);
        Set<BlockPos> newPositions = new HashSet<>();
        positions.forEach(pos -> newPositions.add(pos.immutable()));

        PAINTED_BLOCKS.clear();
        PAINTED_BLOCKS_BY_CHUNK.clear();
        newPositions.forEach(ClientPaintedBlockCache::add);

        oldPositions.stream()
                .filter(pos -> !newPositions.contains(pos))
                .forEach(pos -> {
                    markRenderDirty(pos);
                    refreshFlywheelVisual(pos, false);
                });
        refreshAllFlywheelVisuals();
        refreshAllContraptionVisuals();
    }

    public static void add(BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        if (PAINTED_BLOCKS.add(immutablePos)) {
            PAINTED_BLOCKS_BY_CHUNK.computeIfAbsent(chunkKey(immutablePos), key -> new HashSet<>()).add(immutablePos);
            markRenderDirty(immutablePos);
            refreshFlywheelVisual(immutablePos, true);
            refreshContraptionVisual(immutablePos);
        }
    }

    public static void addAll(Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = new ArrayList<>();
        for (BlockPos pos : positions) {
            BlockPos immutablePos = pos.immutable();
            if (PAINTED_BLOCKS.add(immutablePos)) {
                PAINTED_BLOCKS_BY_CHUNK.computeIfAbsent(chunkKey(immutablePos), key -> new HashSet<>()).add(immutablePos);
                changedPositions.add(immutablePos);
            }
        }

        if (!changedPositions.isEmpty()) {
            markRenderDirty(changedPositions);
            changedPositions.forEach(pos -> refreshFlywheelVisual(pos, true));
            refreshContraptionVisuals(changedPositions);
        }
    }

    public static void addAll(Collection<BlockPos> positions) {
        positions.forEach(ClientPaintedBlockCache::add);
    }

    public static void remove(BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        if (PAINTED_BLOCKS.remove(immutablePos)) {
            removeFromChunkIndex(immutablePos);
            markRenderDirty(immutablePos);
            refreshFlywheelVisual(immutablePos, false);
            refreshContraptionVisual(immutablePos);
        }
    }

    public static void removeAll(Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = new ArrayList<>();
        for (BlockPos pos : positions) {
            BlockPos immutablePos = pos.immutable();
            if (PAINTED_BLOCKS.remove(immutablePos)) {
                removeFromChunkIndex(immutablePos);
                changedPositions.add(immutablePos);
            }
        }

        if (!changedPositions.isEmpty()) {
            markRenderDirty(changedPositions);
            changedPositions.forEach(pos -> refreshFlywheelVisual(pos, false));
            refreshContraptionVisuals(changedPositions);
        }
    }

    public static void removeAll(Collection<BlockPos> positions) {
        positions.forEach(ClientPaintedBlockCache::remove);
    }

    public static boolean contains(BlockPos pos) {
        return PAINTED_BLOCKS.contains(pos);
    }

    public static Set<BlockPos> positions() {
        return Set.copyOf(PAINTED_BLOCKS);
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
                Set<BlockPos> chunkPositions = PAINTED_BLOCKS_BY_CHUNK.get(ChunkPos.asLong(chunkX, chunkZ));
                if (chunkPositions == null) {
                    continue;
                }

                for (BlockPos pos : chunkPositions) {
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
        PAINTED_BLOCKS.forEach(ClientPaintedBlockCache::markRenderDirty);
        refreshAllFlywheelVisuals();
        refreshAllContraptionVisuals();
    }

    private static void markRenderDirty(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            updateSkyLightSources(pos);
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
        Set<BlockPos> lightPositions = new HashSet<>();

        for (BlockPos pos : positions) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
            updateSkyLightSources(pos);
            BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                    .forEach(lightPos -> lightPositions.add(lightPos.immutable()));
        }

        if (minecraft.level != null) {
            lightPositions.forEach(minecraft.level.getLightEngine()::checkBlock);
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

    private static long chunkKey(BlockPos pos) {
        return ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private static void updateSkyLightSources(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            minecraft.level.getChunk(pos).getSkyLightSources().update(minecraft.level.getChunk(pos), pos.getX() & 15, pos.getY(), pos.getZ() & 15);
        }
    }

    private static void removeFromChunkIndex(BlockPos pos) {
        long chunkKey = chunkKey(pos);
        Set<BlockPos> chunkPositions = PAINTED_BLOCKS_BY_CHUNK.get(chunkKey);
        if (chunkPositions != null) {
            chunkPositions.remove(pos);
            if (chunkPositions.isEmpty()) {
                PAINTED_BLOCKS_BY_CHUNK.remove(chunkKey);
            }
        }
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
