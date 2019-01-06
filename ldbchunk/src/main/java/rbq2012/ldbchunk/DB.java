package rbq2012.ldbchunk;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by barco on 2018/3/22.
 */

public final class DB {

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
        return;
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

    private static native long nativeBegin(String str);

    private static native int nativeOpenDb(long ptr);

    private static native int nativeGetTile(long ptr, int x, int y, int z, int dim);

    private static native int nativeGetData(long ptr, int x, int y, int z, int dim);

    private static native void nativeSetTile(long ptr, int x, int y, int z, int dim, int id, int data);

    private static native void nativeSetData(long ptr, int x, int y, int z, int dim, int data);

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
