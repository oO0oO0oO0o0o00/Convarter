package rbq2012.convarter.configguide;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import rbq2012.convarter.R;
import rbq2012.convarter.activities.ActivityRunJs;

import static rbq2012.convarter.Constants.SPREF_KEY_NOASK_JSOTHERS;
import static rbq2012.convarter.Constants.SPREF_PREF;
import static rbq2012.convarter.configguide.FragmentJsothers.PREF_KEY_CACHE_CHUNKS;
import static rbq2012.convarter.configguide.FragmentJsothers.PREF_KEY_OPTIMIZATION;

public class ActivityJs extends ActivityBase {

    private boolean no_page_2;

    @Override
    protected void forward(Fragment frag, int identifier) {
        switch (identifier) {
            case 0:
                chpage(new FragmentSelJs(), 1, getString(R.string.setup_fjs_title));
                no_page_2 = getSharedPreferences(SPREF_PREF, MODE_PRIVATE).getBoolean(SPREF_KEY_NOASK_JSOTHERS, false);
                if (no_page_2) {
                    setContinueBtnFinish();
                    configuration.putInt(PREF_KEY_CACHE_CHUNKS, 64);
                    configuration.putInt(PREF_KEY_OPTIMIZATION, 2);
                }
                break;
            case 1:
                if (!no_page_2) {
                    chpage(new FragmentJsothers(), 2, getString(R.string.setup_fjso_title));
                    setContinueBtnFinish();
                    break;
                }
            case 2:
                currentFrag().onContinue();
                startActivity(new Intent(this, ActivityRunJs.class).putExtras(configuration));
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chpage(new FragmentSelMap(), 0, getString(R.string.setup_fmaps_title0));
        setContinueBtnContinue();
    }
}
