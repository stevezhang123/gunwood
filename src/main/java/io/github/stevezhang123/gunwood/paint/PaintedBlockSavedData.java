package io.github.stevezhang123.gunwood.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PaintedBlockSavedData extends SavedData {
    private static final String DATA_NAME = "gunwood_painted_blocks";
    private static final String TAG_PAINTED_BLOCKS = "PaintedBlocks";
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";
    private static final int TAG_COMPOUND = 10;

    private final Set<BlockPos> paintedBlocks = new HashSet<>();

    public static PaintedBlockSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(PaintedBlockSavedData::new, PaintedBlockSavedData::load),
                DATA_NAME
        );
    }

    public static PaintedBlockSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PaintedBlockSavedData data = new PaintedBlockSavedData();
        ListTag paintedBlocks = tag.getList(TAG_PAINTED_BLOCKS, TAG_COMPOUND);

        for (int i = 0; i < paintedBlocks.size(); i++) {
            CompoundTag posTag = paintedBlocks.getCompound(i);
            data.paintedBlocks.add(new BlockPos(posTag.getInt(TAG_X), posTag.getInt(TAG_Y), posTag.getInt(TAG_Z)));
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag paintedBlocks = new ListTag();

        for (BlockPos pos : this.paintedBlocks) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt(TAG_X, pos.getX());
            posTag.putInt(TAG_Y, pos.getY());
            posTag.putInt(TAG_Z, pos.getZ());
            paintedBlocks.add(posTag);
        }

        tag.put(TAG_PAINTED_BLOCKS, paintedBlocks);
        return tag;
    }

    public boolean add(BlockPos pos) {
        if (this.paintedBlocks.add(pos.immutable())) {
            this.setDirty();
            return true;
        }
        return false;
    }

    public List<BlockPos> addAll(Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = new ArrayList<>();

        for (BlockPos pos : positions) {
            BlockPos immutablePos = pos.immutable();
            if (this.paintedBlocks.add(immutablePos)) {
                changedPositions.add(immutablePos);
            }
        }

        if (!changedPositions.isEmpty()) {
            this.setDirty();
        }
        return changedPositions;
    }

    public boolean remove(BlockPos pos) {
        if (this.paintedBlocks.remove(pos.immutable())) {
            this.setDirty();
            return true;
        }
        return false;
    }

    public List<BlockPos> removeAll(Collection<BlockPos> positions) {
        List<BlockPos> changedPositions = new ArrayList<>();

        for (BlockPos pos : positions) {
            BlockPos immutablePos = pos.immutable();
            if (this.paintedBlocks.remove(immutablePos)) {
                changedPositions.add(immutablePos);
            }
        }

        if (!changedPositions.isEmpty()) {
            this.setDirty();
        }
        return changedPositions;
    }

    public void removeAll(Collection<BlockPos> positions) {
        boolean changed = false;

        for (BlockPos pos : positions) {
            changed |= this.paintedBlocks.remove(pos.immutable());
        }

        if (changed) {
            this.setDirty();
        }
    }

    public boolean contains(BlockPos pos) {
        return this.paintedBlocks.contains(pos);
    }

    public Set<BlockPos> getAll() {
        return Set.copyOf(this.paintedBlocks);
    }
}
