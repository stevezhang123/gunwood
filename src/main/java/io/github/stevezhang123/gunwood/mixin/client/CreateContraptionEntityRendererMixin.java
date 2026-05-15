package io.github.stevezhang123.gunwood.mixin.client;

import io.github.stevezhang123.gunwood.compat.create.GunwoodContraptionRenderRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer", remap = false)
public abstract class CreateContraptionEntityRendererMixin {
    @Redirect(
            method = "renderActors",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/api/behaviour/movement/MovementBehaviour;renderInContraption(Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;Lcom/simibubi/create/foundation/virtualWorld/VirtualRenderWorld;Lcom/simibubi/create/content/contraptions/render/ContraptionMatrices;Lnet/minecraft/client/renderer/MultiBufferSource;)V"
            ),
            require = 0
    )
    private static void gunwood$skipHiddenContraptionActorFallbackRender(
            @Coerce Object movementBehaviour,
            @Coerce Object context,
            @Coerce Object renderWorld,
            @Coerce Object matrices,
            @Coerce Object buffer
    ) {
        if (!GunwoodContraptionRenderRules.shouldSkipMovementContext(context)) {
            GunwoodContraptionRenderRules.renderMovementBehaviour(movementBehaviour, context, renderWorld, matrices, buffer);
        }
    }
}
