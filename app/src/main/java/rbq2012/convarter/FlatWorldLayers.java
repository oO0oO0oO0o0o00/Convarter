package rbq2012.convarter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spout.nbt.StringTag;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import rbq2012.ldbchunk.DB;
import rbq2012.ldbchunk.Names;


/**
 * Created by barco on 2018/3/16.
 */

public final class FlatWorldLayers {

    public static final String JSON_KEY_BIOME_ID = "biome_id";
    public static final String JSON_KEY_BLOCK_LAYERS = "block_layers";
    static private FlatWorldLayers instance = null;

    static public Layers newFlatWorldLayers(LevelDat dat) {
        if (instance == null) instance = new FlatWorldLayers();
        return instance.new BedrockLayers(dat);
    }

    static public Layers newFlatWorldLayers(DB database) {
        if (instance == null) instance = new FlatWorldLayers();
        return instance.new PocketLayers(database);
    }

    public abstract class Layers {

        protected List<Byte> list;

        abstract public void load();

        abstract public void setBiomeId(byte id);

        abstract public byte getBiomeId();

        abstract public byte[] getLayers();

        abstract public void deleteLayerAt(int index);

        abstract public byte getLayerIdAt(int index);

        abstract public byte getLayerDataAt(int index);

        abstract public byte getLayerCountAt(int index);

        abstract public void setLayerIdAt(int index, byte id);

        abstract public void setLayerDataAt(int index, byte data);

        abstract public void setLayerCountAt(int index, byte count);

        abstract public void addLayerAt(int index, byte id, byte data, byte count);

        abstract public void save();

    }

    final private class BedrockLayers extends Layers {

        private LevelDat dat;
        private byte biome_id;

        public BedrockLayers(LevelDat dat) {
            this.dat = dat;
            list = new ArrayList<>(256);
            load();
        }

        @Override
        public void load() {
            JSONObject layers;
            try {
                String str = ((StringTag) dat.getRoot().getValue().get("FlatWorldLayers")).getValue();
                layers = new JSONObject(str);
            } catch (Exception e) {
                e.printStackTrace();
                layers = new JSONObject();
            }
            try {
                biome_id = (byte) layers.getInt(JSON_KEY_BIOME_ID);
            } catch (Exception e) {
                biome_id = 1;
            }
            if (!layers.has(JSON_KEY_BLOCK_LAYERS)) {
                return;
            }
            try {
                JSONArray jarr = layers.getJSONArray(JSON_KEY_BLOCK_LAYERS);
                list.clear();
                for (int i = 0, lim = jarr.length(); i < lim; i++) {
                    JSONObject obj = jarr.getJSONObject(i);
                    list.add((byte) Names.resolve(obj.getString("block_name")));
                    list.add((byte) obj.getInt("block_data"));
                    list.add((byte) obj.getInt("count"));
                }
            } catch (JSONException e) {
            }
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
        public byte[] getLayers() {
            int len = list.size();
            byte[] bytes = new byte[len];
            for (int i = 0; i < len; i++) {
                bytes[i] = list.get(i);
            }
            return bytes;
        }

        @Override
        public void deleteLayerAt(int index) {
            int lim = list.size() - 3;
            for (int i = index * 3; i < lim; i++) {
                list.set(i, list.get(i + 3));
            }
            list.remove(lim + 2);
            list.remove(lim + 1);
            list.remove(lim);
        }

        @Override
        public byte getLayerIdAt(int index) {
            return list.get(index * 3);
        }

        @Override
        public byte getLayerDataAt(int index) {
            return list.get(index * 3 + 1);
        }

        @Override
        public byte getLayerCountAt(int index) {
            return list.get(index * 3 + 2);
        }

        @Override
        public void setLayerIdAt(int index, byte id) {
            list.set(index * 3, id);
        }

        @Override
        public void setLayerDataAt(int index, byte data) {
            list.set(index * 3 + 1, data);
        }

        @Override
        public void setLayerCountAt(int index, byte count) {
            list.set(index * 3 + 2, count);
        }

        @Override
        public void addLayerAt(int index, byte id, byte data, byte count) {
            int lim = list.size();
            int base = index * 3;
            if (base >= lim) {
                list.add(id);
                list.add(data);
                list.add(count);
                return;
            }
            list.add(list.get(lim - 3));
            list.add(list.get(lim - 2));
            list.add(list.get(lim - 1));
            lim -= 3;
            for (int i = base; i < lim; i++) {
                list.set(i + 3, list.get(i));
            }
            list.set(base, id);
            list.set(base, data);
            list.set(base, count);
        }

        @Override
        public void save() {
            try {
                JSONObject jso = new JSONObject();
                jso.put(JSON_KEY_BIOME_ID, biome_id);
                int jarrlen = list.size() / 3;
                JSONArray jarr = new JSONArray();
                for (int i = 0; i < jarrlen; i++) {
                    int base = i * 3;
                    JSONObject obj = new JSONObject();
                    obj.put("block_name", "minecraft:air");
                    obj.put("block_data", list.get(base + 1));
                    obj.put("count", list.get(base + 2));
                    jarr.put(i, obj);
                }
                jso.put(JSON_KEY_BLOCK_LAYERS, jarr);
                dat.getRoot().getValue().put(
                        new StringTag("FlatWorldLayers", jso.toString(4)));
                dat.save();
            } catch (Exception why) {
            }
        }
    }

    //Not supported yet.
    final private class PocketLayers extends Layers {

        private DB database;

        public PocketLayers(DB database) {
            this.database = database;
            load();
        }

        @Override
        public void load() {
            //
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
        public byte[] getLayers() {
            return null;
        }

        @Override
        public void deleteLayerAt(int index) {
            //
        }

        @Override
        public byte getLayerIdAt(int index) {
            return 0;
        }

        @Override
        public byte getLayerDataAt(int index) {
            return 0;
        }

        @Override
        public byte getLayerCountAt(int index) {
            return 0;
        }

        @Override
        public void setLayerIdAt(int index, byte id) {
            //
        }

        @Override
        public void setLayerDataAt(int index, byte data) {
            //
        }

        @Override
        public void setLayerCountAt(int index, byte count) {
            //
        }

        @Override
        public void addLayerAt(int index, byte id, byte data, byte count) {
            //
        }

        @Override
        public void save() {
            //
        }
    }

}
