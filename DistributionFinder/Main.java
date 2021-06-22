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

		mainGUI.itemsList.addListSelectionListener(e -> onSelectItem(mainGUI.itemsList.getSelectedValue()));

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private static void onSelectItem(String itemName) {
		updateReadable(itemName);
		updateWikiMedia(itemName);
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
	}

	private static void updateWikiMedia(String itemName) {
		// Clear the WikiMedia tab
		mainGUI.distributionWikiMediaTextArea.setText("");
		addBuildingsToWikiMedia(itemName);

		mainGUI.distributionWikiMediaTextArea.append("'''''EDITOR! CHECK THE FOLLOWING SECTION TO SEE IF IT CONTAINS ANY ITEMS'''''");
		addContainersToWikiMedia(itemName);
	}

	private static void addBuildingsToWikiMedia(String itemName) {
		mainGUI.distributionWikiMediaTextArea.append("=== Buildings ===\n");
		mainGUI.distributionWikiMediaTextArea.append("" +
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
				mainGUI.distributionWikiMediaTextArea.append("|rowspan=" + numChances);
			mainGUI.distributionWikiMediaTextArea.append("|" + l.name + "\n");

			for (Container c : containersWithItem) {
				List<Item> items = c.getItem(itemName);

				// The container name
				if (items.size() > 1)
					mainGUI.distributionWikiMediaTextArea.append("|rowspan=" + items.size());
				mainGUI.distributionWikiMediaTextArea.append("|" + c.name + "\n");

				// The container's rolls
				if (items.size() > 1)
					mainGUI.distributionWikiMediaTextArea.append("|rowspan=" + items.size());
				mainGUI.distributionWikiMediaTextArea.append("|" + c.rolls + "\n");

				for (Item i : items) {
					mainGUI.distributionWikiMediaTextArea.append("|" + i.odds + "\n" +
							"|-\n");
				}
			}
		}
		mainGUI.distributionWikiMediaTextArea.append("|}\n");
	}
	private static void addContainersToWikiMedia(String itemName) {
		mainGUI.distributionWikiMediaTextArea.append("=== Containers ===\n");
		mainGUI.distributionWikiMediaTextArea.append("A list of containers that the item can be found in, not limited to buildings.\n");
		mainGUI.distributionWikiMediaTextArea.append(
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
				mainGUI.distributionWikiMediaTextArea.append("|rowspan=" + items.size());
			mainGUI.distributionWikiMediaTextArea.append("|" + c.name + "\n");

			// The container's rolls
			if (items.size() > 1)
				mainGUI.distributionWikiMediaTextArea.append("|rowspan=" + items.size());
			mainGUI.distributionWikiMediaTextArea.append("|" + c.rolls + "\n");

			for (Item i : items) {
				mainGUI.distributionWikiMediaTextArea.append("|" + i.odds + "\n" +
						"|-\n");
			}
		}

		mainGUI.distributionWikiMediaTextArea.append("|}\n");
	}
}
