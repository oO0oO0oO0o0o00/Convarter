package rbq2012.convarter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.faendir.rhino_android.ForceQuitException;
import com.faendir.rhino_android.RhinoAndroidHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.annotations.JSFunction;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import rbq2012.convarter.Constants;
import rbq2012.convarter.FileUtil;
import rbq2012.convarter.FlatWorldLayers;
import rbq2012.convarter.GameMapVersion;
import rbq2012.convarter.LevelDat;
import rbq2012.convarter.R;
import rbq2012.convarter.configguide.FragmentBase;
import rbq2012.ldbchunk.DB;
import rbq2012.ldbchunk.Names;

import static rbq2012.convarter.configguide.FragmentJsothers.PREF_KEY_CACHE_CHUNKS;
import static rbq2012.convarter.configguide.FragmentJsothers.PREF_KEY_OPTIMIZATION;

/**
 * Created by barco on 2018/3/14.
 */

public final class ActivityRunJs extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    static final private int MSG_LOGSTR = 1;
    static final private int MSG_LOGERR = 2;
    static final private int MSG_ERROPENDB = 3;
    static final private int MSG_ERROPENDAT = 4;
    static final private int MSG_ERRSAVEDB = 5;
    static final private int MSG_ERRSAVEDAT = 6;
    static final private int MSG_REQINPUT = 7;
    static final private int MSG_ABORT = 8;
    static final private int MSG_FIN = 9;

    private TextView m_logcat;
    private StringBuilder m_logtxt;

    private TextView m_title;
    private View m_progress;
    private JsThread m_thread;
    private View m_btnclose;

    private TextView m_inputprompt;
    private EditText m_input;
    private View m_inputarea;
    private View m_inputenter;

    private Semaphore seminput;
    private String userinput;

    private void log(String str) {
        m_logtxt.append(">>> ");
        m_logtxt.append(str);
        m_logtxt.append("\n");
        m_logcat.setText(m_logtxt);
    }

    private void log(Throwable err) {
        m_logtxt.append("====Exception====\n");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        err.printStackTrace(pw);
        m_logtxt.append(sw.toString());
        pw.close();
        m_logtxt.append("\n");
        m_logcat.setText(m_logtxt);
    }

    private void logRes(@StringRes int resid) {
        log(getString(resid));
    }

    private void onFailure() {
        m_title.setText(R.string.runjs_title_failed);
        m_progress.setVisibility(View.GONE);
        m_btnclose.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runjs);

        //Init logcat
        m_logcat = findViewById(R.id.text);
        m_logtxt = new StringBuilder(getString(R.string.runjs_logcat_firstline));
        m_logcat.setText(m_logtxt);

        //Locate other views
        m_title = findViewById(R.id.title);
        m_progress = findViewById(R.id.progress);
        m_inputprompt = findViewById(R.id.text2);
        m_input = findViewById(R.id.input);
        m_inputarea = (View) m_inputprompt.getParent();
        m_btnclose = findViewById(R.id.button2);
        m_btnclose.setOnClickListener(this);
        m_inputenter = findViewById(R.id.button);
        m_inputenter.setOnClickListener(this);

        //Start script
        m_title.setText(R.string.runjs_title_running);
        Intent intent = getIntent();
        String map_dir = intent.getStringExtra(FragmentBase.CONF_MAPDIR);
        File root = new File(Environment.getExternalStorageDirectory(), Constants.PATH_MINECRAFTPE_DIR);
        File map_file = new File(root, Constants.FNAME_MINECRAFTPE_MAPS);
        map_file = new File(map_file, map_dir);
        MeowHandler handler = new MeowHandler();
        String script = intent.getStringExtra(FragmentBase.CONF_JSEXT);
        File script_file = new File(root, Constants.FNAME_MINECRAFTPE_SCRIPTS);
        script_file = new File(script_file, script);
        script = FileUtil.readTextFile(script_file);
        m_thread = new JsThread(handler, map_file, script,
                intent.getIntExtra(PREF_KEY_CACHE_CHUNKS, 64),
                intent.getIntExtra(PREF_KEY_OPTIMIZATION, 2));
        m_thread.start();
    }

    private void reqInput(String title, String hint) {
        if (title != null) m_inputprompt.setText(title);
        else m_inputprompt.setText(R.string.runjs_input_prompt);
        if (hint != null) m_input.setHint(hint);
        else m_input.setHint(R.string.runjs_input_hint);
        m_inputarea.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                userinput = m_input.getText().toString();
                seminput.release();
                break;
            case R.id.button2:
                finish();
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        m_thread.interrupt();
    }

    private class MeowHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOGSTR:
                    log((String) msg.obj);
                    break;
                case MSG_LOGERR:
                    log((Throwable) msg.obj);
                    onFailure();
                    break;
                case MSG_ERROPENDB:
                    logRes(R.string.runjs_erropendb);
                    onFailure();
                    break;
                case MSG_ERROPENDAT:
                    logRes(R.string.runjs_erropendat);
                    onFailure();
                    break;
                case MSG_ERRSAVEDB:
                    logRes(R.string.runjs_errsavedb);
                    onFailure();
                    break;
                case MSG_ERRSAVEDAT:
                    logRes(R.string.runjs_errsavedat);
                    onFailure();
                    break;
                case MSG_REQINPUT: {
                    String[] txts = (String[]) msg.obj;
                    reqInput(txts[0], txts[1]);
                    break;
                }
                case MSG_ABORT:
                    finish();
                    break;
                default: {
                    m_title.setText(R.string.runjs_title_succ);
                    m_progress.setVisibility(View.GONE);
                    m_btnclose.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (m_progress.getVisibility() == View.GONE) {
            super.onBackPressed();
            return;
        }
        AlertDialog dia = new AlertDialog.Builder(this)
                .setMessage(R.string.runjs_abort_msg)
                .setPositiveButton(R.string.global_yes, this)
                .create();
        dia.show();
    }

    private class JsThread extends Thread {

        private MeowHandler handler;
        private DB db;
        private LevelDat dat;
        private File map_dir;
        private String script;
        private GameMapVersion version;
        private FlatWorldLayers.Layers layers;
        private int max_cached_chunks;
        private int optimization_script;

        public JsThread(MeowHandler handler, File map_dir, String script, int max_cached_chunks, int optimization_script) {
            this.handler = handler;
            this.map_dir = map_dir;
            this.script = script;
            this.max_cached_chunks = max_cached_chunks;
            this.optimization_script = optimization_script;
        }

        private JSONObject iter(CompoundMap map) throws JSONException {
            Iterator<Tag<?>> iter = map.iterator();
            JSONObject jso = new JSONObject();
            while (iter.hasNext()) {
                Tag tag = iter.next();
                String valstr = null;
                boolean compound = false;
                switch (tag.getType()) {
                    case TAG_COMPOUND:
                        compound = true;
                        break;
                    default:
                        valstr = tag.toString();
                }
                if (compound) {
                    jso.put(tag.getName(), iter(((CompoundTag) tag).getValue()));
                } else {
                    jso.put(tag.getName(), valstr);
                }
            }
            return jso;
        }

//        class JSOComparator implements Comparator<JSONObject> {
//
//            @Override
//            public int compare(JSONObject jsonObject, JSONObject t1) {
//                try {
//                    int id0 = jsonObject.getInt("id");
//                    int id1 = t1.getInt("id");
//                    if (id0 == id1) {
//                        id0 = jsonObject.getInt("data");
//                        id1 = t1.getInt("data");
//                        return id0 - id1;
//                    }
//                    return id0 - id1;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return 0;
//            }
//
//        @Override
//        public boolean equals(Object o) {
//            return (o instanceof JSOComparator);
//        }
//    }

        @Override
        public void run() {

            //Open db and load from dat, dat won't be keep opened.
            try {
                db = new DB(new File(map_dir, "db"));
                db.open();
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(MSG_ERROPENDB);
                return;
            }
            dat = new LevelDat(new File(map_dir, "level.dat"));
            if (!dat.load()) {
                handler.sendEmptyMessage(MSG_ERROPENDAT);
                //fin();
                return;
            }

            //////  Test Code
//            try {
//                byte[] arr = db.get(new byte[]{85, 0, 0, 0, 0, 0, 0, 0, 47, 0});
//                ByteArrayInputStream inp = new ByteArrayInputStream(arr);
//                BufferedInputStream bis = new BufferedInputStream(inp);
//                bis.skip(0x202);
//                NBTInputStream nis = new NBTInputStream(bis);
//                CompoundTag tag = (CompoundTag) nis.readTag();
//                CompoundMap map = tag.getValue();
//                JSONObject jso = iter(map);
//                String result = jso.toString(4);
//                FileUtil.writeTextFile(new File("/sdcard/#aa6/level.db.txt"), result);
//                System.out.print(result);
//                Message msg = new Message();
//                msg.what = MSG_LOGSTR;
//                msg.obj = result;
//                handler.sendMessage(msg);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                log(e);
//            }
//            if (true) return;
//            try {
//                File file = new File("/sdcard/#aa6/blox.json");
//                JSONObject jso = new JSONObject(FileUtil.readTextFile(file));
//                JSONArray arr = jso.getJSONArray("blocks");
//                int len = arr.length();
//                JSONObject[] objs = new JSONObject[len];
//                for (int i = 0; i < len; i++) {
//                    objs[i] = arr.getJSONObject(i);
//                }
//                Arrays.sort(objs, new JSOComparator());
//                for (int i = 0; i < len; i++) {
//                    arr.put(i, objs[i]);
//                }
//                FileUtil.writeTextFile(file, jso.toString(4));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                CompoundTag tag = (CompoundTag) dat.getRoot();
//                CompoundMap map = tag.getValue();
//                JSONObject jso = iter(dat.getRoot().getValue());
//                String result = jso.toString(4);
//                FileUtil.writeTextFile(new File("/sdcard/#aa6/level.dat.txt"), result);
//                System.out.print(result);
//                Message msg = new Message();
//                msg.what = MSG_LOGSTR;
//                msg.obj = result;
//                handler.sendMessage(msg);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            //////  End Test Code

            ///Version detect
            {
                int stver = ((IntTag) dat.getRoot().getValue().get("StorageVersion")).getValue();
                if (4 >= stver) version = GameMapVersion.VERSION_POCKET;
                else if (7 <= stver) version = GameMapVersion.VERSION_BEDROCK;
                else if (dat.getRoot().getValue().containsKey("FlatWorldLayers"))
                    version = GameMapVersion.VERSION_BEDROCK;
                else version = GameMapVersion.VERSION_POCKET;
            }

            ///Load flat world layers
            {
                if (version == GameMapVersion.VERSION_BEDROCK) {
                    String json = FileUtil.getAssetText(getAssets(), "blox.json");
                    if (json == null) return;
                    Names.loadBlockNames(json);
                    layers = FlatWorldLayers.newFlatWorldLayers(dat);
                } else layers = FlatWorldLayers.newFlatWorldLayers(db);
            }

            ///Test code****************************************************************************

            db.setLayers(layers.getLayers());

            db.setMaxChunksCount(max_cached_chunks);

//            for (int i = 0; i < 256; i++) {
//                db.setTile(1360 + i, 5, 19, 0, i, 0);
//            }

            //db.test();

            //db.close();
            //if (true) return;

            ///End test code************************************************************************

            ///Run script
            try {
                //InterruptableContextFactory.init();
                org.mozilla.javascript.Context ctx = new RhinoAndroidHelper(ActivityRunJs.this).enterContext();
                //new InterruptableContextFactory().enterContext();
                ctx.setOptimizationLevel(optimization_script);
                Script script = ctx.compileString(this.script, "script", 0, null);
                this.script = null;
                ScriptableObject scope = ctx.initStandardObjects(new MeowScope(), false);
                List<String> lnames = new ArrayList<>(32);
                for (Method met : MeowScope.class.getDeclaredMethods()) {
                    if (met.getAnnotation(JSFunction.class) != null) lnames.add(met.getName());
                }
                scope.defineFunctionProperties(lnames.toArray(new String[0]), MeowScope.class, ScriptableObject.DONTENUM);
                script.exec(ctx, scope);
                Context.exit();
            } catch (Throwable e) {
                fin();
                if (e instanceof ForceQuitException) {
                    handler.sendEmptyMessage(MSG_ABORT);
                    return;
                }
                e.printStackTrace();
                log(e);
                return;
            }

            //Finally
            fin();

            //Let the Activity know we're done
            handler.sendEmptyMessage(MSG_FIN);
        }

        private void log(String s) {
            Message msg = new Message();
            msg.what = MSG_LOGSTR;
            msg.obj = s;
            handler.sendMessage(msg);
        }

        private void log(Throwable e) {
            Message msg = new Message();
            msg.what = MSG_LOGERR;
            msg.obj = e;
            handler.sendMessage(msg);
        }

        private void fin() {
            try {
                db.close();
            } catch (Throwable e) {
            }
        }

        private class MeowScope extends ImporterTopLevel {
            @JSFunction
            public void log(String s) {
                JsThread.this.log(s);
            }

            @JSFunction
            public int getTile(int x, int y, int z) {
                return db.getTile(x, y, z, 0);
            }

            @JSFunction
            public int getData(int x, int y, int z) {
                return db.getData(x, y, z, 0);
            }

            @JSFunction
            public void setTile(int x, int y, int z, int id, int data) {
                db.setTile(x, y, z, 0, id, data);
            }

            @JSFunction
            public void setData(int x, int y, int z, int data) {
                db.setData(x, y, z, 0, data);
            }


            @JSFunction
            public Object dbget(String key) {
                return db.get(key.getBytes(Charset.defaultCharset()));
            }

            @JSFunction
            public Object dbget2(NativeArray arr) {
                byte[] a = JsArrayToBytes(arr);
                return db.get(a);
            }

            private byte[] JsArrayToBytes(NativeArray arr) {
                Object[] ar = arr.toArray();
                byte[] a = new byte[ar.length];
                for (int i = 0; i < a.length; i++) {
                    Object o = ar[i];
                    if (o instanceof Double) {
                        Double d = (Double) o;
                        byte b = d.byteValue();
                        a[i] = b;
                    } else throw new RuntimeException("00");
                }
                return a;
            }

            private File root = new File("/sdcard/games/com.mojang/scripts");

            @JSFunction
            public void dbdel(String key) {
                db.delete(key.getBytes(Charset.defaultCharset()));
            }

            @JSFunction
            public void dbdel2(NativeArray key) {
                db.delete(JsArrayToBytes(key));
            }

            @JSFunction
            public void dbdel3(NativeJavaArray key) {
                byte[] bs = (byte[]) key.unwrap();
                db.delete(bs);
            }

            @JSFunction
            public void dbput2(NativeArray key, String fname) {
                byte[] k = JsArrayToBytes(key);
                byte[] v = new byte[6145];
                try {
                    FileReader fr = new FileReader(new File(root, fname));
                    for (int i = 0; i < 6145; i++) {
                    /*
                    int h=fr.read();
					int l=fr.read();
					if(h>'a')h-='a';
					else h-='0';
					if(l>'a')l-='a';
					else l-='0';
					l=h<<4+l;
					if(l>=128)l-=256;
					v[i]=(byte) l;
					fr.read();*/
                        v[i] = 4;
                    }
                    v[0] = 0;
                    fr.close();
                } catch (Exception e) {
                    JsThread.this.log(e);
                }
                db.put(k, v);
            }

            @JSFunction
            public Object dbkeys() {
                List res = new ArrayList();
                rbq2012.ldbchunk.Iterator it = db.iterator();
                for (it.seekToFirst(); it.isValid(); it.next()) {
                    res.add(it.getKey());
                }
                it.close();
                return res;
            }

            @JSFunction
            public void fwrite(String fname, String cont, boolean append) {
                try {
                    FileWriter fw = new FileWriter(fname, append);
                    fw.write(cont);
                    fw.close();
                } catch (Exception e) {
                    JsThread.this.log(e);
                }
            }
        }

    }

}

/*class InterruptableContextFactory extends ContextFactory {

    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            ContextFactory.initGlobal(new InterruptableContextFactory());
            initialized = true;
        }
    }

    @Override
    protected void observeInstructionCount(Context context, int i) {
        super.observeInstructionCount(context, i);
        if (Thread.currentThread().isInterrupted()) {
            throw new ForceQuitException();
        }
    }

    @Override
    protected Context makeContext() {
        Context cx = super.makeContext();
        //set a number of instructions here
        cx.setInstructionObserverThreshold(1000);
        return cx;
    }
}*/