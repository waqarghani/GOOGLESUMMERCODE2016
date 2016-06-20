/**
 * Siox_Segmentation plug-in for ImageJ and Fiji.
 * 2009 Ignacio Arganda-Carreras, Johannes Schindelin, Stephan Saalfeld 
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation (http://www.gnu.org/licenses/gpl.txt )
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package activeSegmentation.gui;

import ij.ImagePlus;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import activeSegmentation.IExampleManager;

/**
 * This class implements the interactive buttons for the Siox segmentation GUI.
 *  
 * @author Ignacio Arganda-Carreras, Johannes Schindelin, Stephan Saalfeld
 *
 */
public class ControlJPanel extends JPanel
{
	/** Generated serial version UID */
	private static final long serialVersionUID = -1037100741242680537L;
	// GUI components
	final JPanel labelsJPanel=new JPanel(new GridBagLayout());
	final JPanel resetJPanel = new JPanel(new GridBagLayout());

	final JLabel fgOrBgJLabel=new JLabel("Add Known ");
	final JButton segmentJButton = new JButton("Segment");
    int numberofClasses;
    IExampleManager exampleManager;

    private static final Icon uploadIcon = new ImageIcon(ExamplePanel.class.getResource("/activeSegmentation/images/upload.png"));
	//System.out.println("In Buttion ICon" +buttonIcon.getDescription());
	private static final Icon downloadIcon = new ImageIcon(ExamplePanel.class.getResource("/activeSegmentation/images/download.png"));

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 21, "Compute" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent LOAD_BUTTON_PRESSED = new ActionEvent( this, 22, "Load" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 23, "Save" );
	

	final JButton computeJButton=new JButton("COMPUTE");	
	final JButton loadJButton=new JButton("LOAD");
	final JButton saveJButton=new JButton("SAVE");


	


	//-----------------------------------------------------------------
	/**
	 * Constructs a control panel for interactive SIOX segmentation on given image.
	 */
	public ControlJPanel(ImagePlus imp,IExampleManager exampleManager, int classes)
	{
		super(new BorderLayout());
		this.exampleManager= exampleManager;
		this.numberofClasses= classes;

		final JPanel controlsBox=new JPanel(new GridBagLayout());

		labelsJPanel.setBorder(BorderFactory.createTitledBorder("LABELS"));
		for(int i = 0; i < numberofClasses; i++)
		{
			
			
		}




		//Scroll panel for the label panel
		JScrollPane scrollPanel = new JScrollPane( labelsJPanel );
		scrollPanel.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPanel.setMinimumSize( labelsJPanel.getPreferredSize() );


		final String resetTooltip = "Reset displayed image";
		computeJButton.setToolTipText(resetTooltip);		
		resetJPanel.add(computeJButton, getGbc(0, 0, 1, false, false));				
		resetJPanel.add(loadJButton, getGbc(1, 0, 1, false, false));
		resetJPanel.add(saveJButton, getGbc(2, 0, 1, false, false));

		controlsBox.add(scrollPanel, getGbc(0, 0, 1, false, true));

		controlsBox.add(resetJPanel, getGbc(0, 2, 1, false, true));

		add(controlsBox, BorderLayout.EAST);


	}// end ControlJPanel constructor

	/**
	 * Returns a gridbag constraint with the given parameters, standard
	 * L&amp;F insets and a west anchor.
	 */
	private static GridBagConstraints getGbc(int x, int y, int width,
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


	



}// end class ControlJPanel
