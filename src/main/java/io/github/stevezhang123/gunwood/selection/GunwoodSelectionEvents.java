package io.github.stevezhang123.gunwood.selection;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class GunwoodSelectionEvents {
    private GunwoodSelectionEvents() {
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            GunwoodSelectionManager.clear(player.getUUID());
        }
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            GunwoodSelectionManager.clear(player.getUUID());
        }
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            GunwoodSelectionManager.clear(player.getUUID());
        }
    }
}
