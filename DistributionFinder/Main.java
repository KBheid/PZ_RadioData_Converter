import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import javax.swing.*;

public class Main {
	static final Globals globals = JsePlatform.standardGlobals();

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
		//LuaValue chunk = globals.loadfile("lua/test.lua");
		//LuaValue ret = chunk.call(LuaValue.valueOf(selected));

		//System.out.println(ret.arg1().tojstring());
	}
}
