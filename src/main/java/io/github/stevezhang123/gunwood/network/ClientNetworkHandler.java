package io.github.stevezhang123.gunwood.network;

import io.github.stevezhang123.gunwood.client.ClientPaintedBlockCache;
import io.github.stevezhang123.gunwood.network.payload.*;

public final class ClientNetworkHandler {
    private ClientNetworkHandler() {}

    public static void handle(Object packet) {
        if (packet instanceof SyncPaintedBlocksPayload sync) ClientPaintedBlockCache.replaceAll(sync.positions());
        else if (packet instanceof AddPaintedBlockPayload add) ClientPaintedBlockCache.add(add.pos());
        else if (packet instanceof AddPaintedBlocksPayload add) ClientPaintedBlockCache.addAll(add.positions());
        else if (packet instanceof RemovePaintedBlockPayload remove) ClientPaintedBlockCache.remove(remove.pos());
        else if (packet instanceof RemovePaintedBlocksPayload remove) ClientPaintedBlockCache.removeAll(remove.positions());
    }
}
