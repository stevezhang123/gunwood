package io.github.stevezhang123.gunwood.client.selection;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.stevezhang123.gunwood.Gunwood;
import io.github.stevezhang123.gunwood.client.ClientPaintedBlockCache;
import io.github.stevezhang123.gunwood.registry.ModItems;
import io.github.stevezhang123.gunwood.selection.GunwoodSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = Gunwood.MODID, value = Dist.CLIENT)
public final class GunwoodSelectionRenderEvents {
    private static final int PAINTED_HIGHLIGHT_RANGE = 32;
    private static final int PAINTED_HIGHLIGHT_LIMIT = 512;

    private GunwoodSelectionRenderEvents() {
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || !isHoldingPaintSelector(minecraft.player)) {
            return;
        }

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        if (GunwoodClientSelectionState.selection().isPresent()) {
            GunwoodSelection selection = GunwoodClientSelectionState.selection().get();
            renderSelectionBox(poseStack, lines, selection, 0.05F, 0.95F, 1.0F, 1.0F, true);
            GunwoodClientSelectionEvents.hoveredSelectionFace(minecraft.player)
                    .ifPresent(face -> renderSelectionFace(poseStack, lines, selection, face));
        } else {
            GunwoodClientSelectionState.firstPos().ifPresent(pos -> {
                renderBlockBox(poseStack, lines, pos, 0.1F, 0.8F, 1.0F, 1.0F, true);
                previewSelection(minecraft, pos)
                        .ifPresent(selection -> renderSelectionBox(poseStack, lines, selection, 0.2F, 0.75F, 1.0F, 0.75F, false));
            });
        }

        BlockPos playerPos = minecraft.player.blockPosition();
        for (BlockPos pos : ClientPaintedBlockCache.positionsNear(playerPos, PAINTED_HIGHLIGHT_RANGE, PAINTED_HIGHLIGHT_LIMIT)) {
            renderBlockBox(poseStack, lines, pos, 1.0F, 0.8F, 0.15F, 0.45F, false);
        }

        poseStack.popPose();
        bufferSource.endBatch(RenderType.lines());
    }

    private static void renderSelectionBox(PoseStack poseStack, VertexConsumer lines, GunwoodSelection selection, float red, float green, float blue, float alpha, boolean prominent) {
        AABB box = GunwoodClientSelectionEvents.selectionBox(selection).inflate(0.004D);
        LevelRenderer.renderLineBox(poseStack, lines, box, red, green, blue, alpha);
        LevelRenderer.renderLineBox(poseStack, lines, box.inflate(0.012D), red, green, blue, alpha);
        if (prominent) {
            LevelRenderer.renderLineBox(poseStack, lines, box.inflate(0.028D), 1.0F, 1.0F, 1.0F, 0.65F);
        }
    }

    private static void renderSelectionFace(PoseStack poseStack, VertexConsumer lines, GunwoodSelection selection, Direction face) {
        AABB box = GunwoodClientSelectionEvents.selectionBox(selection).inflate(0.035D);
        double thickness = 0.035D;
        AABB faceBox = switch (face) {
            case EAST -> new AABB(box.maxX - thickness, box.minY, box.minZ, box.maxX + thickness, box.maxY, box.maxZ);
            case WEST -> new AABB(box.minX - thickness, box.minY, box.minZ, box.minX + thickness, box.maxY, box.maxZ);
            case UP -> new AABB(box.minX, box.maxY - thickness, box.minZ, box.maxX, box.maxY + thickness, box.maxZ);
            case DOWN -> new AABB(box.minX, box.minY - thickness, box.minZ, box.maxX, box.minY + thickness, box.maxZ);
            case SOUTH -> new AABB(box.minX, box.minY, box.maxZ - thickness, box.maxX, box.maxY, box.maxZ + thickness);
            case NORTH -> new AABB(box.minX, box.minY, box.minZ - thickness, box.maxX, box.maxY, box.minZ + thickness);
        };
        LevelRenderer.renderLineBox(poseStack, lines, faceBox, 1.0F, 0.95F, 0.25F, 1.0F);
        LevelRenderer.renderLineBox(poseStack, lines, faceBox.inflate(0.012D), 1.0F, 1.0F, 1.0F, 0.75F);
    }

    private static void renderBlockBox(PoseStack poseStack, VertexConsumer lines, BlockPos pos, float red, float green, float blue, float alpha, boolean prominent) {
        AABB box = new AABB(pos).inflate(0.006D);
        LevelRenderer.renderLineBox(poseStack, lines, box, red, green, blue, alpha);
        if (prominent) {
            LevelRenderer.renderLineBox(poseStack, lines, box.inflate(0.018D), 1.0F, 1.0F, 1.0F, 0.7F);
        }
    }

    private static java.util.Optional<GunwoodSelection> previewSelection(Minecraft minecraft, BlockPos firstPos) {
        if (minecraft.hitResult == null || minecraft.hitResult.getType() != HitResult.Type.BLOCK) {
            return java.util.Optional.empty();
        }

        return java.util.Optional.of(new GunwoodSelection(firstPos, ((BlockHitResult) minecraft.hitResult).getBlockPos()));
    }

    private static boolean isHoldingPaintSelector(Player player) {
        return isPaintSelector(player.getItemInHand(InteractionHand.MAIN_HAND))
                || isPaintSelector(player.getItemInHand(InteractionHand.OFF_HAND));
    }

    private static boolean isPaintSelector(ItemStack stack) {
        return stack.is(ModItems.PAINT_SELECTOR.get());
    }
}
