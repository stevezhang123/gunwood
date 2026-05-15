package io.github.stevezhang123.gunwood.paint;

import io.github.stevezhang123.gunwood.network.ModNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class GunwoodPaintActions {
    private GunwoodPaintActions() {
    }

    public static int paint(ServerPlayer player, ServerLevel level, BlockPos pos) {
        if (PaintedBlockManager.contains(level, pos)) {
            return 0;
        }

        PaintedBlockManager.add(level, pos);
        ModNetworking.syncAddedToNearby(level, pos);
        player.sendSystemMessage(Component.literal("已涂漆：" + pos.getX() + " " + pos.getY() + " " + pos.getZ()));
        return 1;
    }

    public static int scrape(ServerPlayer player, ServerLevel level, BlockPos pos) {
        if (!PaintedBlockManager.contains(level, pos)) {
            return 0;
        }

        PaintedBlockManager.remove(level, pos);
        ModNetworking.syncRemovedToNearby(level, pos);
        player.sendSystemMessage(Component.literal("已清除涂漆：" + pos.getX() + " " + pos.getY() + " " + pos.getZ()));
        return 1;
    }

    public static int paintMany(ServerPlayer player, ServerLevel level, Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = new ArrayList<>();

        for (BlockPos pos : positions) {
            if (!PaintedBlockManager.contains(level, pos)) {
                changedPositions.add(pos.immutable());
            }
        }

        if (changedPositions.isEmpty()) {
            player.sendSystemMessage(Component.literal("已涂漆 0 个方块"));
            return 0;
        }

        PaintedBlockManager.addAll(level, changedPositions);
        ModNetworking.syncAddedToNearby(level, changedPositions.getFirst(), changedPositions);
        player.sendSystemMessage(Component.literal("已涂漆 " + changedPositions.size() + " 个方块"));
        return changedPositions.size();
    }

    public static int scrapeMany(ServerPlayer player, ServerLevel level, Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = new ArrayList<>();

        for (BlockPos pos : positions) {
            if (PaintedBlockManager.contains(level, pos)) {
                changedPositions.add(pos.immutable());
            }
        }

        if (changedPositions.isEmpty()) {
            player.sendSystemMessage(Component.literal("已清除涂漆 0 个方块"));
            return 0;
        }

        PaintedBlockManager.removeAll(level, changedPositions);
        ModNetworking.syncRemovedToNearby(level, changedPositions.getFirst(), changedPositions);
        player.sendSystemMessage(Component.literal("已清除涂漆 " + changedPositions.size() + " 个方块"));
        return changedPositions.size();
    }
}
