package io.github.stevezhang123.gunwood.compat.create;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.util.Collection;

public final class GunwoodCreateContraptionCompat {
    private static final String CONTRAPTION_ENTITY_CLASS = "com.simibubi.create.content.contraptions.AbstractContraptionEntity";

    private GunwoodCreateContraptionCompat() {
    }

    public static void refreshPaintedBlock(Level level, BlockPos pos) {
        refreshContraptions(level);
    }

    public static void refreshPaintedBlocks(Level level, Collection<BlockPos> positions) {
        if (!positions.isEmpty()) {
            refreshContraptions(level);
        }
    }

    public static void refreshAllContraptions(Level level) {
        refreshContraptions(level);
    }

    private static void refreshContraptions(Level level) {
        if (!(level instanceof ClientLevel clientLevel)) {
            return;
        }

        for (Entity entity : clientLevel.entitiesForRendering()) {
            if (!isContraptionEntity(entity)) {
                continue;
            }

            Object contraption = invokeNoArgs(entity, "getContraption");
            invokeNoArgs(contraption, "invalidateClientContraptionStructure");
            invokeNoArgs(contraption, "invalidateClientContraptionChildren");
        }
    }

    private static boolean isContraptionEntity(Entity entity) {
        Class<?> type = entity.getClass();
        while (type != null) {
            if (CONTRAPTION_ENTITY_CLASS.equals(type.getName())) {
                return true;
            }
            type = type.getSuperclass();
        }
        return false;
    }

    private static Object invokeNoArgs(Object object, String methodName) {
        if (object == null) {
            return null;
        }
        try {
            Method method = object.getClass().getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(object);
        } catch (ReflectiveOperationException | SecurityException ignored) {
            return null;
        }
    }
}
