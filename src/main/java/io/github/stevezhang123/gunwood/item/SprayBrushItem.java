package io.github.stevezhang123.gunwood.item;

import io.github.stevezhang123.gunwood.paint.GunwoodPaintActions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SprayBrushItem extends Item {
    public SprayBrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level instanceof ServerLevel serverLevel && context.getPlayer() instanceof ServerPlayer player) {
            GunwoodPaintActions.paint(player, serverLevel, context.getClickedPos());
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
