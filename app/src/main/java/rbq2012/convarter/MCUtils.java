package rbq2012.convarter;

import android.os.Environment;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.spout.nbt.IntTag;

import java.io.File;

import rbq2012.convarter.data.GameMapVersion;
import rbq2012.convarter.data.LevelDat;

public final class MCUtils {

    static public GameMapVersion versionDetect(LevelDat dat) {
        GameMapVersion version;
        int stver = ((IntTag) dat.getRoot().getValue().get("StorageVersion")).getValue();
        if (4 >= stver) version = GameMapVersion.VERSION_0_15;
        else if (5 == stver) version = GameMapVersion.VERSION_1_0;
        else if (7 <= stver) version = GameMapVersion.VERSION_1_2;
        else if (dat.getRoot().getValue().containsKey("FlatWorldLayers"))
            version = GameMapVersion.VERSION_1_2;
        else version = GameMapVersion.VERSION_0_15;
        return version;
    }

    static private File gameMapRoot() {
        File f = new File(Environment.getExternalStorageDirectory(), Constants.PATH_MINECRAFTPE_DIR);
        f = new File(f, Constants.FNAME_MINECRAFTPE_MAPS);
        return f;
    }

    @NonNull
    static public File gameMapDir(String name) {
        return new File(gameMapRoot(), name);
    }

    @Contract(pure = true)
    static public int translateCacheValue(int val) {
        val += 7;
        int ret = 1;
        for (int i = 0; i < val; i++) {
            ret *= 2;
        }
        return ret;
    }

    @Contract(pure = true)
    static public int translateOptimizationLevel(int val) {
        return val - 1;
    }

}
