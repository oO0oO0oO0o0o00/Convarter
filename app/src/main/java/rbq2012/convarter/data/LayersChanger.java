package rbq2012.convarter.data;

import android.os.Environment;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.util.List;

import rbq2012.convarter.Constants;
import rbq2012.convarter.MCUtils;
import rbq2012.convarter.R;
import rbq2012.ldbchunk.DB;

public final class LayersChanger {

    private File mapdir;
    private FlatWorldLayers.Layers layers_modified;
    private GameMapVersion version;
    private FlatWorldLayers.Layers layers_original;

    public LayersChanger(String mapsubdir) {
        File dir = new File(Environment.getExternalStorageDirectory(), Constants.PATH_MINECRAFTPE_DIR);
        dir = new File(dir, Constants.FNAME_MINECRAFTPE_MAPS);
        mapdir = new File(dir, mapsubdir);
    }

    public int load() {
        File datfile = new File(mapdir, "level.dat");
        if (!datfile.isFile()) return -1;
        LevelDat dat = new LevelDat(datfile);
        dat.load();

        //Load layers
        DB db = new DB(new File(mapdir, "db"));
        version = MCUtils.versionDetect(dat);
        layers_modified = FlatWorldLayers.newFlatWorldLayers(dat, db, version, false);
        if (layers_modified == null) {
            return R.string.editlayers_corrupt;
        } else if (layers_modified instanceof FlatWorldLayers.DummyLayers) {
            return R.string.editlayers_nonflat;
        }
        //if (layers_modified instanceof FlatWorldLayers.PocketLayers) {
        db.end();
        //}
        //layers_modified.load();
        layers_original = layers_modified.clone();
        return 0;
    }

    @Contract(pure = true)
    public boolean isOldVersion() {
        switch (version) {
            case VERSION_1_2:
                return false;
            default:
                return true;
        }
    }

    @Contract(pure = true)
    public List<FlatWorldLayers.Layer> getLayersForControl() {
        return layers_modified.getLayersForControl();
    }

    public boolean save(boolean override) {
        try {
            DB db = new DB(new File(mapdir, "db"));
            db.openDb();
            layers_modified.save();
            //Override existing area.
            if (override && !isOldVersion()) {
                byte[] bytes = layers_original.getLayersForNativeUse();
                db.setLayers(bytes);
                bytes = layers_modified.getLayersForNativeUse();
                db.chflat(bytes);
            }
            db.end();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
