package io.github.stevezhang123.gunwood.mixin.client.embeddium;

import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.world.WorldSlice", remap = false)
public interface EmbeddiumWorldSliceAccessor {
    @Invoker(value = "getBlockState", remap = false)
    BlockState gunwood$getBlockState(int blockX, int blockY, int blockZ);
}
