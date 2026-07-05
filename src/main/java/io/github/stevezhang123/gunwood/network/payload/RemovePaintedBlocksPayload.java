package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public record RemovePaintedBlocksPayload(long[] positions) implements CustomPacketPayload {
    public static final Type<RemovePaintedBlocksPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Gunwood.MODID, "remove_painted_blocks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RemovePaintedBlocksPayload> STREAM_CODEC = CustomPacketPayload.codec(
            RemovePaintedBlocksPayload::write,
            RemovePaintedBlocksPayload::new
    );

    public RemovePaintedBlocksPayload {
        positions = positions.clone();
    }

    public RemovePaintedBlocksPayload(List<BlockPos> positions) {
        this(positions.stream().mapToLong(BlockPos::asLong).toArray());
    }

    public RemovePaintedBlocksPayload(RegistryFriendlyByteBuf buffer) {
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
    public Type<RemovePaintedBlocksPayload> type() {
        return TYPE;
    }
}
