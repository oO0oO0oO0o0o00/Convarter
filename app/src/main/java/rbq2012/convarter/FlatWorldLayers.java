package rbq2012.convarter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spout.nbt.StringTag;

import java.io.File;
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

    static public Layers newFlatWorldLayers(LevelDat dat) {
        return new BedrockLayers(dat);
    }

    static public Layers newFlatWorldLayers(DB database) {
        return new PocketLayers(database);
    }

    static public abstract class Layers {

        protected List<Layer> list;

        abstract public void load();

        abstract public void setBiomeId(byte id);

        abstract public byte getBiomeId();

        abstract public byte[] getLayersForNativeUse();

        abstract public int getItemsCount();

        abstract public void deleteLayerAt(int index);

        abstract public byte getLayerIdAt(int index);

        abstract public byte getLayerDataAt(int index);

        abstract public byte getLayerCountAt(int index);

        abstract public void setLayerIdAt(int index, byte id);

        abstract public void setLayerDataAt(int index, byte data);

        abstract public void setLayerCountAt(int index, byte count);

        abstract public void addLayerAt(int index, byte id, byte data, byte count);

        public List<Layer> getLayersForControl() {
            return list;
        }

        abstract public void save();

        @Override
        abstract public Layers clone();
    }

    static final private class BedrockLayers extends Layers {

        private LevelDat dat;
        private byte biome_id;

        private BedrockLayers(BedrockLayers src) {
            dat = src.dat;
            biome_id = src.biome_id;
            list = src.list;
        }

        public BedrockLayers(LevelDat dat) {
            this.dat = dat;
            list = new ArrayList<>(64);
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
                    Layer layer = new Layer();
                    layer.id = (byte) Names.resolve(obj.getString("block_name"));
                    layer.data = (byte) obj.getInt("block_data");
                    layer.count = (byte) obj.getInt("count");
                    list.add(layer);
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
        public byte[] getLayersForNativeUse() {
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

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        public void deleteLayerAt(int index) {
            int lim = list.size() - 1;
            for (int i = index; i < lim; i++) {
                list.set(i, list.get(i + 1));
            }
            list.remove(lim);
        }

        @Override
        public byte getLayerIdAt(int index) {
            return list.get(index).id;
        }

        @Override
        public byte getLayerDataAt(int index) {
            return list.get(index).data;
        }

        @Override
        public byte getLayerCountAt(int index) {
            return list.get(index).count;
        }

        @Override
        public void setLayerIdAt(int index, byte id) {
            list.get(index).id = id;
        }

        @Override
        public void setLayerDataAt(int index, byte data) {
            list.get(index).data = data;
        }

        @Override
        public void setLayerCountAt(int index, byte count) {
            list.get(index).count = count;
        }

        @Override
        public void addLayerAt(int index, byte id, byte data, byte count) {
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
                jso.put(JSON_KEY_BLOCK_LAYERS, jarr);
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
    static final private class PocketLayers extends Layers {

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
        public byte[] getLayersForNativeUse() {
            return null;
        }

        @Override
        public int getItemsCount() {
            return 0;
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

        @Override
        public Layers clone() {
            return null;
        }
    }

    static public class Layer {
        public byte id;
        public byte data;
        public byte count;
    }

}
