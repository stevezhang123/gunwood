package io.github.stevezhang123.gunwood.registry;

import io.github.stevezhang123.gunwood.Gunwood;
import io.github.stevezhang123.gunwood.item.ChunkScrapeBrushItem;
import io.github.stevezhang123.gunwood.item.GlassesItem;
import io.github.stevezhang123.gunwood.item.ScraperItem;
import io.github.stevezhang123.gunwood.item.SprayBrushItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Gunwood.MODID);

    public static final DeferredItem<Item> SPRAY_BRUSH = ITEMS.registerItem("spray_brush", SprayBrushItem::new, new Item.Properties());
    public static final DeferredItem<Item> SCRAPER = ITEMS.registerItem("scraper", ScraperItem::new, new Item.Properties());
    public static final DeferredItem<Item> CHUNK_SCRAPE_BRUSH = ITEMS.registerItem("chunk_scrape_brush", ChunkScrapeBrushItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> INVISIBLE_PAINT = registerSimpleItem("invisible_paint");
    public static final DeferredItem<Item> GLASSES = ITEMS.registerItem("glasses", GlassesItem::new, new Item.Properties().stacksTo(1));

    private ModItems() {
    }

    private static DeferredItem<Item> registerSimpleItem(String name) {
        return ITEMS.registerSimpleItem(name, new Item.Properties());
    }
}
