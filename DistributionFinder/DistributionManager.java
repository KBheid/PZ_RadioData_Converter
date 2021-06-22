import LuaJavaDefines.Container;
import LuaJavaDefines.Item;
import LuaJavaDefines.Location;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.*;

public class DistributionManager {
    public List<Location> locations;
    public List<Container> locationlessContainers;

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

    // Ignore unchecked casting in this method - we must cast the return values of the lua calls
    @SuppressWarnings("unchecked")
    public void parseDistributions(String contents) {
        // Load in the Distribution values
        LuaValue chunk = Main.globals.load(contents);
        chunk.call();

        LuaValue chunk2 = Main.globals.loadfile("lua/distributionParser.lua").call();

        LuaTable returnVal = chunk2.checktable();

        locations = (List<Location>) returnVal.get("locations").touserdata();
        locationlessContainers = (List<Container>) returnVal.get("locationLesscontainers").touserdata();
    }
}

