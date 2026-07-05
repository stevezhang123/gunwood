package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RemovePaintedBlockPayload(long pos) implements CustomPacketPayload {
    public static final Type<RemovePaintedBlockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Gunwood.MODID, "remove_painted_block"));
    public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, RemovePaintedBlockPayload> STREAM_CODEC = CustomPacketPayload.codec(
            RemovePaintedBlockPayload::write,
            RemovePaintedBlockPayload::new
    );

    public RemovePaintedBlockPayload(BlockPos pos) {
        this(pos.asLong());
    }

    public RemovePaintedBlockPayload(RegistryFriendlyByteBuf buffer) {
        this(buffer.readLong());
    }

    public BlockPos blockPos() {
        return BlockPos.of(this.pos);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeLong(this.pos);
    }

    @Override
    public Type<RemovePaintedBlockPayload> type() {
        return TYPE;
    }
}
