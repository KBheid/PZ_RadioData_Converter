package LuaJavaDefines;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Container {
    public String name;
    public int rolls = -1;
    public List<Item> items = new ArrayList<>();
    boolean procedural;
    public boolean hasParentLocation;

/*    public Container(String line) {
        line = line.substring("container[".length(), line.length() - 1);
        Map<String, String> vals = Item.getValMap(line.split("\\|"));

        name = vals.get("name");
        hasParentLocation = vals.containsKey("location");
        if (vals.containsKey("rolls"))
            rolls = Integer.parseInt(vals.get("rolls"));

        procedural = vals.containsKey("procedural");
    }*/

    public Container(String name, int rolls, List<Item> items, boolean procedural) {
        this.name = name;
        this.rolls = rolls;
        this.items = items;
        this.procedural = procedural;
    }

    boolean containsItem(String itemName) {
        for (Item i : items)
            if (i.name.equals(itemName))
                return true;
        return false;
    }

    public List<Item> getItem(String itemName) {
        List<Item> out = new ArrayList<>();

        for (Item i : items)
            if (i.name.equals(itemName))
                out.add(i);
        return out;
    }
}
