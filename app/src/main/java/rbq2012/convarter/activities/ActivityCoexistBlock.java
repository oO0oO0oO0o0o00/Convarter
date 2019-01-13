package rbq2012.convarter.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;

import rbq2012.convarter.MCUtils;
import rbq2012.convarter.UiUtil;
import rbq2012.convarter.data.BlockIcons;
import rbq2012.convarter.databinding.ActivityCoexistBlockBinding;

import rbq2012.convarter.R;
import rbq2012.ldbchunk.ChunkCache;
import rbq2012.ldbchunk.ChunkSource;

import static rbq2012.convarter.activities.DialogEditLayer.INTENT_KEY_ID;
import static rbq2012.convarter.configguide.FragmentBase.CONF_MAPDIR;

public final class ActivityCoexistBlock extends AppCompatActivity {

    static final private int REQ_CODE_PICK1 = 2012;
    static final private int REQ_CODE_PICK2 = 2013;

    private boolean iconsLoaded;
    ActivityCoexistBlockBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coexist_block);
        binding.block1.setIndex(1);
        binding.block2.setIndex(2);
        iconsLoaded = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_PICK1:
                if (resultCode != RESULT_OK) break;
                binding.block1.setBlockid(data.getIntExtra(INTENT_KEY_ID, 0));
                break;
            case REQ_CODE_PICK2:
                if (resultCode != RESULT_OK) break;
                binding.block2.setBlockid(data.getIntExtra(INTENT_KEY_ID, 0));
                break;
        }
    }

    static private void updateFrom(ActivityCoexistBlockBinding binding) {
        String s = binding.etBlockx.getText().toString();
        binding.setBlockx(s.isEmpty() ? 0 : Integer.parseInt(s));
        s = binding.etBlocky.getText().toString();
        binding.setBlocky(s.isEmpty() ? 0 : Integer.parseInt(s));
        s = binding.etBlockz.getText().toString();
        binding.setBlockz(s.isEmpty() ? 0 : Integer.parseInt(s));
        binding.setApplyArea(binding.cbApplyArea.isChecked());
        s = binding.block1.etBlockid.getText().toString();
        binding.block1.setBlockid(s.isEmpty() ? 0 : Integer.parseInt(s));
        s = binding.block1.etBlockdata.getText().toString();
        binding.block1.setBlockdata(s.isEmpty() ? 0 : Integer.parseInt(s));
        s = binding.block2.etBlockid.getText().toString();
        binding.block2.setBlockid(s.isEmpty() ? 0 : Integer.parseInt(s));
        s = binding.block2.etBlockdata.getText().toString();
        binding.block2.setBlockdata(s.isEmpty() ? 0 : Integer.parseInt(s));
    }

    private ChunkSource getDbAndOpen() {
        ChunkSource source = new ChunkSource(new File(
                MCUtils.gameMapDir(getIntent().getStringExtra(CONF_MAPDIR)), "db"));
        source.openDb();
        return source;
    }

    public void pick(View view) {
        if (!iconsLoaded) {
            BlockIcons.load(getResources());
            iconsLoaded = true;
        }
        int req;
        req = "1".equals(view.getTag().toString()) ? REQ_CODE_PICK1 : REQ_CODE_PICK2;
        startActivityForResult(new Intent(this, DialogPickBlock.class), req);
    }

    public void swap(View view) {
        String id1 = binding.block1.etBlockid.getText().toString();
        String data1 = binding.block1.etBlockdata.getText().toString();
        String id2 = binding.block2.etBlockid.getText().toString();
        String data2 = binding.block2.etBlockdata.getText().toString();
        binding.block1.setBlockid(id2.isEmpty() ? 0 : Integer.parseInt(id2));
        binding.block1.setBlockdata(data2.isEmpty() ? 0 : Integer.parseInt(data2));
        binding.block2.setBlockid(id1.isEmpty() ? 0 : Integer.parseInt(id1));
        binding.block2.setBlockdata(data1.isEmpty() ? 0 : Integer.parseInt(data1));
    }

    public void deleteChunk(View view) {
//        ChunkSource source = getDbAndOpen();
//        updateFrom(binding);
//        source.voidChunk(binding.getBlockx()<<4,binding.getBlockz()<<4,0);
//        if(binding.getApplyArea()){
//            if
//        }
    }

    public void save(View view) {
        ChunkSource source = getDbAndOpen();
        updateFrom(binding);
        ChunkCache cache = new ChunkCache(4, source);
        if (binding.getApplyArea()) {
            int xmin = binding.getBlockx();
            int zmin = binding.getBlockz();
            int y = binding.getBlocky();
            for (int x = xmin; x < xmin + 14; x++) {
                for (int z = zmin; z < zmin + 14; z++) {
                    cache.setBlock(x, y, z, 0,
                            binding.block1.getBlockid() << 8 | binding.block1.getBlockdata());
                    cache.setBlock3(x, y, z, 0, 1,
                            binding.block2.getBlockid() << 8 | binding.block2.getBlockdata());
                }
            }
        } else {
            int x = binding.getBlockx();
            int z = binding.getBlockz();
            int y = binding.getBlocky();
            cache.setBlock(x, y, z, 0,
                    binding.block1.getBlockid() << 8 | binding.block1.getBlockdata());
            cache.setBlock3(x, y, z, 0, 1,
                    binding.block2.getBlockid() << 8 | binding.block2.getBlockdata());
        }
        cache.evictAll();
        source.closeDb();
        UiUtil.toast(this, "done");
    }
}
