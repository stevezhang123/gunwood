package io.github.stevezhang123.gunwood.client.selection;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.stevezhang123.gunwood.Gunwood;
import io.github.stevezhang123.gunwood.client.ClientPaintedBlockCache;
import io.github.stevezhang123.gunwood.config.GunwoodClientConfig;
import io.github.stevezhang123.gunwood.config.GunwoodClientConfig.Color;
import io.github.stevezhang123.gunwood.registry.ModItems;
import io.github.stevezhang123.gunwood.selection.GunwoodSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
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
    private GunwoodSelectionRenderEvents() {
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        boolean showSelectionOverlays = GunwoodSelectionOverlayVisibility.shouldShowSelectionOverlays(minecraft.player);
        boolean showPaintedBlockOverlays = GunwoodSelectionOverlayVisibility.shouldShowPaintedBlockOverlays(minecraft.player);
        if (!showSelectionOverlays && !showPaintedBlockOverlays) {
            return;
        }

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        if (showSelectionOverlays && GunwoodClientSelectionState.selection().isPresent()) {
            GunwoodSelection selection = GunwoodClientSelectionState.selection().get();
            renderSelectionBox(poseStack, lines, selection, GunwoodClientConfig.selectionOutlineColor(), true);
            selectionHighlightFace(minecraft.player)
                    .ifPresent(face -> renderSelectionFace(poseStack, lines, selection, face));
        } else if (showSelectionOverlays && GunwoodSelectionOverlayVisibility.isHoldingPaintSelector(minecraft.player)) {
            GunwoodClientSelectionState.firstPos().ifPresent(pos -> {
                renderBlockBox(poseStack, lines, pos, GunwoodClientConfig.selectionOutlineColor(), true);
                previewSelection(minecraft, pos)
                        .ifPresent(selection -> renderSelectionBox(poseStack, lines, selection, GunwoodClientConfig.selectionOutlineColor(), false));
            });
        }

        if (showPaintedBlockOverlays) {
            BlockPos playerPos = minecraft.player.blockPosition();
            for (BlockPos pos : ClientPaintedBlockCache.positionsNear(
                    playerPos,
                    GunwoodClientConfig.PAINTED_BLOCK_OVERLAY_RANGE.get(),
                    GunwoodClientConfig.MAX_PAINTED_BLOCK_OVERLAY_COUNT.get()
            )) {
                renderBlockBox(poseStack, lines, pos, GunwoodClientConfig.paintedBlockOutlineColor(), false);
            }
        }

        poseStack.popPose();
        bufferSource.endBatch(RenderType.lines());
    }

    private static void renderSelectionBox(PoseStack poseStack, VertexConsumer lines, GunwoodSelection selection, Color color, boolean prominent) {
        AABB box = GunwoodClientSelectionEvents.selectionBox(selection).inflate(GunwoodClientConfig.OUTLINE_INFLATION.get());
        renderInflatedLineBox(poseStack, lines, box, color, GunwoodClientConfig.SELECTION_LINE_WIDTH.get());
        if (prominent) {
            renderInflatedLineBox(poseStack, lines, box.inflate(0.028D), GunwoodClientConfig.hoveredSelectionOutlineColor(), 1.0D);
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
        renderInflatedLineBox(poseStack, lines, faceBox, GunwoodClientConfig.selectionFaceHighlightColor(), 1.0D);
        renderInflatedLineBox(poseStack, lines, faceBox.inflate(0.012D), GunwoodClientConfig.hoveredSelectionOutlineColor(), 1.0D);
    }

    private static void renderBlockBox(PoseStack poseStack, VertexConsumer lines, BlockPos pos, Color color, boolean prominent) {
        AABB box = new AABB(pos).inflate(GunwoodClientConfig.OUTLINE_INFLATION.get());
        renderInflatedLineBox(poseStack, lines, box, color, prominent ? GunwoodClientConfig.SELECTION_LINE_WIDTH.get() : GunwoodClientConfig.PAINTED_BLOCK_LINE_WIDTH.get());
        if (prominent) {
            renderInflatedLineBox(poseStack, lines, box.inflate(0.018D), GunwoodClientConfig.hoveredSelectionOutlineColor(), 1.0D);
        }
    }

    private static void renderInflatedLineBox(PoseStack poseStack, VertexConsumer lines, AABB box, Color color, double width) {
        int passes = Math.max(1, (int) Math.round(width));
        for (int pass = 0; pass < passes; pass++) {
            LevelRenderer.renderLineBox(
                    poseStack,
                    lines,
                    box.inflate(pass * GunwoodClientConfig.OUTLINE_INFLATION.get()),
                    color.red(),
                    color.green(),
                    color.blue(),
                    color.alpha()
            );
        }
    }

    private static java.util.Optional<Direction> selectionHighlightFace(Player player) {
        if (Screen.hasControlDown() && GunwoodSelectionOverlayVisibility.isHoldingPaintSelector(player)) {
            return GunwoodClientSelectionEvents.selectionAdjustmentFace(player);
        }
        return GunwoodClientSelectionEvents.hoveredSelectionFace(player);
    }

    private static java.util.Optional<GunwoodSelection> previewSelection(Minecraft minecraft, BlockPos firstPos) {
        if (minecraft.hitResult == null || minecraft.hitResult.getType() != HitResult.Type.BLOCK) {
            return java.util.Optional.empty();
        }

        return java.util.Optional.of(new GunwoodSelection(firstPos, ((BlockHitResult) minecraft.hitResult).getBlockPos()));
    }

}
