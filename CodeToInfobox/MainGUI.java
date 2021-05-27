import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class MainGUI {
	public JPanel mainPanel;
	public JButton generateButton;
	public JTextArea inputTextArea;
	public JTextArea infoboxTextArea;
	public JTextArea formattedCodeTextArea;

	private JButton copyButton;
	private JTabbedPane tabbedPane1;

	MainGUI() {
		formattedCodeTextArea.setTabSize(Main.TAB_SIZE);

		copyButton.addActionListener(e -> {
			// Get the content of the selected tab
			String selectedTabContent;
			switch (tabbedPane1.getTitleAt(tabbedPane1.getSelectedIndex())) {
				case "Formatted Code":
					selectedTabContent = formattedCodeTextArea.getText();
					break;
				default:
					selectedTabContent = infoboxTextArea.getText();
			}

			StringSelection stringSelection = new StringSelection(selectedTabContent);
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
}
