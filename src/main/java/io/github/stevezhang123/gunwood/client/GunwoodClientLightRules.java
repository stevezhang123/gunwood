package io.github.stevezhang123.gunwood.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class GunwoodClientLightRules {
    private GunwoodClientLightRules() {
    }

    public static boolean isPainted(BlockGetter level, BlockPos pos) {
        if (level instanceof ChunkAccess chunk) {
            return ClientPaintedBlockCache.contains(toWorldPos(chunk, pos));
        }

        return ClientPaintedBlockCache.contains(pos);
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
