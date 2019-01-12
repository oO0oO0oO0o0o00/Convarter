package rbq2012.ldbchunk;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by barco on 2018/3/22.
 */

public final class DB {

    static final private int OPCODE_VOIDCHUNK = 1;

    private long ptr;

    ////////
    //

    public DB(String str) {
        ptr = nativeBegin(str);
    }

    public DB(@NonNull @NotNull File file) {
        ptr = nativeBegin(file.getPath());
    }

    ////////
    //Open & end

    public void openDb() {
        nativeOpenDb(ptr);
    }

    public void closeDb() {
        nativeCloseDb(ptr);
    }

    public void end() {
        nativeEnd(ptr);
        ptr = 0;
    }

    ////////
    //Direct leveldb access

    public byte[] get(byte[] key) {
        return nativeGetRaw(ptr, key);
    }

    public void delete(byte[] key) {
    }

    public void put(byte[] key, byte[] value) {
        nativePutRaw(ptr, key, value);
    }

    public Iterator iterator() {
        return new Iterator(nativeIterator(ptr));
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

    public int getBlock(int x, int y, int z, int dimension) {
        return nativeGetBlock(ptr, x, y, z, dimension);
    }

    public void setBlock(int x, int y, int z, int dimension, int block) {
        nativeSetBlock(ptr, x, y, z, dimension, block);
    }

    public int getBlock3(int x, int y, int z, int dimension, int layer) {
        return nativeGetBlock3(ptr, x, y, z, dimension, layer);
    }

    public void setBlock3(int x, int y, int z, int dimension, int layer, int block) {
        nativeSetBlock3(ptr, x, y, z, dimension, layer, block);
    }

    public int voidChunks(int xfrom, int zfrom, int xto, int zto, int dim) {
        return nativeSpecialOperation(ptr, OPCODE_VOIDCHUNK, new int[]{xfrom, zfrom, xto, zto, dim});
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

    private static native long nativeBegin(String str);

    private static native int nativeOpenDb(long ptr);

    private static native int nativeGetTile(long ptr, int x, int y, int z, int dim);

    private static native int nativeGetData(long ptr, int x, int y, int z, int dim);

    private static native void nativeSetTile(long ptr, int x, int y, int z, int dim, int id, int data);

    private static native int nativeGetBlock(long ptr, int x, int y, int z, int dim);

    private static native void nativeSetBlock(long ptr, int x, int y, int z, int dim, int block);

    private static native int nativeGetBlock3(long ptr, int x, int y, int z, int dim, int layer);

    private static native void nativeSetBlock3(long ptr, int x, int y, int z, int dim, int layer, int block);

    private static native int nativeSpecialOperation(long ptr, int opcode, int args[]);

    private static native void nativeSetMaxChunksCount(long ptr, int lim);

    private static native void nativeRegisterLayers(long ptr, byte[] array);

    private static native void nativeCloseDb(long ptr);

    private static native void nativeEnd(long ptr);

    private static native void nativeTest(long ptr);

    private static native void nativeChflat(long ptr, byte[] bnew);

    private static native byte[] nativeGetRaw(long ptr, byte[] key);

    private static native void nativePutRaw(long ptr, byte[] key, byte[] value);

    private static native long nativeIterator(long dbPtr);

    static {
        System.loadLibrary("ldbchunkjni");
    }

}
