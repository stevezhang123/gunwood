package io.github.stevezhang123.gunwood.item;

import io.github.stevezhang123.gunwood.network.ModNetworking;
import io.github.stevezhang123.gunwood.paint.PaintedBlockManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ChunkScrapeBrushItem extends Item {
    public ChunkScrapeBrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level instanceof ServerLevel serverLevel && context.getPlayer() != null) {
            BlockPos clickedPos = context.getClickedPos();
            ChunkPos chunkPos = new ChunkPos(clickedPos);

            if (PaintedBlockManager.contains(serverLevel, clickedPos)) {
                List<BlockPos> removed = clearPaintedChunk(serverLevel, chunkPos);
                ModNetworking.syncRemovedToNearby(serverLevel, clickedPos, removed);
                context.getPlayer().sendSystemMessage(Component.literal(
                        "已清除区块涂漆：" + chunkPos.x + " " + chunkPos.z + "，共 " + removed.size() + " 个方块"
                ));
            } else {
                BlockState clickedState = serverLevel.getBlockState(clickedPos);
                if (!isNonTransparent(serverLevel, clickedPos, clickedState)) {
                    return InteractionResult.PASS;
                }

                List<BlockPos> added = paintChunk(serverLevel, chunkPos);
                ModNetworking.syncAddedToNearby(serverLevel, clickedPos, added);
                context.getPlayer().sendSystemMessage(Component.literal(
                        "已涂漆区块：" + chunkPos.x + " " + chunkPos.z + "，共 " + added.size() + " 个方块"
                ));
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static List<BlockPos> paintChunk(ServerLevel level, ChunkPos chunkPos) {
        List<BlockPos> positions = new ArrayList<>();

        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++) {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++) {
                for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (isNonTransparent(level, pos, state) && !PaintedBlockManager.contains(level, pos)) {
                        positions.add(pos.immutable());
                    }
                }
            }
        }

        PaintedBlockManager.addAll(level, positions);
        return positions;
    }

    private static List<BlockPos> clearPaintedChunk(ServerLevel level, ChunkPos chunkPos) {
        List<BlockPos> positions = PaintedBlockManager.getAll(level).stream()
                .filter(pos -> new ChunkPos(pos).equals(chunkPos))
                .toList();

        PaintedBlockManager.removeAll(level, positions);
        return positions;
    }

    private static boolean isNonTransparent(ServerLevel level, BlockPos pos, BlockState state) {
        return !state.isAir() && state.getLightBlock(level, pos) > 0;
    }
}
