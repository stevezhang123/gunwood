package io.github.stevezhang123.gunwood.compat.ftbultimine;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;

public final class GunwoodFTBAirScrapeCompat {
    private static final String FTB_ULTIMINE_MOD_ID = "ftbultimine";
    private static final String FTB_ULTIMINE_CLASS = "dev.ftb.mods.ftbultimine.FTBUltimine";

    private GunwoodFTBAirScrapeCompat() {
    }

    public static boolean isUltiminePressed(ServerPlayer player) {
        if (!ModList.get().isLoaded(FTB_ULTIMINE_MOD_ID)) {
            return false;
        }

        try {
            Class<?> ultimineClass = Class.forName(FTB_ULTIMINE_CLASS);
            Method getInstance = ultimineClass.getMethod("getInstance");
            Object instance = getInstance.invoke(null);
            Method getOrCreatePlayerData = ultimineClass.getMethod("getOrCreatePlayerData", net.minecraft.world.entity.player.Player.class);
            Object playerData = getOrCreatePlayerData.invoke(instance, player);
            Method isPressed = playerData.getClass().getMethod("isPressed");
            Object result = isPressed.invoke(playerData);
            return result instanceof Boolean pressed && pressed;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return false;
        }
    }
}
