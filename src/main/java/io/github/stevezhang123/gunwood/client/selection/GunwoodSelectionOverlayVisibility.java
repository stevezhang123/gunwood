package io.github.stevezhang123.gunwood.client.selection;

import io.github.stevezhang123.gunwood.client.render.GunwoodClientRenderRules;
import io.github.stevezhang123.gunwood.config.GunwoodClientConfig;
import io.github.stevezhang123.gunwood.registry.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class GunwoodSelectionOverlayVisibility {
    private GunwoodSelectionOverlayVisibility() {
    }

    public static boolean shouldShowPaintedBlockOverlays(Player player) {
        return (isHoldingPaintSelector(player) && GunwoodClientConfig.SHOW_PAINTED_BLOCKS_WHEN_HOLDING_SELECTION_TOOL.get())
                || (isHoldingScraper(player) && GunwoodClientConfig.SHOW_PAINTED_BLOCKS_WHEN_HOLDING_SCRAPER.get())
                || (GunwoodClientConfig.SHOW_PAINTED_BLOCKS_WHEN_WEARING_GOGGLES.get() && GunwoodClientRenderRules.isWearingGlasses());
    }

    public static boolean shouldShowSelectionOverlays(Player player) {
        return (isHoldingPaintSelector(player) && GunwoodClientConfig.SHOW_SELECTIONS_WHEN_HOLDING_SELECTION_TOOL.get())
                || (isHoldingScraper(player) && GunwoodClientConfig.SHOW_SELECTIONS_WHEN_HOLDING_SCRAPER.get());
    }

    public static boolean isHoldingPaintSelector(Player player) {
        return isPaintSelector(player.getItemInHand(InteractionHand.MAIN_HAND))
                || isPaintSelector(player.getItemInHand(InteractionHand.OFF_HAND));
    }

    private static boolean isHoldingScraper(Player player) {
        return isScraper(player.getItemInHand(InteractionHand.MAIN_HAND))
                || isScraper(player.getItemInHand(InteractionHand.OFF_HAND));
    }

    private static boolean isPaintSelector(ItemStack stack) {
        return stack.is(ModItems.PAINT_SELECTOR.get());
    }

    private static boolean isScraper(ItemStack stack) {
        return stack.is(ModItems.SCRAPER.get());
    }
}
