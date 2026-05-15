package io.github.stevezhang123.gunwood.client;

import io.github.stevezhang123.gunwood.compat.create.GunwoodFlywheelCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class ClientPaintedBlockCache {
    private static final Set<BlockPos> PAINTED_BLOCKS = new HashSet<>();

    private ClientPaintedBlockCache() {
    }

    public static void replaceAll(Collection<BlockPos> positions) {
        Set<BlockPos> oldPositions = Set.copyOf(PAINTED_BLOCKS);
        Set<BlockPos> newPositions = new HashSet<>();
        positions.forEach(pos -> newPositions.add(pos.immutable()));

        PAINTED_BLOCKS.clear();
        newPositions.forEach(ClientPaintedBlockCache::add);

        oldPositions.stream()
                .filter(pos -> !newPositions.contains(pos))
                .forEach(pos -> {
                    markRenderDirty(pos);
                    refreshFlywheelVisual(pos, false);
                });
        refreshAllFlywheelVisuals();
    }

    public static void add(BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        if (PAINTED_BLOCKS.add(immutablePos)) {
            markRenderDirty(immutablePos);
            refreshFlywheelVisual(immutablePos, true);
        }
    }

    public static void addAll(Collection<BlockPos> positions) {
        positions.forEach(ClientPaintedBlockCache::add);
    }

    public static void remove(BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        if (PAINTED_BLOCKS.remove(immutablePos)) {
            markRenderDirty(immutablePos);
            refreshFlywheelVisual(immutablePos, false);
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

    public static void refreshAllRendering() {
        PAINTED_BLOCKS.forEach(ClientPaintedBlockCache::markRenderDirty);
        refreshAllFlywheelVisuals();
    }

    private static void markRenderDirty(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                    .forEach(lightPos -> minecraft.level.getLightEngine().checkBlock(lightPos.immutable()));
        }

        if (minecraft.levelRenderer != null) {
            try {
                minecraft.levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
            } catch (NullPointerException ignored) {
                // The level renderer can outlive its view area briefly while leaving a world.
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
}
