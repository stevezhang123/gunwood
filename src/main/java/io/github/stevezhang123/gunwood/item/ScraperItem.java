package io.github.stevezhang123.gunwood.item;

import io.github.stevezhang123.gunwood.paint.PaintedBlockManager;
import io.github.stevezhang123.gunwood.network.ModNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ScraperItem extends Item {
    public ScraperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level instanceof ServerLevel serverLevel && context.getPlayer() != null) {
            BlockPos pos = context.getClickedPos();
            PaintedBlockManager.remove(serverLevel, pos);
            ModNetworking.syncRemovedToNearby(serverLevel, pos);
            context.getPlayer().sendSystemMessage(Component.literal("已清除涂漆：" + pos.getX() + " " + pos.getY() + " " + pos.getZ()));
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
