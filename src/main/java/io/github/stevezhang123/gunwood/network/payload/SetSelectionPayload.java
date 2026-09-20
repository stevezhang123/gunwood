package io.github.stevezhang123.gunwood.network.payload;

import io.github.stevezhang123.gunwood.selection.GunwoodSelection;
import net.minecraft.core.BlockPos;

public record SetSelectionPayload(BlockPos firstPos, BlockPos secondPos) {
    public SetSelectionPayload {
        firstPos = firstPos.immutable();
        secondPos = secondPos.immutable();
    }
    public SetSelectionPayload(GunwoodSelection selection) {
        this(selection.firstPos(), selection.secondPos());
    }
}
