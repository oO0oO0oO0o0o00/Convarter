package rbq2012.convarter.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public final class BlockNames {

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

    public static void loadBlockNames(String jsonText) {
        if (block_names != null) return;
        //android.util.Log.e("123", jsonText);
        //jsonText = new String(jsonText.getBytes(Charset.forName("UTF-8")));
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
                JSONObject readables = jso.getJSONObject("readables");
                locale_names[id] = new HashMap<>(2);
                for (java.util.Iterator<String> iter = readables.keys(); iter.hasNext(); ) {
                    String key = iter.next();
                    if (key.equals("default")) readable_names[id] = readables.getString(key);
                    else locale_names[id].put(key, readables.getString(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        ///What the Fuck are you doing??????????????????????????????????????????
        ///???????????????????????????????????????????????
        ///????????????????????????
        ///??????
        ///??
        ///?
        loadBlockNames("{\n" +
                "    \"blocks\": [\n" +
                "        {\n" +
                "            \"id\": \"0\",\n" +
                "            \"name\": \"air\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Air\",\n" +
                "                \"zh_cn\": \"空气\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"1\",\n" +
                "            \"name\": \"stone\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stone\",\n" +
                "                \"zh_cn\": \"石头\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"2\",\n" +
                "            \"name\": \"grass\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Grass Block\",\n" +
                "                \"zh_cn\": \"草方块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"3\",\n" +
                "            \"name\": \"dirt\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Dirt\",\n" +
                "                \"zh_cn\": \"泥土\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"4\",\n" +
                "            \"name\": \"cobblestone\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Cobblestone\",\n" +
                "                \"zh_cn\": \"圆石\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"5\",\n" +
                "            \"name\": \"planks\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Wood Planks\",\n" +
                "                \"zh_cn\": \"木板\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"6\",\n" +
                "            \"name\": \"sapling\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Sapling\",\n" +
                "                \"zh_cn\": \"树苗\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"7\",\n" +
                "            \"name\": \"bedrock\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Bedrock\",\n" +
                "                \"zh_cn\": \"基岩\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"8\",\n" +
                "            \"name\": \"flowing_water\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Water\",\n" +
                "                \"zh_cn\": \"水\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"9\",\n" +
                "            \"name\": \"water\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stationary Water\",\n" +
                "                \"zh_cn\": \"静止的水\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"10\",\n" +
                "            \"name\": \"flowing_lava\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Lava\",\n" +
                "                \"zh_cn\": \"岩浆\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"11\",\n" +
                "            \"name\": \"lava\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stationary Lava\",\n" +
                "                \"zh_cn\": \"静止的岩浆\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"12\",\n" +
                "            \"name\": \"sand\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Sand\",\n" +
                "                \"zh_cn\": \"沙子\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"13\",\n" +
                "            \"name\": \"gravel\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Gravel\",\n" +
                "                \"zh_cn\": \"沙砾\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"14\",\n" +
                "            \"name\": \"gold_ore\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Gold Ore\",\n" +
                "                \"zh_cn\": \"金矿\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"15\",\n" +
                "            \"name\": \"iron_ore\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Iron Ore\",\n" +
                "                \"zh_cn\": \"铁矿\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"16\",\n" +
                "            \"name\": \"coal_ore\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Coal Ore\",\n" +
                "                \"zh_cn\": \"煤矿\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"17\",\n" +
                "            \"name\": \"log\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Wood\",\n" +
                "                \"zh_cn\": \"木头\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"18\",\n" +
                "            \"name\": \"leaves\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Leaves\",\n" +
                "                \"zh_cn\": \"树叶\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"19\",\n" +
                "            \"name\": \"sponge\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Sponge\",\n" +
                "                \"zh_cn\": \"海绵\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"20\",\n" +
                "            \"name\": \"glass\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Glass\",\n" +
                "                \"zh_cn\": \"玻璃\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"21\",\n" +
                "            \"name\": \"lapis_ore\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Lapis Lazuli Ore\",\n" +
                "                \"zh_cn\": \"青金石矿\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"22\",\n" +
                "            \"name\": \"lapis_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Lapis Lazuli Block\",\n" +
                "                \"zh_cn\": \"青金石\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"23\",\n" +
                "            \"name\": \"dispenser\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Dispenser\",\n" +
                "                \"zh_cn\": \"发射器\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"24\",\n" +
                "            \"name\": \"sandstone\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Sandstone\",\n" +
                "                \"zh_cn\": \"沙石\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"25\",\n" +
                "            \"name\": \"noteblock\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Note Block\",\n" +
                "                \"zh_cn\": \"笔记\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"26\",\n" +
                "            \"name\": \"bed\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Bed\",\n" +
                "                \"zh_cn\": \"床\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"27\",\n" +
                "            \"name\": \"golden_rail\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Powered Rail\",\n" +
                "                \"zh_cn\": \"动力铁轨\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"28\",\n" +
                "            \"name\": \"detector_rail\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Detector Rail\",\n" +
                "                \"zh_cn\": \"检测铁轨\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"29\",\n" +
                "            \"name\": \"sticky_piston\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Sticky Piston\",\n" +
                "                \"zh_cn\": \"粘性活塞\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"30\",\n" +
                "            \"name\": \"web\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Cobweb\",\n" +
                "                \"zh_cn\": \"蜘蛛网\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"31\",\n" +
                "            \"name\": \"tallgrass\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Tall Grass\",\n" +
                "                \"zh_cn\": \"高草\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"32\",\n" +
                "            \"name\": \"deadbush\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Dead Bush\",\n" +
                "                \"zh_cn\": \"枯树\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"33\",\n" +
                "            \"name\": \"piston\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Piston\",\n" +
                "                \"zh_cn\": \"活塞\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"34\",\n" +
                "            \"name\": \"pistonarmcollision\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Piston Head\",\n" +
                "                \"zh_cn\": \"活塞头\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"35\",\n" +
                "            \"name\": \"wool\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Wool\",\n" +
                "                \"zh_cn\": \"羊毛\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"37\",\n" +
                "            \"name\": \"yellow_flower\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Dandelion\",\n" +
                "                \"zh_cn\": \"黄花\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"38\",\n" +
                "            \"name\": \"red_flower\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Flower\",\n" +
                "                \"zh_cn\": \"花\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"39\",\n" +
                "            \"name\": \"brown_mushroom\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Brown Mushroom\",\n" +
                "                \"zh_cn\": \"棕蘑菇\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"40\",\n" +
                "            \"name\": \"red_mushroom\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Red Mushroom\",\n" +
                "                \"zh_cn\": \"红蘑菇\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"41\",\n" +
                "            \"name\": \"gold_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Block of Gold\",\n" +
                "                \"zh_cn\": \"金块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"42\",\n" +
                "            \"name\": \"iron_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Block of Iron\",\n" +
                "                \"zh_cn\": \"铁块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"43\",\n" +
                "            \"name\": \"double_stone_slab\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Double Stone Slab\",\n" +
                "                \"zh_cn\": \"双石半砖\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"44\",\n" +
                "            \"name\": \"stone_slab\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stone Slab\",\n" +
                "                \"zh_cn\": \"石半砖\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"45\",\n" +
                "            \"name\": \"brick_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Bricks\",\n" +
                "                \"zh_cn\": \"砖\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"46\",\n" +
                "            \"name\": \"tnt\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"TNT\",\n" +
                "                \"zh_cn\": \"TNT\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"47\",\n" +
                "            \"name\": \"bookshelf\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Bookshelf\",\n" +
                "                \"zh_cn\": \"书架\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"48\",\n" +
                "            \"name\": \"mossy_cobblestone\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Moss Stone\",\n" +
                "                \"zh_cn\": \"苔石\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"49\",\n" +
                "            \"name\": \"obsidian\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Obsidian\",\n" +
                "                \"zh_cn\": \"黑曜石\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"50\",\n" +
                "            \"name\": \"torch\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Torch\",\n" +
                "                \"zh_cn\": \"火把\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"51\",\n" +
                "            \"name\": \"fire\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Fire\",\n" +
                "                \"zh_cn\": \"火\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"52\",\n" +
                "            \"name\": \"mob_spawner\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Monster Spawner\",\n" +
                "                \"zh_cn\": \"刷怪笼\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"53\",\n" +
                "            \"name\": \"oak_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Oak Wood Stairs\",\n" +
                "                \"zh_cn\": \"木楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"54\",\n" +
                "            \"name\": \"chest\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Chest\",\n" +
                "                \"zh_cn\": \"箱子\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"55\",\n" +
                "            \"name\": \"redstone_wire\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Redstone Wire\",\n" +
                "                \"zh_cn\": \"红石线\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"56\",\n" +
                "            \"name\": \"diamond_ore\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Diamond Ore\",\n" +
                "                \"zh_cn\": \"钻石矿\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"57\",\n" +
                "            \"name\": \"diamond_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Block of Diamond\",\n" +
                "                \"zh_cn\": \"钻石块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"58\",\n" +
                "            \"name\": \"crafting_table\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Crafting Table\",\n" +
                "                \"zh_cn\": \"工作台\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"59\",\n" +
                "            \"name\": \"wheat\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Crops\",\n" +
                "                \"zh_cn\": \"小麦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"60\",\n" +
                "            \"name\": \"farmland\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Farmland\",\n" +
                "                \"zh_cn\": \"耕地\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"61\",\n" +
                "            \"name\": \"furnace\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Furnace\",\n" +
                "                \"zh_cn\": \"熔炉\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"62\",\n" +
                "            \"name\": \"lit_furnace\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Burning Furnace\",\n" +
                "                \"zh_cn\": \"燃烧的熔炉\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"63\",\n" +
                "            \"name\": \"standing_sign\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Sign\",\n" +
                "                \"zh_cn\": \"木牌\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"64\",\n" +
                "            \"name\": \"wooden_door\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Wooden Door\",\n" +
                "                \"zh_cn\": \"木门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"65\",\n" +
                "            \"name\": \"ladder\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Ladder\",\n" +
                "                \"zh_cn\": \"梯子\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"66\",\n" +
                "            \"name\": \"rail\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Rail\",\n" +
                "                \"zh_cn\": \"铁轨\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"67\",\n" +
                "            \"name\": \"stone_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Cobblestone Stairs\",\n" +
                "                \"zh_cn\": \"圆石楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"68\",\n" +
                "            \"name\": \"wall_sign\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Wall Sign\",\n" +
                "                \"zh_cn\": \"墙上的木牌\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"69\",\n" +
                "            \"name\": \"lever\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Lever\",\n" +
                "                \"zh_cn\": \"拉杆\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"70\",\n" +
                "            \"name\": \"stone_pressure_plate\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stone Pressure Plate\",\n" +
                "                \"zh_cn\": \"石压力板\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"71\",\n" +
                "            \"name\": \"iron_door\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Iron Door\",\n" +
                "                \"zh_cn\": \"铁门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"72\",\n" +
                "            \"name\": \"oak_pressure_plate\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Oak Pressure Plate\",\n" +
                "                \"zh_cn\": \"木压力板\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"73\",\n" +
                "            \"name\": \"redstone_ore\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Redstone Ore\",\n" +
                "                \"zh_cn\": \"红石矿\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"74\",\n" +
                "            \"name\": \"lit_redstone_ore\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Glowing Redstone Ore\",\n" +
                "                \"zh_cn\": \"发光的红石矿\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"75\",\n" +
                "            \"name\": \"unlit_redstone_torch\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Redstone Torch (inactive)\",\n" +
                "                \"zh_cn\": \"红石火把（未激活）\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"76\",\n" +
                "            \"name\": \"lit_redstone_torch\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Redstone Torch (active)\",\n" +
                "                \"zh_cn\": \"红石火把（激活）\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"77\",\n" +
                "            \"name\": \"stone_button\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stone Button\",\n" +
                "                \"zh_cn\": \"石按钮\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"78\",\n" +
                "            \"name\": \"snow_layer\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Top Snow\",\n" +
                "                \"zh_cn\": \"积雪\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"79\",\n" +
                "            \"name\": \"ice\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Ice\",\n" +
                "                \"zh_cn\": \"冰\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"80\",\n" +
                "            \"name\": \"snow\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Snow\",\n" +
                "                \"zh_cn\": \"雪\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"81\",\n" +
                "            \"name\": \"cactus\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Cactus\",\n" +
                "                \"zh_cn\": \"仙人掌\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"82\",\n" +
                "            \"name\": \"clay\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Clay\",\n" +
                "                \"zh_cn\": \"黏土\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"83\",\n" +
                "            \"name\": \"reeds\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Sugar Cane\",\n" +
                "                \"zh_cn\": \"甘蔗\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"84\",\n" +
                "            \"name\": \"jukebox\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Jukebox\",\n" +
                "                \"zh_cn\": \"唱片机\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"85\",\n" +
                "            \"name\": \"fence\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Oak Fence\",\n" +
                "                \"zh_cn\": \"木栅栏\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"86\",\n" +
                "            \"name\": \"pumpkin\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Pumpkin\",\n" +
                "                \"zh_cn\": \"南瓜\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"87\",\n" +
                "            \"name\": \"netherrack\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Netherrack\",\n" +
                "                \"zh_cn\": \"地狱岩\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"88\",\n" +
                "            \"name\": \"soul_sand\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Soul Sand\",\n" +
                "                \"zh_cn\": \"灵魂沙\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"89\",\n" +
                "            \"name\": \"glowstone\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Glowstone\",\n" +
                "                \"zh_cn\": \"萤石\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"90\",\n" +
                "            \"name\": \"portal\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Portal\",\n" +
                "                \"zh_cn\": \"传送门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"91\",\n" +
                "            \"name\": \"lit_pumpkin\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Jack o'Lantern\",\n" +
                "                \"zh_cn\": \"南瓜灯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"92\",\n" +
                "            \"name\": \"cake\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Cake\",\n" +
                "                \"zh_cn\": \"蛋糕\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"93\",\n" +
                "            \"name\": \"unpowered_repeater\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Redstone Repeater (inactive)\",\n" +
                "                \"zh_cn\": \"红石重复（未激活）\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"94\",\n" +
                "            \"name\": \"powered_repeater\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Redstone Repeater (active)\",\n" +
                "                \"zh_cn\": \"红石重复（激活）\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"95\",\n" +
                "            \"name\": \"invisiblebedrock\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Invisible Bedrock\",\n" +
                "                \"zh_cn\": \"隐形基岩\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"96\",\n" +
                "            \"name\": \"oak_trapdoor\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Oak Trapdoor\",\n" +
                "                \"zh_cn\": \"木陷阱门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"97\",\n" +
                "            \"name\": \"monster_egg\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Monster Egg\",\n" +
                "                \"zh_cn\": \"怪物卵\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"98\",\n" +
                "            \"name\": \"stonebrick\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stone Bricks\",\n" +
                "                \"zh_cn\": \"石砖块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"99\",\n" +
                "            \"name\": \"brown_mushroom_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Brown Mushroom\",\n" +
                "                \"zh_cn\": \"棕蘑菇块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"100\",\n" +
                "            \"name\": \"red_mushroom_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Red Mushroom\",\n" +
                "                \"zh_cn\": \"红蘑菇块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"101\",\n" +
                "            \"name\": \"iron_bars\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Iron Bars\",\n" +
                "                \"zh_cn\": \"铁栅栏\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"102\",\n" +
                "            \"name\": \"glass_pane\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Glass Pane\",\n" +
                "                \"zh_cn\": \"玻璃板\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"103\",\n" +
                "            \"name\": \"melon_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Melon\",\n" +
                "                \"zh_cn\": \"西瓜\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"104\",\n" +
                "            \"name\": \"pumpkin_stem\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Pumpkin Stem\",\n" +
                "                \"zh_cn\": \"南瓜藤\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"105\",\n" +
                "            \"name\": \"melon_stem\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Melon Stem\",\n" +
                "                \"zh_cn\": \"西瓜藤\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"106\",\n" +
                "            \"name\": \"vine\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Vines\",\n" +
                "                \"zh_cn\": \"蕨\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"107\",\n" +
                "            \"name\": \"fence_gate\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Oak Fence Gate\",\n" +
                "                \"zh_cn\": \"木栅栏门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"108\",\n" +
                "            \"name\": \"brick_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Brick Stairs\",\n" +
                "                \"zh_cn\": \"砖楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"109\",\n" +
                "            \"name\": \"stone_brick_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stone Brick Stairs\",\n" +
                "                \"zh_cn\": \"石砖楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"110\",\n" +
                "            \"name\": \"mycelium\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Mycelium\",\n" +
                "                \"zh_cn\": \"Mycelium\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"111\",\n" +
                "            \"name\": \"waterlily\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Lily Pad\",\n" +
                "                \"zh_cn\": \"荷叶\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"112\",\n" +
                "            \"name\": \"nether_brick\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Nether Brick\",\n" +
                "                \"zh_cn\": \"地狱砖\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"113\",\n" +
                "            \"name\": \"nether_brick_fence\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Nether Brick Fence\",\n" +
                "                \"zh_cn\": \"地狱砖栅栏\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"114\",\n" +
                "            \"name\": \"nether_brick_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Nether Brick Stairs\",\n" +
                "                \"zh_cn\": \"地狱砖楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"115\",\n" +
                "            \"name\": \"nether_wart\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Nether Wart\",\n" +
                "                \"zh_cn\": \"地狱疣\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"116\",\n" +
                "            \"name\": \"enchanting_table\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Enchantment Table\",\n" +
                "                \"zh_cn\": \"附魔台\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"117\",\n" +
                "            \"name\": \"brewing_stand\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Brewing Stand\",\n" +
                "                \"zh_cn\": \"酿造台\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"118\",\n" +
                "            \"name\": \"cauldron\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Cauldron\",\n" +
                "                \"zh_cn\": \"坩埚\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"119\",\n" +
                "            \"name\": \"end_portal\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"End Portal\",\n" +
                "                \"zh_cn\": \"末地传送门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"120\",\n" +
                "            \"name\": \"end_portal_frame\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"End Portal Frame\",\n" +
                "                \"zh_cn\": \"末地传送门框架\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"121\",\n" +
                "            \"name\": \"end_stone\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"End Stone\",\n" +
                "                \"zh_cn\": \"末地石\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"122\",\n" +
                "            \"name\": \"dragon_egg\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Dragon Egg\",\n" +
                "                \"zh_cn\": \"杀妈蛋\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"123\",\n" +
                "            \"name\": \"redstone_lamp\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Redstone Lamp (inactive)\",\n" +
                "                \"zh_cn\": \"红石灯（未激活）\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"124\",\n" +
                "            \"name\": \"lit_redstone_lamp\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Redstone Lamp (active)\",\n" +
                "                \"zh_cn\": \"红石灯（激活）\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"125\",\n" +
                "            \"name\": \"dropper\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Dropper\",\n" +
                "                \"zh_cn\": \"点滴器\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"126\",\n" +
                "            \"name\": \"activator_rail\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Activator Rail\",\n" +
                "                \"zh_cn\": \"激活铁轨\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"127\",\n" +
                "            \"name\": \"cocoa\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Cocoa\",\n" +
                "                \"zh_cn\": \"可可豆\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"128\",\n" +
                "            \"name\": \"sandstone_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Sandstone Stairs\",\n" +
                "                \"zh_cn\": \"沙石楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"129\",\n" +
                "            \"name\": \"emerald_ore\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Emerald Ore\",\n" +
                "                \"zh_cn\": \"绿宝石矿\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"130\",\n" +
                "            \"name\": \"ender_chest\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Ender Chest\",\n" +
                "                \"zh_cn\": \"末影箱\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"131\",\n" +
                "            \"name\": \"tripwire_hook\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Tripwire Hook\",\n" +
                "                \"zh_cn\": \"拌线钩\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"132\",\n" +
                "            \"name\": \"tripwire\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Tripwire\",\n" +
                "                \"zh_cn\": \"拌线\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"133\",\n" +
                "            \"name\": \"emerald_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Block of Emerald\",\n" +
                "                \"zh_cn\": \"绿宝石块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"134\",\n" +
                "            \"name\": \"spruce_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Spruce Wood Stairs\",\n" +
                "                \"zh_cn\": \"云杉木楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"135\",\n" +
                "            \"name\": \"birch_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Birch Wood Stairs\",\n" +
                "                \"zh_cn\": \"橡木楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"136\",\n" +
                "            \"name\": \"jungle_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Jungle Wood Stairs\",\n" +
                "                \"zh_cn\": \"丛林木楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"137\",\n" +
                "            \"name\": \"command_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Command Block\",\n" +
                "                \"zh_cn\": \"命令块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"138\",\n" +
                "            \"name\": \"beacon\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Beacon\",\n" +
                "                \"zh_cn\": \"信标\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"139\",\n" +
                "            \"name\": \"cobblestone_wall\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Cobblestone Wall\",\n" +
                "                \"zh_cn\": \"圆石墙\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"140\",\n" +
                "            \"name\": \"flower_pot\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Flower Pot\",\n" +
                "                \"zh_cn\": \"花盆\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"141\",\n" +
                "            \"name\": \"carrots\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Carrots\",\n" +
                "                \"zh_cn\": \"胡萝卜\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"142\",\n" +
                "            \"name\": \"potatoes\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Potato\",\n" +
                "                \"zh_cn\": \"土豆\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"143\",\n" +
                "            \"name\": \"oak_button\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Oak Button\",\n" +
                "                \"zh_cn\": \"木按钮\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"144\",\n" +
                "            \"name\": \"skull\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Mob head\",\n" +
                "                \"zh_cn\": \"怪物头\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"145\",\n" +
                "            \"name\": \"anvil\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Anvil\",\n" +
                "                \"zh_cn\": \"铁砧\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"146\",\n" +
                "            \"name\": \"trapped_chest\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Trapped Chest\",\n" +
                "                \"zh_cn\": \"陷阱箱\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"147\",\n" +
                "            \"name\": \"light_weighted_pressure_plate\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Weighted Pressure Plate (Light)\",\n" +
                "                \"zh_cn\": \"重量压力板（轻）\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"148\",\n" +
                "            \"name\": \"heavy_weighted_pressure_plate\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Weighted Pressure Plate (Heavy)\",\n" +
                "                \"zh_cn\": \"重量压力板（重）\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"149\",\n" +
                "            \"name\": \"unpowered_comparator\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Redstone Comparator (unpowered)\",\n" +
                "                \"zh_cn\": \"红石比较器（未激活）\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"150\",\n" +
                "            \"name\": \"powered_comparator\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Redstone Comparator (powered)\",\n" +
                "                \"zh_cn\": \"红石比较器（激活）\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"151\",\n" +
                "            \"name\": \"daylight_detector\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Daylight Sensor\",\n" +
                "                \"zh_cn\": \"感光板\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"152\",\n" +
                "            \"name\": \"redstone_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Block of Redstone\",\n" +
                "                \"zh_cn\": \"红石块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"153\",\n" +
                "            \"name\": \"quartz_ore\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Nether Quartz Ore\",\n" +
                "                \"zh_cn\": \"地狱石英矿\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"154\",\n" +
                "            \"name\": \"hopper\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Hopper\",\n" +
                "                \"zh_cn\": \"漏斗\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"155\",\n" +
                "            \"name\": \"quartz_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Block of Quartz\",\n" +
                "                \"zh_cn\": \"石英块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"156\",\n" +
                "            \"name\": \"quartz_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Quartz Stairs\",\n" +
                "                \"zh_cn\": \"石英楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"157\",\n" +
                "            \"name\": \"double_wooden_slab\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Wooden Double Slab\",\n" +
                "                \"zh_cn\": \"双木半砖\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"158\",\n" +
                "            \"name\": \"wooden_slab\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Wooden Slab\",\n" +
                "                \"zh_cn\": \"木半砖\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"159\",\n" +
                "            \"name\": \"stained_hardened_clay\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Terracotta\",\n" +
                "                \"zh_cn\": \"染色陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"160\",\n" +
                "            \"name\": \"stained_glass_pane\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stained Glass Pane\",\n" +
                "                \"zh_cn\": \"染色玻璃板\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"161\",\n" +
                "            \"name\": \"leaves2\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Acacia Leaves\",\n" +
                "                \"zh_cn\": \"金合欢树叶\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"162\",\n" +
                "            \"name\": \"log2\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Acacia Log\",\n" +
                "                \"zh_cn\": \"金合欢木\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"163\",\n" +
                "            \"name\": \"acacia_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Acacia Wood Stairs\",\n" +
                "                \"zh_cn\": \"金合欢木楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"164\",\n" +
                "            \"name\": \"dark_oak_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Dark Oak Wood Stairs\",\n" +
                "                \"zh_cn\": \"黑橡木楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"165\",\n" +
                "            \"name\": \"slime\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Slime Block\",\n" +
                "                \"zh_cn\": \"史莱姆方块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"167\",\n" +
                "            \"name\": \"iron_trapdoor\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Iron Trapdoor\",\n" +
                "                \"zh_cn\": \"铁陷阱门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"168\",\n" +
                "            \"name\": \"prismarine\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Prismarine\",\n" +
                "                \"zh_cn\": \"海晶石\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"169\",\n" +
                "            \"name\": \"sealantern\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Sea Lantern\",\n" +
                "                \"zh_cn\": \"海晶灯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"170\",\n" +
                "            \"name\": \"hay_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Hay Bale\",\n" +
                "                \"zh_cn\": \"干草捆\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"171\",\n" +
                "            \"name\": \"carpet\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Carpet\",\n" +
                "                \"zh_cn\": \"地毯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"172\",\n" +
                "            \"name\": \"terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Terracotta\",\n" +
                "                \"zh_cn\": \"陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"173\",\n" +
                "            \"name\": \"coal_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Block of Coal\",\n" +
                "                \"zh_cn\": \"煤炭块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"174\",\n" +
                "            \"name\": \"packed_ice\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Packed Ice\",\n" +
                "                \"zh_cn\": \"密集冰\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"175\",\n" +
                "            \"name\": \"double_plant\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Sunflower\",\n" +
                "                \"zh_cn\": \"太阳花\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"176\",\n" +
                "            \"name\": \"standing_banner\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Standing Banner\",\n" +
                "                \"zh_cn\": \"立旗\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"177\",\n" +
                "            \"name\": \"wall_banner\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Wall Banner\",\n" +
                "                \"zh_cn\": \"墙上的旗\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"178\",\n" +
                "            \"name\": \"daylight_detector_inverted\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Inverted Daylight Sensor\",\n" +
                "                \"zh_cn\": \"倒置日光传感器\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"179\",\n" +
                "            \"name\": \"red_sandstone\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Red Sandstone\",\n" +
                "                \"zh_cn\": \"红沙石\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"180\",\n" +
                "            \"name\": \"red_sandstone_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Red Sandstone Stairs\",\n" +
                "                \"zh_cn\": \"红沙石楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"181\",\n" +
                "            \"name\": \"double_stone_slab2\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Double Red Sandstone Slab\",\n" +
                "                \"zh_cn\": \"双红沙石半砖\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"182\",\n" +
                "            \"name\": \"stone_slab2\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Red Sandstone Slab\",\n" +
                "                \"zh_cn\": \"红沙石半砖\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"183\",\n" +
                "            \"name\": \"spruce_fence_gate\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Spruce Fence Gate\",\n" +
                "                \"zh_cn\": \"云杉栅栏门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"184\",\n" +
                "            \"name\": \"birch_fence_gate\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Birch Fence Gate\",\n" +
                "                \"zh_cn\": \"桦木栅栏门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"185\",\n" +
                "            \"name\": \"jungle_fence_gate\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Jungle Fence Gate\",\n" +
                "                \"zh_cn\": \"丛林木栅栏门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"186\",\n" +
                "            \"name\": \"dark_oak_fence_gate\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Dark Oak Fence Gate\",\n" +
                "                \"zh_cn\": \"黑橡木栅栏门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"187\",\n" +
                "            \"name\": \"acacia_fence_gate\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Acacia Fence Gate\",\n" +
                "                \"zh_cn\": \"金合欢木栅栏门 Fence Gate\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"188\",\n" +
                "            \"name\": \"repeating_command_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Repeating Command Block\",\n" +
                "                \"zh_cn\": \"重复命令块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"189\",\n" +
                "            \"name\": \"chain_command_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Chain Command Block\",\n" +
                "                \"zh_cn\": \"链命令块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"193\",\n" +
                "            \"name\": \"spruce_door\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Spruce Door\",\n" +
                "                \"zh_cn\": \"云杉门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"194\",\n" +
                "            \"name\": \"birch_door\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Birch Door\",\n" +
                "                \"zh_cn\": \"桦木门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"195\",\n" +
                "            \"name\": \"jungle_door\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Jungle Door\",\n" +
                "                \"zh_cn\": \"丛林木门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"196\",\n" +
                "            \"name\": \"acacia_door\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Acacia Door\",\n" +
                "                \"zh_cn\": \"金合欢木门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"197\",\n" +
                "            \"name\": \"dark_oak_door\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Dark Oak Door\",\n" +
                "                \"zh_cn\": \"黑橡木门\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"198\",\n" +
                "            \"name\": \"grass_path\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Grass Path\",\n" +
                "                \"zh_cn\": \"草路\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"199\",\n" +
                "            \"name\": \"frame\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Item Frame\",\n" +
                "                \"zh_cn\": \"物品框\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"200\",\n" +
                "            \"name\": \"chorus_flower\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Chorus Flower\",\n" +
                "                \"zh_cn\": \"紫颂花\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"201\",\n" +
                "            \"name\": \"purpur_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Purpur Block\",\n" +
                "                \"zh_cn\": \"紫珀块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"202\",\n" +
                "            \"name\": \"purpur_stairs\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Purpur Stairs\",\n" +
                "                \"zh_cn\": \"紫珀楼梯\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"205\",\n" +
                "            \"name\": \"undyed_shulker_box\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"潜影盒 (未染色)\",\n" +
                "                \"zh_cn\": \"\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"206\",\n" +
                "            \"name\": \"end_bricks\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"End Stone Bricks\",\n" +
                "                \"zh_cn\": \"末地石砖\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"207\",\n" +
                "            \"name\": \"frosted_ice\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Frosted Ice\",\n" +
                "                \"zh_cn\": \"Frosted冰\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"208\",\n" +
                "            \"name\": \"end_rod\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"End Rod\",\n" +
                "                \"zh_cn\": \"末地烛\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"209\",\n" +
                "            \"name\": \"end_gateway\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"末地传送门\",\n" +
                "                \"zh_cn\": \"\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"213\",\n" +
                "            \"name\": \"magma\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"熔岩块\",\n" +
                "                \"zh_cn\": \"\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"214\",\n" +
                "            \"name\": \"nether_wart_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Nether Wart Block\",\n" +
                "                \"zh_cn\": \"地狱疣\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"215\",\n" +
                "            \"name\": \"red_nether_brick\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Red Nether Brick\",\n" +
                "                \"zh_cn\": \"红地狱砖\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"216\",\n" +
                "            \"name\": \"bone_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Bone Block\",\n" +
                "                \"zh_cn\": \"骨方块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"218\",\n" +
                "            \"name\": \"shulker_box\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Shulker Box\",\n" +
                "                \"zh_cn\": \"潜影盒\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"219\",\n" +
                "            \"name\": \"purple_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Purple Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"紫色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"220\",\n" +
                "            \"name\": \"white_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"White Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"白色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"221\",\n" +
                "            \"name\": \"orange_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Orange Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"橙色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"222\",\n" +
                "            \"name\": \"magenta_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Magenta Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"洋红带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"223\",\n" +
                "            \"name\": \"light_blue_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Light Blue Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"浅蓝带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"224\",\n" +
                "            \"name\": \"yellow_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Yellow Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"黄色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"225\",\n" +
                "            \"name\": \"lime_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Lime Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"黄绿带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"226\",\n" +
                "            \"name\": \"pink_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Pink Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"粉色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"227\",\n" +
                "            \"name\": \"gray_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Gray Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"灰色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"228\",\n" +
                "            \"name\": \"silver_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Light Gray Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"浅灰带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"229\",\n" +
                "            \"name\": \"cyan_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Cyan Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"青色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"231\",\n" +
                "            \"name\": \"blue_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Blue Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"蓝色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"232\",\n" +
                "            \"name\": \"brown_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Brown Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"棕色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"233\",\n" +
                "            \"name\": \"green_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Green Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"绿色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"234\",\n" +
                "            \"name\": \"red_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Red Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"红色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"235\",\n" +
                "            \"name\": \"black_glazed_terracotta\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Black Glazed Terracotta\",\n" +
                "                \"zh_cn\": \"黑色带釉陶瓦\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"236\",\n" +
                "            \"name\": \"concrete\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Concrete\",\n" +
                "                \"zh_cn\": \"混凝土\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"237\",\n" +
                "            \"name\": \"concretepowder\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Concrete Powder\",\n" +
                "                \"zh_cn\": \"混凝土粉末\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"240\",\n" +
                "            \"name\": \"chorus_plant\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Chorus Plant\",\n" +
                "                \"zh_cn\": \"紫颂植物\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"241\",\n" +
                "            \"name\": \"stained_glass\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stained Glass\",\n" +
                "                \"zh_cn\": \"染色玻璃\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"243\",\n" +
                "            \"name\": \"podzol\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Podzol\",\n" +
                "                \"zh_cn\": \"灰化土\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"244\",\n" +
                "            \"name\": \"beetroot\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Beetroot\",\n" +
                "                \"zh_cn\": \"甜菜根\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"245\",\n" +
                "            \"name\": \"stonecutter\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Stonecutter\",\n" +
                "                \"zh_cn\": \"切石机\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"246\",\n" +
                "            \"name\": \"glowingobsidian\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Glowing Obsidian\",\n" +
                "                \"zh_cn\": \"发光的黑曜石\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"247\",\n" +
                "            \"name\": \"netherreactor\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Nether Reactor Core\",\n" +
                "                \"zh_cn\": \"地狱反应堆心\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"248\",\n" +
                "            \"name\": \"info_update\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Update Game Block\",\n" +
                "                \"zh_cn\": \"更新方块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"249\",\n" +
                "            \"name\": \"info_update2\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Update Game Block\",\n" +
                "                \"zh_cn\": \"更新方块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"250\",\n" +
                "            \"name\": \"movingblock\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Block moved by Piston\",\n" +
                "                \"zh_cn\": \"被活塞推动的方块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"251\",\n" +
                "            \"name\": \"observer\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Observer\",\n" +
                "                \"zh_cn\": \"侦测器\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"252\",\n" +
                "            \"name\": \"structure_block\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"Structure Block\",\n" +
                "                \"zh_cn\": \"结构方块\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"255\",\n" +
                "            \"name\": \"reserved6\",\n" +
                "            \"readables\": {\n" +
                "                \"default\": \"reserved6\",\n" +
                "                \"zh_cn\": \"保留6\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}");
    }

}
