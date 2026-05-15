package io.github.stevezhang123.gunwood.item;

import io.github.stevezhang123.gunwood.selection.GunwoodSelectionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

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
}
