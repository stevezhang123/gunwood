package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.Gunwood;
import io.github.stevezhang123.gunwood.selection.GunwoodSelection;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SetSelectionPayload(BlockPos firstPos, BlockPos secondPos) implements CustomPacketPayload {
    public static final Type<SetSelectionPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Gunwood.MODID, "set_selection"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetSelectionPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SetSelectionPayload::firstPos,
            BlockPos.STREAM_CODEC,
            SetSelectionPayload::secondPos,
            SetSelectionPayload::new
    );

    public SetSelectionPayload {
        firstPos = firstPos.immutable();
        secondPos = secondPos.immutable();
    }

    public SetSelectionPayload(GunwoodSelection selection) {
        this(selection.firstPos(), selection.secondPos());
    }

    @Override
    public Type<SetSelectionPayload> type() {
        return TYPE;
    }
}
