package rbq2012.convarter.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;

import rbq2012.convarter.R;
import rbq2012.convarter.configguide.FragmentBase;

import static rbq2012.convarter.configguide.FragmentJsothers.PREF_KEY_NOASK_JSOTHERS;
import static rbq2012.convarter.configguide.FragmentSelJs.SPREF_KEY_JSNOTICE;

public final class ActivitySettings extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    SharedPreferences spref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        spref = getSharedPreferences(FragmentBase.SPREF_PREF, MODE_PRIVATE);
        CheckBox cbox = findViewById(R.id.checkbox);
        cbox.setChecked(!spref.getBoolean(SPREF_KEY_JSNOTICE, false));
        cbox.setOnCheckedChangeListener(this);
        cbox = findViewById(R.id.checkbox1);
        cbox.setChecked(spref.getBoolean(PREF_KEY_NOASK_JSOTHERS, false));
        cbox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.checkbox:
                spref.edit().putBoolean(SPREF_KEY_JSNOTICE, !b).apply();
                break;
            case R.id.checkbox1:
                spref.edit().putBoolean(PREF_KEY_NOASK_JSOTHERS, b).apply();
                break;
        }
    }
}
