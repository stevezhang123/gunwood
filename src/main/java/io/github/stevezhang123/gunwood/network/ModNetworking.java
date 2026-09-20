package io.github.stevezhang123.gunwood.network;

import io.github.stevezhang123.gunwood.Gunwood;
import io.github.stevezhang123.gunwood.network.payload.*;
import io.github.stevezhang123.gunwood.paint.PaintedBlockManager;
import io.github.stevezhang123.gunwood.selection.GunwoodSelection;
import io.github.stevezhang123.gunwood.selection.GunwoodSelectionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Collection;
import java.util.function.Supplier;

public final class ModNetworking {
    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Gunwood.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    private ModNetworking() {}

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SyncPaintedBlocksPayload.class,
                (m, b) -> b.writeLongArray(m.positions()),
                b -> new SyncPaintedBlocksPayload(b.readLongArray()), ModNetworking::handleClient,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, AddPaintedBlockPayload.class,
                (m, b) -> b.writeLong(m.pos()),
                b -> new AddPaintedBlockPayload(b.readLong()), ModNetworking::handleClient,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, AddPaintedBlocksPayload.class,
                (m, b) -> b.writeLongArray(m.positions()),
                b -> new AddPaintedBlocksPayload(b.readLongArray()), ModNetworking::handleClient,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, RemovePaintedBlockPayload.class,
                (m, b) -> b.writeLong(m.pos()),
                b -> new RemovePaintedBlockPayload(b.readLong()), ModNetworking::handleClient,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, RemovePaintedBlocksPayload.class,
                (m, b) -> b.writeLongArray(m.positions()),
                b -> new RemovePaintedBlocksPayload(b.readLongArray()), ModNetworking::handleClient,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id, SetSelectionPayload.class,
                (m, b) -> { b.writeBlockPos(m.firstPos()); b.writeBlockPos(m.secondPos()); },
                b -> new SetSelectionPayload(b.readBlockPos(), b.readBlockPos()), ModNetworking::handleSelection,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private static <T> void handleClient(T packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientNetworkHandler.handle(packet)));
        context.setPacketHandled(true);
    }

    private static void handleSelection(SetSelectionPayload packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) GunwoodSelectionManager.setSelection(player,
                    new GunwoodSelection(packet.firstPos(), packet.secondPos()));
        });
        context.setPacketHandled(true);
    }

    public static void sendSelection(SetSelectionPayload selection) { CHANNEL.sendToServer(selection); }

    public static void syncAllToPlayer(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncPaintedBlocksPayload(PaintedBlockManager.getAllLongArray(player.serverLevel())));
    }

    private static void sendNear(ServerLevel level, BlockPos center, Object packet) {
        CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                center.getX(), center.getY(), center.getZ(), 64, level.dimension())), packet);
    }

    public static void syncAddedToNearby(ServerLevel level, BlockPos pos) {
        sendNear(level, pos, new AddPaintedBlockPayload(pos.asLong()));
    }

    public static void syncAddedToNearby(ServerLevel level, BlockPos center, Collection<BlockPos> positions) {
        syncAddedToNearby(level, center, positions.stream().mapToLong(BlockPos::asLong).toArray());
    }

    public static void syncAddedToNearby(ServerLevel level, BlockPos center, long[] positions) {
        if (positions.length > 0) sendNear(level, center, new AddPaintedBlocksPayload(positions));
    }

    public static void syncRemovedToNearby(ServerLevel level, BlockPos pos) {
        sendNear(level, pos, new RemovePaintedBlockPayload(pos.asLong()));
    }

    public static void syncRemovedToNearby(ServerLevel level, BlockPos center, Collection<BlockPos> positions) {
        syncRemovedToNearby(level, center, positions.stream().mapToLong(BlockPos::asLong).toArray());
    }

    public static void syncRemovedToNearby(ServerLevel level, BlockPos center, long[] positions) {
        if (positions.length > 0) sendNear(level, center, new RemovePaintedBlocksPayload(positions));
    }
}
