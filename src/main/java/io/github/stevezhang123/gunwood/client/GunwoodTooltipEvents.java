package io.github.stevezhang123.gunwood.client;

import io.github.stevezhang123.gunwood.Gunwood;
import io.github.stevezhang123.gunwood.config.GunwoodClientConfig;
import io.github.stevezhang123.gunwood.registry.ModItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Gunwood.MODID, value = Dist.CLIENT)
public final class GunwoodTooltipEvents {
    private static final TextColor FAINT_GRAY = TextColor.fromRgb(0x777777);

    private GunwoodTooltipEvents() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!GunwoodClientConfig.ENABLE_ADVANCED_TOOLTIPS.get()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.is(ModItems.SPRAY_BRUSH.get())) {
            addShiftTooltip(event, "spray_brush", 7);
            event.getToolTip().add(Component.translatable("tooltip.gunwood.spray_brush.flavor").withStyle(style -> style.withColor(FAINT_GRAY)));
        } else if (stack.is(ModItems.SCRAPER.get())) {
            addShiftTooltip(event, "scraper", 7);
        } else if (stack.is(ModItems.GLASSES.get())) {
            addShiftTooltip(event, "glasses", 5);
        } else if (stack.is(ModItems.PAINT_SELECTOR.get())) {
            addShiftTooltip(event, "paint_selector", 7);
        }
    }

    private static void addShiftTooltip(ItemTooltipEvent event, String itemName, int detailLines) {
        if (!Screen.hasShiftDown()) {
            event.getToolTip().add(Component.translatable("tooltip.gunwood." + itemName + ".summary"));
            return;
        }

        for (int i = 1; i <= detailLines; i++) {
            event.getToolTip().add(Component.translatable("tooltip.gunwood." + itemName + ".detail." + i));
        }
    }
}
