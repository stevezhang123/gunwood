package io.github.stevezhang123.gunwood.client;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.TickEvent;

@EventBusSubscriber(modid = Gunwood.MODID, value = Dist.CLIENT)
public final class ClientGlassesVisibilityEvents {
    private static boolean hadGlasses;

    private ClientGlassesVisibilityEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            hadGlasses = false;
            return;
        }

        boolean hasGlasses = ClientPaintedBlockVisibility.hasGlasses();
        if (hasGlasses != hadGlasses) {
            hadGlasses = hasGlasses;
            ClientPaintedBlockCache.refreshAllRendering();
        }
    }
}
