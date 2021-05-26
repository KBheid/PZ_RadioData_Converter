import javax.swing.*;
import javax.swing.filechooser.FileFilter;
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
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML Files", "xml", "exemel"));
			fileChooser.addChoosableFileFilter(new DirectoryNameFilter("ProjectZomboid"));
			fileChooser.setAcceptAllFileFilterUsed(false);

			fileChooser.setCurrentDirectory(new File("."));
			int result = fileChooser.showOpenDialog(mainPanel);

			if (result == JFileChooser.APPROVE_OPTION) {
				// If the directory was chosen, delve for the contents.
				if (fileChooser.getFileFilter() instanceof DirectoryNameFilter) {
					String fileName = getRadioDirectoryFromPZDirectory(fileChooser.getSelectedFile().getAbsolutePath());

					// Test if file exists, if it does, then set the text, if not, set to 'incorrect directory chosen'

					File f = new File(fileName + File.separator + "RadioData.xml");
					if (f.exists()) {
						inFilenameText.setText(f.getAbsolutePath());
						inFileCheck.setSelected(true);
					}
					else {
						inFilenameText.setText("Incorrect directory chosen.");
						inFileCheck.setSelected(false);
					}
				}

				else {
					inFilenameText.setText(fileChooser.getSelectedFile().getAbsolutePath());
					inFileCheck.setSelected(true);
				}

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

		String steamInstallDir = getDefaultSteamInstallDirectory();
		String pzDataDirStr = getRadioDirectoryFromPZDirectory(steamInstallDir);

		File pzDataDir = new File(pzDataDirStr);
		if (!pzDataDir.exists())
			return;

		for (File f : pzDataDir.listFiles()) {
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

	private String getDefaultSteamInstallDirectory() {
		String os = System.getProperty("os.name").toLowerCase();

		boolean isWindows = os.contains("win");
		boolean isMac = os.contains("mac");

		String steamInstallDir;

		if (isWindows) {
			steamInstallDir = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\ProjectZomboid";
		}
		else if (isMac) {
			steamInstallDir = "~/Library/Application Support/Steam/steamapps/common/ProjectZomboid";
		}
		else {
			// we try for Linux. Maybe works, maybe doesn't.
			steamInstallDir = "~/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid";
		}

		return steamInstallDir;
	}

	private String getRadioDirectoryFromPZDirectory(String startDirectory) {
		return startDirectory + File.separator + "media" + File.separator + "radio";
	}
}


class DirectoryNameFilter extends FileFilter {
	private String filename;

	DirectoryNameFilter(String filename) { this.filename = filename; }

	@Override
	public boolean accept(File f) {
		return f.isDirectory();
	}

	@Override
	public String getDescription() {
		return filename + " Directory";
	}
}