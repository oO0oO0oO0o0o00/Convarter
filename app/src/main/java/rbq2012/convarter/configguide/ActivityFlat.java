package rbq2012.convarter.configguide;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import rbq2012.convarter.R;
import rbq2012.convarter.activities.ActivityModifyFlat;

public final class ActivityFlat extends ActivityBase {
    @Override
    protected void forward(Fragment frag, int identifier) {
        startActivity(new Intent(this, ActivityModifyFlat.class).putExtras(configuration));
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentSelMap frag = new FragmentSelMap();
        frag.enableCreate();
        chpage(frag, 0, getString(R.string.setup_fmaps_title0));
        setContinueBtnFinish();
    }
}
