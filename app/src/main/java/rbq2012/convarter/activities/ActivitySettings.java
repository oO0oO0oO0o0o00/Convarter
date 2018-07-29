package rbq2012.convarter.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import rbq2012.convarter.R;
import rbq2012.convarter.UiUtil;

import static rbq2012.convarter.Constants.SPREF_KEY_NOASK_JSOTHERS;
import static rbq2012.convarter.Constants.SPREF_KEY_JSNOTICE;
import static rbq2012.convarter.Constants.SPREF_KEY_SHOW_EDIT_LAYERS_HELP;
import static rbq2012.convarter.Constants.SPREF_NEWCOMER_NOTICE;
import static rbq2012.convarter.Constants.SPREF_PREF;

public final class ActivitySettings extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    SharedPreferences spref;

    public void onClickResetNoAsks(View view) {
        spref.edit().putBoolean(SPREF_KEY_JSNOTICE, true)
                .putBoolean(SPREF_KEY_NOASK_JSOTHERS, false)
                .putBoolean(SPREF_NEWCOMER_NOTICE, true)
                .putBoolean(SPREF_KEY_SHOW_EDIT_LAYERS_HELP, true)
                .apply();
        UiUtil.toast(this, R.string.setup_finish);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        spref = getSharedPreferences(SPREF_PREF, MODE_PRIVATE);
        CheckBox cbox = findViewById(R.id.checkbox);
        cbox.setChecked(spref.getBoolean(SPREF_KEY_NOASK_JSOTHERS, false));
        cbox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.checkbox:
                spref.edit().putBoolean(SPREF_KEY_NOASK_JSOTHERS, !b).apply();
                break;
        }
    }
}
