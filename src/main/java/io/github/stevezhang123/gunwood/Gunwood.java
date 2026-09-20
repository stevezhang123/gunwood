package io.github.stevezhang123.gunwood;

import com.mojang.logging.LogUtils;
import io.github.stevezhang123.gunwood.compat.curios.GunwoodCuriosClientCompat;
import io.github.stevezhang123.gunwood.config.GunwoodClientConfig;
import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import io.github.stevezhang123.gunwood.network.ModNetworking;
import io.github.stevezhang123.gunwood.paint.PaintedBlockSyncEvents;
import io.github.stevezhang123.gunwood.registry.ModCreativeTabs;
import io.github.stevezhang123.gunwood.registry.ModItems;
import io.github.stevezhang123.gunwood.selection.GunwoodSelectionEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(Gunwood.MODID)
public class Gunwood {
    public static final String MODID = "gunwood";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(
            new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));

    public Gunwood() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::addCreative);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modBus);
        MinecraftForge.EVENT_BUS.addListener(PaintedBlockSyncEvents::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(PaintedBlockSyncEvents::onPlayerChangedDimension);
        MinecraftForge.EVENT_BUS.addListener(GunwoodSelectionEvents::onPlayerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(GunwoodSelectionEvents::onPlayerRespawn);
        MinecraftForge.EVENT_BUS.addListener(GunwoodSelectionEvents::onPlayerChangedDimension);
        registerOptionalCompat("ftbultimine", "io.github.stevezhang123.gunwood.compat.ftbultimine.GunwoodFTBUltimineCompat");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GunwoodCommonConfig.SPEC, "gunwood-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GunwoodClientConfig.SPEC, "gunwood-client.toml");
    }

    private static void registerOptionalCompat(String modId, String className) {
        if (!ModList.get().isLoaded(modId)) return;
        try {
            Class.forName(className).getMethod("register").invoke(null);
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to register optional compat for {}", modId, exception);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ModNetworking.register();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(GunwoodCuriosClientCompat::registerInvisibleGlassesRenderer);
        }
    }
}
