import LuaJavaDefines.Container;
import LuaJavaDefines.Item;
import LuaJavaDefines.Location;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.*;

public class DistributionManager {
    public List<Location> locations = new ArrayList<>();
    public List<Location> vehicles = new ArrayList<>();
    public List<Container> locationlessContainers = new ArrayList<>();

    public Set<String> getAllItemNames() {
        Set<String> out = new HashSet<>();

        // Add all items from locations
        for (Location l : locations)
            for (Container c : l.containers)
                for (Item i : c.items)
                    out.add(i.name);

        for (Location l : vehicles)
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

    @SuppressWarnings("unchecked")
    public void parseDistributions() {
        // Parse regular items
        LuaValue chunk2 = Main.globals.loadfile("lua/distributionParser.lua").call();
        LuaTable returnVal = chunk2.checktable();

        locations = (List<Location>) returnVal.get("locations").touserdata();
        locationlessContainers = (List<Container>) returnVal.get("locationLesscontainers").touserdata();
    }

    @SuppressWarnings("unchecked")
    public void parseVehicleDistributions() {
        // Parse vehicles
        LuaValue chunk2 = Main.globals.loadfile("lua/distributionVehiclesParser.lua").call();

        vehicles = (List<Location>) chunk2.touserdata();
    }
}

