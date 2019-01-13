package rbq2012.ldbchunk;

public class Chunk {

    private long ptr;

    public Chunk(long ptr) {
        this.ptr = ptr;
    }

    public void setBlock(int x, int y, int z, int block) {
        if (ptr != 0) nativeChSetBlock(ptr, x, y, z, block);
    }

    public int getBlock(int x, int y, int z) {
        if (ptr != 0) return nativeChGetBlock(ptr, x, y, z);
        return 0;
    }

    public void setBlock3(int x, int y, int z, int layer, int block) {
        if (ptr != 0) nativeChSetBlock3(ptr, x, y, z, layer, block);
    }

    public int getBlock3(int x, int y, int z, int layer) {
        if (ptr != 0) return nativeChGetBlock3(ptr, x, y, z, layer);
        return 0;
    }

    public int save() {
        if (ptr != 0) return nativeChSave(ptr);
        return 0;
    }

    public int saveTo(ChunkSource source) {
        if (ptr != 0) return nativeChSaveTo(ptr, source.getPtr());
        return 0;
    }

    public int close() {
        if (ptr != 0) {
            int ret = nativeChClose(ptr);
            ptr = 0;
            return ret;
        }
        return 0;
    }

    public void discard() {
        if (ptr != 0) nativeChDiscard(ptr);
        ptr = 0;
    }

    private static native void nativeChSetBlock(long ptr, int x, int y, int z, int block);

    private static native int nativeChGetBlock(long ptr, int x, int y, int z);

    private static native void nativeChSetBlock3(long ptr, int x, int y, int z, int layer, int block);

    private static native int nativeChGetBlock3(long ptr, int x, int y, int z, int layer);

    private static native int nativeChSave(long ptr);

    private static native int nativeChSaveTo(long ptr, long chsptr);

    private static native int nativeChClose(long ptr);

    private static native void nativeChDiscard(long ptr);
}
