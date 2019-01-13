package rbq2012.convarter.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rbq2012.convarter.data.BlockIcons;
import rbq2012.convarter.FileUtil;
import rbq2012.convarter.R;
import rbq2012.convarter.data.BlockNames;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public final class DialogPickBlock extends AppCompatActivity implements TextWatcher {

    static final public String INTENT_KEY_ID = "id";

    private MeowAdapter ada;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pickblock);

        BlockIcons.load(getResources());

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        getWindow().setLayout(metrics.widthPixels, WRAP_CONTENT);

        ListView list = findViewById(R.id.list);
        View empty = findViewById(R.id.empty);
        list.setEmptyView(empty);
        ada = new MeowAdapter();
        list.setAdapter(ada);
        list.setOnItemClickListener(ada);

        EditText et = findViewById(R.id.edittext);
        et.addTextChangedListener(this);

        setResult(RESULT_CANCELED);

        ada.update("");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //
    }

    @Override
    public void afterTextChanged(Editable editable) {
        ada.update(editable.toString());
    }

    private class MeowAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private String locale;
        private List<Block> all, list;

        public MeowAdapter() {
            all = new ArrayList<>(256);
            list = new ArrayList<>(256);

            locale = getString(R.string.global_lang);
            if (locale.equals("default")) locale = null;

            for (int i = 0; i < 256; i++) {
                String name = BlockNames.getNameIfValid(i);
                if (name == null) continue;
                Block blk = new Block(i, name, BlockNames.getLocaleName(i, locale));
                all.add(blk);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Block getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View root = getLayoutInflater().inflate(R.layout.entry_pickblock_list, viewGroup, false);
            Block blk = getItem(i);
            TextView tv = root.findViewById(R.id.text);
            tv.setText(blk.locale_name);
            tv = root.findViewById(R.id.text2);
            tv.setText(getString(R.string.pickblock_entry_detail, blk.id, blk.name));
            /*LinearLayout iv = root.findViewById(R.id.image);
            ImageView im = new ImageView(DialogPickBlock.this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(48, 48);
            iv.addView(im);*/
            ImageView im = root.findViewById(R.id.image);
            im.setImageDrawable(BlockIcons.get(blk.id));
            return root;
        }

        public void update(String filter) {
            filter = filter.trim().toLowerCase();
            list.clear();
            for (int i = 0; i < all.size(); i++) {
                Block blk = all.get(i);
                if (Integer.toString(blk.id).contains(filter)
                        || blk.name.toLowerCase().contains(filter)
                        || blk.locale_name.toLowerCase().contains(filter)) {
                    list.add(blk);
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            setResult(RESULT_OK, getIntent().putExtra(INTENT_KEY_ID, getItem(i).id));
            finish();
        }
    }

    static private class Block {
        public int id;
        public String name;
        public String locale_name;

        public Block(int id, String name, String locale_name) {
            this.id = id;
            this.name = name;
            this.locale_name = locale_name;
        }
    }
}
