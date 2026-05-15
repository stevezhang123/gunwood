package io.github.stevezhang123.gunwood.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.rightclick.RightClickHandler;
import dev.ftb.mods.ftbultimine.api.shape.ShapeContext;
import io.github.stevezhang123.gunwood.paint.GunwoodPaintActions;
import io.github.stevezhang123.gunwood.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public final class GunwoodUltimineRightClickHandler implements RightClickHandler {
    public static final GunwoodUltimineRightClickHandler INSTANCE = new GunwoodUltimineRightClickHandler();

    private GunwoodUltimineRightClickHandler() {
    }

    @Override
    public int handleRightClickBlock(ShapeContext shapeContext, InteractionHand hand, Collection<BlockPos> positions) {
        ServerPlayer player = shapeContext.player();
        if (!(player.level() instanceof ServerLevel level)) {
            return 0;
        }

        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.SPRAY_BRUSH.get())) {
            return GunwoodPaintActions.paintMany(player, level, positions);
        }

        if (stack.is(ModItems.SCRAPER.get())) {
            return GunwoodPaintActions.scrapeMany(player, level, positions);
        }

        return 0;
    }
}
