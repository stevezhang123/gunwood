package io.github.stevezhang123.gunwood.network.payload;

public record SyncPaintedBlocksPayload(long[] positions) {
    public SyncPaintedBlocksPayload { positions = positions.clone(); }
}
