import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Code to Infobox Tool");

		MainGUI mainGUI = new MainGUI();
		frame.add(mainGUI.mainPanel);

		mainGUI.itemsList.addListSelectionListener(e -> {
			if (e.getValueIsAdjusting())
				onSelectItem(mainGUI.itemsList.getSelectedValue());
		});

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	/**
	 * When an object is selected from the items list
	 * @param selected The String name of the selected item.
	 */
	private static void onSelectItem(String selected) {
		System.out.println(selected);
	}
}
