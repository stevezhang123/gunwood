package io.github.stevezhang123.gunwood.item;

import io.github.stevezhang123.gunwood.compat.curios.GunwoodCuriosCompat;
import io.github.stevezhang123.gunwood.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public final class GunwoodGlassesHelper {
    private GunwoodGlassesHelper() {
    }

    public static boolean isWearingGunwoodGlasses(Player player) {
        return player != null
                && (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.GLASSES.get())
                || GunwoodCuriosCompat.hasGlasses(player));
    }
}
