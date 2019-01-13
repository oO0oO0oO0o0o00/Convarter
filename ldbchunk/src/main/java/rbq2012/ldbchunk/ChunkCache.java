package rbq2012.ldbchunk;

import android.util.LruCache;

public final class ChunkCache extends LruCache<ChunkKey, Chunk> {

    private ChunkSource mChunkSource;

    public ChunkCache(int maxSize, ChunkSource chunkSource) {
        super(maxSize);
        mChunkSource = chunkSource;
    }

    @Override
    protected Chunk create(ChunkKey key) {
        return mChunkSource.getOrCreateChunk(key.xdiv16, key.zdiv16, key.dimension);
    }

    @Override
    protected void entryRemoved(boolean evicted, ChunkKey key, Chunk oldValue, Chunk newValue) {
        oldValue.close();
    }

    public void setBlock(int x, int y, int z, int dim, int block) {
        get(new ChunkKey(x >> 4, z >> 4, dim))
                .setBlock(x & 0xf, y, z & 0xf, block);
    }

    public int getBlock(int x, int y, int z, int dim) {
        return get(new ChunkKey(x >> 4, z >> 4, dim))
                .getBlock(x & 0xf, y, z & 0xf);
    }

    public void setBlock3(int x, int y, int z, int dim, int layer, int block) {
        get(new ChunkKey(x >> 4, z >> 4, dim))
                .setBlock3(x & 0xf, y, z & 0xf, layer, block);
    }

    public int getBlock3(int x, int y, int z, int dim, int layer) {
        return get(new ChunkKey(x >> 4, z >> 4, dim))
                .getBlock3(x & 0xf, y, z & 0xf, layer);
    }
}
