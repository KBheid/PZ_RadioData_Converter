package LuaJavaDefines;

import java.util.*;

public class Location {
    public String name;
    boolean isShop;
    public List<Container> containers;

    public Location(String name, boolean isShop, List<Container> containers) {
        this.name = name;
        this.isShop = isShop;
        this.containers = containers;
    }

    public boolean containsItem(String itemName) {
        for (Container c : containers) {
            if (c.containsItem(itemName))
                return true;
        }
        return false;
    }

    public Set<Container> getContainersWithItem(String itemName) {
        Set<Container> out = new HashSet<>();
        for (Container c : containers)
            if (c.containsItem(itemName))
                out.add(c);
        return out;
    }

    public Set<String> getContainerNames() {
        Set<String> out = new HashSet<>();
        for (Container c : containers)
            out.add(c.name);

        return out;
    }
}
