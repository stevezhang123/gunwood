package io.github.stevezhang123.gunwood.selection;

import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class GunwoodSelectionManager {
    private static final Map<UUID, BlockPos> FIRST_POSITIONS = new HashMap<>();
    private static final Map<UUID, GunwoodSelection> SELECTIONS = new HashMap<>();

    private GunwoodSelectionManager() {
    }

    public static void select(ServerPlayer player, BlockPos pos) {
        UUID playerId = player.getUUID();
        BlockPos immutablePos = pos.immutable();
        BlockPos firstPos = FIRST_POSITIONS.get(playerId);

        if (firstPos == null || SELECTIONS.containsKey(playerId)) {
            FIRST_POSITIONS.put(playerId, immutablePos);
            SELECTIONS.remove(playerId);
            player.sendSystemMessage(Component.translatable("message.gunwood.selection.first_set"));
            return;
        }

        GunwoodSelection selection = new GunwoodSelection(firstPos, immutablePos);
        if (selection.volume() > maxSelectionVolume()) {
            player.sendSystemMessage(Component.translatable("message.gunwood.selection.too_large", selection.volume(), maxSelectionVolume()));
            return;
        }

        FIRST_POSITIONS.put(playerId, firstPos);
        SELECTIONS.put(playerId, selection);
        player.sendSystemMessage(Component.translatable("message.gunwood.selection.second_set", selection.volume()));
    }

    public static Optional<GunwoodSelection> getSelection(ServerPlayer player) {
        return Optional.ofNullable(SELECTIONS.get(player.getUUID()));
    }

    public static void setSelection(ServerPlayer player, GunwoodSelection selection) {
        int maxVolume = maxAdjustableSelectionVolume();
        if (selection.volume() > maxVolume) {
            player.sendSystemMessage(Component.translatable("message.gunwood.selection.too_large", selection.volume(), maxVolume));
            return;
        }

        UUID playerId = player.getUUID();
        FIRST_POSITIONS.put(playerId, selection.firstPos());
        SELECTIONS.put(playerId, selection);
    }

    public static void clear(ServerPlayer player) {
        clear(player.getUUID());
        player.sendSystemMessage(Component.translatable("message.gunwood.selection.cleared"));
    }

    public static void clear(UUID playerId) {
        FIRST_POSITIONS.remove(playerId);
        SELECTIONS.remove(playerId);
    }

    public static int maxSelectionVolume() {
        return GunwoodCommonConfig.MAX_SELECTION_VOLUME.get();
    }

    public static int maxAdjustableSelectionVolume() {
        return Math.min(GunwoodCommonConfig.MAX_SELECTION_VOLUME.get(), GunwoodCommonConfig.MAX_BATCH_OPERATION_BLOCKS.get());
    }
}
