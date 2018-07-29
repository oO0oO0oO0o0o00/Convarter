package rbq2012.convarter.test;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import rbq2012.convarter.R;
import rbq2012.convarter.configguide.ActivityBase;

public final class ActivityMapPicker extends ActivityBase {

    public final static int REQ_CODE = 10;

    private View button;

    @Override
    protected void forward(Fragment frag, int identifier) {
        //
    }

    public void confirm(View v) {
        setResult(RESULT_OK, new Intent().putExtras(getConfiguration()));
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act_map_picker);
        button = findViewById(R.id.button);
        button.setEnabled(false);
        setResult(RESULT_CANCELED);
    }

    @Override
    public void setContinueBtnEnabled(boolean enabled) {
        if (null != button) button.setEnabled(enabled);
    }
}
