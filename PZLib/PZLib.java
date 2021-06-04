import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
	public static String readAllOfFile(String filename) {
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
}
