package rbq2012.convarter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rbq2012.convarter.BlockIcons;
import rbq2012.convarter.Constants;
import rbq2012.convarter.FileUtil;
import rbq2012.convarter.FlatWorldLayers;
import rbq2012.convarter.LevelDat;
import rbq2012.convarter.R;
import rbq2012.convarter.UnreliableRandom;
import rbq2012.ldbchunk.Names;

import static android.widget.Toast.LENGTH_SHORT;
import static rbq2012.convarter.activities.DialogEditLayer.INTENT_KEY_COUNT;
import static rbq2012.convarter.activities.DialogEditLayer.INTENT_KEY_DATA;
import static rbq2012.convarter.activities.DialogEditLayer.INTENT_KEY_ID;
import static rbq2012.convarter.activities.DialogEditLayer.INTENT_KEY_INDEX;
import static rbq2012.convarter.activities.DialogEditLayer.INTENT_KEY_MODE_ADD;
import static rbq2012.convarter.configguide.FragmentBase.CONF_MAPDIR;

public final class ActivityModifyFlat extends AppCompatActivity {

    static final private int REQ_CODE_EDITLAYER = 2012;
    static final public String SPREF_NAME_LAYERS = "layers_pref";
    static final public String SPREF_KEY_SHOWHELP = "show_help";

    private FlatWorldLayers.Layers layers_original;
    private FlatWorldLayers.Layers layers_modified;
    private CheckBox cbox;
    private MeowAdapter ada;
    private File mapdir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyflat);

        //Load needed resources.
        Names.loadBlockNames(FileUtil.getAssetText(getAssets(), "blox.json"));
        BlockIcons.load(getResources());

        //load level.dat
        File file = new File(Environment.getExternalStorageDirectory(), Constants.PATH_MINECRAFTPE_DIR);
        file = new File(file, Constants.FNAME_MINECRAFTPE_MAPS);
        file = new File(file, getIntent().getStringExtra(CONF_MAPDIR));
        mapdir = file;
        file = new File(file, "level.dat");
        if (!file.isFile()) return;
        LevelDat dat = new LevelDat(file);
        dat.load();

        //Load layers
        layers_modified = FlatWorldLayers.newFlatWorldLayers(dat);
        layers_modified.load();
        layers_original = layers_modified.clone();


        //Setup list
        DragListView lv = findViewById(R.id.list);
        ada = new MeowAdapter();
        lv.setLayoutManager(new LinearLayoutManager(this));
        lv.setCanDragHorizontally(false);
        lv.setDragListListener(new MeowListener());
        lv.setAdapter(ada, false);

        //Sow help at first open.
        SharedPreferences spref = getSharedPreferences(SPREF_NAME_LAYERS, MODE_PRIVATE);
        if (spref.getBoolean(SPREF_KEY_SHOWHELP, true)) {
            AlertDialog dia = new AlertDialog.Builder(this)
                    .setTitle(R.string.editlayer_diahelp_title)
                    .setView(R.layout.dialog_editlayer_help)
                    .setPositiveButton(R.string.global_yes, null)
                    .create();
            dia.show();
            spref.edit().putBoolean(SPREF_KEY_SHOWHELP, false).apply();
        }
    }

    @Override
    protected void onDestroy() {
        BlockIcons.release();
        super.onDestroy();
    }

    private void addOrEditLayer(int index, boolean add) {
        Intent intent = new Intent(this, DialogEditLayer.class);
        intent.putExtra(INTENT_KEY_MODE_ADD, add);
        intent.putExtra(INTENT_KEY_INDEX, index);
        if (add) {
            intent.putExtra(INTENT_KEY_ID, 0);
            intent.putExtra(INTENT_KEY_DATA, 0);
            intent.putExtra(INTENT_KEY_COUNT, 1);
        } else {
            FlatWorldLayers.Layer l = ada.getItemList().get(index).second;
            intent.putExtra(INTENT_KEY_ID, (int) l.id);
            intent.putExtra(INTENT_KEY_DATA, (int) l.data);
            intent.putExtra(INTENT_KEY_COUNT, (int) l.count);
        }
        startActivityForResult(intent, REQ_CODE_EDITLAYER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_EDITLAYER:
                if (resultCode != RESULT_OK) break;
                FlatWorldLayers.Layer layer = new FlatWorldLayers.Layer();
                layer.id = (byte) data.getIntExtra(INTENT_KEY_ID, 0);
                layer.data = (byte) data.getIntExtra(INTENT_KEY_DATA, 0);
                layer.count = (byte) data.getIntExtra(INTENT_KEY_COUNT, 1);
                Pair<Long, FlatWorldLayers.Layer> pair = new Pair<>(
                        ada.getLongForLayer(layer), layer
                );
                int pos = data.getIntExtra(INTENT_KEY_INDEX, 0);
                if (data.getBooleanExtra(INTENT_KEY_MODE_ADD, true)) {
                    ada.addItem(pos + 1, pair);
                } else {
                    ada.getItemList().set(pos, pair);
                    ada.notifyDataSetChanged();
                }
                break;
        }
    }

    public void save(View view) {
        List<FlatWorldLayers.Layer> dst = layers_modified.getLayersForControl();
        dst.clear();
        List<Pair<Long, FlatWorldLayers.Layer>> list = ada.getItemList();
        for (int i = list.size() - 1; i >= 0; i--) {
            dst.add(list.get(i).second);
        }
        layers_modified.save();
        //byte[] bytes = layers_original.getLayersForNativeUse();
        //byte[] bytes1 = layers_modified.getLayersForNativeUse();
    }

    private class MeowListener extends DragListView.DragListListenerAdapter {
        @Override
        public void onItemDragStarted(int position) {
            //mRefreshLayout.setEnabled(false);
            //Toast.makeText(mDragListView.getContext(), "Start - position: " + position, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onItemDragEnded(int fromPosition, int toPosition) {
            //mRefreshLayout.setEnabled(true);
            if (fromPosition != toPosition) {
                //Toast.makeText(mDragListView.getContext(), "End - position: " + toPosition, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MeowAdapter extends
            DragItemAdapter<Pair<Long, FlatWorldLayers.Layer>, MeowAdapter.MeowViewHolder>
            implements View.OnClickListener {

        private String locale;

        public MeowAdapter() {
            locale = getString(R.string.global_lang);
            if (locale.equals("default")) locale = null;
            List<Pair<Long, FlatWorldLayers.Layer>> list = new ArrayList<>(64);
            List<FlatWorldLayers.Layer> src = layers_modified.getLayersForControl();
            for (int i = src.size() - 1; i >= 0; i--) {
                FlatWorldLayers.Layer layer = src.get(i);
                list.add(new Pair<Long, FlatWorldLayers.Layer>(getLongForLayer(layer), layer));
            }
            setItemList(list);
        }

        private long getLongForLayer(FlatWorldLayers.Layer layer) {
//            long ret = layer.id;
//            ret <<= 8;
//            ret |= layer.data;
//            ret <<= 8;
//            ret |= layer.count;
//            return ret;
            return UnreliableRandom.nextLong();
        }

        @Override
        public long getUniqueItemId(int i) {
            return getItemList().get(i).first;
        }

        @Override
        public MeowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = getLayoutInflater().inflate(R.layout.entry_modifyflat_list, parent, false);
            return new MeowViewHolder(root);
        }

        @Override
        public void onBindViewHolder(MeowViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            FlatWorldLayers.Layer layer = mItemList.get(position).second;
            BitmapDrawable drawable = BlockIcons.get(layer.id);
            //if (drawable != null)
            holder.iv.setImageDrawable(drawable);
            holder.tv.setText(getString(
                    R.string.editlayers_entry_text,
                    layer.count,
                    Names.getLocaleName(layer.id, locale)));
            holder.tvsub.setText(getString(
                    R.string.editlayers_entry_text2, layer.id,
                    layer.data, Names.getName(layer.id)));
            holder.btndel.setTag(position);
            holder.btnadd.setTag(position);
            holder.itemView.setTag(position);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button:
                    if (getItemCount() <= 3) {
                        Toast.makeText(ActivityModifyFlat.this,
                                getString(R.string.editlayers_cantdelete), LENGTH_SHORT).show();
                        return;
                    }
                    removeItem((Integer) view.getTag());
                    notifyDataSetChanged();
                    break;
                case R.id.button1:
                    addOrEditLayer((Integer) view.getTag(), true);
                    notifyDataSetChanged();
                    break;
            }
        }

        class MeowViewHolder extends DragItemAdapter.ViewHolder {

            private ImageView iv;
            private TextView tv;
            private TextView tvsub;
            private ImageButton btndel, btnadd;

            public MeowViewHolder(View itemView) {
                super(itemView, R.id.image, false);
                iv = itemView.findViewById(R.id.image);
                tv = itemView.findViewById(R.id.text);
                tvsub = itemView.findViewById(R.id.text2);
                btndel = itemView.findViewById(R.id.button);
                btndel.setOnClickListener(MeowAdapter.this);
                btnadd = itemView.findViewById(R.id.button1);
                btnadd.setOnClickListener(MeowAdapter.this);
            }

            @Override
            public void onItemClicked(View view) {
                addOrEditLayer((Integer) view.getTag(), false);
            }
        }
    }
}
