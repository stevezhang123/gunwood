package io.github.stevezhang123.gunwood.compat.create;

import io.github.stevezhang123.gunwood.client.ClientPaintedBlockCache;
import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Method;
import java.util.Collection;

public final class GunwoodFlywheelCompat {
    private static final String VISUALIZATION_MANAGER_CLASS = "dev.engine_room.flywheel.api.visualization.VisualizationManager";

    private GunwoodFlywheelCompat() {
    }

    public static void onPaintedBlockAdded(Level level, BlockPos pos) {
        refreshPaintedBlock(level, pos);
    }

    public static void onPaintedBlockRemoved(Level level, BlockPos pos) {
        refreshPaintedBlock(level, pos);
    }

    public static void refreshPaintedBlock(Level level, BlockPos pos) {
        if (level == null || !level.isClientSide() || !GunwoodCommonConfig.enableCreateCompat()) {
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
        if (level == null || !level.isClientSide() || !GunwoodCommonConfig.enableCreateCompat()) {
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
        if (!GunwoodClientRenderRules.shouldSkipBlockEntity(blockEntity)) {
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
            Method method = findQueueMethod(visualManager.getClass(), methodName);
            if (method != null) {
                method.invoke(visualManager, blockEntity);
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // Flywheel is optional; reflection failures must not affect normal Gunwood rendering.
        }
    }

    private static Method findQueueMethod(Class<?> type, String methodName) {
        try {
            return type.getMethod(methodName, Object.class);
        } catch (NoSuchMethodException ignored) {
        }

        Class<?> current = type;
        while (current != null) {
            try {
                Method method = current.getDeclaredMethod(methodName, Object.class);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
                current = current.getSuperclass();
            } catch (SecurityException ignored) {
                return null;
            }
        }

        for (Class<?> iface : type.getInterfaces()) {
            Method method = findQueueMethod(iface, methodName);
            if (method != null) {
                return method;
            }
        }

        return null;
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
