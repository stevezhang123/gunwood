package io.github.stevezhang123.gunwood.item;

import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import io.github.stevezhang123.gunwood.paint.GunwoodPaintActions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ScraperItem extends Item {
    public ScraperItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GunwoodCommonConfig.SCRAPER_MAX_DAMAGE.get();
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return GunwoodToolEnchantmentHelper.isBookEnchantable(book);
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return GunwoodToolEnchantmentHelper.xpRepairRatio();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level instanceof ServerLevel serverLevel && context.getPlayer() instanceof ServerPlayer player) {
            GunwoodPaintActions.scrape(player, serverLevel, context.getClickedPos(), context.getItemInHand(), context.getHand());
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            int scraped = GunwoodPaintActions.scrapeCurrentSelection(serverPlayer, serverLevel, stack, hand);
            if (scraped <= 0) {
                scraped = GunwoodPaintActions.scrapeAirTarget(serverPlayer, serverLevel, stack, hand);
            }
            if (scraped > 0) {
                return InteractionResultHolder.success(stack);
            }
        }

        return InteractionResultHolder.pass(stack);
    }
}
