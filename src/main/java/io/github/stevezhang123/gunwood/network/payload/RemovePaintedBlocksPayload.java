package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record RemovePaintedBlocksPayload(List<BlockPos> positions) implements CustomPacketPayload {
    public static final Type<RemovePaintedBlocksPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Gunwood.MODID, "remove_painted_blocks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RemovePaintedBlocksPayload> STREAM_CODEC = CustomPacketPayload.codec(
            RemovePaintedBlocksPayload::write,
            RemovePaintedBlocksPayload::new
    );

    public RemovePaintedBlocksPayload {
        positions = positions.stream()
                .map(BlockPos::immutable)
                .toList();
    }

    public RemovePaintedBlocksPayload(RegistryFriendlyByteBuf buffer) {
        this(buffer.readList(itemBuffer -> itemBuffer.readBlockPos()));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeCollection(this.positions, (itemBuffer, pos) -> itemBuffer.writeBlockPos(pos));
    }

    @Override
    public Type<RemovePaintedBlocksPayload> type() {
        return TYPE;
    }
}
