package io.github.stevezhang123.gunwood.client.selection;

import io.github.stevezhang123.gunwood.Gunwood;
import io.github.stevezhang123.gunwood.network.payload.SetSelectionPayload;
import io.github.stevezhang123.gunwood.registry.ModItems;
import io.github.stevezhang123.gunwood.selection.GunwoodSelection;
import io.github.stevezhang123.gunwood.selection.GunwoodSelectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

@EventBusSubscriber(modid = Gunwood.MODID, value = Dist.CLIENT)
public final class GunwoodClientSelectionEvents {
    private GunwoodClientSelectionEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            return;
        }

        ItemStack stack = player.getItemInHand(event.getHand());
        if (!stack.is(ModItems.PAINT_SELECTOR.get())) {
            return;
        }

        if (player.isShiftKeyDown()) {
            GunwoodClientSelectionState.clear();
        } else {
            GunwoodClientSelectionState.select(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            GunwoodClientSelectionState.clear();
            return;
        }

        if (!isHoldingSelectionRelatedItem(minecraft.player)) {
            GunwoodClientSelectionState.clearPartial();
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.screen != null) {
            return;
        }
        if (!Screen.hasControlDown() || !isHoldingPaintSelector(minecraft.player)) {
            return;
        }

        Optional<Direction> face = hoveredSelectionFace(minecraft.player);
        if (face.isEmpty()) {
            return;
        }

        int scrollAmount = event.getScrollDeltaY() > 0.0D ? 1 : -1;
        Optional<GunwoodSelection> adjusted = GunwoodClientSelectionState.adjustSelection(face.get(), scrollAmount, GunwoodSelectionManager.MAX_SELECTION_VOLUME);
        if (adjusted.isPresent()) {
            PacketDistributor.sendToServer(new SetSelectionPayload(adjusted.get()));
            minecraft.player.displayClientMessage(Component.translatable("message.gunwood.selection.adjusted", adjusted.get().volume()), true);
        } else {
            minecraft.player.displayClientMessage(Component.translatable("message.gunwood.selection.adjust_blocked"), true);
        }
        event.setCanceled(true);
    }

    private static boolean isHoldingSelectionRelatedItem(Player player) {
        return isSelectionRelatedItem(player.getItemInHand(InteractionHand.MAIN_HAND))
                || isSelectionRelatedItem(player.getItemInHand(InteractionHand.OFF_HAND));
    }

    private static boolean isSelectionRelatedItem(ItemStack stack) {
        return stack.is(ModItems.PAINT_SELECTOR.get())
                || stack.is(ModItems.SPRAY_BRUSH.get())
                || stack.is(ModItems.SCRAPER.get());
    }

    private static boolean isHoldingPaintSelector(Player player) {
        return isPaintSelector(player.getItemInHand(InteractionHand.MAIN_HAND))
                || isPaintSelector(player.getItemInHand(InteractionHand.OFF_HAND));
    }

    private static boolean isPaintSelector(ItemStack stack) {
        return stack.is(ModItems.PAINT_SELECTOR.get());
    }

    public static Optional<Direction> hoveredSelectionFace(Player player) {
        Optional<GunwoodSelection> selection = GunwoodClientSelectionState.selection();
        if (selection.isEmpty()) {
            return Optional.empty();
        }

        AABB box = selectionBox(selection.get()).inflate(0.03D);
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(64.0D));
        Optional<Vec3> hit = box.clip(eye, end);
        return hit.flatMap(vec -> faceForHit(selection.get(), vec));
    }

    public static AABB selectionBox(GunwoodSelection selection) {
        BlockPos min = selection.min();
        BlockPos max = selection.max();
        return new AABB(min.getX(), min.getY(), min.getZ(), max.getX() + 1.0D, max.getY() + 1.0D, max.getZ() + 1.0D);
    }

    private static Optional<Direction> faceForHit(GunwoodSelection selection, Vec3 hit) {
        AABB box = selectionBox(selection);
        double epsilon = 0.06D;
        Direction bestFace = null;
        double bestDistance = Double.MAX_VALUE;

        bestFace = nearestFace(bestFace, Math.abs(hit.x - box.maxX), epsilon, Direction.EAST);
        bestDistance = bestFace == Direction.EAST ? Math.abs(hit.x - box.maxX) : bestDistance;
        if (Math.abs(hit.x - box.minX) < Math.min(bestDistance, epsilon)) {
            bestFace = Direction.WEST;
            bestDistance = Math.abs(hit.x - box.minX);
        }
        if (Math.abs(hit.y - box.maxY) < Math.min(bestDistance, epsilon)) {
            bestFace = Direction.UP;
            bestDistance = Math.abs(hit.y - box.maxY);
        }
        if (Math.abs(hit.y - box.minY) < Math.min(bestDistance, epsilon)) {
            bestFace = Direction.DOWN;
            bestDistance = Math.abs(hit.y - box.minY);
        }
        if (Math.abs(hit.z - box.maxZ) < Math.min(bestDistance, epsilon)) {
            bestFace = Direction.SOUTH;
            bestDistance = Math.abs(hit.z - box.maxZ);
        }
        if (Math.abs(hit.z - box.minZ) < Math.min(bestDistance, epsilon)) {
            bestFace = Direction.NORTH;
        }

        return Optional.ofNullable(bestFace);
    }

    private static Direction nearestFace(Direction current, double distance, double epsilon, Direction face) {
        return distance < epsilon ? face : current;
    }
}
