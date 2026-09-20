package io.github.stevezhang123.gunwood.compat.create;

import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Resolves Create 1.20.1 water wheel structural blocks without loading Create classes. */
public final class CreateWaterWheelCompat {
    private static final String STANDARD = "create:water_wheel";
    private static final String LARGE = "create:large_water_wheel";
    private static final String STRUCTURAL = "create:water_wheel_structural";

    private CreateWaterWheelCompat() {}

    public record WaterWheelTarget(BlockPos center, boolean large, List<BlockPos> positions) {}

    public static Optional<WaterWheelTarget> resolveWholeWheel(BlockGetter level, BlockPos clickedPos) {
        BlockState clickedState = level.getBlockState(clickedPos);
        String id = id(clickedState);
        if (STANDARD.equals(id)) {
            if (!GunwoodCommonConfig.ENABLE_WHOLE_WATER_WHEEL_PAINTING.get()) return Optional.empty();
            return Optional.of(new WaterWheelTarget(clickedPos.immutable(), false, List.of(clickedPos.immutable())));
        }
        if (!LARGE.equals(id) && !STRUCTURAL.equals(id)) return Optional.empty();
        if (!GunwoodCommonConfig.ENABLE_WHOLE_LARGE_WATER_WHEEL_PAINTING.get()) return Optional.empty();
        BlockPos center = findLargeCenter(level, clickedPos);
        if (center == null) return Optional.empty();
        Direction.Axis axis = level.getBlockState(center).getValue(BlockStateProperties.AXIS);
        List<BlockPos> positions = new ArrayList<>(9);
        positions.add(center.immutable());
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                if (a == 0 && b == 0) continue;
                BlockPos candidate = switch (axis) {
                    case X -> center.offset(0, a, b);
                    case Y -> center.offset(a, 0, b);
                    case Z -> center.offset(a, b, 0);
                };
                if (center.equals(findLargeCenter(level, candidate))) positions.add(candidate.immutable());
            }
        }
        return Optional.of(new WaterWheelTarget(center.immutable(), true, List.copyOf(positions)));
    }

    private static BlockPos findLargeCenter(BlockGetter level, BlockPos pos) {
        BlockPos current = pos;
        for (int depth = 0; depth <= 2; depth++) {
            BlockState state = level.getBlockState(current);
            String blockId = id(state);
            if (LARGE.equals(blockId)) return current;
            if (!STRUCTURAL.equals(blockId) || !state.hasProperty(BlockStateProperties.FACING)) return null;
            Direction direction = state.getValue(BlockStateProperties.FACING);
            current = current.relative(direction);
        }
        return null;
    }

    private static String id(BlockState state) {
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return key == null ? "" : key.toString();
    }
}
