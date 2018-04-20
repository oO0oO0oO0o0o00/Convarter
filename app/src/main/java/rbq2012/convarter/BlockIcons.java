package rbq2012.convarter;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public final class BlockIcons {

    static int users = 0;
    static private BitmapDrawable[] icons = new BitmapDrawable[256];

    static public synchronized void load(Resources res) {
        users++;
        if (users != 1) return;
        AssetManager manager = res.getAssets();
        for (int i = 0; i < 256; i++) {
            try {
                InputStream is = manager.open("icons/" + i + ".png", AssetManager.ACCESS_BUFFER);
                BitmapDrawable bd = new BitmapDrawable(res, is);
                is.close();
                icons[i] = bd;
            } catch (IOException e) {//Not exist.
            }
        }
    }

    static public synchronized void release() {
        if (users == 0) return;
        users--;
        if (users != 0) return;
        for (int i = 0; i < 256; i++) {
            icons[i] = null;
        }
        System.gc();
    }

    @Nullable
    static public BitmapDrawable get(int i) {
        return icons[i];
    }

}
