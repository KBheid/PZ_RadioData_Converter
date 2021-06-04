import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;


public class MainGUI {
	public JPanel mainPanel;
	public JList<String> itemsList;
	public JTextArea distributionTextArea;

	private JTextField filterItemsTextField;
	private JTextField selectedItemTextField;

	// A list of all the items in the game
	public List<String> allItems = new ArrayList<>();

	MainGUI() {
		// TODO: Delete, it's just for testing
		for (int i=0; i<itemsList.getModel().getSize(); i++) {
			allItems.add(itemsList.getModel().getElementAt(i));
		}

		setupFilter();
		setupList();
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
	}

	private void onFilterChange() {
		String text = filterItemsTextField.getText();

		DefaultListModel<String> newList = new DefaultListModel<>();
		for (String s : allItems) {
			// get rid of all spaces
			String toFind = PZLib.stripString(s, " ").toLowerCase();
			String searchString = PZLib.stripString(text, " ").toLowerCase();

			if (toFind.startsWith(searchString))
				newList.addElement(s);
		}

		itemsList.setModel(newList);
	}
}
