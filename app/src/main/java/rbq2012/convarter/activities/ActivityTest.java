package rbq2012.convarter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.UnsupportedEncodingException;

import rbq2012.convarter.MCUtils;
import rbq2012.convarter.R;
import rbq2012.convarter.UiUtil;
import rbq2012.convarter.configguide.FragmentBase;
import rbq2012.convarter.test.ActivityMapPicker;
import rbq2012.ldbchunk.DB;
import rbq2012.ldbchunk.Iterator;

public final class ActivityTest extends AppCompatActivity {

    private TextView logcat;
    private StringBuilder logtxt;

    public void fun_iterate_db(View v) {
        startActivityForResult(new Intent(this, ActivityMapPicker.class), ActivityMapPicker.REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivityMapPicker.REQ_CODE:
                if (resultCode != RESULT_OK) break;
                do_iterate_db(MCUtils.gameMapDir(data.getStringExtra(FragmentBase.CONF_MAPDIR)));
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        logcat = findViewById(R.id.text);
        logtxt = new StringBuilder(">>> These things are only for those intends to research about"
                + "Minecraft's game map format. \n");
        logcat.setText(logtxt.toString());
    }

    private void log(String txt) {
        logtxt.append(">>> ");
        logtxt.append(txt);
        logtxt.append("\n");
        logcat.setText(logtxt);
    }

    private void loga(String txt) {
        logtxt.append(">>> ");
        logtxt.append(txt);
        logtxt.append("\n");
    }

    private void lupdate() {
        logcat.setText(logtxt);
    }

    private String dumpBytes(byte[] bytes) {
        char[] table = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int h = b;
            h = (h >> 4) & 0xf;
            sb.append(table[h]);
            sb.append(table[b & 0xf]);
        }
        return sb.toString();
    }

    public void do_iterate_db(File mapdir) {
        DB db = new DB(new File(mapdir, "db"));
        db.open();
        Iterator iter = db.iterator();
        byte[] k = null;
        for (iter.seekToFirst(); iter.isValid(); iter.next()) {
            byte[] key = iter.getKey();
            loga(dumpBytes(key));
            //It's hard to distinguish between string key and chunk key.
            //This waorkaround would fit most applications.
            if (!(
                    (key.length == 9 || key.length == 10)
                            && (key[0] < 0x10 && key[0] > -0x10)
            )) {
                loga(new String(key));
                loga(dumpBytes(iter.getValue()));
                if (new String(key).startsWith("game")) k = key;
            } //else k = key;
        }
        loga("aaaa");
        if (k != null) loga(dumpBytes(db.get(k)));
        lupdate();
        db.close();
    }
}
