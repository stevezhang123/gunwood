package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SyncPaintedBlocksPayload(List<BlockPos> positions) implements CustomPacketPayload {
    public static final Type<SyncPaintedBlocksPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Gunwood.MODID, "sync_painted_blocks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPaintedBlocksPayload> STREAM_CODEC = CustomPacketPayload.codec(
            SyncPaintedBlocksPayload::write,
            SyncPaintedBlocksPayload::new
    );

    public SyncPaintedBlocksPayload {
        positions = List.copyOf(positions);
    }

    public SyncPaintedBlocksPayload(RegistryFriendlyByteBuf buffer) {
        this(buffer.readList(itemBuffer -> itemBuffer.readBlockPos()));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeCollection(this.positions, (itemBuffer, pos) -> itemBuffer.writeBlockPos(pos));
    }

    @Override
    public Type<SyncPaintedBlocksPayload> type() {
        return TYPE;
    }
}
