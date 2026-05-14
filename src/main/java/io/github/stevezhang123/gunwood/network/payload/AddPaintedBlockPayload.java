package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AddPaintedBlockPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<AddPaintedBlockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Gunwood.MODID, "add_painted_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AddPaintedBlockPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            AddPaintedBlockPayload::pos,
            AddPaintedBlockPayload::new
    );

    public AddPaintedBlockPayload {
        pos = pos.immutable();
    }

    @Override
    public Type<AddPaintedBlockPayload> type() {
        return TYPE;
    }
}
