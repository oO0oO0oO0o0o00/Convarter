package rbq2012.ldbchunk;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public final class Names {

    static private String[] block_names = null;
    static private String[] readable_names;
    static private Map<String, String>[] locale_names;

    static public int resolve(String name) {
        if (name.startsWith("minecraft:")) {
            name = name.substring(10);
        }
        for (int i = 0; i <= 255; i++) {
            if (block_names[i] != null &&
                    block_names[i].equals(name)) return i;
        }
        return 0;
    }

    static public String getName(int i) {
        String str = block_names[i];
        if (str == null) return block_names[0];
        return str;
    }

    static public String getNameIfValid(int i) {
        return block_names[i];
    }

    static public String getReadableName(int i) {
        String str = readable_names[i];
        if (str == null) return readable_names[0];
        return str;
    }

    static public String getLocaleName(int i, String locale) {
        if (locale == null) return getReadableName(i);
        String str = locale_names[i].get(locale);
        if (str == null) return getReadableName(i);
        return str;
    }

    static public void loadBlockNames(String jsonText) {
        if (block_names != null) return;
        try {
            JSONObject jso = new JSONObject(jsonText);
            JSONArray jarr = jso.getJSONArray("blocks");
            block_names = new String[256];
            readable_names = new String[256];
            locale_names = new Map[256];
            for (int i = 0, lim = jarr.length(); i < lim; i++) {
                jso = jarr.getJSONObject(i);
                int id = jso.getInt("id");
                String name = jso.getString("name");
                block_names[id] = name;
                registerBlockName(name, (byte) id);
                JSONObject readables = jso.getJSONObject("readables");
                locale_names[id] = new HashMap<>(2);
                for (java.util.Iterator<String> iter = readables.keys(); iter.hasNext(); ) {
                    String key = iter.next();
                    if (key.equals("default")) readable_names[id] = readables.getString(key);
                    else locale_names[id].put(key, readables.getString(key));
                }
            }
            Names.block_names = block_names;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static private native void registerBlockName(String name, byte id);

    static {
        System.loadLibrary("ldbchunkjni");
    }
}
