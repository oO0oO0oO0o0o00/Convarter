package rbq2012.ldbchunk;

import java.io.File;

/**
 * Created by barco on 2018/3/22.
 */

public final class DB {

    private long ptr;
    private String dir;

    ////////
    //

    public DB(String str) {
        ptr = 0;
        dir = str;
    }

    public DB(File file) {
        ptr = 0;
        dir = file.getPath();
    }

    ////////
    //Open & close

    public void open() {
        ptr = nativeOpen(dir);
    }

    public void close() {
        nativeClose(ptr);
        ptr = 0;
    }

    ////////
    //Direct leveldb access

    public byte[] get(byte[] key) {
        return new byte[]{0};
    }

    public void delete(byte[] key) {
        return;
    }

    public void put(byte[] key, byte[] value) {
        return;
    }

    public Iterator iterator() {
        return new Iterator();
    }

    ////////
    //Mcpe gamemap access setup

    public void setLayers(byte[] arr) {
        nativeRegisterLayers(ptr, arr);
    }

    public void setMaxChunksCount(int limit) {
        nativeSetMaxChunksCount(ptr, limit);
    }

    ////////
    //Mcpe manipulations

    public int getTile(int x, int y, int z, int dimension) {
        return nativeGetTile(ptr, x, y, z, dimension);
    }

    public int getData(int x, int y, int z, int dimension) {
        return nativeGetData(ptr, x, y, z, dimension);
    }

    public void setTile(int x, int y, int z, int dimension, int id, int data) {
        nativeSetTile(ptr, x, y, z, dimension, id, data);
    }

    public void setData(int x, int y, int z, int dimension, int data) {
        nativeSetData(ptr, x, y, z, dimension, data);
    }

    public void chflat(byte[] layers) {
        nativeChflat(ptr, layers);
    }

    ////////
    //Test

    public void test() {
        nativeTest(ptr);
    }

    ////////
    //Interface to native part

    private static native long nativeOpen(String str);

    private static native int nativeGetTile(long ptr, int x, int y, int z, int dim);

    private static native int nativeGetData(long ptr, int x, int y, int z, int dim);

    private static native void nativeSetTile(long ptr, int x, int y, int z, int dim, int id, int data);

    private static native void nativeSetData(long ptr, int x, int y, int z, int dim, int data);

    private static native void nativeSetMaxChunksCount(long ptr, int lim);

    private static native void nativeRegisterLayers(long ptr, byte[] array);

    private static native void nativeClose(long ptr);

    private static native void nativeTest(long ptr);

    private static native void nativeChflat(long ptr, byte[] bnew);

    static {
        System.loadLibrary("ldbchunkjni");
    }

}
