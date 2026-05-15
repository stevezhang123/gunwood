package io.github.stevezhang123.gunwood.registry;

import io.github.stevezhang123.gunwood.Gunwood;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import io.github.stevezhang123.gunwood.item.GlassesItem;
import io.github.stevezhang123.gunwood.item.PaintSelectorItem;
import io.github.stevezhang123.gunwood.item.ScraperItem;
import io.github.stevezhang123.gunwood.item.SprayBrushItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Gunwood.MODID);

    public static final DeferredItem<Item> SPRAY_BRUSH = ITEMS.registerItem("spray_brush", SprayBrushItem::new, new Item.Properties().durability(GunwoodCommonConfig.DEFAULT_SPRAYER_MAX_DAMAGE));
    public static final DeferredItem<Item> SCRAPER = ITEMS.registerItem("scraper", ScraperItem::new, new Item.Properties().durability(GunwoodCommonConfig.DEFAULT_SCRAPER_MAX_DAMAGE));
    public static final DeferredItem<Item> PAINT_SELECTOR = ITEMS.registerItem("paint_selector", PaintSelectorItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> GLASSES = ITEMS.registerItem("glasses", GlassesItem::new, new Item.Properties().stacksTo(1));

    private ModItems() {
    }
}
