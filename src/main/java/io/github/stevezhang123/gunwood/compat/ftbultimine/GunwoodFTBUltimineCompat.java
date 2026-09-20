package io.github.stevezhang123.gunwood.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

/** Bridge to the 1.20.1 FTB Ultimine public player-data API. */
public final class GunwoodFTBUltimineCompat {
    private GunwoodFTBUltimineCompat() {}
    public static void register() {}

    public static boolean isPressed(ServerPlayer player) {
        return FTBUltimine.instance != null && FTBUltimine.instance.getOrCreatePlayerData(player).isPressed();
    }

    public static Collection<BlockPos> selectedPositions(ServerPlayer player, BlockPos origin, Direction face, int limit) {
        if (!isPressed(player)) return List.of();
        FTBUltiminePlayerData data = FTBUltimine.instance.getOrCreatePlayerData(player);
        data.updateBlocks(player, origin, face, false, limit);
        return data.cachedPositions();
    }
}
