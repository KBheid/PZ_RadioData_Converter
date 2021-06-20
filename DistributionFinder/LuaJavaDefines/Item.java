package LuaJavaDefines;

import java.util.HashMap;
import java.util.Map;

public class Item {
    public String name;
    public float odds;

    /*public Item(String line) {
        line = line.substring("item[".length(), line.length() - 1);
        Map<String, String> vals = getValMap(line.split("\\|"));

        name = vals.get("name");
        odds = Float.parseFloat(vals.get("odds"));
    }*/

    public Item(String name, float odds) {
        this.name = name;
        this.odds = odds;
    }

    public static Map<String, String> getValMap(String[] inp) {
        Map<String, String> retMap = new HashMap<>();

        for (String s : inp) {
            String[] keyVal = s.split(":");
            retMap.put(keyVal[0], (keyVal.length == 2) ? keyVal[1] : "");
        }

        return retMap;
    }
}
