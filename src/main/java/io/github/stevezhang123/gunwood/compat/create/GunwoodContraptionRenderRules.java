package io.github.stevezhang123.gunwood.compat.create;

import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.function.Function;

public final class GunwoodContraptionRenderRules {
    private static Constructor<?> renderedBlocksConstructor;

    private GunwoodContraptionRenderRules() {
    }

    public static Object filterRenderedBlocks(Object clientContraption, Object renderedBlocks) {
        if (!GunwoodCommonConfig.enableCreateContraptionCompat()) {
            return renderedBlocks;
        }

        Object contraption = getField(clientContraption, "contraption");
        if (contraption == null || renderedBlocks == null) {
            return renderedBlocks;
        }

        Iterable<?> positions = renderedBlockPositions(renderedBlocks);
        Function<BlockPos, ?> lookup = renderedBlockLookup(renderedBlocks);
        if (positions == null || lookup == null) {
            return renderedBlocks;
        }

        List<BlockPos> visiblePositions = new ArrayList<>();
        for (Object rawPos : positions) {
            if (rawPos instanceof BlockPos localPos && !shouldSkipContraptionLocalPos(contraption, localPos)) {
                visiblePositions.add(localPos.immutable());
            }
        }

        return createRenderedBlocks(renderedBlocks.getClass(), lookup, visiblePositions, renderedBlocks);
    }

    public static BitSet filterRenderedBlockEntities(Object clientContraption, BitSet original) {
        if (!GunwoodCommonConfig.enableCreateContraptionCompat()) {
            return original;
        }

        Object contraption = getField(clientContraption, "contraption");
        Object renderedBlockEntities = getField(clientContraption, "renderedBlockEntities");
        if (contraption == null || !(renderedBlockEntities instanceof List<?> blockEntities)) {
            return original;
        }

        BitSet adjusted = (BitSet) original.clone();
        for (int i = 0; i < blockEntities.size(); i++) {
            Object blockEntity = blockEntities.get(i);
            if (blockEntity instanceof BlockEntity be && shouldSkipContraptionLocalPos(contraption, be.getBlockPos())) {
                adjusted.clear(i);
            }
        }

        return adjusted;
    }

    public static boolean shouldSkipContraptionBlockEntity(Object contraptionVisual, BlockEntity blockEntity) {
        if (!GunwoodCommonConfig.enableCreateContraptionCompat()) {
            return false;
        }

        Object entity = getFieldInHierarchy(contraptionVisual, "entity");
        Object contraption = invokeNoArgs(entity, "getContraption");
        return blockEntity != null && contraption != null && shouldSkipContraptionLocalPos(contraption, blockEntity.getBlockPos());
    }

    public static boolean shouldSkipContraptionActor(Object contraptionVisual, Object actor) {
        if (!GunwoodCommonConfig.enableCreateContraptionCompat()) {
            return false;
        }

        Object entity = getFieldInHierarchy(contraptionVisual, "entity");
        Object contraption = invokeNoArgs(entity, "getContraption");
        Object blockInfo = invokeNoArgs(actor, "getLeft");
        Object localPos = invokeNoArgs(blockInfo, "pos");
        return contraption != null && localPos instanceof BlockPos pos && shouldSkipContraptionLocalPos(contraption, pos);
    }

    public static boolean shouldSkipMovementContext(Object context) {
        if (!GunwoodCommonConfig.enableCreateContraptionCompat()) {
            return false;
        }

        Object contraption = getField(context, "contraption");
        Object localPos = getField(context, "localPos");
        return contraption != null && localPos instanceof BlockPos pos && shouldSkipContraptionLocalPos(contraption, pos);
    }

    public static void renderMovementBehaviour(Object movementBehaviour, Object context, Object renderWorld, Object matrices, Object buffer) {
        Method method = findMethodInHierarchy(movementBehaviour, "renderInContraption", 4);
        if (method == null) {
            return;
        }

        try {
            method.setAccessible(true);
            method.invoke(movementBehaviour, context, renderWorld, matrices, buffer);
        } catch (ReflectiveOperationException | SecurityException ignored) {
            // Create is optional and its exact movement renderer implementations vary by version.
        }
    }

    public static boolean shouldSkipContraptionLocalPos(Object contraption, BlockPos localPos) {
        Object anchor = getFieldInHierarchy(contraption, "anchor");
        if (!(anchor instanceof BlockPos anchorPos)) {
            return false;
        }

        return GunwoodClientRenderRules.shouldSkipPaintedPos(anchorPos.offset(localPos));
    }

    private static Iterable<?> renderedBlockPositions(Object renderedBlocks) {
        Object positions = invokeNoArgs(renderedBlocks, "positions");
        return positions instanceof Iterable<?> iterable ? iterable : null;
    }

    @SuppressWarnings("unchecked")
    private static Function<BlockPos, ?> renderedBlockLookup(Object renderedBlocks) {
        Object lookup = invokeNoArgs(renderedBlocks, "lookup");
        return lookup instanceof Function<?, ?> function ? (Function<BlockPos, ?>) function : null;
    }

    private static Object createRenderedBlocks(Class<?> renderedBlocksClass, Function<BlockPos, ?> lookup, Iterable<BlockPos> positions, Object fallback) {
        try {
            if (renderedBlocksConstructor == null || renderedBlocksConstructor.getDeclaringClass() != renderedBlocksClass) {
                renderedBlocksConstructor = renderedBlocksClass.getDeclaredConstructor(Function.class, Iterable.class);
                renderedBlocksConstructor.setAccessible(true);
            }
            return renderedBlocksConstructor.newInstance(lookup, positions);
        } catch (ReflectiveOperationException | SecurityException ignored) {
            return fallback;
        }
    }

    private static Object getField(Object object, String name) {
        if (object == null) {
            return null;
        }
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (ReflectiveOperationException | SecurityException ignored) {
            return null;
        }
    }

    private static Object getFieldInHierarchy(Object object, String name) {
        if (object == null) {
            return null;
        }

        Class<?> type = object.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            } catch (ReflectiveOperationException | SecurityException ignored) {
                return null;
            }
        }

        return null;
    }

    private static Object invokeNoArgs(Object object, String name) {
        if (object == null) {
            return null;
        }
        try {
            Method method = object.getClass().getMethod(name);
            method.setAccessible(true);
            return method.invoke(object);
        } catch (ReflectiveOperationException | SecurityException ignored) {
            return null;
        }
    }

    private static Method findMethodInHierarchy(Object object, String name, int parameterCount) {
        if (object == null) {
            return null;
        }

        Class<?> type = object.getClass();
        while (type != null) {
            for (Method method : type.getDeclaredMethods()) {
                if (method.getName().equals(name) && method.getParameterCount() == parameterCount) {
                    return method;
                }
            }
            for (Class<?> interfaceType : type.getInterfaces()) {
                Method method = findMethodInInterface(interfaceType, name, parameterCount);
                if (method != null) {
                    return method;
                }
            }
            type = type.getSuperclass();
        }

        return null;
    }

    private static Method findMethodInInterface(Class<?> type, String name, int parameterCount) {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == parameterCount) {
                return method;
            }
        }
        return null;
    }
}
