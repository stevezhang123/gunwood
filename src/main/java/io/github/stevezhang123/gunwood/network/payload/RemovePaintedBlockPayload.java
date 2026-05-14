package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RemovePaintedBlockPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<RemovePaintedBlockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Gunwood.MODID, "remove_painted_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RemovePaintedBlockPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            RemovePaintedBlockPayload::pos,
            RemovePaintedBlockPayload::new
    );

    public RemovePaintedBlockPayload {
        pos = pos.immutable();
    }

    @Override
    public Type<RemovePaintedBlockPayload> type() {
        return TYPE;
    }
}
