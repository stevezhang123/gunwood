package io.github.stevezhang123.gunwood.network.payload;

public record AddPaintedBlocksPayload(long[] positions) {
    public AddPaintedBlocksPayload { positions = positions.clone(); }
}
