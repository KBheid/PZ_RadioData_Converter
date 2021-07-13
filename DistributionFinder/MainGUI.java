import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Set;


public class MainGUI {
	public JPanel mainPanel;
	public JMenuBar menuBar;
	public JList<String> itemsList;
	public JList<String> locationsList;
	public JList<String> containerList;
	public JTextArea distributionMediaWikiTextArea;
	public JTextArea distributionReadableTextArea;
	public JTabbedPane rightTabbedPane;

	private JTextField filterItemsTextField;
	private JTextField selectedItemTextField;
	private JButton copyButton;
	private JCheckBox BuildingDistributionsCheck;
	private JCheckBox VehicleDistributionsCheck;
	private JCheckBox ProceduralDistributionsCheck;

	MainGUI() {
		menuBar = new JMenuBar();
		JMenu mainMenu = new JMenu("File");

		JMenuItem projZombDirPicker = new JMenuItem("Project Zomboid directory...");
		JMenuItem distFilePicker = new JMenuItem("Building Distribution file...");
		JMenuItem procedFilePicker = new JMenuItem("Procedural Distribution file...");
		JMenuItem vehicleFilePicker = new JMenuItem("Vehicle Distribution file...");
		mainMenu.add(projZombDirPicker);
		mainMenu.add(distFilePicker);
		mainMenu.add(procedFilePicker);
		mainMenu.add(vehicleFilePicker);

		projZombDirPicker.addActionListener(e -> onChooseDir());
		distFilePicker.addActionListener(e -> {
			File f = chooseFile("Distributions.lua");
			if (f != null) {
				updateDistribution(f, false);
				updateList();
			}
		});
		procedFilePicker.addActionListener(e -> {
			File f = chooseFile("ProceduralDistributions.lua");
			if (f != null) {
				updateProceduralDistribution(f, false);
				updateList();
			}
		});
		vehicleFilePicker.addActionListener(e -> {
			File f = chooseFile("VehicleDistributions.lua");
			if (f != null) {
				updateVehicleDistribution(f, false);
				updateList();
			}
		});

		// Reset filters when selecting the other tab
		rightTabbedPane.addChangeListener(e -> {
			filterItemsTextField.setText("Filter " + rightTabbedPane.getTitleAt(rightTabbedPane.getSelectedIndex()));
			updateList();
		});

		menuBar.add(mainMenu);

		setupCopyButton();
		setupFilter();
		setupList();
	}


	private void onChooseDir() {

		File pzDir = PZLib.promptForPZDir(mainPanel);
		// If the value is null, then they selected cancel.
		if (pzDir == null)
			return;

		String distFileLoc = PZLib.appendSubdirectories(
				pzDir.getAbsolutePath(),
				"media/lua/server/Items/Distributions.lua");

		String vehicleDistFileLoc = PZLib.appendSubdirectories(
				pzDir.getAbsolutePath(),
				"media/lua/server/Vehicles/VehicleDistributions.lua");

		String proceduralDistFileLoc = PZLib.appendSubdirectories(
				pzDir.getAbsolutePath(),
				"media/lua/server/Items/ProceduralDistributions.lua");

		updateDistribution(new File(distFileLoc), true);
		updateVehicleDistribution(new File(vehicleDistFileLoc), true);
		updateProceduralDistribution(pzDir, true);

		updateList();
	}
	private File chooseFile(String fileExampleName) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.addChoosableFileFilter(new FileNameExtensionFilter(fileExampleName + " file", "lua"));
		jfc.setCurrentDirectory(new File("."));
		int result = jfc.showOpenDialog(mainPanel);

		if (result != JFileChooser.APPROVE_OPTION)
			return null;

		return jfc.getSelectedFile();
	}

	private void updateDistribution(File distFile, boolean resetOnFail) {
		if (distFile.exists()) {
			BuildingDistributionsCheck.setSelected(true);

			Main.globals.loadfile(distFile.getAbsolutePath()).call();
			Main.parser.parseDistributions();

			return;
		}

		if (resetOnFail)
			BuildingDistributionsCheck.setSelected(false);
	}
	private void updateVehicleDistribution(File distFile, boolean resetOnFail) {
		if (distFile.exists()) {
			VehicleDistributionsCheck.setSelected(true);

			Main.globals.loadfile(distFile.getAbsolutePath()).call();
			Main.parser.parseVehicleDistributions();

			return;
		}

		if (resetOnFail)
			VehicleDistributionsCheck.setSelected(false);
	}
	private void updateProceduralDistribution(File distFile, boolean resetOnFail) {
		if (distFile.exists()) {
			ProceduralDistributionsCheck.setSelected(true);
			return;
		}

		if (resetOnFail)
			ProceduralDistributionsCheck.setSelected(false);
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

		locationsList.addListSelectionListener(e -> {
			selectedItemTextField.setText(locationsList.getSelectedValue());

			DefaultListModel<String> containersModel = new DefaultListModel<>();
			Set<String> subContainerNames =
					Main.parser.getContainerNamesFromLocationName(locationsList.getSelectedValue());

			for (String name : subContainerNames)
				containersModel.addElement(name);

			containerList.setModel(containersModel);
		});
	}
	private void setupCopyButton() {
		// Set up copy button functionality
		copyButton.addActionListener(e -> {
			StringSelection stringSelection = new StringSelection(distributionMediaWikiTextArea.getText());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);

			copyButton.setText("Copied!");
		});

		copyButton.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {}
			@Override
			public void focusLost(FocusEvent e) {
				copyButton.setText("Copy to clipboard");
			}
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
			case "Locations":
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
