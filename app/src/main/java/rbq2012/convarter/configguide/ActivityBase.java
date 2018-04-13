package rbq2012.convarter.configguide;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Stack;

import rbq2012.convarter.R;

/**
 * Created by 喵喵喵 on 2018/2/26.
 */

public abstract class ActivityBase extends AppCompatActivity {

    final static public String SPREF_NAME_SETUP = "configguide";

    abstract protected void forward(Fragment frag, int identifier);

    //private ViewGroup m_frame=null;
    private FragmentInfo m_fragment = null;
    private Stack<FragmentInfo> m_fragments = null;
    private Button m_btn = null;
    private EvHan m_evhan = null;

    protected Bundle configuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //m_frame=(ViewGroup)findViewById(R.id.setup_frame);
        m_fragments = new Stack<>();
        m_evhan = new EvHan();
        m_btn = findViewById(R.id.btnnext);
        m_btn.setOnClickListener(m_evhan);
        Button bcancel = findViewById(R.id.btncancel);
        bcancel.setOnClickListener(m_evhan);
        configuration = new Bundle();
    }

    @Override
    final public void onBackPressed() {
        if (m_fragments.isEmpty()) {
            finish();
            return;
        }

        FragmentInfo frag = m_fragments.pop();
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.remove(m_fragment.fragment);
        trans.add(R.id.frame, frag.fragment);
        trans.commit();
        m_fragment = frag;
        setTitle(frag.def_title);
        setContinueBtnContinue();
    }

    protected void chpage(FragmentBase frag, int identifier, String defaultTitle) {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        if (frag == null) {
            finish();
            return;
        }
        FragmentInfo old = m_fragment;
        if (old != null) {
            trans.remove(old.fragment);
            m_fragments.push(old);
        }
        trans.add(R.id.frame, frag);
        trans.commit();
        m_fragment = new FragmentInfo(identifier, defaultTitle, frag);
        setTitle(defaultTitle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        m_fragment.fragment.onActivityResult(requestCode, resultCode, data);
    }

    public void setContinueBtnEnabled(boolean enabled) {
        m_btn.setEnabled(enabled);
    }

    public void setContinueBtnContinue() {
        m_btn.setText(R.string.setup_nextstep);
    }

    public void setContinueBtnFinish() {
        m_btn.setText(R.string.setup_finish);
    }

    public void setTitle(String text) {
        if (text != null)
            getSupportActionBar().setTitle(text);
    }

    public Bundle getConfiguration() {
        return configuration;
    }

    private class FragmentInfo {

        public int step;
        public String def_title;
        public FragmentBase fragment;

        public FragmentInfo(int step, String def_title, FragmentBase fragment) {
            this.step = step;
            this.def_title = def_title;
            this.fragment = fragment;
        }

    }

    private class EvHan implements View.OnClickListener, DialogInterface.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view == m_btn) {
                FragmentInfo fi = m_fragment;
                forward(fi.fragment, fi.step);
                return;
            }
            if (m_fragments.isEmpty()) {
                finish();
                return;
            }
            AlertDialog dia = new AlertDialog.Builder(ActivityBase.this)
                    .setMessage(R.string.setup_cancelconfirm)
                    .setPositiveButton(android.R.string.ok, this).create();
            dia.show();
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            finish();
        }
    }

    protected FragmentBase currentFrag() {
        return m_fragment.fragment;
    }

}
