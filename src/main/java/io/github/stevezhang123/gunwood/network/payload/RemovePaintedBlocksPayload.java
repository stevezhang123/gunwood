package io.github.stevezhang123.gunwood.network.payload;

public record RemovePaintedBlocksPayload(long[] positions) {
    public RemovePaintedBlocksPayload { positions = positions.clone(); }
}
