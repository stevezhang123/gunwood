package io.github.stevezhang123.gunwood.item;

import io.github.stevezhang123.gunwood.config.GunwoodCommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class GunwoodToolEnchantmentHelper {
    private GunwoodToolEnchantmentHelper() {
    }

    public static boolean isBookEnchantable(ItemStack book) {
        return GunwoodCommonConfig.ALLOW_MENDING_ON_SPRAYER_AND_SCRAPER.get() || !containsMending(book);
    }

    public static float xpRepairRatio() {
        return GunwoodCommonConfig.ALLOW_MENDING_ON_SPRAYER_AND_SCRAPER.get() ? 1.0F : 0.0F;
    }

    private static boolean containsMending(ItemStack stack) {
        for (Holder<Enchantment> enchantment : EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet()) {
            if (enchantment.is(Enchantments.MENDING)) {
                return true;
            }
        }
        return false;
    }
}
