package io.github.stevezhang123.gunwood.registry;

import io.github.stevezhang123.gunwood.Gunwood;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Gunwood.MODID);

    public static final RegistryObject<CreativeModeTab> GUNWOOD_TAB = CREATIVE_MODE_TABS.register(
            "gunwood_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gunwood.gunwood_tab"))
                    .icon(() -> ModItems.SPRAY_BRUSH.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SPRAY_BRUSH.get());
                        output.accept(ModItems.SCRAPER.get());
                        output.accept(ModItems.PAINT_SELECTOR.get());
                        output.accept(ModItems.GLASSES.get());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }
}
