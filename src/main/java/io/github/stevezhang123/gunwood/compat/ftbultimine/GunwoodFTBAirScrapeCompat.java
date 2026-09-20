package io.github.stevezhang123.gunwood.compat.ftbultimine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public final class GunwoodFTBAirScrapeCompat {
    private static final String BRIDGE = "io.github.stevezhang123.gunwood.compat.ftbultimine.GunwoodFTBUltimineCompat";
    private GunwoodFTBAirScrapeCompat() {}

    public static boolean isUltiminePressed(ServerPlayer player) {
        if (!ModList.get().isLoaded("ftbultimine")) return false;
        try {
            return Boolean.TRUE.equals(Class.forName(BRIDGE).getMethod("isPressed", ServerPlayer.class).invoke(null, player));
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static Collection<BlockPos> selectedPositions(ServerPlayer player, BlockPos origin, Direction face, int limit) {
        if (!ModList.get().isLoaded("ftbultimine")) return List.of();
        try {
            Method method = Class.forName(BRIDGE).getMethod("selectedPositions", ServerPlayer.class, BlockPos.class, Direction.class, int.class);
            return (Collection<BlockPos>) method.invoke(null, player, origin, face, limit);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return List.of();
        }
    }
}
