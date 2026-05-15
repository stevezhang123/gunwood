package io.github.stevezhang123.gunwood.paint;

import io.github.stevezhang123.gunwood.network.ModNetworking;
import io.github.stevezhang123.gunwood.registry.ModItems;
import io.github.stevezhang123.gunwood.selection.GunwoodSelection;
import io.github.stevezhang123.gunwood.selection.GunwoodSelectionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class GunwoodPaintActions {
    private GunwoodPaintActions() {
    }

    public static int paint(ServerPlayer player, ServerLevel level, BlockPos pos) {
        Optional<GunwoodSelection> selection = GunwoodSelectionManager.getSelection(player);
        if (selection.isPresent()) {
            GunwoodSelection gunwoodSelection = selection.get();
            if (isSelectionWithinLimit(player, gunwoodSelection)) {
                return paintSelection(player, level, gunwoodSelection);
            }

            GunwoodSelectionManager.clear(player.getUUID());
        }

        return paintSingle(player, level, pos);
    }

    public static int scrape(ServerPlayer player, ServerLevel level, BlockPos pos) {
        Optional<GunwoodSelection> selection = GunwoodSelectionManager.getSelection(player);
        if (selection.isPresent()) {
            GunwoodSelection gunwoodSelection = selection.get();
            if (isSelectionWithinLimit(player, gunwoodSelection)) {
                return scrapeSelection(player, level, gunwoodSelection);
            }

            GunwoodSelectionManager.clear(player.getUUID());
        }

        return scrapeSingle(player, level, pos);
    }

    public static int paintSingle(ServerPlayer player, ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!GunwoodPaintRules.canPaint(level, pos, state, player)) {
            return 0;
        }

        PaintedBlockManager.add(level, pos);
        ModNetworking.syncAddedToNearby(level, pos);
        player.sendSystemMessage(Component.literal("已涂漆：" + pos.getX() + " " + pos.getY() + " " + pos.getZ()));
        return 1;
    }

    public static int scrapeSingle(ServerPlayer player, ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!GunwoodPaintRules.canScrape(level, pos, state, player)) {
            return 0;
        }

        PaintedBlockManager.remove(level, pos);
        ModNetworking.syncRemovedToNearby(level, pos);
        player.sendSystemMessage(Component.literal("已清除涂漆：" + pos.getX() + " " + pos.getY() + " " + pos.getZ()));
        return 1;
    }

    public static int paintMany(ServerPlayer player, ServerLevel level, Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = collectPaintTargets(player, level, positions);

        if (changedPositions.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.gunwood.selection.no_targets"));
            return 0;
        }

        PaintedBlockManager.addAll(level, changedPositions);
        ModNetworking.syncAddedToNearby(level, changedPositions.getFirst(), changedPositions);
        player.sendSystemMessage(Component.translatable("message.gunwood.selection.bulk_painted", changedPositions.size()));
        return changedPositions.size();
    }

    public static int scrapeMany(ServerPlayer player, ServerLevel level, Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = collectScrapeTargets(player, level, positions);

        if (changedPositions.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.gunwood.selection.no_targets"));
            return 0;
        }

        PaintedBlockManager.removeAll(level, changedPositions);
        ModNetworking.syncRemovedToNearby(level, changedPositions.getFirst(), changedPositions);
        player.sendSystemMessage(Component.translatable("message.gunwood.selection.bulk_scraped", changedPositions.size()));
        return changedPositions.size();
    }

    private static int paintSelection(ServerPlayer player, ServerLevel level, GunwoodSelection selection) {
        List<BlockPos> changedPositions = collectPaintTargets(player, level, selection.positions());
        if (changedPositions.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.gunwood.selection.no_targets"));
            return 0;
        }

        int paintBudget = availablePaintBudget(player);
        if (paintBudget <= 0) {
            player.sendSystemMessage(Component.translatable("message.gunwood.selection.resource_missing"));
            return 0;
        }

        if (paintBudget < changedPositions.size()) {
            changedPositions = new ArrayList<>(changedPositions.subList(0, paintBudget));
        }

        PaintedBlockManager.addAll(level, changedPositions);
        consumeInvisiblePaint(player, changedPositions.size());
        ModNetworking.syncAddedToNearby(level, changedPositions.getFirst(), changedPositions);
        player.sendSystemMessage(Component.translatable("message.gunwood.selection.bulk_painted", changedPositions.size()));
        return changedPositions.size();
    }

    private static int scrapeSelection(ServerPlayer player, ServerLevel level, GunwoodSelection selection) {
        return scrapeMany(player, level, selection.positions());
    }

    private static boolean isSelectionWithinLimit(ServerPlayer player, GunwoodSelection selection) {
        if (selection.volume() <= GunwoodSelectionManager.MAX_SELECTION_VOLUME) {
            return true;
        }

        player.sendSystemMessage(Component.translatable("message.gunwood.selection.too_large", selection.volume(), GunwoodSelectionManager.MAX_SELECTION_VOLUME));
        return false;
    }

    private static List<BlockPos> collectPaintTargets(ServerPlayer player, ServerLevel level, Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = new ArrayList<>();
        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            if (GunwoodPaintRules.canPaint(level, pos, state, player)) {
                changedPositions.add(pos.immutable());
            }
        }
        return changedPositions;
    }

    private static List<BlockPos> collectScrapeTargets(ServerPlayer player, ServerLevel level, Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = new ArrayList<>();
        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            if (GunwoodPaintRules.canScrape(level, pos, state, player)) {
                changedPositions.add(pos.immutable());
            }
        }
        return changedPositions;
    }

    private static int availablePaintBudget(ServerPlayer player) {
        if (player.isCreative()) {
            return Integer.MAX_VALUE;
        }

        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.INVISIBLE_PAINT.get())) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void consumeInvisiblePaint(ServerPlayer player, int count) {
        if (player.isCreative() || count <= 0) {
            return;
        }

        int remaining = count;
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.is(ModItems.INVISIBLE_PAINT.get())) {
                continue;
            }

            int consumed = Math.min(remaining, stack.getCount());
            stack.shrink(consumed);
            remaining -= consumed;
            if (remaining == 0) {
                return;
            }
        }
    }
}
