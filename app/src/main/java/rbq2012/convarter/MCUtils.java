package rbq2012.convarter;

import org.spout.nbt.IntTag;

public final class MCUtils {

    static public GameMapVersion versionDetect(LevelDat dat) {
        GameMapVersion version;
        int stver = ((IntTag) dat.getRoot().getValue().get("StorageVersion")).getValue();
        if (4 >= stver) version = GameMapVersion.VERSION_POCKET;
        else if (7 <= stver) version = GameMapVersion.VERSION_BEDROCK;
        else if (dat.getRoot().getValue().containsKey("FlatWorldLayers"))
            version = GameMapVersion.VERSION_BEDROCK;
        else version = GameMapVersion.VERSION_POCKET;
        return version;
    }

}
