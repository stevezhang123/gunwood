package io.github.stevezhang123.gunwood.client.render;

import io.github.stevezhang123.gunwood.client.ClientPaintedBlockCache;
import io.github.stevezhang123.gunwood.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class GunwoodClientRenderRules {
    private GunwoodClientRenderRules() {
    }

    public static boolean shouldSkipBlockRender(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return shouldSkipPaintedPos(pos);
    }

    public static boolean shouldSkipBlockRender(BlockGetter level, BlockPos pos, BlockState state) {
        return shouldSkipPaintedPos(pos);
    }

    public static boolean shouldSkipBlockEntity(BlockEntity blockEntity) {
        return blockEntity != null && shouldSkipPaintedPos(blockEntity.getBlockPos());
    }

    public static boolean shouldSkipCreateKineticRender(BlockEntity blockEntity) {
        return shouldSkipBlockEntity(blockEntity);
    }

    public static boolean shouldSkipPaintedPos(BlockPos pos) {
        return Minecraft.getInstance().player != null
                && ClientPaintedBlockCache.contains(pos)
                && !isWearingGlasses();
    }

    public static boolean isWearingGlasses() {
        Player player = Minecraft.getInstance().player;
        return player != null && player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.GLASSES.get());
    }
}
