import LuaJavaDefines.Container;
import LuaJavaDefines.Item;
import LuaJavaDefines.Location;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import javax.swing.*;
import java.util.List;
import java.util.Set;

public class Main {
	static final Globals globals = JsePlatform.standardGlobals();
	static final DistributionManager parser = new DistributionManager();

	static MainGUI mainGUI;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Distribution Finder");

		mainGUI = new MainGUI();
		frame.add(mainGUI.mainPanel);
		frame.setJMenuBar(mainGUI.menuBar);

		mainGUI.itemsList.addListSelectionListener(e -> onSelectItem(mainGUI.itemsList.getSelectedValue()));
		mainGUI.locationsList.addListSelectionListener(e -> onSelectLocation(mainGUI.locationsList.getSelectedValue()));
		mainGUI.containerList.addListSelectionListener(e -> {
			if (mainGUI.containerList.getSelectedValue() != null)
				onSelectContainer(mainGUI.containerList.getSelectedValue(), mainGUI.locationsList.getSelectedValue());
		});

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	// ============  SEARCH BY ITEM  ============
	private static void onSelectItem(String itemName) {
		updateReadable(itemName);
		updateMediaWiki(itemName);
	}
	private static void updateReadable(String itemName) {
		// Clear the readable tab
		mainGUI.distributionReadableTextArea.setText("");

		for (Location l : parser.locations) {
			if (!l.containsItem(itemName))
				continue;

			mainGUI.distributionReadableTextArea.append("Location: " + l.name + "\n");
			for (Container c : l.containers) {
				List<Item> occurrences = c.getItem(itemName);
				if (occurrences.size() > 0) {
					mainGUI.distributionReadableTextArea.append("\t" + c.name + ", rolls: " + c.rolls + "\n");
					for (Item item : occurrences)
						mainGUI.distributionReadableTextArea.append("\t\t" + item.odds + "\n");
				}
			}
		}

		for (Container c : parser.locationlessContainers) {
			List<Item> occurrences = c.getItem(itemName);
			if (occurrences.size() > 0) {
				mainGUI.distributionReadableTextArea.append(c.name + ", rolls: " + c.rolls + "\n");
				for (Item item : occurrences)
					mainGUI.distributionReadableTextArea.append("\tOdds:" + item.odds + "\n");
			}
		}

		if (!parser.vehicles.isEmpty()) {
			for (Location l : parser.vehicles) {
				if (!l.containsItem(itemName))
					continue;

				mainGUI.distributionReadableTextArea.append("Vehicle: " + l.name + "\n");
				for (Container c : l.containers) {
					List<Item> occurrences = c.getItem(itemName);
					if (occurrences.size() > 0) {
						mainGUI.distributionReadableTextArea.append("\t" + c.name + ", rolls: " + c.rolls + "\n");
						for (Item item : occurrences)
							mainGUI.distributionReadableTextArea.append("\t\t" + item.odds + "\n");
					}
				}
			}
		}
	}
	private static void updateMediaWiki(String itemName) {
		// Clear the MediaWiki tab
		mainGUI.distributionMediaWikiTextArea.setText("");
		addBuildingsToMediaWiki(itemName);

		mainGUI.distributionMediaWikiTextArea.append("'''''EDITOR! CHECK THE FOLLOWING SECTION TO SEE IF IT CONTAINS ANY ITEMS'''''\n");
		addContainersToMediaWiki(itemName);

		mainGUI.distributionMediaWikiTextArea.append("'''''EDITOR! CHECK THE FOLLOWING SECTION TO SEE IF IT CONTAINS ANY ITEMS'''''\n");
		addVehiclesToMediaWiki(itemName);
	}

	private static void addBuildingsToMediaWiki(String itemName) {
		mainGUI.distributionMediaWikiTextArea.append("=== Buildings ===\n");
		mainGUI.distributionMediaWikiTextArea.append("" +
				"{| class=\"pztable\" style=\"text-align:center;\"\n" +
				"|-\n" +
				"!Building/Room\n" +
				"!Container\n" +
				"!Rolls\n" +
				"!Chance\n" +
				"|-\n");

		for (Location l : parser.locations) {
			if (!l.containsItem(itemName))
				continue;

			Set<Container> containersWithItem = l.getContainersWithItem(itemName);

			int numChances = 0;
			for (Container c : containersWithItem) {
				numChances += c.getItem(itemName).size();
			}

			// Expand the rows covered to be as many as there are chances for the item
			if (numChances > 1)
				mainGUI.distributionMediaWikiTextArea.append("|rowspan=" + numChances);
			mainGUI.distributionMediaWikiTextArea.append("|" + l.name + "\n");

			for (Container c : containersWithItem) {
				List<Item> items = c.getItem(itemName);

				// The container name
				if (items.size() > 1)
					mainGUI.distributionMediaWikiTextArea.append("|rowspan=" + items.size());
				mainGUI.distributionMediaWikiTextArea.append("|" + c.name + "\n");

				// The container's rolls
				if (items.size() > 1)
					mainGUI.distributionMediaWikiTextArea.append("|rowspan=" + items.size());
				mainGUI.distributionMediaWikiTextArea.append("|" + c.rolls + "\n");

				for (Item i : items) {
					mainGUI.distributionMediaWikiTextArea.append("|" + i.odds + "\n" +
							"|-\n");
				}
			}
		}
		mainGUI.distributionMediaWikiTextArea.append("|}\n");
	}
	private static void addContainersToMediaWiki(String itemName) {
		mainGUI.distributionMediaWikiTextArea.append("=== Containers ===\n");
		mainGUI.distributionMediaWikiTextArea.append("A list of containers that the item can be found in, not limited to buildings.\n");
		mainGUI.distributionMediaWikiTextArea.append(
				"{| class=\"pztable\" style=\"text-align:center;\"\n" +
						"|-\n" +
						"!Container\n" +
						"!Rolls\n" +
						"!Chance\n" +
						"|-\n");

		for (Container c : parser.locationlessContainers) {
			List<Item> items = c.getItem(itemName);

			if (items.size() < 1)
				continue;

			// The container name
			if (items.size() > 1)
				mainGUI.distributionMediaWikiTextArea.append("|rowspan=" + items.size());
			mainGUI.distributionMediaWikiTextArea.append("|" + c.name + "\n");

			// The container's rolls
			if (items.size() > 1)
				mainGUI.distributionMediaWikiTextArea.append("|rowspan=" + items.size());
			mainGUI.distributionMediaWikiTextArea.append("|" + c.rolls + "\n");

			for (Item i : items) {
				mainGUI.distributionMediaWikiTextArea.append("|" + i.odds + "\n" +
						"|-\n");
			}
		}

		mainGUI.distributionMediaWikiTextArea.append("|}\n");
	}
	private static void addVehiclesToMediaWiki(String itemName) {
		mainGUI.distributionMediaWikiTextArea.append("=== Vehicles ===\n");
		mainGUI.distributionMediaWikiTextArea.append(
				"{| class=\"pztable\" style=\"text-align:center;\"\n" +
						"|-\n" +
						"!Vehicle Name\n" +
						"!Container\n" +
						"!Rolls\n" +
						"!Chance\n" +
						"|-\n");

		for (Location l : parser.vehicles) {
			if (!l.containsItem(itemName))
				continue;

			Set<Container> containersWithItem = l.getContainersWithItem(itemName);

			int numChances = 0;
			for (Container c : containersWithItem) {
				numChances += c.getItem(itemName).size();
			}

			// Expand the rows covered to be as many as there are chances for the item
			if (numChances > 1)
				mainGUI.distributionMediaWikiTextArea.append("|rowspan=" + numChances);
			mainGUI.distributionMediaWikiTextArea.append("|" + l.name + "\n");

			for (Container c : containersWithItem) {
				List<Item> items = c.getItem(itemName);

				// The container name
				if (items.size() > 1)
					mainGUI.distributionMediaWikiTextArea.append("|rowspan=" + items.size());
				mainGUI.distributionMediaWikiTextArea.append("|" + c.name + "\n");

				// The container's rolls
				if (items.size() > 1)
					mainGUI.distributionMediaWikiTextArea.append("|rowspan=" + items.size());
				mainGUI.distributionMediaWikiTextArea.append("|" + c.rolls + "\n");

				for (Item i : items) {
					mainGUI.distributionMediaWikiTextArea.append("|" + i.odds + "\n" +
							"|-\n");
				}
			}
		}
		mainGUI.distributionMediaWikiTextArea.append("|}\n");
	}

	// ============ SEARCH BY CONTAINER ============
	private static void onSelectContainer(String containerName, String location) {
		updateReadableByContainer(containerName, location);
		updateMediaWikiByContainer(containerName, location);
	}
	private static void updateReadableByContainer(String containerName, String location) {
		mainGUI.distributionReadableTextArea.setText("");
		Container con = null;
		Location l = parser.getLocationOrVehicleByName(location);
		if (l.name.equals(location)) {
			for (Container c : l.containers)
				if (c.name.equals(containerName)) {
					con = c;
					break;
				}
		}

		mainGUI.distributionReadableTextArea.append(location + "\n");
		mainGUI.distributionReadableTextArea.append("\t" + containerName + "\n");
		mainGUI.distributionReadableTextArea.append("\tRolls: " + con.rolls + "\n");

		for (Item i : con.items)
			mainGUI.distributionReadableTextArea.append("\t\t" + i.name + " : " + i.odds + "\n");
	}
	private static void updateMediaWikiByContainer(String containerName, String location) {
		mainGUI.distributionMediaWikiTextArea.setText("");
		mainGUI.distributionMediaWikiTextArea.append("MediaWiki format is not available for containers.\n");
		mainGUI.distributionMediaWikiTextArea.append("Supported types include:\n");
		mainGUI.distributionMediaWikiTextArea.append("\tItems\n");
		mainGUI.distributionMediaWikiTextArea.append("\tLocations\n");
	}


	// ============ SEARCH BY LOCATION ============
	private static void onSelectLocation(String location) {
		updateReadableByLocation(location);
		updateMediaWikiByLocation(location);
	}
	private static void updateReadableByLocation(String location) {
		Location loc = Main.parser.getLocationOrVehicleByName(location);

		mainGUI.distributionReadableTextArea.setText("");
		mainGUI.distributionReadableTextArea.append(location + "\n");

		for (Container c : loc.containers) {
			mainGUI.distributionReadableTextArea.append("\t" + c.name + "\n");
			mainGUI.distributionReadableTextArea.append("\tRolls: " + c.rolls + "\n");

			for (Item i : c.items) {
				mainGUI.distributionReadableTextArea.append("\t\t" + i.name + " : " + i.odds + "\n");
			}
		}

	}
	private static void updateMediaWikiByLocation(String location) {
		mainGUI.distributionMediaWikiTextArea.setText("");
		mainGUI.distributionMediaWikiTextArea.append("Coming soon.\n");

		Location l = Main.parser.getLocationOrVehicleByName(location);
	}

}
