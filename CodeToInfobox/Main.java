import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static final int TAB_SIZE = 8;

	private static String patternStr = "\\{.*}|\\*.*\\*";
	private static Pattern pattern = Pattern.compile(patternStr);

	public static void main(String[] args) {
		JFrame frame = new JFrame("Code to Infobox Tool");

		MainGUI mainGUI = new MainGUI();
		frame.add(mainGUI.mainPanel);

		mainGUI.generateButton.addActionListener(e -> {

			String infoboxText = onGenerateInfobox(mainGUI.inputTextArea.getText());
			String formattedCodeText = onGenerateFormattedCode(mainGUI.inputTextArea.getText());

			mainGUI.infoboxTextArea.setText(infoboxText);
			mainGUI.formattedCodeTextArea.setText(formattedCodeText);
		});

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private static String onGenerateInfobox(String input) {
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

	private static String onGenerateFormattedCode(String input) {
		StringBuilder out = new StringBuilder();

		// Treat each line as its own entry
		String[] lines = input.split("\n");

		// Get the maximum length of a line
		int maxLength = 0;
		for (String line : lines) {
			String[] splitEquals = line.split("=");
			if (splitEquals.length < 2)
				continue;

			String beforeEquals = splitEquals[0].trim();
			maxLength = (beforeEquals.length() > maxLength) ? beforeEquals.length() : maxLength;
		}

		// For each line, set it to one tab passed max size
		for (String line : lines) {
			String[] splitEquals = line.split("=");

			// If the line doesn't have an equals, skip it
			if (splitEquals.length < 2) {
				// ... unless it's the item definition line
				if (line.trim().startsWith("item"))
					out.append("\t").append(line.trim()).append("\n");
				continue;
			}

			String beforeEquals = splitEquals[0].trim();
			String afterEquals = splitEquals[1].trim();

			// We want each line to have a tab after it, even if it's the maximum
			//  so we use a do while
			int numTabs = 0;
			int curLength = beforeEquals.length();
			do {
				numTabs++;
				// We add TAB_SIZE-(curLength%TAB_SIZE) because tab stops occur at TAB_SIZE distances.
				//  That is to say that a tab may only be the length of 1 character, if the line
				//  is only one character away from the tab stop.
				curLength += TAB_SIZE-(curLength%TAB_SIZE);
			}
			// Again, because each line has a tab after it, we need to stop
			//  when we reach the max length + 1 tab character's length
			while (curLength < maxLength+TAB_SIZE-(maxLength%TAB_SIZE));


			out.append("\t\t")
					.append(beforeEquals)
					.append(repeat(numTabs, "\t"))
					.append("= ")
					.append(afterEquals).append("\n");
		}

		return out.toString();
	}


	private static Map<String, String> parseCode(String in) {
		Map<String, String> out = new HashMap<>();

		String[] lines = in.split("\n");

		// Find item name
		for (String line : lines) {
			if (line.contains("item")) {
				// Exclude the 'item ' prior to the item's name
				out.put("itemName", stripString(line).substring(5));
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
						out.put("damageType", kv[1].trim());
					else
						out.put("damageType", types[types.length-1].trim());
					break;
				// Max units
				case "UseDelta":
					float f = Float.parseFloat(kv[1].trim());
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
			case "Drainable":
				return "wikiForms/Drainable.txt";
			case "Food":
				return "wikiForms/Food.txt";
			case "Literature":
				return "wikiForms/Literature.txt";
			case "Weapon":
				return "wikiForms/Weapon.txt";
			default:
				return "wikiForms/Normal.txt";
		}
	}

	private static String repeat(int count, String with) {
		return new String(new char[count]).replace("\0", with);
	}
}
