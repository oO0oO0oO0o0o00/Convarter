package rbq2012.convarter.configguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.stream.NBTInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import rbq2012.convarter.R;
import rbq2012.convarter.activities.ActivityCreateLevel;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.view.View.GONE;
import static rbq2012.convarter.Constants.*;

/**
 * Created by 喵喵喵 on 2018/2/27.
 */

public class FragmentSelMap extends FragmentBase {

    static private final int REQ_CODE_CREATELEVEL = 2012;

    private GameMapsListAdapter m_list_adapter = null;
    private boolean m_first_resume = true;
    private boolean m_allow_create = false;

    public void enableCreate() {
        m_allow_create = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_config_selmap, container, false);
        m_list_adapter = new GameMapsListAdapter(root);
        m_list_adapter.update();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (m_first_resume) {
            m_first_resume = false;
            return;
        }
        m_list_adapter.update();
    }

    @Override
    public void onContinue() {
        //
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_CREATELEVEL:
                if (resultCode != Activity.RESULT_OK) break;
                String path = data.getStringExtra(ActivityCreateLevel.INTENT_KEY_PATH);
                m_list_adapter.setSeldir(path);
                getConfiguration().putString(CONF_MAPDIR, path);
                break;
        }
    }

    private class GameMapsAdapter extends RecyclerView.Adapter<GameMapsAdapter.ViewHolder> {

        private List<MapInfo> mList;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }

            public void load() {
                //
            }
        }

    }

    private class GameMapsListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private List<MapInfo> maps;
        private LayoutInflater inflater;
        private View[] list_or_emptyviews;
        private int select_index = -1;
        private View select_view = null;
        private String force_seldir = null;

        //0: Have maps;		1: No maps, have folder;
        // 2: No folder;	3: No permission;
        private int state = 0;

        @Override
        public int getCount() {
            return maps.size();
        }

        @Override
        public MapInfo getItem(int i) {
            return maps.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (m_allow_create && i == 0) {
                startActivityForResult(new Intent(getActivity(), ActivityCreateLevel.class), REQ_CODE_CREATELEVEL);
                return;
            }
            if (select_index == i) return;
            if (select_view != null)
                ((Checkable) select_view).setChecked(false);//findViewById(R.id.entry_selmark).setVisibility(View.INVISIBLE);
            ((Checkable) view).setChecked(true);//findViewById(R.id.entry_selmark).setVisibility(View.VISIBLE);
            select_index = i;
            select_view = view;
            getConfiguration().putString(CONF_MAPDIR, getItem(i).path);
            setCanContinue(true);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View root;

            //If it's for creating new.
            if (m_allow_create && i == 0) {
                root = inflater.inflate(R.layout.entry_config_fmaps_create, viewGroup, false);
            } else {
                root = inflater.inflate(R.layout.entry_config_fmaps_list, viewGroup, false);
                TextView tv = root.findViewById(R.id.text);
                MapInfo info = getItem(i);
                tv.setText(info.name);
                tv = root.findViewById(R.id.text1);
                tv.setText(info.last_played_time);
                tv = root.findViewById(R.id.text2);
                tv.setText(info.path);
                if (select_index == i) ((Checkable) root).setChecked(true);
            }
            return root;
        }

        public void setSeldir(String path) {
            force_seldir = path;
        }

        private void gotoState(int state) {
            if (this.state != state) {
                list_or_emptyviews[this.state].setVisibility(GONE);
                list_or_emptyviews[state].setVisibility(View.VISIBLE);
                this.state = state;
            }
            notifyDataSetChanged();
        }

        //Called after activity resumed. User may modify game maps during which.
        public void update() {

            //Back up current scroll value and selected item.
            ListView lv = (ListView) list_or_emptyviews[0];
            int scroll = lv.getScrollY();
            String seldir = null;
            if (select_index != -1) seldir = maps.get(select_index).path;
            if (force_seldir != null) seldir = force_seldir;

            //Clear.
            maps.clear();

            //Load game maps.
            File dir = Environment.getExternalStorageDirectory();
            dir = new File(dir, PATH_MINECRAFTPE_DIR);
            dir = new File(dir, FNAME_MINECRAFTPE_MAPS);

            //No dir
            if (!dir.isDirectory()) {
                gotoState(2);
                setCanContinue(false);
                return;
            }

            //No perm.
            File[] files = dir.listFiles();
            if (files == null) {
                gotoState(3);
                setCanContinue(false);
                return;
            }

            //Add dummy entry if enabled create new.
            if (m_allow_create) maps.add(new MapInfo());

            //Filter only "valid" map folders.
            List<File> files_mc = new ArrayList<>();
            for (File f : files) {
                if (new File(f, "level.dat").isFile()) {
                    files_mc.add(f);
                }
            }

            //If no maps and can't create.
            if (files_mc.size() == 0 && !m_allow_create) {
                gotoState(1);
                setCanContinue(false);
                return;
            }

            //Load maps' name & last playe time.
            int new_index = -1;
            for (int i = 0, size = files_mc.size(); i < size; i++) {
                File f = files_mc.get(i);
                String path = f.getName();
                String name = null;
                String time;

                try {
                    //Prefer name in levelname.txt.
                    File dat = new File(f, "levelname.txt");
                    if (dat.isFile()) {
                        FileReader fr = new FileReader(dat);
                        BufferedReader br = new BufferedReader(fr);
                        name = br.readLine();
                        br.close();
                    }

                    //Read from level.dat.
                    dat = new File(f, "level.dat");
                    FileInputStream fis = new FileInputStream(dat);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.skip(8);
                    NBTInputStream nis = new NBTInputStream(bis, false, ByteOrder.LITTLE_ENDIAN);
                    CompoundTag tag = (CompoundTag) nis.readTag();
                    CompoundMap map = tag.getValue();

                    //Set name if not set yet from levelname.txt. Set last played time.
                    if (name == null) name = ((StringTag) map.get("LevelName")).getValue();
                    long ltime = ((LongTag) map.get("LastPlayed")).getValue() * 1000;
                    time = DateUtils.formatDateTime(getActivity(), ltime, FORMAT_SHOW_DATE | FORMAT_SHOW_TIME);
                    bis.close();
                } catch (Exception e) {
                    //Don't crash for one or more failed gamemaps.
                    e.printStackTrace();
                    name = path;
                    time = getString(R.string.setup_fmaps_date_unknown);
                }

                //Create a structure and add to list.
                MapInfo info = new MapInfo(
                        name, path, getString(R.string.setup_fmaps_lastplayed, time)
                );
                maps.add(info);

                //See if it's the previously selected map.
                if (path.equals(seldir)) new_index = i;
            }

            //Try recover previous selection.
            select_index = new_index;
            if (m_allow_create) select_index++;

            //Finished.
            gotoState(0);
            lv.setScrollY(scroll);
            if (select_index == -1 + (m_allow_create ? 1 : 0)) setCanContinue(false);
            else setCanContinue(true);
        }

        public GameMapsListAdapter(View container) {
            inflater = LayoutInflater.from(getActivity());
            maps = new ArrayList<>();
            list_or_emptyviews = new View[4];
            list_or_emptyviews[1] = container.findViewById(R.id.frag_nomaps);
            list_or_emptyviews[2] = container.findViewById(R.id.frag_nofolder);
            list_or_emptyviews[3] = container.findViewById(R.id.frag_noperm);
            ListView list = container.findViewById(R.id.list);
            list_or_emptyviews[0] = list;
            list.setAdapter(this);
            list.setOnItemClickListener(this);
        }
    }

    private class MapInfo {
        public String name;
        public String path;
        public String last_played_time;

        //Used to create dummy entry.
        public MapInfo() {
            name = null;
            path = null;
            last_played_time = null;
        }

        //Used to create valid entry.
        public MapInfo(String name, String path, String last_played_time) {
            this.name = name;
            this.path = path;
            this.last_played_time = last_played_time;
        }
    }

}
