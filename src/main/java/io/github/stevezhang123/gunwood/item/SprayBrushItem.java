package io.github.stevezhang123.gunwood.item;

import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import io.github.stevezhang123.gunwood.compat.ftbultimine.GunwoodFTBAirScrapeCompat;
import io.github.stevezhang123.gunwood.paint.GunwoodPaintActions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SprayBrushItem extends Item {
    public SprayBrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GunwoodCommonConfig.SPRAYER_MAX_DAMAGE.get();
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
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
            var selected = GunwoodCommonConfig.ENABLE_FTB_ULTIMINE_COMPAT.get()
                    ? GunwoodFTBAirScrapeCompat.selectedPositions(player, context.getClickedPos(), context.getClickedFace(), GunwoodCommonConfig.MAX_FTB_CHAIN_BLOCKS.get())
                    : java.util.List.<net.minecraft.core.BlockPos>of();
            if (selected.isEmpty()) GunwoodPaintActions.paint(player, serverLevel, context.getClickedPos(), context.getItemInHand(), context.getHand());
            else GunwoodPaintActions.paintMany(player, serverLevel, selected, context.getItemInHand(), context.getHand());
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
