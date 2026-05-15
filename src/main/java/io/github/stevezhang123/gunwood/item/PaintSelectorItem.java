package io.github.stevezhang123.gunwood.item;

import io.github.stevezhang123.gunwood.selection.GunwoodSelectionManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class PaintSelectorItem extends Item {
    public PaintSelectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (context.getPlayer() instanceof ServerPlayer player) {
            if (player.isShiftKeyDown()) {
                GunwoodSelectionManager.clear(player);
            } else {
                GunwoodSelectionManager.select(player, context.getClickedPos());
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.gunwood.paint_selector.select"));
        tooltipComponents.add(Component.translatable("tooltip.gunwood.paint_selector.clear"));
        tooltipComponents.add(Component.translatable("tooltip.gunwood.paint_selector.adjust"));
        tooltipComponents.add(Component.translatable("tooltip.gunwood.paint_selector.highlight"));
    }
}
