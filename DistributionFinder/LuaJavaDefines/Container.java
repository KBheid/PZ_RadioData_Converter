package LuaJavaDefines;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Container {
    public String name;
    public int rolls;
    public List<Item> items;
    boolean procedural;

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
