package com.KBHeid;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

public class MainGUI {
	public JPanel mainPanel;
	public JTextField inFilenameText;
	public JButton inFilenameButton;
	public JButton goButton;
	public JButton outFilenameButton;
	public JTextField outFilenameText;
	private JCheckBox inFileCheck;
	private JCheckBox outFileCheck;


	MainGUI() {
		JFileChooser fileChooser = new JFileChooser();

		// Try to find the files.
		try {
			searchForInputFile();
			searchForOutputDir();
		} catch (IOException e) {
			// Will like literally never happen. Thanks for making
			// me write a try/catch, java
			e.printStackTrace();
		}

		inFilenameButton.addActionListener(e -> {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileNameExtensionFilter("XML FILES", "xml", "exemel"));

			fileChooser.setCurrentDirectory(new File("."));
			int result = fileChooser.showOpenDialog(mainPanel);

			if (result == JFileChooser.APPROVE_OPTION) {
				inFilenameText.setText(fileChooser.getSelectedFile().getAbsolutePath());
				inFileCheck.setSelected(true);
			}

			goButton.setEnabled(outFileCheck.isSelected() && inFileCheck.isSelected());
			fileChooser.resetChoosableFileFilters();
		});

		outFilenameButton.addActionListener(e -> {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int result = fileChooser.showOpenDialog(mainPanel);

			fileChooser.setCurrentDirectory(new File("."));
			if (result == JFileChooser.APPROVE_OPTION) {
				outFilenameText.setText(fileChooser.getSelectedFile().getAbsolutePath());
				outFileCheck.setSelected(true);
			}

			goButton.setEnabled(outFileCheck.isSelected() && inFileCheck.isSelected());
			fileChooser.resetChoosableFileFilters();
		});
	}


	private void searchForInputFile() throws IOException {
		File startDir = new File(".");
		String fileToFind = "RadioData.xml";

		if (!startDir.exists())
			return;

		for (File f : startDir.listFiles()) {
			if (f.getName().equals(fileToFind)) {
				inFilenameText.setText(f.getCanonicalPath());
				inFileCheck.setSelected(true);
				goButton.setEnabled(outFileCheck.isSelected() && inFileCheck.isSelected());
				return;
			}
		}

		String os = System.getProperty("os.name").toLowerCase();

		boolean isWindows = os.contains("win");
		boolean isMac = os.contains("mac");

		String steamInstallDir;

		if (isWindows) {
			steamInstallDir = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\ProjectZomboid\\media\\radio";
		}
		else if (isMac) {
			steamInstallDir = "~/Library/Application Support/Steam/steamapps/common/ProjectZomboid/media/radio";
		}
		else {
			// we try for Linux. Maybe works, maybe doesn't.
			steamInstallDir = "~/.steam/steam/SteamApps/common/ProjectZomboid/media/radio";
		}

		File installDir = new File(steamInstallDir);
		if (!installDir.exists())
			return;

		for (File f : installDir.listFiles()) {
			if (f.getName().equals(fileToFind)) {
				inFilenameText.setText(f.getCanonicalPath());
				inFileCheck.setSelected(true);
				goButton.setEnabled(outFileCheck.isSelected() && inFileCheck.isSelected());
				return;
			}
		}
	}

	private void searchForOutputDir() throws IOException {
		String dirTofind = "wiki";

		File wikiDirDir = new File(".");
		if (!wikiDirDir.exists())
			return;

		for (File f : wikiDirDir.listFiles()) {
			if (f.getName().equals(dirTofind)) {
				outFilenameText.setText(f.getCanonicalPath());
				outFileCheck.setSelected(true);
				goButton.setEnabled(outFileCheck.isSelected() && inFileCheck.isSelected());
				return;
			}
		}
	}
}
