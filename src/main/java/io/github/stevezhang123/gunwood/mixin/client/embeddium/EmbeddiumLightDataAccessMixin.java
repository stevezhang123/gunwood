package io.github.stevezhang123.gunwood.mixin.client.embeddium;

import io.github.stevezhang123.gunwood.client.GunwoodClientLightRules;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.model.light.data.LightDataAccess", remap = false)
public abstract class EmbeddiumLightDataAccessMixin {
    @Shadow
    protected BlockAndTintGetter world;

    @Unique
    private final BlockPos.MutableBlockPos gunwood$lightPos = new BlockPos.MutableBlockPos();

    @Inject(method = "compute", at = @At("HEAD"), cancellable = true, remap = false)
    private void gunwood$treatPaintedBlocksAsTransparentLightSamples(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        BlockPos pos = this.gunwood$lightPos.set(x, y, z);
        if (!GunwoodCommonConfig.ENABLE_EMBEDDIUM_COMPAT.get() || !GunwoodClientLightRules.isPainted(this.world, pos)) {
            return;
        }

        int light = LevelRenderer.getLightColor(this.world, Blocks.AIR.defaultBlockState(), pos);
        int blockLight = Math.max(LightTexture.block(light), this.world.getBrightness(LightLayer.BLOCK, pos));
        int skyLight = Math.max(LightTexture.sky(light), this.world.getBrightness(LightLayer.SKY, pos));

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
