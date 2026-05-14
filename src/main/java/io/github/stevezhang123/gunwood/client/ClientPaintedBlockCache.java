package io.github.stevezhang123.gunwood.client;

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
        PAINTED_BLOCKS.clear();
        positions.forEach(ClientPaintedBlockCache::add);
        oldPositions.forEach(ClientPaintedBlockCache::markRenderDirty);
    }

    public static void add(BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        if (PAINTED_BLOCKS.add(immutablePos)) {
            markRenderDirty(immutablePos);
        }
    }

    public static void remove(BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        if (PAINTED_BLOCKS.remove(immutablePos)) {
            markRenderDirty(immutablePos);
        }
    }

    public static boolean contains(BlockPos pos) {
        return PAINTED_BLOCKS.contains(pos);
    }

    private static void markRenderDirty(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                    .forEach(lightPos -> minecraft.level.getLightEngine().checkBlock(lightPos.immutable()));
        }

        if (minecraft.levelRenderer != null) {
            minecraft.levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
