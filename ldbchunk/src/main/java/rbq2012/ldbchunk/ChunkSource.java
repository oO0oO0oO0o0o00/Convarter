package rbq2012.ldbchunk;

import java.io.File;

public final class ChunkSource {

    private long ptr;

    public ChunkSource(File db) {
        ptr = nativeChBegin(db.getAbsolutePath());
    }

    public ChunkSource(String dbpath) {
        ptr = nativeChBegin(dbpath);
    }

    public long getPtr() {
        return ptr;
    }

    public int openDb() {
        if (ptr != 0) return nativeChOpenDb(ptr);
        return -1000;
    }

    public void closeDb() {
        if (ptr != 0) nativeChCloseDb(ptr);
    }

    public void end() {
        nativeChEnd(ptr);
        ptr = 0;
    }

    public Chunk getOrCreateChunk(int xdiv16, int zdiv16, int dim) {
        return new Chunk(nativeChGetOrCreateChunk(ptr, xdiv16, zdiv16, dim));
    }

    public byte[] getRaw(byte[] key) {
        if (ptr != 0) return nativeChGetRaw(ptr, key);
        return null;
    }

    public void putRaw(byte[] key, byte[] value) {
        if (ptr != 0) nativeChPutRaw(ptr, key, value);
    }

    public Iterator iterator() {
        return new Iterator(nativeChIterator(ptr));
    }

    public void voidChunk(int xdiv16, int zdiv16, int dim) {
        if (ptr != 0) nativeChVoidChunk(ptr, xdiv16, zdiv16, dim);
    }

    private static native long nativeChBegin(String dbpath);

    private static native int nativeChOpenDb(long ptr);

    private static native void nativeChCloseDb(long ptr);

    private static native void nativeChEnd(long ptr);

    private static native long nativeChGetOrCreateChunk(long dbptr, int xdiv16, int zdiv16, int dim);

    private static native byte[] nativeChGetRaw(long ptr, byte[] key);

    private static native void nativeChPutRaw(long ptr, byte[] key, byte[] value);

    private static native long nativeChIterator(long ptr);

    private static native void nativeChVoidChunk(long ptr, int xdiv16, int zdiv16, int dim);

    static {
        System.loadLibrary("ldbchunkjni");
    }

}
