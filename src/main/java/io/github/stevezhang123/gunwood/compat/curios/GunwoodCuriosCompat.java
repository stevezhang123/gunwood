package io.github.stevezhang123.gunwood.compat.curios;

import io.github.stevezhang123.gunwood.registry.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public final class GunwoodCuriosCompat {
    private static final String CURIOS_MOD_ID = "curios";
    private static final String CURIOS_API_CLASS = "top.theillusivec4.curios.api.CuriosApi";

    private GunwoodCuriosCompat() {
    }

    public static boolean hasGlasses(LivingEntity entity) {
        if (entity == null || !ModList.get().isLoaded(CURIOS_MOD_ID)) {
            return false;
        }

        try {
            Optional<?> inventory = getCuriosInventory(entity);
            if (inventory.isEmpty()) {
                return false;
            }

            Object handler = inventory.get();
            return findFirstCurio(handler) || scanCuriosStacks(handler);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }

    private static Optional<?> getCuriosInventory(LivingEntity entity) throws ReflectiveOperationException {
        Class<?> curiosApi = Class.forName(CURIOS_API_CLASS);
        Method getCuriosInventory = curiosApi.getMethod("getCuriosInventory", LivingEntity.class);
        Object result = getCuriosInventory.invoke(null, entity);
        return result instanceof Optional<?> optional ? optional : Optional.empty();
    }

    private static boolean findFirstCurio(Object handler) throws ReflectiveOperationException {
        Method findFirstCurio = findPublicMethod(handler.getClass(), "findFirstCurio", Predicate.class);
        if (findFirstCurio == null) {
            return false;
        }

        Object result = findFirstCurio.invoke(handler, (Predicate<ItemStack>) stack -> stack.is(ModItems.GLASSES.get()));
        return result instanceof Optional<?> optional && optional.isPresent();
    }

    private static boolean scanCuriosStacks(Object handler) throws ReflectiveOperationException {
        Method getCurios = findPublicMethod(handler.getClass(), "getCurios");
        if (getCurios == null) {
            return false;
        }

        Object curios = getCurios.invoke(handler);
        if (!(curios instanceof Map<?, ?> curiosMap)) {
            return false;
        }

        for (Object stacksHandler : curiosMap.values()) {
            if (containsGlasses(stacksHandler)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsGlasses(Object stacksHandler) throws ReflectiveOperationException {
        Method getStacks = findPublicMethod(stacksHandler.getClass(), "getStacks");
        if (getStacks == null) {
            return false;
        }

        Object stacks = getStacks.invoke(stacksHandler);
        Method getSlots = findPublicMethod(stacks.getClass(), "getSlots");
        Method getStackInSlot = findPublicMethod(stacks.getClass(), "getStackInSlot", int.class);
        if (getSlots == null || getStackInSlot == null) {
            return false;
        }

        int slots = (int) getSlots.invoke(stacks);
        for (int slot = 0; slot < slots; slot++) {
            Object stack = getStackInSlot.invoke(stacks, slot);
            if (stack instanceof ItemStack itemStack && itemStack.is(ModItems.GLASSES.get())) {
                return true;
            }
        }
        return false;
    }

    private static Method findPublicMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        try {
            return type.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException ignored) {
            for (Class<?> interfaceType : type.getInterfaces()) {
                Method method = findPublicMethod(interfaceType, name, parameterTypes);
                if (method != null) {
                    return method;
                }
            }
            Class<?> superClass = type.getSuperclass();
            return superClass == null ? null : findPublicMethod(superClass, name, parameterTypes);
        }
    }
}
