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
	public JTextArea outputTextArea;

	private JButton copyButton;

	MainGUI() {
		copyButton.addActionListener(e -> {
			StringSelection stringSelection = new StringSelection(outputTextArea.getText());
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
