import LuaJavaDefines.Container;
import LuaJavaDefines.Item;
import LuaJavaDefines.Location;

import java.util.*;

public class OutputParser {
    private Location lastLocation;
    private Container lastContainer;

    public List<Location> locations = new ArrayList<>();
    public List<Container> locationlessContainers = new ArrayList<>();

    /*
    public void parseLine(String line) {
        if (line.startsWith("location")) {
            lastLocation = new Location(line);
            locations.add(lastLocation);
        }

        if (line.startsWith("container")) {
            Container c = new Container(line);
            lastContainer = c;

            if (c.hasParentLocation)
                lastLocation.containers.add(c);
            else
                locationlessContainers.add(c);
        }

        if (line.startsWith("item")) {
            Item i = new Item(line);
            lastContainer.items.add(i);
        }
    }*/

    public Set<String> getAllItemNames() {
        Set<String> out = new HashSet<>();

        // Add all items from locations
        for (Location l : locations)
            for (Container c : l.containers)
                for (Item i : c.items)
                    out.add(i.name);

        // Add all items from containers without parent locations
        for (Container c : locationlessContainers)
            for (Item i : c.items)
                out.add(i.name);

        return out;
    }

    public Set<String> getAllLocationNames() {
        Set<String> out = new HashSet<>();
        for (Location l : locations)
            out.add(l.name);

        return out;
    }
}

