package io.github.stevezhang123.gunwood.client;

import io.github.stevezhang123.gunwood.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public final class ClientPaintedBlockVisibility {
    private ClientPaintedBlockVisibility() {
    }

    public static boolean shouldHideOrdinaryBlockModel(BlockState state, BlockPos pos) {
        // Ordinary block hiding stage only. This does not handle BlockEntityRenderer, Flywheel, or Create-specific rendering paths.
        return state.getRenderShape() == RenderShape.MODEL
                && !state.hasBlockEntity()
                && ClientPaintedBlockCache.contains(pos)
                && !isWearingGlasses();
    }

    private static boolean isWearingGlasses() {
        Player player = Minecraft.getInstance().player;
        return player != null && player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.GLASSES.get());
    }
}
