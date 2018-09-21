package rbq2012.convarter.configguide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import rbq2012.convarter.MCUtils;
import rbq2012.convarter.R;

import static rbq2012.convarter.Constants.SPREF_DEF_JSCACHE;
import static rbq2012.convarter.Constants.SPREF_DEF_JSLOADFLAT;
import static rbq2012.convarter.Constants.SPREF_DEF_JSOPT;
import static rbq2012.convarter.Constants.SPREF_KEY_JSCACHE;
import static rbq2012.convarter.Constants.SPREF_KEY_JSLOADFLAT;
import static rbq2012.convarter.Constants.SPREF_KEY_JSOPT;
import static rbq2012.convarter.Constants.SPREF_KEY_NOASK_JSOTHERS;

public final class FragmentJsothers extends FragmentBase implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    final static public String PREF_KEY_CACHE_CHUNKS = "cache_chunks_max";
    final static public String PREF_KEY_OPTIMIZATION = "optimization_javascript";
    final static public String PREF_KEY_GENERATE_FLAT_LAYERS = "gen_flat";

    private TextView m_tv_number, m_tv_level;
    private SeekBar m_sbar, m_sbar2;
    private CheckBox m_chk, m_chk_genflat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_config_jsadv, null, false);
        m_tv_number = root.findViewById(R.id.text);
        m_tv_level = root.findViewById(R.id.text2);
        m_sbar = root.findViewById(R.id.seek);
        m_sbar.setOnSeekBarChangeListener(this);
        m_sbar2 = root.findViewById(R.id.seek2);
        m_sbar2.setOnSeekBarChangeListener(this);
        m_sbar.setProgress(48);
        m_sbar2.setProgress(3);
        m_chk = root.findViewById(R.id.checkbox);
        m_chk_genflat = root.findViewById(R.id.checkbox1);
        m_chk_genflat.setOnCheckedChangeListener(this);

        SharedPreferences prefs = getPref();
        m_sbar.setProgress(prefs.getInt(SPREF_KEY_JSCACHE, SPREF_DEF_JSCACHE));
        m_sbar2.setProgress(prefs.getInt(SPREF_KEY_JSOPT, SPREF_DEF_JSOPT));
        m_chk_genflat.setChecked(prefs.getBoolean(SPREF_KEY_JSLOADFLAT, SPREF_DEF_JSLOADFLAT));
        return root;
    }

    @Override
    public void onContinue() {
        getPref().edit()
                .putBoolean(SPREF_KEY_NOASK_JSOTHERS, m_chk.isChecked())
                .putInt(SPREF_KEY_JSCACHE, m_sbar.getProgress())
                .putInt(SPREF_KEY_JSOPT, m_sbar2.getProgress())
                .putBoolean(SPREF_KEY_JSLOADFLAT, m_chk_genflat.isChecked())
                .apply();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seek:
                i = MCUtils.translateCacheValue(i);
                m_tv_number.setText(Integer.toString(i));
                getConfiguration().putInt(PREF_KEY_CACHE_CHUNKS, i);
                break;
            case R.id.seek2:
                i = MCUtils.translateOptimizationLevel(i);
                if (i == -1) m_tv_level.setText(R.string.setup_fjso_optnoopt);
                else m_tv_level.setText(getString(R.string.setup_fjso_optlvl, i));
                getConfiguration().putInt(PREF_KEY_OPTIMIZATION, i);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        getConfiguration().putBoolean(PREF_KEY_GENERATE_FLAT_LAYERS, b);
    }
}
