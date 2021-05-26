import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	private static String patternStr = "\\{.*}|\\*.*\\*";
	private static Pattern pattern = Pattern.compile(patternStr);

	public static void main(String[] args) {
		JFrame frame = new JFrame("Code to Infobox Tool");

		MainGUI mainGUI = new MainGUI();
		frame.add(mainGUI.mainPanel);

		mainGUI.generateButton.addActionListener(e -> {

			String convertedText = onGenerate(mainGUI.inputTextArea.getText());
			mainGUI.outputTextArea.setText(convertedText);
		});

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private static String onGenerate(String input) {
		StringBuilder out = new StringBuilder();

		// Get all values from the input code
		Map<String, String> values = parseCode(input);

		// Get the filename for the wiki form to use
		//  This loads different infobox layouts
		String wikiFormPath = getFilenameFromType(values.get("Type"));

		if (!fileExists(wikiFormPath))
			return "[Error]: cannot find '" + wikiFormPath + "' file.";

		// Read the contents of the file into wikiForm
		String wikiForm = readAllOfFile(wikiFormPath);

		for (String line : wikiForm.split("\n")) {
			// Find any values in the form {...} or *...*, they are treated the same.
			Matcher matcher = pattern.matcher(line);

			// If the line doesn't have any {...} or *...*, then write it and continue
			if (!matcher.find()) {
				out.append(line).append("\n");
				continue;
			}

			// Get the key to be found in code values
			String replacementKey = line.substring(matcher.start()+1, matcher.end()-1);

			// If the key doesn't exist, then we shouldn't write the line at all.
			//  This causes lines that have {...} but do not have a relevant value to not be written,
			//    which is important so we do not have empty values in the infobox.
			if (!values.containsKey(replacementKey))
				continue;

			String replaced = matcher.replaceFirst(values.get(replacementKey));
			out.append(replaced).append("\n");
		}

		return out.toString();
	}


	private static Map<String, String> parseCode(String in) {
		Map<String, String> out = new HashMap<>();

		String[] lines = in.split("\n");

		// Find item name
		for (String line : lines) {
			if (line.contains("item")) {
				out.put("itemName", stripString(line).substring(4));
				break;
			}
		}

		for (String line : lines) {
			line = stripString(line);

			String[] kv = line.split("=");
			// Skip if there is no = sign
			if (kv.length < 2)
				continue;

			// Special cases
			switch (kv[0].trim()) {
				// One or two handed
				case "TwoHandWeapon":
					out.put("oneOrTwoHanded", "Two-handed");
					break;
				// Damage type
				case "Categories":
					String[] types = kv[1].split(";");
					if (types.length < 2)
						out.put("damageType", kv[1]);
					else
						out.put("damageType", types[types.length-1]);
					break;
				// Max units
				case "UseDelta":
					float f = Float.parseFloat(kv[1]);
					out.put("maxUnits", String.valueOf(1/f));
					break;
				default:
					out.put(kv[0].trim(), kv[1].trim());
			}
		}
		// Finalize special cases
		if (!out.containsKey("oneOrTwoHanded")) {
			out.put("oneOrTwoHanded", "One-handed");
			// Used for 'secondary_item' - there can't be a secondary item if
			// the item uses two hands already.
			out.put("oneHanded", "");
		}

		return out;
	}

	private static String stripString(String toStrip) {
		return toStrip.trim().replace(",", "");
	}

	private static String readAllOfFile(String filename) {
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

	private static boolean fileExists(String filename) {
		return Main.class.getResourceAsStream(filename) != null;
	}

	private static String getFilenameFromType(String itemType) {
		if (itemType == null)
			return null;

		switch (itemType) {
			case "Weapon":
				return "wikiForms/wikiForm_Weapon.txt";
			case "Literature":
				return "wikiForms/wikiForm_Literature.txt";
			case "Food":
				return "wikiForms/wikiForm_Food.txt";
			case "Drainable":
				return "wikiForms/wikiForm_Drainable.txt";
			default:
				return "wikiForms/wikiForm_Normal.txt";
		}
	}
}
