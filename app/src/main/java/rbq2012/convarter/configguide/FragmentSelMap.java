package rbq2012.convarter.configguide;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.view.View.GONE;
import static rbq2012.convarter.Constants.*;

/**
 * Created by 喵喵喵 on 2018/2/27.
 */

public class FragmentSelMap extends FragmentBase {

    private GameMapsListAdapter m_list_adapter = null;
    private boolean m_first_resume = true;

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

    private class GameMapsListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private List<MapInfo> maps = null;
        private LayoutInflater inflater = null;
        private View[] list_or_emptyviews = null;
        private int select_index = -1;
        private View select_view = null;

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
            if (select_index == i) return;
            view.findViewById(R.id.entry_selmark).setVisibility(View.VISIBLE);
            if (select_view != null)
                select_view.findViewById(R.id.entry_selmark).setVisibility(View.INVISIBLE);
            select_index = i;
            select_view = view;
            getConfiguration().putString(CONF_MAPDIR, getItem(i).path);
            setCanContinue(true);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewGroup root = (ViewGroup) inflater.inflate(R.layout.entry_config_fmaps_list, viewGroup, false);
            LinearLayout t = (LinearLayout) root.getChildAt(0);
            TextView tv = (TextView) t.getChildAt(0);
            MapInfo info = maps.get(i);
            tv.setText(info.name);
            tv = (TextView) t.getChildAt(1);
            tv.setText(info.last_played_time);
            tv = (TextView) t.getChildAt(2);
            tv.setText(info.path);
            if (select_index == i) {
                root.getChildAt(1).setVisibility(View.VISIBLE);
            }
            return root;
        }

        private void gotoState(int state) {
            if (this.state != state) {
                list_or_emptyviews[this.state].setVisibility(GONE);
                list_or_emptyviews[state].setVisibility(View.VISIBLE);
                this.state = state;
            }
            notifyDataSetChanged();
        }

        public void update() {
            ListView lv = (ListView) list_or_emptyviews[0];
            int scroll = lv.getScrollY();
            String seldir = null;
            if (select_index != -1) seldir = maps.get(select_index).path;
            maps.clear();
            File dir = Environment.getExternalStorageDirectory();
            dir = new File(dir, PATH_MINECRAFTPE_DIR);
            dir = new File(dir, FNAME_MINECRAFTPE_MAPS);
            if (!dir.isDirectory()) {//No dir
                gotoState(2);
                setCanContinue(false);
                return;
            }
            File[] files = dir.listFiles();
            if (files == null) {//No perm.
                gotoState(3);
                setCanContinue(false);
                return;
            }
            List<File> files_mc = new ArrayList<>();
            for (File f : files) {
                if (new File(f, "level.dat").isFile()) {
                    files_mc.add(f);
                }
            }
            if (files_mc.size() == 0) {
                gotoState(1);
                setCanContinue(false);
                return;
            }
            int new_index = -1;
            for (int i = 0, size = files_mc.size(); i < size; i++) {
                File f = files_mc.get(i);
                String path = f.getName();
                String name = null;
                String time;
                try {
                    File dat = new File(f, "levelname.txt");
                    if (dat.isFile()) {
                        FileReader fr = new FileReader(dat);
                        BufferedReader br = new BufferedReader(fr);
                        name = br.readLine();
                        br.close();
                    }
                    dat = new File(f, "level.dat");
                    FileInputStream fis = new FileInputStream(dat);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.skip(8);
                    NBTInputStream nis = new NBTInputStream(bis, false, ByteOrder.LITTLE_ENDIAN);
                    CompoundTag tag = (CompoundTag) nis.readTag();
                    CompoundMap map = tag.getValue();
                    if (name == null) name = ((StringTag) map.get("LevelName")).getValue();
                    long ltime = ((LongTag) map.get("LastPlayed")).getValue() * 1000;
                    //Date date = new Date(ltime);
                    time = DateUtils.formatDateTime(getActivity(), ltime, FORMAT_SHOW_DATE | FORMAT_SHOW_TIME);
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    name = path;
                    time = getString(R.string.setup_fmaps_date_unknown);
                }
                MapInfo info = new MapInfo(
                        name, path, getString(R.string.setup_fmaps_lastplayed, time)
                );
                maps.add(info);
                if (path.equals(seldir)) new_index = i;
            }
            select_index = new_index;
            gotoState(0);
            lv.setScrollY(scroll);
            if (select_index == -1) setCanContinue(false);
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

        public MapInfo(String name, String path, String last_played_time) {
            this.name = name;
            this.path = path;
            this.last_played_time = last_played_time;
        }
    }

}
