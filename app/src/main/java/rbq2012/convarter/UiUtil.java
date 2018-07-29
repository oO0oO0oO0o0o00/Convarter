package rbq2012.convarter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

public final class UiUtil {

    public static void toast(Context ctx, String str) {
        Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context ctx, @StringRes int res) {
        Toast.makeText(ctx, res, Toast.LENGTH_SHORT).show();
    }

}
