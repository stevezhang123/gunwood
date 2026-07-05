package io.github.stevezhang123.gunwood.network;

import io.github.stevezhang123.gunwood.client.ClientPaintedBlockCache;
import io.github.stevezhang123.gunwood.network.payload.AddPaintedBlockPayload;
import io.github.stevezhang123.gunwood.network.payload.AddPaintedBlocksPayload;
import io.github.stevezhang123.gunwood.network.payload.RemovePaintedBlockPayload;
import io.github.stevezhang123.gunwood.network.payload.RemovePaintedBlocksPayload;
import io.github.stevezhang123.gunwood.network.payload.SetSelectionPayload;
import io.github.stevezhang123.gunwood.network.payload.SyncPaintedBlocksPayload;
import io.github.stevezhang123.gunwood.paint.PaintedBlockManager;
import io.github.stevezhang123.gunwood.selection.GunwoodSelection;
import io.github.stevezhang123.gunwood.selection.GunwoodSelectionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Collection;

public final class ModNetworking {
    private static final String VERSION = "1";
    private static final double NEARBY_SYNC_RADIUS = 64.0D;

    private ModNetworking() {
    }

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);

        registrar.playToClient(
                SyncPaintedBlocksPayload.TYPE,
                SyncPaintedBlocksPayload.STREAM_CODEC,
                (payload, context) -> ClientPaintedBlockCache.replaceAll(payload.positions())
        );
        registrar.playToClient(
                AddPaintedBlockPayload.TYPE,
                AddPaintedBlockPayload.STREAM_CODEC,
                (payload, context) -> ClientPaintedBlockCache.add(payload.pos())
        );
        registrar.playToClient(
                AddPaintedBlocksPayload.TYPE,
                AddPaintedBlocksPayload.STREAM_CODEC,
                (payload, context) -> ClientPaintedBlockCache.addAll(payload.positions())
        );
        registrar.playToClient(
                RemovePaintedBlockPayload.TYPE,
                RemovePaintedBlockPayload.STREAM_CODEC,
                (payload, context) -> ClientPaintedBlockCache.remove(payload.pos())
        );
        registrar.playToClient(
                RemovePaintedBlocksPayload.TYPE,
                RemovePaintedBlocksPayload.STREAM_CODEC,
                (payload, context) -> ClientPaintedBlockCache.removeAll(payload.positions())
        );
        registrar.playToServer(
                SetSelectionPayload.TYPE,
                SetSelectionPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (context.player() instanceof ServerPlayer player) {
                        GunwoodSelectionManager.setSelection(player, new GunwoodSelection(payload.firstPos(), payload.secondPos()));
                    }
                }
        );
    }

    public static void syncAllToPlayer(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        PacketDistributor.sendToPlayer(player, new SyncPaintedBlocksPayload(PaintedBlockManager.getAllLongArray(level)));
    }

    public static void syncAddedToNearby(ServerLevel level, BlockPos pos) {
        PacketDistributor.sendToPlayersNear(level, null, pos.getX(), pos.getY(), pos.getZ(), NEARBY_SYNC_RADIUS, new AddPaintedBlockPayload(pos));
    }

    public static void syncAddedToNearby(ServerLevel level, BlockPos center, Collection<BlockPos> positions) {
        long[] positionLongs = positions.stream().mapToLong(BlockPos::asLong).toArray();
        syncAddedToNearby(level, center, positionLongs);
    }

    public static void syncAddedToNearby(ServerLevel level, BlockPos center, long[] positionLongs) {
        if (positionLongs.length > 0) {
            PacketDistributor.sendToPlayersNear(level, null, center.getX(), center.getY(), center.getZ(), NEARBY_SYNC_RADIUS, new AddPaintedBlocksPayload(positionLongs));
        }
    }

    public static void syncRemovedToNearby(ServerLevel level, BlockPos pos) {
        PacketDistributor.sendToPlayersNear(level, null, pos.getX(), pos.getY(), pos.getZ(), NEARBY_SYNC_RADIUS, new RemovePaintedBlockPayload(pos));
    }

    public static void syncRemovedToNearby(ServerLevel level, BlockPos center, Collection<BlockPos> positions) {
        long[] positionLongs = positions.stream().mapToLong(BlockPos::asLong).toArray();
        syncRemovedToNearby(level, center, positionLongs);
    }

    public static void syncRemovedToNearby(ServerLevel level, BlockPos center, long[] positionLongs) {
        if (positionLongs.length > 0) {
            PacketDistributor.sendToPlayersNear(level, null, center.getX(), center.getY(), center.getZ(), NEARBY_SYNC_RADIUS, new RemovePaintedBlocksPayload(positionLongs));
        }
    }
}
