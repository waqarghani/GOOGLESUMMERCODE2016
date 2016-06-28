package activeSegmentation.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

public class Util {
	
	/**
	 * Returns a gridbag constraint with the given parameters, standard
	 * L&amp;F insets and a west anchor.
	 */
	public static GridBagConstraints getGbc(int x, int y, int width,
			boolean vFill, boolean hFill)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 5, 5);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		if (vFill) { // position may grow vertical
			c.fill = GridBagConstraints.VERTICAL;
			c.weighty = 1.0;
		}
		if (hFill) { // position may grow horizontally
			c.fill = hFill
					? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
		}
		return c;
	}

	public static  JList model(){
		DefaultListModel traces = new DefaultListModel();
		traces.addElement(" ");
		JList list=new JList(traces);
		list.setVisibleRowCount(5);
		list.setFixedCellHeight(20);
		list.setFixedCellWidth(100);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		return list;
	}


}
