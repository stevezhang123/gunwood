package io.github.stevezhang123.gunwood.mixin.client.sodium;

import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.world.LevelSlice", remap = false)
public interface SodiumLevelSliceAccessor {
    @Invoker(value = "getBlockState", remap = false)
    BlockState gunwood$getBlockState(int blockX, int blockY, int blockZ);
}
