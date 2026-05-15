package io.github.stevezhang123.gunwood.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.stevezhang123.gunwood.registry.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomHeadLayer.class)
public abstract class InvisibleGlassesHeadLayerMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void gunwood$hideEquippedGlasses(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            LivingEntity livingEntity,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci
    ) {
        if (livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.GLASSES.get())) {
            ci.cancel();
        }
    }
}
