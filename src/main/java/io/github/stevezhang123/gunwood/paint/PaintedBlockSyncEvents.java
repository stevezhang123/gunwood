package io.github.stevezhang123.gunwood.paint;

import io.github.stevezhang123.gunwood.network.ModNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class PaintedBlockSyncEvents {
    private PaintedBlockSyncEvents() {
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PaintedBlockManager.refreshAllLight(player.serverLevel());
            ModNetworking.syncAllToPlayer(player);
        }
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PaintedBlockManager.refreshAllLight(player.serverLevel());
            ModNetworking.syncAllToPlayer(player);
        }
    }
}
