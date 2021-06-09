import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class MainGUI {
	public JPanel mainPanel;
	public JList<String> itemsList;
	public JTextArea distributionTextArea;

	private JTextField filterItemsTextField;
	private JTextField selectedItemTextField;

	// A list of all the items in the game
	public Set<String> allItems = new HashSet<>();

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

		// Read all of the items from the file into String ret
		LuaValue chunk2 = Main.globals.loadfile("lua/test.lua");
		String ret = chunk2.call().tojstring();

		// Put them in the list and update the list
		for (String s : ret.split("\n")) {
			allItems.add(s.replace("TINT", "").replace("TEXTURE_", ""));
		}

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
		for (String s : allItems) {
			// get rid of all spaces
			String toFind = PZLib.stripString(s, " ").toLowerCase();
			String searchString = PZLib.stripString(text, " ").toLowerCase();

			if (toFind.startsWith(searchString))
				newList.addElement(s);
		}

		itemsList.setModel(newList);
	}

	private void updateList() {
		DefaultListModel<String> newList = new DefaultListModel<>();

		for (String s : allItems) {
			newList.addElement(s);
		}

		itemsList.setModel(newList);
	}
}
