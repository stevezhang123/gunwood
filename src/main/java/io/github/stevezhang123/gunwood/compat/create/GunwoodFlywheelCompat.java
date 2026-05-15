package io.github.stevezhang123.gunwood.compat.create;

import io.github.stevezhang123.gunwood.client.ClientPaintedBlockCache;
import io.github.stevezhang123.gunwood.client.render.GunwoodRenderVisibility;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Method;
import java.util.Collection;

public final class GunwoodFlywheelCompat {
    private static final String VISUALIZATION_MANAGER_CLASS = "dev.engine_room.flywheel.api.visualization.VisualizationManager";
    private static final String VISUAL_MANAGER_CLASS = "dev.engine_room.flywheel.api.visualization.VisualManager";

    private GunwoodFlywheelCompat() {
    }

    public static void onPaintedBlockAdded(Level level, BlockPos pos) {
        refreshPaintedBlock(level, pos);
    }

    public static void onPaintedBlockRemoved(Level level, BlockPos pos) {
        refreshPaintedBlock(level, pos);
    }

    public static void refreshPaintedBlock(Level level, BlockPos pos) {
        if (level == null || !level.isClientSide()) {
            return;
        }

        markBlockRendererDirty(pos);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return;
        }

        refreshBlockEntity(blockEntity);
    }

    public static void refreshAllPaintedBlocks(Level level) {
        refreshAllPaintedBlocks(level, ClientPaintedBlockCache.positions());
    }

    private static void refreshAllPaintedBlocks(Level level, Collection<BlockPos> positions) {
        if (level == null || !level.isClientSide()) {
            return;
        }

        positions.forEach(pos -> refreshPaintedBlock(level, pos));
    }

    private static void refreshBlockEntity(BlockEntity blockEntity) {
        Object blockEntityVisualManager = getBlockEntityVisualManager(blockEntity.getLevel());
        if (blockEntityVisualManager == null) {
            return;
        }

        invokeVisualQueue(blockEntityVisualManager, "queueRemove", blockEntity);
        if (!GunwoodRenderVisibility.shouldHideBlockEntity(blockEntity)) {
            invokeVisualQueue(blockEntityVisualManager, "queueAdd", blockEntity);
        }
    }

    private static Object getBlockEntityVisualManager(Level level) {
        if (level == null) {
            return null;
        }

        try {
            Class<?> visualizationManagerClass = Class.forName(VISUALIZATION_MANAGER_CLASS, false, Thread.currentThread().getContextClassLoader());
            Method get = visualizationManagerClass.getMethod("get", LevelAccessor.class);
            Object visualizationManager = get.invoke(null, level);
            if (visualizationManager == null) {
                return null;
            }

            return visualizationManagerClass.getMethod("blockEntities").invoke(visualizationManager);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return null;
        }
    }

    private static void invokeVisualQueue(Object visualManager, String methodName, BlockEntity blockEntity) {
        try {
            Class<?> visualManagerClass = Class.forName(VISUAL_MANAGER_CLASS, false, Thread.currentThread().getContextClassLoader());
            visualManagerClass.getMethod(methodName, Object.class).invoke(visualManager, blockEntity);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // Flywheel is optional; reflection failures must not affect normal Gunwood rendering.
        }
    }

    private static void markBlockRendererDirty(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.levelRenderer != null) {
            try {
                minecraft.levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
            } catch (NullPointerException ignored) {
                // The level renderer can outlive its view area briefly while leaving a world.
            }
        }
    }
}
