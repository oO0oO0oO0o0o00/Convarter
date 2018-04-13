package rbq2012.ldbchunk;

import org.json.JSONArray;
import org.json.JSONObject;

public final class Names {

    static private String[] block_names = null;

    static public int resolve(String name) {
        if (name.startsWith("minecraft:")) {
            name = name.substring(10);
        }
        for (int i = 0; i <= 255; i++) {
            if (block_names[i].equals(name)) return i;
        }
        return 0;
    }

    static public void loadBlockNames(String jsonText) {
        if (block_names != null) return;
        try {
            JSONObject jso = new JSONObject(jsonText);
            JSONArray jarr = jso.getJSONArray("blocks");
            String[] names = new String[256];
            for (int i = 0, lim = jarr.length(); i < lim; i++) {
                jso = jarr.getJSONObject(i);
                int id = jso.getInt("id");
                String name = jso.getString("name");
                names[id] = name;
                registerBlockName(name, (byte) id);
            }
            block_names = names;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static private native void registerBlockName(String name, byte id);

    {
        System.loadLibrary("ldbchunkjni");
    }
}
