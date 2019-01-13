package rbq2012.ldbchunk;

public final class ChunkKey {

    public int xdiv16;
    public int zdiv16;
    public int dimension;

    public ChunkKey(int xdiv16, int zdiv16, int dimension) {
        this.xdiv16 = xdiv16;
        this.zdiv16 = zdiv16;
        this.dimension = dimension;
    }

    @Override
    public int hashCode() {
        return (xdiv16 << 4) | (zdiv16 << 2) | dimension;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChunkKey)) return false;
        ChunkKey ano = (ChunkKey) obj;
        return xdiv16 == ano.xdiv16 && zdiv16 == ano.zdiv16 && dimension == ano.dimension;
    }
}
