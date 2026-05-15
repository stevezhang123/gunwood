package io.github.stevezhang123.gunwood.paint;

import io.github.stevezhang123.gunwood.network.ModNetworking;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import io.github.stevezhang123.gunwood.selection.GunwoodSelection;
import io.github.stevezhang123.gunwood.selection.GunwoodSelectionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class GunwoodPaintActions {
    private GunwoodPaintActions() {
    }

    public static int paint(ServerPlayer player, ServerLevel level, BlockPos pos, ItemStack toolStack, InteractionHand hand) {
        Optional<GunwoodSelection> selection = GunwoodSelectionManager.getSelection(player);
        if (GunwoodCommonConfig.ALLOW_SELECTION_BATCH_PAINTING.get() && selection.isPresent()) {
            GunwoodSelection gunwoodSelection = selection.get();
            if (isSelectionWithinLimit(player, gunwoodSelection)) {
                return paintSelection(player, level, gunwoodSelection, toolStack, hand);
            }

            GunwoodSelectionManager.clear(player.getUUID());
        }

        return paintSingle(player, level, pos, toolStack, hand);
    }

    public static int scrape(ServerPlayer player, ServerLevel level, BlockPos pos, ItemStack toolStack, InteractionHand hand) {
        Optional<GunwoodSelection> selection = GunwoodSelectionManager.getSelection(player);
        if (GunwoodCommonConfig.ALLOW_SELECTION_BATCH_SCRAPING.get() && selection.isPresent()) {
            GunwoodSelection gunwoodSelection = selection.get();
            if (isSelectionWithinLimit(player, gunwoodSelection)) {
                return scrapeSelectionInternal(player, level, gunwoodSelection, toolStack, hand);
            }

            GunwoodSelectionManager.clear(player.getUUID());
        }

        return scrapeSingle(player, level, pos, toolStack, hand);
    }

    public static int scrapeCurrentSelection(ServerPlayer player, ServerLevel level, ItemStack toolStack, InteractionHand hand) {
        if (!GunwoodCommonConfig.ALLOW_SELECTION_BATCH_SCRAPING.get()) {
            return 0;
        }

        Optional<GunwoodSelection> selection = GunwoodSelectionManager.getSelection(player);
        if (selection.isEmpty()) {
            return 0;
        }

        GunwoodSelection gunwoodSelection = selection.get();
        if (!isSelectionWithinLimit(player, gunwoodSelection)) {
            GunwoodSelectionManager.clear(player.getUUID());
            return 0;
        }

        return scrapeSelectionInternal(player, level, gunwoodSelection, toolStack, hand);
    }

    public static int paintSingle(ServerPlayer player, ServerLevel level, BlockPos pos, ItemStack toolStack, InteractionHand hand) {
        BlockState state = level.getBlockState(pos);
        if (!GunwoodPaintRules.canPaint(level, pos, state, player)) {
            return 0;
        }
        int damagePerUse = GunwoodCommonConfig.SPRAYER_DAMAGE_PER_USE.get();
        if (!hasDurabilityForUse(player, toolStack, damagePerUse)) {
            player.sendSystemMessage(Component.translatable("message.gunwood.tool.no_durability"));
            return 0;
        }

        PaintedBlockManager.add(level, pos);
        damageTool(player, toolStack, hand, damagePerUse);
        ModNetworking.syncAddedToNearby(level, pos);
        player.sendSystemMessage(Component.literal("已涂漆：" + pos.getX() + " " + pos.getY() + " " + pos.getZ()));
        return 1;
    }

    public static int scrapeSingle(ServerPlayer player, ServerLevel level, BlockPos pos, ItemStack toolStack, InteractionHand hand) {
        BlockState state = level.getBlockState(pos);
        if (!GunwoodPaintRules.canScrape(level, pos, state, player)) {
            return 0;
        }
        int damagePerUse = GunwoodCommonConfig.SCRAPER_DAMAGE_PER_USE.get();
        if (!hasDurabilityForUse(player, toolStack, damagePerUse)) {
            player.sendSystemMessage(Component.translatable("message.gunwood.tool.no_durability"));
            return 0;
        }

        PaintedBlockManager.remove(level, pos);
        damageTool(player, toolStack, hand, damagePerUse);
        ModNetworking.syncRemovedToNearby(level, pos);
        player.sendSystemMessage(Component.literal("已清除涂漆：" + pos.getX() + " " + pos.getY() + " " + pos.getZ()));
        return 1;
    }

    public static int paintMany(ServerPlayer player, ServerLevel level, Collection<BlockPos> positions, ItemStack toolStack, InteractionHand hand) {
        List<BlockPos> changedPositions = collectPaintTargets(player, level, positions);

        if (changedPositions.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.gunwood.selection.no_targets"));
            return 0;
        }
        int damagePerUse = GunwoodCommonConfig.SPRAYER_DAMAGE_PER_USE.get();
        if (!hasDurabilityForUse(player, toolStack, damagePerUse)) {
            player.sendSystemMessage(Component.translatable("message.gunwood.tool.no_durability"));
            return 0;
        }
        changedPositions = limitBatchTargets(changedPositions);

        PaintedBlockManager.addAll(level, changedPositions);
        damageTool(player, toolStack, hand, damagePerUse);
        ModNetworking.syncAddedToNearby(level, changedPositions.getFirst(), changedPositions);
        player.sendSystemMessage(Component.translatable("message.gunwood.selection.bulk_painted", changedPositions.size()));
        return changedPositions.size();
    }

    public static int scrapeMany(ServerPlayer player, ServerLevel level, Collection<BlockPos> positions, ItemStack toolStack, InteractionHand hand) {
        List<BlockPos> changedPositions = collectScrapeTargets(player, level, positions);

        if (changedPositions.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.gunwood.selection.no_targets"));
            return 0;
        }
        int damagePerUse = GunwoodCommonConfig.SCRAPER_DAMAGE_PER_USE.get();
        if (!hasDurabilityForUse(player, toolStack, damagePerUse)) {
            player.sendSystemMessage(Component.translatable("message.gunwood.tool.no_durability"));
            return 0;
        }
        changedPositions = limitBatchTargets(changedPositions);

        PaintedBlockManager.removeAll(level, changedPositions);
        damageTool(player, toolStack, hand, damagePerUse);
        ModNetworking.syncRemovedToNearby(level, changedPositions.getFirst(), changedPositions);
        player.sendSystemMessage(Component.translatable("message.gunwood.selection.bulk_scraped", changedPositions.size()));
        return changedPositions.size();
    }

    private static int paintSelection(ServerPlayer player, ServerLevel level, GunwoodSelection selection, ItemStack toolStack, InteractionHand hand) {
        List<BlockPos> changedPositions = collectPaintTargets(player, level, selection.positions());
        if (changedPositions.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.gunwood.selection.no_targets"));
            return 0;
        }
        int damagePerUse = GunwoodCommonConfig.SPRAYER_DAMAGE_PER_USE.get();
        if (!hasDurabilityForUse(player, toolStack, damagePerUse)) {
            player.sendSystemMessage(Component.translatable("message.gunwood.tool.no_durability"));
            return 0;
        }
        changedPositions = limitBatchTargets(changedPositions);

        PaintedBlockManager.addAll(level, changedPositions);
        damageTool(player, toolStack, hand, damagePerUse);
        ModNetworking.syncAddedToNearby(level, changedPositions.getFirst(), changedPositions);
        player.sendSystemMessage(Component.translatable("message.gunwood.selection.bulk_painted", changedPositions.size()));
        return changedPositions.size();
    }

    private static int scrapeSelectionInternal(ServerPlayer player, ServerLevel level, GunwoodSelection selection, ItemStack toolStack, InteractionHand hand) {
        return scrapeMany(player, level, selection.positions(), toolStack, hand);
    }

    private static boolean isSelectionWithinLimit(ServerPlayer player, GunwoodSelection selection) {
        if (selection.volume() <= GunwoodSelectionManager.maxSelectionVolume()) {
            return true;
        }

        player.sendSystemMessage(Component.translatable("message.gunwood.selection.too_large", selection.volume(), GunwoodSelectionManager.maxSelectionVolume()));
        return false;
    }

    private static List<BlockPos> collectPaintTargets(ServerPlayer player, ServerLevel level, Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = new ArrayList<>();
        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            if (GunwoodPaintRules.canPaint(level, pos, state, player, GunwoodCommonConfig.REQUIRE_BUILD_PERMISSION_FOR_BATCH_OPERATIONS.get())) {
                changedPositions.add(pos.immutable());
            }
        }
        return changedPositions;
    }

    private static List<BlockPos> collectScrapeTargets(ServerPlayer player, ServerLevel level, Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = new ArrayList<>();
        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            if (GunwoodPaintRules.canScrape(level, pos, state, player, GunwoodCommonConfig.REQUIRE_BUILD_PERMISSION_FOR_BATCH_OPERATIONS.get())) {
                changedPositions.add(pos.immutable());
            }
        }
        return changedPositions;
    }

    private static List<BlockPos> limitBatchTargets(List<BlockPos> changedPositions) {
        int limit = GunwoodCommonConfig.MAX_BATCH_OPERATION_BLOCKS.get();
        if (changedPositions.size() <= limit) {
            return changedPositions;
        }
        return new ArrayList<>(changedPositions.subList(0, limit));
    }

    private static boolean hasDurabilityForUse(ServerPlayer player, ItemStack toolStack, int damagePerUse) {
        if (player.isCreative()) {
            return true;
        }
        if (damagePerUse <= 0) {
            return true;
        }
        if (toolStack.isEmpty() || !toolStack.isDamageableItem()) {
            return false;
        }
        return toolStack.getDamageValue() < toolStack.getMaxDamage();
    }

    private static void damageTool(ServerPlayer player, ItemStack toolStack, InteractionHand hand, int count) {
        if (player.isCreative() || count <= 0) {
            return;
        }
        if (toolStack.isDamageableItem()) {
            if (GunwoodCommonConfig.USE_UNBREAKING_FOR_SPRAYER_AND_SCRAPER.get()) {
                toolStack.hurtAndBreak(count, player, LivingEntity.getSlotForHand(hand));
            } else {
                int newDamage = toolStack.getDamageValue() + count;
                if (newDamage >= toolStack.getMaxDamage()) {
                    Item brokenItem = toolStack.getItem();
                    toolStack.shrink(1);
                    player.onEquippedItemBroken(brokenItem, LivingEntity.getSlotForHand(hand));
                } else {
                    toolStack.setDamageValue(newDamage);
                }
            }
        }
    }
}
