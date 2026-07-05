package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AddPaintedBlockPayload(long pos) implements CustomPacketPayload {
    public static final Type<AddPaintedBlockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Gunwood.MODID, "add_painted_block"));
    public static final CustomPacketPayload.Type<AddPaintedBlockPayload> ID = TYPE;
    public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, AddPaintedBlockPayload> STREAM_CODEC = CustomPacketPayload.codec(
            AddPaintedBlockPayload::write,
            AddPaintedBlockPayload::new
    );

    public AddPaintedBlockPayload(BlockPos pos) {
        this(pos.asLong());
    }

    public AddPaintedBlockPayload(RegistryFriendlyByteBuf buffer) {
        this(buffer.readLong());
    }

    public BlockPos blockPos() {
        return BlockPos.of(this.pos);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeLong(this.pos);
    }

    @Override
    public Type<AddPaintedBlockPayload> type() {
        return TYPE;
    }
}
