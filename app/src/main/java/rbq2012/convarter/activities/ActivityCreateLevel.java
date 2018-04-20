package rbq2012.convarter.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.LongTag;
import org.spout.nbt.StringTag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

import rbq2012.convarter.Constants;
import rbq2012.convarter.FileUtil;
import rbq2012.convarter.LevelDat;
import rbq2012.convarter.R;

public final class ActivityCreateLevel extends AppCompatActivity {

    static final private Pattern pat_name = Pattern.compile("[\\x00-\\x1f\n\r]");

    static final private Pattern pat_fname = Pattern.compile("[\\\\\\/\\?\\*\\|]");
    private static final String FNAME_LEVEL_DAT = "level.dat";
    private static final String FNAME_NOTICE_TXT = "created_by_convarter.txt";
    public static final String INTENT_KEY_PATH = "path";

    public void save(View v) {
        EditText tv = findViewById(R.id.text);
        String str = tv.getText().toString();
        if (str == null || str.equals("")) str = tv.getHint().toString();
        if (str == null || str.equals("")) return;
        str = pat_name.matcher(str).replaceAll("");
        String fname = pat_fname.matcher(str).replaceAll("");
        File dir = new File(Environment.getExternalStorageDirectory(), Constants.PATH_MINECRAFTPE_DIR);
        dir = new File(dir, Constants.FNAME_MINECRAFTPE_MAPS);
        dir.mkdirs();
        File subdir = new File(dir, fname);
        while (subdir.exists()) {
            fname += "_";
            subdir = new File(dir, fname);
        }
        if (!subdir.mkdir()) return;
        FileUtil.extractAsset(getAssets(), FNAME_LEVEL_DAT, AssetManager.ACCESS_BUFFER, new File(subdir, FNAME_LEVEL_DAT));
        FileUtil.extractAsset(getAssets(), FNAME_NOTICE_TXT, AssetManager.ACCESS_BUFFER, new File(subdir, FNAME_NOTICE_TXT));

        //Save name to both txt and dat, save time to dat.
        FileUtil.writeTextFile(new File(subdir, "levelname.txt"), str);
        LevelDat dat = new LevelDat(new File(subdir, "level.dat"));
        dat.load();
        CompoundMap map = dat.getRoot().getValue();
        map.put(new StringTag("LevelName", str));
        map.put(new LongTag("LastPlayed", System.currentTimeMillis() / 1000));
        dat.save();
        new File(subdir, "db").mkdir();
        setResult(RESULT_OK, new Intent().putExtra(INTENT_KEY_PATH, fname));
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlevel);
        setResult(RESULT_CANCELED);
    }
}
