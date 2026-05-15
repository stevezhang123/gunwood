package io.github.stevezhang123.gunwood.client;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = Gunwood.MODID, value = Dist.CLIENT)
public final class ClientGlassesVisibilityEvents {
    private static boolean hadGlasses;

    private ClientGlassesVisibilityEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
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
