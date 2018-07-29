package rbq2012.ldbchunk;

public final class Iterator {

    private long ptr;

    Iterator(long ptr) {
        this.ptr = ptr;
    }

    public void seekToFirst() {
        nativeSeekToFirst(ptr);
    }

    public boolean isValid() {
        return nativeValid(ptr);
    }

    public void next() {
        nativeNext(ptr);
    }

    public byte[] getKey() {
        return nativeKey(ptr);
    }

    public byte[] getValue() {
        return nativeValue(ptr);
    }

    public void close() {
        nativeDestroy(ptr);
    }

    private static native void nativeDestroy(long ptr);

    private static native void nativeSeekToFirst(long ptr);

    private static native void nativeSeekToLast(long ptr);

    private static native void nativeSeek(long ptr, byte[] key);

    private static native boolean nativeValid(long ptr);

    private static native void nativeNext(long ptr);

    private static native void nativePrev(long ptr);

    private static native byte[] nativeKey(long dbPtr);

    private static native byte[] nativeValue(long dbPtr);
}
