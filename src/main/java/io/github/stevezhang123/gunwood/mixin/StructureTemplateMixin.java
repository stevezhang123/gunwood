package io.github.stevezhang123.gunwood.mixin;

import io.github.stevezhang123.gunwood.network.ModNetworking;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import io.github.stevezhang123.gunwood.paint.PaintedBlockManager;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Arrays;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
    @Unique
    private static final String gunwood$TAG_PAINTED_POSITIONS = "gunwood:painted_positions";
    @Unique
    private static final int gunwood$TAG_LONG_ARRAY = 12;

    @Unique
    private long[] gunwood$paintedRelativePositions = new long[0];

    @Inject(method = "fillFromWorld", at = @At("TAIL"))
    private void gunwood$capturePaintedBlocks(
            Level level,
            BlockPos pos,
            Vec3i size,
            boolean withEntities,
            @Nullable Block toIgnore,
            CallbackInfo ci
    ) {
        if (!GunwoodCommonConfig.enableSableSchematicCompat()
                || !(level instanceof ServerLevel serverLevel)
                || size.getX() < 1
                || size.getY() < 1
                || size.getZ() < 1) {
            this.gunwood$paintedRelativePositions = new long[0];
            return;
        }

        BlockPos maxPos = pos.offset(size).offset(-1, -1, -1);
        BlockPos min = new BlockPos(
                Math.min(pos.getX(), maxPos.getX()),
                Math.min(pos.getY(), maxPos.getY()),
                Math.min(pos.getZ(), maxPos.getZ())
        );
        BlockPos max = new BlockPos(
                Math.max(pos.getX(), maxPos.getX()),
                Math.max(pos.getY(), maxPos.getY()),
                Math.max(pos.getZ(), maxPos.getZ())
        );
        LongList relativePositions = new LongArrayList();

        for (BlockPos worldPos : BlockPos.betweenClosed(min, max)) {
            if (PaintedBlockManager.contains(serverLevel, worldPos.asLong())) {
                BlockPos relativePos = worldPos.subtract(min);
                relativePositions.add(relativePos.asLong());
            }
        }

        this.gunwood$paintedRelativePositions = relativePositions.toLongArray();
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void gunwood$savePaintedBlocks(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if (GunwoodCommonConfig.enableSableSchematicCompat() && this.gunwood$paintedRelativePositions.length > 0) {
            cir.getReturnValue().putLongArray(gunwood$TAG_PAINTED_POSITIONS, this.gunwood$paintedRelativePositions);
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void gunwood$loadPaintedBlocks(HolderGetter<Block> blockGetter, CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(gunwood$TAG_PAINTED_POSITIONS, gunwood$TAG_LONG_ARRAY)) {
            this.gunwood$paintedRelativePositions = tag.getLongArray(gunwood$TAG_PAINTED_POSITIONS);
        } else {
            this.gunwood$paintedRelativePositions = new long[0];
        }
    }

    @Inject(method = "placeInWorld", at = @At("RETURN"))
    private void gunwood$restorePaintedBlocks(
            ServerLevelAccessor serverLevel,
            BlockPos offset,
            BlockPos pos,
            StructurePlaceSettings settings,
            RandomSource random,
            int flags,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!GunwoodCommonConfig.enableSableSchematicCompat()
                || !cir.getReturnValue()
                || this.gunwood$paintedRelativePositions.length == 0
                || !(serverLevel.getLevel() instanceof ServerLevel level)) {
            return;
        }

        BoundingBox boundingBox = settings.getBoundingBox();
        LongArrayList worldPositions = new LongArrayList(this.gunwood$paintedRelativePositions.length);
        Arrays.stream(this.gunwood$paintedRelativePositions)
                .mapToObj(BlockPos::of)
                .map(relativePos -> StructureTemplate.calculateRelativePosition(settings, relativePos).offset(offset))
                .filter(worldPos -> boundingBox == null || boundingBox.isInside(worldPos))
                .mapToLong(BlockPos::asLong)
                .forEach(worldPositions::add);

        if (worldPositions.isEmpty()) {
            return;
        }

        LongList changedPositions = PaintedBlockManager.addAllLongs(level, worldPositions);
        if (!changedPositions.isEmpty()) {
            ModNetworking.syncAddedToNearby(level, offset, changedPositions.toLongArray());
        }
    }
}
