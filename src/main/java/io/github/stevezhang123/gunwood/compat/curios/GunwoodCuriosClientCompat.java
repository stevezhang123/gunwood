package io.github.stevezhang123.gunwood.compat.curios;

import com.mojang.logging.LogUtils;
import io.github.stevezhang123.gunwood.registry.ModItems;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

public final class GunwoodCuriosClientCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CURIOS_RENDERER_REGISTRY_CLASS = "top.theillusivec4.curios.api.client.CuriosRendererRegistry";
    private static final String CURIO_RENDERER_CLASS = "top.theillusivec4.curios.api.client.ICurioRenderer";

    private GunwoodCuriosClientCompat() {
    }

    public static void registerInvisibleGlassesRenderer() {
        try {
            Class<?> registryClass = Class.forName(CURIOS_RENDERER_REGISTRY_CLASS);
            Class<?> rendererClass = Class.forName(CURIO_RENDERER_CLASS);
            Method register = registryClass.getMethod("register", Item.class, Supplier.class);

            Supplier<Object> rendererSupplier = () -> Proxy.newProxyInstance(
                    rendererClass.getClassLoader(),
                    new Class<?>[]{rendererClass},
                    (proxy, method, args) -> null
            );
            register.invoke(null, ModItems.GLASSES.get(), rendererSupplier);
        } catch (ClassNotFoundException exception) {
            // Curios is optional; if it is absent there is no renderer to suppress.
        } catch (ReflectiveOperationException exception) {
            LOGGER.warn("Failed to register invisible Curios renderer for Gunwood glasses", exception);
        }
    }
}
