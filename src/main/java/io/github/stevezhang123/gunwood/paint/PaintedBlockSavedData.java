package io.github.stevezhang123.gunwood.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PaintedBlockSavedData extends SavedData {
    private static final String DATA_NAME = "gunwood_painted_blocks";
    private static final String TAG_PAINTED_BLOCKS = "PaintedBlockPositions";
    private static final String TAG_LEGACY_PAINTED_BLOCKS = "PaintedBlocks";
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";
    private static final int TAG_COMPOUND = 10;
    private static final int TAG_LONG_ARRAY = 12;

    private final LongSet paintedBlocks = new LongOpenHashSet();

    public static PaintedBlockSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(PaintedBlockSavedData::new, PaintedBlockSavedData::load),
                DATA_NAME
        );
    }

    public static PaintedBlockSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PaintedBlockSavedData data = new PaintedBlockSavedData();
        if (tag.contains(TAG_PAINTED_BLOCKS, TAG_LONG_ARRAY)) {
            Arrays.stream(tag.getLongArray(TAG_PAINTED_BLOCKS)).forEach(data.paintedBlocks::add);
            return data;
        }

        ListTag paintedBlocks = tag.getList(TAG_LEGACY_PAINTED_BLOCKS, TAG_COMPOUND);
        for (int i = 0; i < paintedBlocks.size(); i++) {
            CompoundTag posTag = paintedBlocks.getCompound(i);
            data.paintedBlocks.add(BlockPos.asLong(posTag.getInt(TAG_X), posTag.getInt(TAG_Y), posTag.getInt(TAG_Z)));
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLongArray(TAG_PAINTED_BLOCKS, this.paintedBlocks.toLongArray());
        return tag;
    }

    public boolean add(BlockPos pos) {
        return add(pos.asLong());
    }

    public boolean add(long pos) {
        if (this.paintedBlocks.add(pos)) {
            this.setDirty();
            return true;
        }
        return false;
    }

    public LongList addAll(Collection<BlockPos> positions) {
        LongArrayList changedPositions = new LongArrayList();

        for (BlockPos pos : positions) {
            long posLong = pos.asLong();
            if (this.paintedBlocks.add(posLong)) {
                changedPositions.add(posLong);
            }
        }

        if (!changedPositions.isEmpty()) {
            this.setDirty();
        }
        return changedPositions;
    }

    public LongList addAllLongs(LongCollection positions) {
        LongArrayList changedPositions = new LongArrayList();
        positions.forEach((long pos) -> {
            if (this.paintedBlocks.add(pos)) {
                changedPositions.add(pos);
            }
        });

        if (!changedPositions.isEmpty()) {
            this.setDirty();
        }
        return changedPositions;
    }

    public boolean remove(BlockPos pos) {
        return remove(pos.asLong());
    }

    public boolean remove(long pos) {
        if (this.paintedBlocks.remove(pos)) {
            this.setDirty();
            return true;
        }
        return false;
    }

    public LongList removeAll(Collection<BlockPos> positions) {
        LongArrayList changedPositions = new LongArrayList();

        for (BlockPos pos : positions) {
            long posLong = pos.asLong();
            if (this.paintedBlocks.remove(posLong)) {
                changedPositions.add(posLong);
            }
        }

        if (!changedPositions.isEmpty()) {
            this.setDirty();
        }
        return changedPositions;
    }

    public LongList removeAllLongs(LongCollection positions) {
        LongArrayList changedPositions = new LongArrayList();
        positions.forEach((long pos) -> {
            if (this.paintedBlocks.remove(pos)) {
                changedPositions.add(pos);
            }
        });

        if (!changedPositions.isEmpty()) {
            this.setDirty();
        }
        return changedPositions;
    }

    public boolean contains(BlockPos pos) {
        return contains(pos.asLong());
    }

    public boolean contains(long pos) {
        return this.paintedBlocks.contains(pos);
    }

    public Set<BlockPos> getAll() {
        Set<BlockPos> positions = new HashSet<>();
        this.paintedBlocks.forEach((long pos) -> positions.add(BlockPos.of(pos)));
        return Set.copyOf(positions);
    }

    public LongSet getAllLongs() {
        return new LongOpenHashSet(this.paintedBlocks);
    }

    public long[] getAllLongArray() {
        return this.paintedBlocks.toLongArray();
    }
}
