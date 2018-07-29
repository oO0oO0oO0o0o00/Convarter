package rbq2012.convarter.configguide;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rbq2012.convarter.Constants;
import rbq2012.convarter.R;
import rbq2012.convarter.activities.ActivityFilePicker;

import static rbq2012.convarter.Constants.SPREF_KEY_JSNOTICE;


/**
 * Created by 喵喵喵 on 2018/2/27.
 */

public class FragmentSelJs extends FragmentBase implements View.OnClickListener {

    private View m_root;
    private ScriptsAdapter m_adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_config_seljs, container, false);
        View fab = root.findViewById(R.id.frag_addfile);
        fab.setOnClickListener(this);
        fab = root.findViewById(R.id.frag_addclip);
        fab.setOnClickListener(this);
        this.m_root = root;
        if (getPref().getBoolean(SPREF_KEY_JSNOTICE, true)) {
            AlertDialog dia = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.setup_fjs_tips)
                    .setMessage(getString(R.string.setup_fjs_about, Constants.PATH_MINECRAFTPE_DIR + Constants.FNAME_MINECRAFTPE_SCRIPTS))
                    .setPositiveButton(R.string.global_yes, null)
                    .create();
            dia.show();
            getPref().edit().putBoolean(SPREF_KEY_JSNOTICE, false).apply();
        }
        ListView lview = m_root.findViewById(R.id.list);
        m_adapter = new ScriptsAdapter(lview);
        m_adapter.update();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onContinue() {
        //
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frag_addfile:
                startActivityForResult(new Intent(getActivity(), ActivityFilePicker.class), ActivityFilePicker.REQ_CODE);
                break;
            case R.id.frag_addclip:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivityFilePicker.REQ_CODE:
                if (resultCode != Activity.RESULT_OK) break;
                String path = data.getStringExtra(ActivityFilePicker.INTENT_KEY_FILE);
                do_import(path);
                break;
            default:
                break;
        }
    }

    private void do_import(String path) {
        File original = new File(path);
        if (!original.canRead()) {
            Toast.makeText(getActivity(), R.string.setup_fjs_errcantread, Toast.LENGTH_SHORT).show();
            return;
        }
        File desDir = new File(Environment.getExternalStorageDirectory(), Constants.PATH_MINECRAFTPE_DIR);
        desDir = new File(desDir, Constants.FNAME_MINECRAFTPE_SCRIPTS);
        desDir.mkdirs();
        if (!desDir.isDirectory()) {
            Toast.makeText(getActivity(), R.string.setup_fjs_errdir, Toast.LENGTH_SHORT).show();
            return;
        }
        if (original.getParentFile().equals(desDir)) {
            Toast.makeText(getActivity(), R.string.setup_fjs_errwtf, Toast.LENGTH_SHORT).show();
            return;
        }
        String basename = original.getName();
        basename = basename.substring(0, basename.lastIndexOf(46));
        String ext = ".js";
        File dest = new File(desDir, basename + ext);
        int count = 0;
        while (dest.exists()) {
            dest = new File(desDir, String.format("%1$s(%2$d)%3$s", basename, count, ext));
            count++;
        }
        try {
            FileInputStream is = new FileInputStream(original);
            FileOutputStream os = new FileOutputStream(dest);
            FileChannel ic = is.getChannel();
            FileChannel oc = os.getChannel();
            ic.transferTo(0, ic.size(), oc);
            ic.close();
            oc.close();
            is.close();
            os.close();
            Toast.makeText(getActivity(), R.string.setup_fjs_succ, Toast.LENGTH_SHORT).show();
            m_adapter.update();
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.setup_fjs_errcpy, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        m_adapter.update();
    }

    private class ScriptsAdapter extends BaseAdapter
            implements FileFilter, View.OnClickListener,
            AdapterView.OnItemClickListener, DialogInterface.OnClickListener {

        private List<ScriptInfo> scripts;
        private LayoutInflater inflator;
        private ListView listview;
        private View selected;
        private int selection;

        public ScriptsAdapter(ListView listview) {
            scripts = new ArrayList<>();
            inflator = LayoutInflater.from(getActivity());
            this.listview = listview;
            listview.setAdapter(this);
            listview.setOnItemClickListener(this);
            selection = -1;
            selected = null;
        }

        public void update() {

            //Backup old selection.
            ScriptInfo old = null;
            if (selection >= 0) old = getItem(selection);

            //Clean up.
            scripts.clear();
            selected = null;
            selection = -1;

            //Load scripts from default directory.
            File detect_dir = new File(Environment.getExternalStorageDirectory(),
                    Constants.PATH_MINECRAFTPE_DIR);
            if (detect_dir.isDirectory()) {
                detect_dir = new File(detect_dir, Constants.FNAME_MINECRAFTPE_SCRIPTS);
                if (!detect_dir.isDirectory()) detect_dir.mkdir();
                else {
                    File[] files = detect_dir.listFiles(this);
                    if (files != null) {
                        Arrays.sort(files);
                        for (File f : files) {
                            ScriptInfo info = new ScriptInfo(f);
                            scripts.add(info);
                        }
                    }
                }
            }

            //Recover selection, if possible.
            if (old != null) for (int i = 0, lim = getCount(); i < lim; i++) {
                if (getItem(i).equals(old)) {
                    selection = i;
                    break;
                }
            }

            //Finally.
            setCanContinue(selection != -1);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return scripts.size();
        }

        @Override
        public ScriptInfo getItem(int i) {
            return scripts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflator.inflate(R.layout.entry_config_fjs, null, false);
            LinearLayout ll = (LinearLayout) view;
            ScriptInfo info = getItem(i);
            TextView tv = (TextView) ll.getChildAt(0);
            tv.setText(info.name);
            View v = ll.getChildAt(1);
            v.setOnClickListener(this);
            if (selection == i) {
                selected = view;
                v.setVisibility(View.VISIBLE);
            }
            return view;
        }

        @Override
        public boolean accept(File file) {
            if (!file.isFile()) return false;
            return file.getName().endsWith(".js");
        }

        @Override
        public void onClick(View view) {
            int ind = selection;
            ScriptInfo si = getItem(ind);
            if (si.builtin) return;
            AlertDialog dia = new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.setup_js_delconfirm)
                    .setPositiveButton(android.R.string.yes, this)
                    .setNegativeButton(android.R.string.no, null)
                    .create();
            dia.show();
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (selection == i) return;
            if (selected != null) {
                LinearLayout ll = (LinearLayout) selected;
                ll.getChildAt(1).setVisibility(View.GONE);
            }
            selection = i;
            selected = view;
            ScriptInfo info = getItem(i);
            Bundle config = getConfiguration();
            if (!info.builtin) {
                LinearLayout ll = (LinearLayout) view;
                ll.getChildAt(1).setVisibility(View.VISIBLE);
                listview.setSelection(0);
                config.putString(CONF_JSEXT, getItem(i).name);
                config.putString(CONF_JSIN, null);
            } else {
                config.putString(CONF_JSIN, "meow");
            }
            setCanContinue(true);
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            File file = getItem(selection).file;
            file.delete();
            update();
        }
    }

    private class ScriptInfo {
        private boolean builtin;
        private String name;
        private File file;
        private int id;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ScriptInfo)) return false;
            ScriptInfo another = (ScriptInfo) obj;
            if (builtin != another.builtin) return false;
            if (builtin) return id == another.id;
            return file.equals(another.file);
        }

        public ScriptInfo(File file) {
            builtin = false;
            name = file.getName();
            this.file = file;
            this.id = -1;
        }

        public ScriptInfo(String name, int id) {
            this.builtin = true;
            this.name = name;
            this.file = null;
            this.id = id;
        }

    }
}
