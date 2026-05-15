package io.github.stevezhang123.gunwood.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;

public final class GunwoodFTBUltimineCompat {
    private GunwoodFTBUltimineCompat() {
    }

    public static void register() {
        RegisterRightClickHandlerEvent.REGISTER.register(dispatcher ->
                dispatcher.registerHandler(GunwoodUltimineRightClickHandler.INSTANCE)
        );
    }
}
