import LuaJavaDefines.Container;
import LuaJavaDefines.Location;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public class MainGUI {
	public JPanel mainPanel;
	public JList<String> itemsList;
	public JList<String> locationsList;
	public JTextArea distributionWikiMediaTextArea;
	public JTextArea distributionReadableTextArea;
	public JTabbedPane rightTabbedPane;

	private JTextField filterItemsTextField;
	private JTextField selectedItemTextField;
	private JButton copyButton;

	MainGUI() {
		// Ew, gross... Java.
		try {
			readDistributions();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setupFilter();
		setupList();
	}

	private void readDistributions() throws IOException {
		// Get file location
		JFileChooser jfc = new JFileChooser();

		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfc.addChoosableFileFilter(new DirectoryNameFilter("ProjectZomboid"));
		jfc.addChoosableFileFilter(new FileNameExtensionFilter("Distribution.lua File", "lua"));
		jfc.setAcceptAllFileFilterUsed(false);

		String installDir = PZLib.getDefaultSteamInstallDirectory();
		File installDirFile = new File(installDir);

		if (installDirFile.exists())
			jfc.setCurrentDirectory(installDirFile.getParentFile());
		else
			jfc.setCurrentDirectory(new File("."));

		int result = jfc.showOpenDialog(mainPanel);

		// If they do not approve, kill the program.
		if (result != JFileChooser.APPROVE_OPTION) {
			System.exit(0);
		}

		// Get which file filter is chosen - find the proper file based on that.
		File distributionsFile;
		if (jfc.getFileFilter() instanceof FileNameExtensionFilter)
			distributionsFile = jfc.getSelectedFile();
		else {
			String filePath = PZLib.appendSubdirectories(jfc.getSelectedFile().getAbsolutePath(),
				"media/lua/server/Items/Distributions.lua");

			distributionsFile = new File(filePath);
		}

		if (!distributionsFile.exists()) {
			readDistributions();
			return;
		}

		// Horribly efficient, just how I like it.
		String contents = PZLib.readAllFromFile(distributionsFile.getAbsolutePath());
		// Just... remove any 'local' from the mix. We'd like to be able to load this in and read it elsewhere
		contents = contents.replace("local", "");

		// Load in the Distribution values
		LuaValue chunk = Main.globals.load(contents);
		chunk.call();

		LuaValue chunk2 = Main.globals.loadfile("lua/distributionParser.lua").call();

		LuaTable returnVal = chunk2.checktable();

		Main.parser.locations = (List<Location>) returnVal.get("locations").touserdata();
		Main.parser.locationlessContainers = (List<Container>) returnVal.get("locationLesscontainers").touserdata();

		updateList();
	}
	private void setupFilter() {
		filterItemsTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {onFilterChange();}
			@Override
			public void removeUpdate(DocumentEvent e) {onFilterChange();}
			@Override
			public void changedUpdate(DocumentEvent e) {onFilterChange();}
		});

		// Select all of the text when the text is clicked
		filterItemsTextField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				filterItemsTextField.selectAll();
			}
			@Override
			public void focusLost(FocusEvent e) {}
		});
	}
	private void setupList() {
		itemsList.addListSelectionListener(e -> {
			selectedItemTextField.setText(itemsList.getSelectedValue());
		});
	}

	private void onFilterChange() {
		String text = filterItemsTextField.getText();
		DefaultListModel<String> newList = new DefaultListModel<>();

		Set<String> list;
		switch (rightTabbedPane.getTitleAt(rightTabbedPane.getSelectedIndex())) {
			case "Locations":
				list = Main.parser.getAllLocationNames();
				break;
			case "Items":
			default:
				list = Main.parser.getAllItemNames();
		}

		for (String s : list) {
			// Get rid of all spaces and set cases to lowercase
			String toFind = PZLib.stripString(s, " ").toLowerCase();
			String searchString = PZLib.stripString(text, " ").toLowerCase();

			if (toFind.contains(searchString))
				newList.addElement(s);
		}

		// Ugly but meh
		switch (rightTabbedPane.getTitleAt(rightTabbedPane.getSelectedIndex())) {
			case "Containers":
				locationsList.setModel(newList);
			case "Items":
			default:
				itemsList.setModel(newList);
		}
	}

	private void updateList() {
		DefaultListModel<String> newItemList = new DefaultListModel<>();
		DefaultListModel<String> newLocationList = new DefaultListModel<>();

		for (String s : Main.parser.getAllItemNames())
			newItemList.addElement(s);

		for (String s : Main.parser.getAllLocationNames())
			newLocationList.addElement(s);

		itemsList.setModel(newItemList);
		locationsList.setModel(newLocationList);
	}
}
