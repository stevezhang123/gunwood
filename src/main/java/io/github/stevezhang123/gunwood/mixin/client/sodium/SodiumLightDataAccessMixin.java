package io.github.stevezhang123.gunwood.mixin.client.sodium;

import io.github.stevezhang123.gunwood.client.GunwoodClientLightRules;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess", remap = false)
public abstract class SodiumLightDataAccessMixin {
    @Shadow
    @Final
    private BlockAndTintGetter level;

    @Unique
    private final BlockPos.MutableBlockPos gunwood$lightPos = new BlockPos.MutableBlockPos();

    @Inject(method = "compute", at = @At("HEAD"), cancellable = true, remap = false)
    private void gunwood$treatPaintedBlocksAsTransparentLightSamples(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        BlockPos pos = this.gunwood$lightPos.set(x, y, z);
        if (!GunwoodClientLightRules.isPainted(this.level, pos)) {
            return;
        }

        int light = LevelRenderer.getLightColor(this.level, Blocks.AIR.defaultBlockState(), pos);
        int blockLight = Math.max(LightTexture.block(light), this.level.getBrightness(LightLayer.BLOCK, pos));
        int skyLight = Math.max(LightTexture.sky(light), this.level.getBrightness(LightLayer.SKY, pos));

        cir.setReturnValue(gunwood$packTransparentLightData(blockLight, skyLight));
    }

    @Unique
    private static int gunwood$packTransparentLightData(int blockLight, int skyLight) {
        int word = 0;
        word |= blockLight & 0xF;
        word |= (skyLight & 0xF) << 4;
        word |= (int) (1.0F * 4096.0F) << 12;
        return word;
    }
}
