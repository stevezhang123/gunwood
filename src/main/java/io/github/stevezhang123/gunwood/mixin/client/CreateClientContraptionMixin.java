package io.github.stevezhang123.gunwood.mixin.client;

import io.github.stevezhang123.gunwood.compat.create.GunwoodContraptionRenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.BitSet;

@Mixin(targets = "com.simibubi.create.content.contraptions.render.ClientContraption", remap = false)
public abstract class CreateClientContraptionMixin {
    @Inject(method = "getRenderedBlocks", at = @At("RETURN"), cancellable = true, require = 0)
    private void gunwood$filterHiddenContraptionBlocks(CallbackInfoReturnable<Object> cir) {
        cir.setReturnValue(GunwoodContraptionRenderRules.filterRenderedBlocks(this, cir.getReturnValue()));
    }

    @Inject(method = "getAndAdjustShouldRenderBlockEntities", at = @At("RETURN"), cancellable = true, require = 0)
    private void gunwood$filterHiddenContraptionBlockEntities(CallbackInfoReturnable<BitSet> cir) {
        cir.setReturnValue(GunwoodContraptionRenderRules.filterRenderedBlockEntities(this, cir.getReturnValue()));
    }
}
