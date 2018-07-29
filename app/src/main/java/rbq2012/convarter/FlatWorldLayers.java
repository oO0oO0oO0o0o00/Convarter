package rbq2012.convarter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.IntTag;
import org.spout.nbt.StringTag;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import rbq2012.ldbchunk.DB;
import rbq2012.ldbchunk.Names;


/**
 * Created by barco on 2018/3/16.
 */

public final class FlatWorldLayers {

    public static final int ERR_LOAD_NO_DATA = -1;
    public static final int ERR_LOAD_CORRUPT = -2;
    public static final String UTF_8 = "UTF-8";

    static public Layers newFlatWorldLayers(LevelDat dat) {
        return new BedrockLayers(dat);
    }

    static public Layers newFlatWorldLayers(DB database) {
        return new PocketLayers(database);
    }

    static public Layers newFlatWorldLayers(LevelDat dat, DB db, GameMapVersion version, boolean db_opened) {
        FlatWorldLayers.Layers layers = null;
        int gen = ((IntTag) (dat.getRoot().getValue().get("Generator"))).getValue();
        if (gen == 2) {
            Log.i("233", "Flat world found, loading layers.");
            if (version == GameMapVersion.VERSION_1_2) {
                layers = FlatWorldLayers.newFlatWorldLayers(dat);
                int errno = layers.load();
                if (errno == FlatWorldLayers.ERR_LOAD_NO_DATA) {
                    Log.w("233", "Layers loading failed for targeted version 1.2, no data.");
                    return null;
                } else if (errno == FlatWorldLayers.ERR_LOAD_CORRUPT) {
                    Log.e("233", "Layers loading failed for targeted version 1.2, corrupted.");
                    return null;
                }
            } else {
                layers = FlatWorldLayers.newFlatWorldLayers(db);
                if (!db_opened) db.open();
                int errno = layers.load();
                if (errno == FlatWorldLayers.ERR_LOAD_NO_DATA) {
                    Log.w("233", "Layers loading failed for targeted version 1.0/0.15, no data.");
                    layers = FlatWorldLayers.newFlatWorldLayers(dat);
                    errno = layers.load();
                    if (errno != 0) {
                        Log.w("233", "Alternate method of layers loading failed: " + errno);
                        return null;
                    }
                } else if (errno == FlatWorldLayers.ERR_LOAD_CORRUPT) {
                    Log.e("233", "Layers loading failed for targeted version 1.0/0.15, corrupted.");
                    return null;
                }
            }
        } else {
            Log.i("233", "Non-flat world found, skipping layers loading.");
            return new DummyLayers();
        }
        return layers;
    }

    static public abstract class Layers {

        protected List<Layer> list;

        abstract public int load();

        abstract public void setBiomeId(byte id);

        abstract public byte getBiomeId();

        final public byte[] getLayersForNativeUse() {
            int len = list.size();
            byte[] bytes = new byte[len * 3];
            for (int i = 0, j = 0; i < len; i++) {
                Layer layer = list.get(i);
                bytes[j] = layer.id;
                j++;
                bytes[j] = layer.data;
                j++;
                bytes[j] = layer.count;
                j++;
            }
            return bytes;
        }

        final public int getItemsCount() {
            return list.size();
        }

        final public void deleteLayerAt(int index) {
            int lim = list.size() - 1;
            for (int i = index; i < lim; i++) {
                list.set(i, list.get(i + 1));
            }
            list.remove(lim);
        }

        final public byte getLayerIdAt(int index) {
            return list.get(index).id;
        }

        final public byte getLayerDataAt(int index) {
            return list.get(index).data;
        }

        final public byte getLayerCountAt(int index) {
            return list.get(index).count;
        }

        final public void setLayerIdAt(int index, byte id) {
            list.get(index).id = id;
        }

        final public void setLayerDataAt(int index, byte data) {
            list.get(index).data = data;
        }

        final public void setLayerCountAt(int index, byte count) {
            list.get(index).count = count;
        }

        final public void addLayerAt(int index, byte id, byte data, byte count) {
            int lim = list.size();
            int base = index;
            Layer layer = new Layer();
            layer.id = id;
            layer.data = data;
            layer.count = count;
            if (base >= lim) {
                list.add(layer);
                return;
            }
            list.add(list.get(lim - 1));
            lim -= 3;
            for (int i = base; i < lim; i++) {
                list.set(i + 1, list.get(i));
            }
            list.set(base, layer);
        }

        final public List<Layer> getLayersForControl() {
            return list;
        }

        abstract public void save();

        @Override
        abstract public Layers clone();
    }

    static final public class BedrockLayers extends Layers {

        private final static String JSON_KEY_ROOT = "FlatWorldLayers";
        private static final String JSON_KEY_BIOME_ID = "biome_id";
        private static final String JSON_KEY_LAYERS = "block_layers";
        private static final String JSON_KEY_ENCODING_VERSION = "encoding_version";
        private static final byte JSON_DEFVAL_BIOME_ID = 1;
        private static final int JSON_DEFVAL_ENCODING_VERSION = 4;

        private LevelDat dat;
        private byte biome_id;
        private int encoding_version;

        private BedrockLayers(BedrockLayers src) {
            dat = src.dat;
            biome_id = src.biome_id;
            list = src.list;
        }

        public BedrockLayers(LevelDat dat) {
            this.dat = dat;
            list = new ArrayList<>(64);
        }

        private void createNew() {
            Log.w("233", "Creating new layers.");
            biome_id = JSON_DEFVAL_BIOME_ID;
            encoding_version = JSON_DEFVAL_ENCODING_VERSION;
            list.add(new Layer((byte) 7));
            list.add(new Layer((byte) 3, (byte) 0, (byte) 2));
            list.add(new Layer((byte) 2));
            return;
        }

        @Override
        public int load() {
            JSONObject layers;
            try {
                CompoundMap map = dat.getRoot().getValue();
                if (!map.containsKey(JSON_KEY_ROOT)) {
                    Log.e("23333", "Dat contains no layers info.");
                }
                String str = ((StringTag) map.get(JSON_KEY_ROOT)).getValue();
                Log.i("255", str);
                try {
                    layers = new JSONObject(str);
                } catch (JSONException e) {
                    createNew();
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ERR_LOAD_NO_DATA;
            }
            try {
                biome_id = (byte) layers.getInt(JSON_KEY_BIOME_ID);
            } catch (Exception e) {
                biome_id = 1;
            }
            if (!layers.has(JSON_KEY_LAYERS)) {
                return 0;
            }
            try {
                JSONArray jarr = layers.getJSONArray(JSON_KEY_LAYERS);
                list.clear();
                for (int i = 0, lim = jarr.length(); i < lim; i++) {
                    JSONObject obj = jarr.getJSONObject(i);
                    Layer layer = new Layer();
                    layer.id = (byte) Names.resolve(obj.getString("block_name"));
                    layer.data = (byte) obj.getInt("block_data");
                    layer.count = (byte) obj.getInt("count");
                    list.add(layer);
                }
            } catch (JSONException e) {
            }
            return 0;
        }

        @Override
        public void setBiomeId(byte id) {
            biome_id = id;
        }

        @Override
        public byte getBiomeId() {
            return biome_id;
        }

        @Override
        public void save() {
            try {
                JSONObject jso = new JSONObject();
                jso.put(JSON_KEY_BIOME_ID, biome_id);
                int jarrlen = list.size();
                JSONArray jarr = new JSONArray();
                for (int i = 0; i < jarrlen; i++) {
                    JSONObject obj = new JSONObject();
                    Layer layer = list.get(i);
                    obj.put("block_name", "minecraft:" + Names.getName(layer.id));
                    obj.put("block_data", layer.data);
                    obj.put("count", layer.count);
                    jarr.put(i, obj);
                }
                jso.put(JSON_KEY_LAYERS, jarr);
                jso.put("encoding_version", 4);
                jso.put("structure_options", null);
                FileUtil.writeTextFile(new File("/sdcard/#aaa8/aaa.txt"), jso.toString(4));
                dat.getRoot().getValue().put(
                        new StringTag("FlatWorldLayers", jso.toString(4)));

                dat.save();
            } catch (Exception why) {
                why.printStackTrace();
            }
        }

        @Override
        public BedrockLayers clone() {
            BedrockLayers layers = new BedrockLayers(this);
            return layers;
        }
    }

    //Not supported yet.
    static final public class PocketLayers extends Layers {

        private static final String FLATWORLDLAYERS = "game_flatworldlayers";

        private DB database;

        public PocketLayers(DB database) {
            this.database = database;
            list = new ArrayList<>(64);
        }

        private boolean is_blank(byte b) {
            if (b == ' ') return true;
            if (b == '\n') return true;
            if (b == '\t') return true;
            if (b == '\r') return true;
            return false;
        }

        private int parse_digit(byte b) {
            if (b < '0') return -1;
            if (b <= '9') return b - '0';
            return -1;
        }

        private boolean parseLayersString(byte[] str) {
            int state = 0;
            int last_id = -1;
            int count = 0;
            int id = 0;
            // 0: init, expecting '['
            // 1: beginning of int ignoring blank
            // 2: in progress of int expecting blank, comma or ']'
            // 3: end of int expecting comma or ']'
            // After ']' ignores all things behind
            for (byte b : str) {
                int comma = 0;
                switch (state) {
                    case 0:
                        if (b == '[') {
                            state = 1;
                        } else {
                            return false;
                        }
                        break;
                    case 1: {
                        int i;
                        if ((i = parse_digit(b)) != -1) {
                            state = 2;
                            id = i;
                        } else if (!is_blank(b)) {
                            return false;
                        }
                    }
                    break;
                    case 2:
                        int i;
                        if ((i = parse_digit(b)) != -1) {
                            id = 10 * id + i;
                        } else if (is_blank(b)) {
                            state = 3;
                        } else if (b == ',') {
                            comma = 1;
                            state = 1;
                        } else if (b == ']') {
                            comma = 2;
                        } else {
                            return false;
                        }
                        break;
                    case 3:
                        if (b == ',') {
                            comma = 1;
                            state = 1;
                        } else if (b == ']') {
                            comma = 2;
                        } else {
                            return false;
                        }
                        break;
                }
                if (comma != 0) {
                    if (last_id == id) {
                        count++;
                    } else {
                        if (last_id != -1) {
                            Layer layer = new Layer();
                            layer.id = (byte) last_id;
                            layer.data = 0;
                            layer.count = (byte) count;
                            list.add(layer);
                        }
                        last_id = id;
                        id = -1;
                        count = 1;
                    }
                }
                if (comma == 2 && last_id != -1) {
                    Layer layer = new Layer();
                    layer.id = (byte) last_id;
                    layer.data = 0;
                    layer.count = 1;
                    list.add(layer);
                }
            }
            return true;
        }

        @Override
        public int load() {
            byte[] res;
            try {
                res = database.get(
                        FLATWORLDLAYERS.getBytes("UTF-8"));
                if (res == null) {
                    list.add(new Layer((byte) 7));
                    list.add(new Layer((byte) 3, (byte) 0, (byte) 2));
                    list.add(new Layer((byte) 2));
                    return 0;
                }
                StringBuilder sb = new StringBuilder();
                for (byte bbb : res) {
                    sb.append(bbb);
                    sb.append(",");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return ERR_LOAD_CORRUPT;
            }
            boolean b = parseLayersString(res);
            if (b) return 0;
            list.clear();
            return ERR_LOAD_CORRUPT;
        }

        @Override
        public void setBiomeId(byte id) {
            //
        }

        @Override
        public byte getBiomeId() {
            return 1;
        }

        @Override
        public void save() {
            StringBuilder sb = new StringBuilder("[");
            for (Layer layer : list) {
                for (byte i = 0; i < layer.count; i++) {
                    sb.append(layer.id);
                    sb.append(",");
                }
            }
            sb.setCharAt(sb.length() - 1, ']');
            String str = sb.toString();
            Log.e("", str);
            byte[] key, val;
            try {
                key = FLATWORLDLAYERS.getBytes(UTF_8);
                val = str.getBytes(UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }
            database.put(key, val);
        }

        @Override
        public Layers clone() {
            return null;
        }
    }

    static final public class DummyLayers extends Layers {
        @Override
        public int load() {
            return 0;
        }

        @Override
        public void setBiomeId(byte id) {
        }

        @Override
        public byte getBiomeId() {
            return 0;
        }

        @Override
        public void save() {
        }

        @Override
        public Layers clone() {
            return null;
        }
    }

    static public class Layer {
        public byte id;
        public byte data;
        public byte count;

        public Layer() {
            this.data = 0;
            this.count = 1;
        }

        public Layer(byte id) {
            this.id = id;
            this.data = 0;
            this.count = 1;
        }

        public Layer(byte id, byte date, byte count) {
            this.id = id;
            this.data = data;
            this.count = count;
        }
    }

}
