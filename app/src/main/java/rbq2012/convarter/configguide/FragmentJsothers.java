package rbq2012.convarter.configguide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import rbq2012.convarter.R;

public final class FragmentJsothers extends FragmentBase implements SeekBar.OnSeekBarChangeListener {

    final static public String PREF_KEY_CACHE_CHUNKS = "cache_chunks_max";
    final static public String PREF_KEY_NOASK_JSOTHERS = "no_ask_js_others";

    private TextView m_tv_number;
    private SeekBar m_sbar;
    private CheckBox m_chk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_config_jsothers, null, false);
        m_tv_number = root.findViewById(R.id.text);
        m_sbar = root.findViewById(R.id.seek);
        m_sbar.setOnSeekBarChangeListener(this);
        m_sbar.setProgress(64);
        m_chk = root.findViewById(R.id.checkbox);
        return root;
    }

    @Override
    public void onContinue() {
        if (m_chk.isChecked()) {
            getPref().edit().putBoolean(PREF_KEY_NOASK_JSOTHERS, true).apply();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        m_tv_number.setText(Integer.toString(i));
        getConfiguration().putInt(PREF_KEY_CACHE_CHUNKS, i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}
