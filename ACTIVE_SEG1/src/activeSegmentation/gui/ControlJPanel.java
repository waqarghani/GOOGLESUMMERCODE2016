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
import ij.gui.Roi;
import ij.gui.StackWindow;
import ij.io.OpenDialog;
import ij.io.SaveDialog;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.IExampleManager;

/**
 * This class implements the interactive buttons for the Siox segmentation GUI.
 *  
 * @author Ignacio Arganda-Carreras, Johannes Schindelin, Stephan Saalfeld
 *
 */
public class ControlJPanel extends StackWindow
{
	/** Generated serial version UID */
	private static final long serialVersionUID = -1037100741242680537L;
	private ImagePlus displayImage;
	// GUI components
	final JPanel labelsJPanel=new JPanel(new GridBagLayout());
	final JPanel resetJPanel = new JPanel(new GridBagLayout());


	final JLabel fgOrBgJLabel=new JLabel("Add Known ");
	final JButton segmentJButton = new JButton("Segment");
	int numberofClasses;
	IExampleManager exampleManager;
	IDataManager dataManager;
	private java.awt.List exampleList[];
	private Color[] colors = new Color[]{Color.blue, Color.green, Color.red,
			Color.cyan, Color.magenta};

	/** array of roi list overlays to paint the transparent rois of each class */
	RoiListOverlay [] roiOverlay;
	private static final Icon uploadIcon = new ImageIcon(ExamplePanel.class.getResource("/activeSegmentation/images/upload.png"));
	//System.out.println("In Buttion ICon" +buttonIcon.getDescription());
	private static final Icon downloadIcon = new ImageIcon(ExamplePanel.class.getResource("/activeSegmentation/images/download.png"));

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 21, "TRAIN" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent LOAD_BUTTON_PRESSED = new ActionEvent( this, 22, "Load" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 23, "Save" );


	final JButton computeJButton=new JButton("COMPUTE");	
	final JButton loadJButton=new JButton("LOAD");
	final JButton saveJButton=new JButton("SAVE");
	/** opacity (in %) of the result overlay image */
	int overlayOpacity = 33;
	/** alpha composite for the result overlay image */
	Composite overlayAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayOpacity / 100f);
	

	ImageOverlay resultOverlay;


	//-----------------------------------------------------------------
	/**
	 * Constructs a control panel for interactive SIOX segmentation on given image.
	 */
	public ControlJPanel(ImagePlus imp,IExampleManager exampleManager, IDataManager dataManager ,int classes)
	{
		super(imp, new OverlayedImageCanvas(imp) );	


		this.displayImage= imp;
		this.exampleManager= exampleManager;
		this.numberofClasses= classes;
		this.dataManager= dataManager;


		roiOverlay = new RoiListOverlay[this.numberofClasses];


		resultOverlay = new ImageOverlay();
		resultOverlay.setComposite( overlayAlpha );
		((OverlayedImageCanvas)ic).addOverlay(resultOverlay);

		this.setTitle("Active Segmentation");
		JPanel imagePanel = new JPanel();	
		imagePanel.add(ic);
		this.exampleList = new java.awt.List[this.numberofClasses];

		for(int i = 0; i < this.numberofClasses ; i++)
		{
			exampleList[i] = new java.awt.List(5);
			exampleList[i].setForeground(colors[i]);
		}



		final JPanel controlsBox=new JPanel(new GridBagLayout());

		labelsJPanel.setBorder(BorderFactory.createTitledBorder("LABELS"));

		int j=0;
		for(int i = 0; i < numberofClasses; i++)
		{
			ActionEvent addbuttonAction= new ActionEvent(this, i,"AddButton");
			addButton( exampleManager.getClassLabels()[i],null ,labelsJPanel,
					addbuttonAction,new Dimension(100, 21),getGbc(0,j , 1, false, false) );
			ActionEvent uploadAction= new ActionEvent(this, i,"UploadButton");
			j++;
			addButton( null,uploadIcon,labelsJPanel,uploadAction,
					new Dimension(20, 21),getGbc(0, j, 1, false, false));
			ActionEvent downloadAction= new ActionEvent(this, i,"DownloadButton");
			addButton( null,downloadIcon ,labelsJPanel,downloadAction,
					new Dimension(20, 21),getGbc(1, j, 1, false, false));
			j++;
			labelsJPanel.add( exampleList[i], getGbc(0,j, 1, false, false));
			j++;
		}




		//Scroll panel for the label panel
		JScrollPane scrollPanel = new JScrollPane( labelsJPanel );
		scrollPanel.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPanel.setMinimumSize( labelsJPanel.getPreferredSize() );


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



	private JButton addButton( final String label, final Icon icon,JComponent panel, 
			final ActionEvent action, Dimension dimension,GridBagConstraints labelsConstraints )
	{
		final JButton button = new JButton();
		panel.add( button, labelsConstraints);
		button.setText( label );
		button.setIcon( icon );
		button.setFont( Common.FONT );
		button.setPreferredSize(dimension);
		System.out.println("ADDED");
		button.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				System.out.println("CLICKED");
				doAction(action);
			}
		} );

		return button;
	}



	private JButton addButton( final String label, final Icon icon, final int x,
			final int y, final int width, final int height,JComponent panel, final ActionEvent action)
	{
		final JButton button = new JButton();
		panel.add( button );
		button.setText( label );
		button.setIcon( icon );
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFont( Common.FONT );
		button.setBounds( x, y, width, height );
		System.out.println("ADDED");
		button.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				System.out.println("CLICKED");
				doAction(action);
			}
		} );

		return button;
	}



	public void doAction( final ActionEvent event )
	{
		if(event==COMPUTE_BUTTON_PRESSED){

		}
		if(event==SAVE_BUTTON_PRESSED){

		}

		if(event==LOAD_BUTTON_PRESSED){

		}

		if(event.getActionCommand()== "AddButton"){	
			addExamples(event.getID());
		}

		if(event.getActionCommand()== "UploadButton"){	

			uploadExamples(event.getID());
		}

		if(event.getActionCommand()== "DownloadButton"){	

			saveRoi(event.getID());
		}

	}	

	/* HELPER FUNCTIONS */

	/* HELPER FUNCTIONS*/


	private void addExamples(int i)
	{
		final Roi r = displayImage.getRoi();

		if (null == r)
			return;

		final int n = displayImage.getCurrentSlice();

		displayImage.killRoi();
		exampleManager.addExample(i, r, n);
		//traceCounter[i]++;
		drawExamples();
		this.updateExampleLists();

	}


	/**
	 * Draw the painted traces on the display image
	 */
	protected void drawExamples()
	{
		final int currentSlice = displayImage.getCurrentSlice();

		for(int i = 0; i < this.numberofClasses; i++)
		{
			roiOverlay[i].setColor(colors[i]);
			final ArrayList< Roi > rois = new ArrayList<Roi>();
			for (Roi r : exampleManager.getExamples(i, currentSlice))
			{
				rois.add(r);
				//IJ.log("painted ROI: " + r + " in color "+ colors[i] + ", slice = " + currentSlice);
			}
			roiOverlay[i].setRoi(rois);
		}

		displayImage.updateAndDraw();
	}



	/**
	 * Update the example lists in the GUI
	 */
	protected void updateExampleLists()
	{
		final int currentSlice = displayImage.getCurrentSlice();

		for(int i = 0; i < numberofClasses; i++)
		{
			exampleList[i].removeAll();
			for(int j=0; j<exampleManager.getExamples(i, currentSlice).size(); j++)
				exampleList[i].add("trace " + j + " (Z=" + currentSlice+")");
		}

	}


	public boolean saveRoi(int  i) {

		String path;
		SaveDialog sd = new SaveDialog("Save ROIs...", "RoiSet", ".zip");
		String name = sd.getFileName();
		if (name == null)
			return false;
		if (!(name.endsWith(".zip") || name.endsWith(".ZIP")))
			name = name + ".zip";
		String dir = sd.getDirectory();
		path = dir+name;

		final int n = displayImage.getCurrentSlice();
		return dataManager.saveExamples(path, exampleManager.getExamples(i, n));

	}


	/**
	 * Add examples defined by the user to the corresponding list
	 * in the GUI and the example list in the segmentation object.
	 * 
	 * @param i GUI list index
	 */
	private void uploadExamples(int i)
	{
		//get selected pixels

		OpenDialog od = new OpenDialog("Choose data file", OpenDialog.getLastDirectory(), "data.arff");
		if (od.getFileName()==null)
			return;


		final int n = displayImage.getCurrentSlice();

		dataManager.openZip(od.getDirectory() + od.getFileName(), i, n);

		drawExamples();
		updateExampleLists();
	}




}// end class ControlJPanel
