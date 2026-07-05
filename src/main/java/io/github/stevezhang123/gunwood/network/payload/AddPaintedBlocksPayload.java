package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public record AddPaintedBlocksPayload(long[] positions) implements CustomPacketPayload {
    public static final Type<AddPaintedBlocksPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Gunwood.MODID, "add_painted_blocks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AddPaintedBlocksPayload> STREAM_CODEC = CustomPacketPayload.codec(
            AddPaintedBlocksPayload::write,
            AddPaintedBlocksPayload::new
    );

    public AddPaintedBlocksPayload {
        positions = positions.clone();
    }

    public AddPaintedBlocksPayload(List<BlockPos> positions) {
        this(positions.stream().mapToLong(BlockPos::asLong).toArray());
    }

    public AddPaintedBlocksPayload(RegistryFriendlyByteBuf buffer) {
        this(readPositions(buffer));
    }

    public List<BlockPos> blockPositions() {
        return Arrays.stream(this.positions)
                .mapToObj(BlockPos::of)
                .toList();
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(this.positions.length);
        for (long pos : this.positions) {
            buffer.writeLong(pos);
        }
    }

    private static long[] readPositions(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        long[] positions = new long[size];
        for (int i = 0; i < size; i++) {
            positions[i] = buffer.readLong();
        }
        return positions;
    }

    @Override
    public Type<AddPaintedBlocksPayload> type() {
        return TYPE;
    }
}
