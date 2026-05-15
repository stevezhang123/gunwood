package io.github.stevezhang123.gunwood.client.selection;

import io.github.stevezhang123.gunwood.selection.GunwoodSelection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Optional;

public final class GunwoodClientSelectionState {
    private static BlockPos firstPos;
    private static BlockPos secondPos;

    private GunwoodClientSelectionState() {
    }

    public static void select(BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        if (firstPos == null || secondPos != null) {
            firstPos = immutablePos;
            secondPos = null;
        } else {
            secondPos = immutablePos;
        }
    }

    public static void clear() {
        firstPos = null;
        secondPos = null;
    }

    public static void clearPartial() {
        if (secondPos == null) {
            firstPos = null;
        }
    }

    public static void setSelection(GunwoodSelection selection) {
        firstPos = selection.firstPos();
        secondPos = selection.secondPos();
    }

    public static Optional<GunwoodSelection> adjustSelection(Direction face, int amount, long maxVolume) {
        Optional<GunwoodSelection> selection = selection();
        if (selection.isEmpty()) {
            return Optional.empty();
        }

        GunwoodSelection adjusted = selection.get().adjustFace(face, amount);
        if (adjusted.equals(selection.get()) || adjusted.volume() > maxVolume) {
            return Optional.empty();
        }

        setSelection(adjusted);
        return Optional.of(adjusted);
    }

    public static Optional<BlockPos> firstPos() {
        return Optional.ofNullable(firstPos);
    }

    public static Optional<GunwoodSelection> selection() {
        if (firstPos == null || secondPos == null) {
            return Optional.empty();
        }

        return Optional.of(new GunwoodSelection(firstPos, secondPos));
    }
}
