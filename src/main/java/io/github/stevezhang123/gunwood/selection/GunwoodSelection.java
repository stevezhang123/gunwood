package io.github.stevezhang123.gunwood.selection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public record GunwoodSelection(BlockPos firstPos, BlockPos secondPos) {
    public GunwoodSelection {
        firstPos = firstPos.immutable();
        secondPos = secondPos.immutable();
    }

    public BlockPos min() {
        return new BlockPos(
                Math.min(firstPos.getX(), secondPos.getX()),
                Math.min(firstPos.getY(), secondPos.getY()),
                Math.min(firstPos.getZ(), secondPos.getZ())
        );
    }

    public BlockPos max() {
        return new BlockPos(
                Math.max(firstPos.getX(), secondPos.getX()),
                Math.max(firstPos.getY(), secondPos.getY()),
                Math.max(firstPos.getZ(), secondPos.getZ())
        );
    }

    public long volume() {
        BlockPos min = min();
        BlockPos max = max();
        return (long) (max.getX() - min.getX() + 1)
                * (max.getY() - min.getY() + 1)
                * (max.getZ() - min.getZ() + 1);
    }

    public List<BlockPos> positions() {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos.betweenClosed(min(), max()).forEach(pos -> positions.add(pos.immutable()));
        return positions;
    }

    public boolean contains(BlockPos pos) {
        BlockPos min = min();
        BlockPos max = max();
        return pos.getX() >= min.getX() && pos.getX() <= max.getX()
                && pos.getY() >= min.getY() && pos.getY() <= max.getY()
                && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
    }

    public GunwoodSelection adjustFace(Direction face, int amount) {
        BlockPos min = min();
        BlockPos max = max();

        int minX = min.getX();
        int minY = min.getY();
        int minZ = min.getZ();
        int maxX = max.getX();
        int maxY = max.getY();
        int maxZ = max.getZ();

        switch (face) {
            case EAST -> maxX += amount;
            case WEST -> minX -= amount;
            case UP -> maxY += amount;
            case DOWN -> minY -= amount;
            case SOUTH -> maxZ += amount;
            case NORTH -> minZ -= amount;
        }

        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return this;
        }

        return new GunwoodSelection(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
    }
}
