package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record AddPaintedBlocksPayload(List<BlockPos> positions) implements CustomPacketPayload {
    public static final Type<AddPaintedBlocksPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Gunwood.MODID, "add_painted_blocks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AddPaintedBlocksPayload> STREAM_CODEC = CustomPacketPayload.codec(
            AddPaintedBlocksPayload::write,
            AddPaintedBlocksPayload::new
    );

    public AddPaintedBlocksPayload {
        positions = positions.stream()
                .map(BlockPos::immutable)
                .toList();
    }

    public AddPaintedBlocksPayload(RegistryFriendlyByteBuf buffer) {
        this(buffer.readList(itemBuffer -> itemBuffer.readBlockPos()));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeCollection(this.positions, (itemBuffer, pos) -> itemBuffer.writeBlockPos(pos));
    }

    @Override
    public Type<AddPaintedBlocksPayload> type() {
        return TYPE;
    }
}
