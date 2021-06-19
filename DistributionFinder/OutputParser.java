import java.util.*;

public class OutputParser {
    private Location lastLocation;
    private Container lastContainer;

    public List<Location> locations = new ArrayList<>();
    public List<Container> locationlessContainers = new ArrayList<>();

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
    }

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

    public static Map<String, String> getValMap(String[] inp) {
        Map<String, String> retMap = new HashMap<>();

        for (String s : inp) {
            String[] keyVal = s.split(":");
            retMap.put(keyVal[0], (keyVal.length == 2) ? keyVal[1] : "");
        }

        return retMap;
    }
}

class Item {
    String name;
    float odds;

    Item(String line) {
        line = line.substring("item[".length(), line.length()-1);
        Map<String, String> vals = OutputParser.getValMap(line.split("\\|"));

        name = vals.get("name");
        odds = Float.parseFloat(vals.get("odds"));
    }
}

class Container {
    String name;
    int rolls = -1;
    List<Item> items = new ArrayList<>();
    boolean procedural;
    boolean hasParentLocation;

    Container(String line) {
        line = line.substring("container[".length(), line.length()-1);
        Map<String, String> vals = OutputParser.getValMap(line.split("\\|"));

        name = vals.get("name");
        hasParentLocation = vals.containsKey("location");
        if (vals.containsKey("rolls"))
            rolls = Integer.parseInt(vals.get("rolls"));

        procedural = vals.containsKey("procedural");
    }

    boolean containsItem(String itemName) {
        for (Item i : items)
            if (i.name.equals(itemName))
                return true;
        return false;
    }

    List<Item> getItem(String itemName) {
        List<Item> out = new ArrayList<>();

        for (Item i : items)
            if (i.name.equals(itemName))
                out.add(i);
        return out;
    }
}

class Location {
    String name;
    boolean isShop;
    List<Container> containers = new ArrayList<>();

    Location(String line) {
        line = line.substring("location[".length(), line.length()-1);
        Map<String, String> vals = OutputParser.getValMap(line.split("\\|"));

        name = vals.get("name");
        isShop = vals.containsKey("isShop");
    }

    boolean containsItem(String itemName) {
        for (Container c : containers) {
            if (c.containsItem(itemName))
                return true;
        }
        return false;
    }

    Set<Container> getContainersWithItem(String itemName) {
        Set<Container> out = new HashSet<>();
        for (Container c : containers)
            if (c.containsItem(itemName))
                out.add(c);
        return out;
    }
}