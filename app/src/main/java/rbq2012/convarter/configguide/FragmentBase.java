package rbq2012.convarter.configguide;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by 喵喵喵 on 2018/2/27.
 */

public abstract class FragmentBase extends Fragment {

    final static public String CONF_MAPDIR = "map_dir";
    final static public String CONF_JSEXT = "js_ext_fname";
    final static public String CONF_JSIN = "js_in_name";
    final static public String SPREF_PREF = "config_pref";

    private ActivityBase activity = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity act = getActivity();
        if (act instanceof ActivityBase) activity = (ActivityBase) act;
        else
            throw new RuntimeException("This fragment was designed to be used within ConfigGuide.");
    }

    abstract public void onContinue();

    protected void setCanContinue(boolean enabled) {
        activity.setContinueBtnEnabled(enabled);
    }

    protected Bundle getConfiguration() {
        return activity.getConfiguration();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public SharedPreferences getPref() {
        if (activity == null) return null;
        return activity.getSharedPreferences(SPREF_PREF, Context.MODE_PRIVATE);
    }

}
