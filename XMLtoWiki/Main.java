import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Main {
	private static String fileSeparator = File.separator;

	private static class Entry {
		String hexColor;
		String text;

		Entry(String hexColor, String text) {
			this.hexColor = hexColor;
			this.text = text;
		}

		@Override
		public String toString() {
			return "|- \n| style=\"color:" + hexColor + "\" | " + text + "\n";
		}
	}

	private static class Broadcast {
		int startTime;
		int endTime;
		int day;
		ArrayList<Entry> entries = new ArrayList<>();

		Broadcast(int day, int startTime, int endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.day = day;
		}

		void addEntry(Entry entry) {
			entries.add(entry);
		}

		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();

			ret.append("Day: ")
				.append(day)
				.append(" Start time: ")
				.append(startTime)
				.append(" End time: ")
				.append(endTime)
				.append("\n{| class=\"mw-collapsible pztable\"\n !Text \n");

			for (Entry e : entries) {
				ret.append(e.toString());
			}

			ret.append("|}<br>\n\n");
			return ret.toString();
		}
	}

	private static class Channel {
		String name;
		String channelType;
		String frequency;
		ArrayList<Broadcast> broadcasts = new ArrayList<>();

		Channel(String name, String channelType, String frequency) {
			this.name = name;
			this.channelType = channelType;
			this.frequency = frequency;
		}

		void addBroadcast(Broadcast b) {
			broadcasts.add(b);
		}

		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			ret.append("===")
				.append(name)
				.append(" - ")
				.append(frequency)
				.append("===\n");

			for (Broadcast broadcast : broadcasts) {
				ret.append(broadcast.toString());
			}

			return ret.toString();
		}
	}

    public static void main(String[] args) {
		JFrame frame = new JFrame("Radio Data Parser");

		JFrame completeFrame = new JFrame("Radio Data Parser - Complete");
		completeFrame.add(new JLabel("Complete! Your files can be found under the specified directory."));
		completeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		completeFrame.pack();

		MainGUI gui = new MainGUI();
		frame.setContentPane(gui.mainPanel);


		gui.goButton.addActionListener(e -> {
			frame.dispose();
			ParseXML(gui.inFilenameText.getText(), gui.outFilenameText.getText());
			completeFrame.setVisible(true);
		});

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private static void ParseXML(String inFilename, String outDir) {
		// Instantiate the Factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			// parse XML file
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(inFilename));

			NodeList channelEntries = doc.getElementsByTagName("ChannelEntry");
			for (int channelEntryIndex=0; channelEntryIndex<channelEntries.getLength(); channelEntryIndex++) {
				Node channelEntry = channelEntries.item(channelEntryIndex);

				String channelName 	= channelEntry.getAttributes().getNamedItem("name").getNodeValue();
				String channelType 	= channelEntry.getAttributes().getNamedItem("cat").getNodeValue();
				String frequency   	= channelEntry.getAttributes().getNamedItem("freq").getNodeValue();

				Channel channel = new Channel(channelName, channelType, frequency);

				// get <ScriptEntry>
				NodeList scriptEntries = channelEntry.getChildNodes();
				for (int scriptEntryIndex = 0; scriptEntryIndex < scriptEntries.getLength(); scriptEntryIndex++) {
					Node scriptEntry = scriptEntries.item(scriptEntryIndex);
					if (!scriptEntry.getNodeName().equals("ScriptEntry"))
						continue;

					// get children <BroadcastEntry>
					NodeList broadcastEntries = scriptEntry.getChildNodes();
					for (int broadcastEntryIndex = 0; broadcastEntryIndex < broadcastEntries.getLength(); broadcastEntryIndex++) {
						Node broadcastEntry = broadcastEntries.item(broadcastEntryIndex);
						if (!broadcastEntry.getNodeName().equals("BroadcastEntry"))
							continue;

						int timeStart = Integer.parseInt(broadcastEntry.getAttributes().getNamedItem("timestamp").getNodeValue());
						int timeEnd = Integer.parseInt(broadcastEntry.getAttributes().getNamedItem("endstamp").getNodeValue());
						int day = Integer.parseInt(broadcastEntry.getAttributes().getNamedItem("day").getNodeValue());

						Broadcast broadcast = new Broadcast(day, timeStart, timeEnd);
						channel.addBroadcast(broadcast);

						// get children <LineEntry>
						NodeList lineEntries = broadcastEntry.getChildNodes();
						for (int lineEntryIndex = 0; lineEntryIndex < lineEntries.getLength(); lineEntryIndex++) {
							Node lineEntry = lineEntries.item(lineEntryIndex);
							if (!lineEntry.getNodeName().equals("LineEntry"))
								continue;

							int red = Integer.parseInt(lineEntry.getAttributes().getNamedItem("r").getNodeValue());
							int green = Integer.parseInt(lineEntry.getAttributes().getNamedItem("g").getNodeValue());
							int blue = Integer.parseInt(lineEntry.getAttributes().getNamedItem("b").getNodeValue());

							String hexColor = String.format("#%02x%02x%02x", red, green, blue);

							String text = lineEntry.getTextContent();
							text = text.replace("[img=music]", "♪");
							text = text.replace("&lt;", "<");
							text = text.replace("&gt;", ">");

							Entry entry = new Entry(hexColor, text);
							broadcast.addEntry(entry);
						}
					}
				}

				File dir = new File(outDir + fileSeparator + channel.channelType);
				dir.mkdirs();

				File f = new File(outDir + fileSeparator + channel.channelType + fileSeparator + channel.name + ".wiki");
				FileOutputStream outputFileStream = new FileOutputStream(f);
				outputFileStream.write(channel.toString().getBytes());
			}

			// Blank file
			File advertsFile = new File(outDir + fileSeparator + "Adverts.wiki");
			advertsFile.delete();
			advertsFile.createNewFile();

			NodeList advertScriptEntries = doc.getElementsByTagName("ChannelEntry").item(0).getChildNodes();
			for (int advertScriptEntryIndex=0; advertScriptEntryIndex<advertScriptEntries.getLength(); advertScriptEntryIndex++) {
				Node advertScriptEntry = advertScriptEntries.item(advertScriptEntryIndex);
				if (!advertScriptEntry.getNodeName().equals("ScriptEntry"))
					continue;

				NodeList broadcastEntries = advertScriptEntry.getChildNodes();
				for (int broadcastEntryIndex = 0; broadcastEntryIndex < broadcastEntries.getLength(); broadcastEntryIndex++) {
					Node broadcastEntry = broadcastEntries.item(broadcastEntryIndex);
					if (!broadcastEntry.getNodeName().equals("BroadcastEntry"))
						continue;

					int timeStart = Integer.parseInt(broadcastEntry.getAttributes().getNamedItem("timestamp").getNodeValue());
					int timeEnd = Integer.parseInt(broadcastEntry.getAttributes().getNamedItem("endstamp").getNodeValue());
					int day = Integer.parseInt(broadcastEntry.getAttributes().getNamedItem("day").getNodeValue());

					Broadcast broadcast = new Broadcast(day, timeStart, timeEnd);

					// get children <LineEntry>
					NodeList lineEntries = broadcastEntry.getChildNodes();
					for (int lineEntryIndex = 0; lineEntryIndex < lineEntries.getLength(); lineEntryIndex++) {
						Node lineEntry = lineEntries.item(lineEntryIndex);
						if (!lineEntry.getNodeName().equals("LineEntry"))
							continue;

						int red = Integer.parseInt(lineEntry.getAttributes().getNamedItem("r").getNodeValue());
						int green = Integer.parseInt(lineEntry.getAttributes().getNamedItem("g").getNodeValue());
						int blue = Integer.parseInt(lineEntry.getAttributes().getNamedItem("b").getNodeValue());

						String hexColor = String.format("#%02x%02x%02x", red, green, blue);

						String text = lineEntry.getTextContent();
						text = text.replace("[img=music]", "♪");
						text = text.replace("&lt;", "<");
						text = text.replace("&gt;", ">");

						Entry entry = new Entry(hexColor, text);
						broadcast.addEntry(entry);
					}


					FileOutputStream outputFileStream = new FileOutputStream(advertsFile, true);
					outputFileStream.write(broadcast.toString().getBytes());
				}
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

    }
}
