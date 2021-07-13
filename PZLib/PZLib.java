import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class PZLib {
	public static String repeat(int count, String with) {
		return new String(new char[count]).replace("\0", with);
	}

	/**
	 * Reads the entirety of a file into a string.
	 * Note: The input filename must exist within the Resources accessible to the current thread.
	 * @param filename The input filename - it should be checked to exist with `bool fileExist(String, Class)`.
	 * @return A string with the contents of the file.
	 */
	public static String readAllOfResource(String filename) {
		StringBuilder contentBuilder = new StringBuilder();

		try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename))
		{
			int amountRead;
			do {
				byte[] bytes = new byte[1024];
				amountRead = stream.read(bytes);

				String bytesRead = new String(bytes, StandardCharsets.UTF_8);
				contentBuilder.append(bytesRead);
			}
			while (amountRead > 0);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return contentBuilder.toString();
	}

	public static String readAllFromFile(String filename) throws IOException {
		StringBuilder sb = new StringBuilder();

		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);

		int bytesRead;
		byte[] bytes = new byte[1024];
		do {
			bytesRead = fis.read(bytes);

			// Eh, not terribly efficient. Oh well.
			String s = new String(bytes, StandardCharsets.UTF_8);
			if (bytesRead > 0)
				sb.append(s.substring(0, bytesRead));
		}
		while (bytesRead >= 0);

		return sb.toString();
	}

	/**
	 * Check if a file exists within the given Resources.
	 * @param filename The resource filename.
	 * @param main The Main class of the program. It is used to find related resources.
	 * @return Whether or not the file can be found.
	 */
	public static boolean fileExists(String filename, Class main) {
		return main.getResourceAsStream(filename) != null;
	}

	/**
	 * Trims and strips characters from a string
	 * @param toStrip The input string to be stripped
	 * @param additionalStrip Additional characters to be removed beyond whitespace
	 * @return The input string without leading/trailing whitespace and no characters from 'additionalStrip'
	 */
	public static String stripString(String toStrip, String additionalStrip) {
		toStrip = toStrip.trim();
		for (char c : additionalStrip.toCharArray())
			toStrip = toStrip.replace(Character.toString(c), "");

		return toStrip;
	}

	/**
	 * Gets the default PZ install directory based on OS.
	 * This could be done by opening registry and yada yada, or we can assume that it's in the default location
	 * and avoid learning.
	 * @return The default PZ install directory.
	 */
	public static String getDefaultSteamInstallDirectory() {
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

	public static String appendSubdirectories(String base, String toAppend) {
		return base + File.separator + toAppend.replace("/", File.separator).replace("\\", File.separator);
	}

	/**
	 * Prompts the user for the PZ directory
	 * @return The chosen file or null if cancelled
	 */
	public static File promptForPZDir(Component parentComponent) {
		JFileChooser jfc = new JFileChooser();

		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.addChoosableFileFilter(new DirectoryNameFilter("ProjectZomboid"));
		jfc.setAcceptAllFileFilterUsed(false);

		// Search for the PZ directory - if we find it, then back out a directory
		//  and place the user there.
		String installDir = PZLib.getDefaultSteamInstallDirectory();
		File installDirFile = new File(installDir);
		if (installDirFile.exists())
			jfc.setCurrentDirectory(installDirFile.getParentFile());
		else
			jfc.setCurrentDirectory(new File("."));

		// Show the dialog, get the result
		int result = jfc.showOpenDialog(parentComponent);


		// If they clicked cancel, return null
		if (result != JFileChooser.APPROVE_OPTION)
			return null;

		// Otherwise get the file they selected
		return jfc.getSelectedFile();
	}
}
