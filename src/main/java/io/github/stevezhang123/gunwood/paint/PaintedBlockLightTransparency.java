package io.github.stevezhang123.gunwood.paint;

import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

public final class PaintedBlockLightTransparency {
    private PaintedBlockLightTransparency() {
    }

    public static boolean isPainted(ServerLevel level, BlockPos pos) {
        return GunwoodCommonConfig.enableLightTransparency() && PaintedBlockManager.contains(level, pos);
    }

    public static boolean isPainted(BlockGetter level, BlockPos pos) {
        if (!GunwoodCommonConfig.enableLightTransparency()) {
            return false;
        }

        if (level instanceof ServerLevel serverLevel) {
            return isPainted(serverLevel, pos);
        }

        if (level instanceof LevelChunk levelChunk && levelChunk.getLevel() instanceof ServerLevel serverLevel) {
            return isPainted(serverLevel, toWorldPos(levelChunk, pos));
        }

        return false;
    }

    private static BlockPos toWorldPos(ChunkAccess chunk, BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        if (x >= 0 && x < 16 && z >= 0 && z < 16) {
            return new BlockPos(chunk.getPos().getMinBlockX() + x, pos.getY(), chunk.getPos().getMinBlockZ() + z);
        }

        return pos;
    }
}
