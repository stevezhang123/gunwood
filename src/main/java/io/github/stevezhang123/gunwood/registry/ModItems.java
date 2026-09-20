package io.github.stevezhang123.gunwood.registry;

import io.github.stevezhang123.gunwood.Gunwood;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import io.github.stevezhang123.gunwood.item.GlassesItem;
import io.github.stevezhang123.gunwood.item.PaintSelectorItem;
import io.github.stevezhang123.gunwood.item.ScraperItem;
import io.github.stevezhang123.gunwood.item.SprayBrushItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Gunwood.MODID);

    public static final RegistryObject<Item> SPRAY_BRUSH = ITEMS.register("spray_brush", () -> new SprayBrushItem(new Item.Properties().durability(GunwoodCommonConfig.DEFAULT_SPRAYER_MAX_DAMAGE)));
    public static final RegistryObject<Item> SCRAPER = ITEMS.register("scraper", () -> new ScraperItem(new Item.Properties().durability(GunwoodCommonConfig.DEFAULT_SCRAPER_MAX_DAMAGE)));
    public static final RegistryObject<Item> PAINT_SELECTOR = ITEMS.register("paint_selector", () -> new PaintSelectorItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GLASSES = ITEMS.register("glasses", () -> new GlassesItem(new Item.Properties().stacksTo(1)));

    private ModItems() {
    }
}
