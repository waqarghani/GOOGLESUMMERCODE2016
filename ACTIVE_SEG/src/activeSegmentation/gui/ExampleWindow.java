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
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.w3c.dom.ls.LSInput;

import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.IExampleManager;


/**
 * 
 *  
 * @author
 *
 */
public class ExampleWindow extends StackWindow
{
	/** Generated serial version UID */
	private static final long serialVersionUID = -1037100741242680537L;
	private ImagePlus displayImage;
	// GUI components
	final JPanel labelsJPanel=new JPanel(new GridBagLayout());
	final JPanel resetJPanel = new JPanel(new GridBagLayout());
	JPanel imagePanel;
	JScrollPane scrollPanel;
	Panel all;
	/** 50% alpha composite */
	final Composite transparency050 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f );

	int numberofClasses;
	IExampleManager exampleManager;
	IDataManager dataManager;
	private JList exampleList[];
	private JList allexampleList[];
	private Color[] colors = new Color[]{Color.blue, Color.green, Color.red,
			Color.cyan, Color.magenta};

	/** array of roi list overlays to paint the transparent rois of each class */
	RoiListOverlay [] roiOverlay;
	private static final Icon uploadIcon = new ImageIcon(ExampleWindow.class.getResource("/activeSegmentation/images/upload.png"));
	private static final Icon downloadIcon = new ImageIcon(ExampleWindow.class.getResource("/activeSegmentation/images/download.png"));

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

	/**
	 * Constructs a control panel for interactive SIOX segmentation on given image.
	 */
	public ExampleWindow(ImagePlus imp,IExampleManager exampleManager, IDataManager dataManager)
	{
		super(imp, new OverlayedImageCanvas(imp) );	


		this.displayImage= imp;
		this.exampleManager= exampleManager;
		this.numberofClasses= exampleManager.getNumOfClasses();
		this.dataManager= dataManager;


		roiOverlay = new RoiListOverlay[Common.MAX_NUM_CLASSES];

		// add roi list overlays (one per class)
		for(int i = 0; i < Common.MAX_NUM_CLASSES; i++)
		{
			roiOverlay[i] = new RoiListOverlay();
			roiOverlay[i].setComposite( transparency050 );
			((OverlayedImageCanvas)ic).addOverlay(roiOverlay[i]);
		}


		resultOverlay = new ImageOverlay();
		resultOverlay.setComposite( overlayAlpha );
		((OverlayedImageCanvas)ic).addOverlay(resultOverlay);

		this.setTitle("Active Segmentation");
		 imagePanel = new JPanel(new GridBagLayout());	
		imagePanel.add(ic, getGbc(0, 0, 1, false, false));
		if(null != sliceSelector){

			sliceSelector.setEnabled(true);
			imagePanel.add(zSelector,getGbc(0, 0, 0, false, false));
			imagePanel.add(sliceSelector,getGbc(0, 1, 1, false, true));
		}


		this.exampleList = new JList[Common.MAX_NUM_CLASSES];
		this.allexampleList = new JList[Common.MAX_NUM_CLASSES];

		for(int i = 0; i < Common.MAX_NUM_CLASSES ; i++){
			
			exampleList[i] = model();
			exampleList[i].setForeground(colors[i]);
			allexampleList[i] = model();
			allexampleList[i].setForeground(colors[i]);
			
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
			addButton( null,uploadIcon,labelsJPanel,uploadAction,
					new Dimension(20, 21),getGbc(1, j, 1, false, false));
			ActionEvent downloadAction= new ActionEvent(this, i,"DownloadButton");
			addButton( null,downloadIcon ,labelsJPanel,downloadAction,
					new Dimension(20, 21),getGbc(2, j, 1, false, false));
			j++;
			exampleList[i].addMouseListener(mouseListener);
			// Add the listbox to a scrolling pane
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
			scrollPane.getViewport().add( exampleList[i] );
			labelsJPanel.add( scrollPane, getGbc(0,j, 1, false, false));
			JScrollPane scrollPane1 = new JScrollPane();
			scrollPane1.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
			scrollPane1.getViewport().add( allexampleList[i] );
			labelsJPanel.add( scrollPane1, getGbc(1,j, 1, false, false));
			j++;
		}

		//Scroll panel for the label panel
		scrollPanel = new JScrollPane( labelsJPanel );
		scrollPanel.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPanel.setMinimumSize( labelsJPanel.getPreferredSize() );

		resetJPanel.add(computeJButton, getGbc(0, 0, 1, false, false));				
		resetJPanel.add(loadJButton, getGbc(1, 0, 1, false, false));
		resetJPanel.add(saveJButton, getGbc(2, 0, 1, false, false));
		controlsBox.add(scrollPanel, getGbc(0, 0, 1, false, true));
		controlsBox.add(resetJPanel, getGbc(0, 2, 1, false, true));
		add(controlsBox, BorderLayout.EAST);




		Panel all = new Panel();
		BoxLayout box = new BoxLayout(all, BoxLayout.X_AXIS);
		all.setLayout(box);
		all.add(imagePanel);
		all.add(controlsBox);
		add(all);  	      	      	   

		this.pack();	 	    
		this.setVisible(true); 

	}// end ControlJPanel constructor


	
	private JList model(){
		DefaultListModel traces = new DefaultListModel();
		traces.addElement(" ");
		JList list=new JList(traces);
		list.setVisibleRowCount(5);
		list.setFixedCellHeight(20);
		list.setFixedCellWidth(100);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		return list;
	}

	private  MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			JList theList = ( JList) mouseEvent.getSource();
			if (mouseEvent.getClickCount() == 1) {
				int index = theList.getSelectedIndex();
				if (index >= 0) {
					String item =theList.getSelectedValue().toString();
					String[] arr= item.split(" ");
					showSelected( Integer.parseInt(arr[1]));
				}

			}

			if (mouseEvent.getClickCount() == 2) {
				int index = theList.getSelectedIndex();
				if (index >= 0) {
					String item =theList.getSelectedValue().toString();
					String[] arr= item.split(" ");
					deleteSelected(Integer.parseInt(arr[1]));
				}
			}
		}
	};



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
			final ActionEvent action, Dimension dimension,GridBagConstraints labelsConstraints ){
		final JButton button = new JButton();
		panel.add( button, labelsConstraints);
		button.setText( label );
		button.setIcon( icon );
		button.setFont( Common.FONT );
		button.setPreferredSize(dimension);
		button.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				doAction(action);
			}
		} );

		return button;
	}


	public void doAction( final ActionEvent event )	{
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


	/**
	 * Select a list and deselect the others
	 * 
	 * @param e item event (originated by a list)
	 * @param i list index
	 */
	private void showSelected(int classId ){
		// find the right slice of the corresponding ROI

		drawExamples();
		displayImage.setColor(Color.YELLOW);
		int index=exampleList[classId].getSelectedIndex();
		final Roi newRoi = 
				exampleManager.getExamples(classId, displayImage.getCurrentSlice())
				.get(index);
		// Set selected trace as current ROI
		newRoi.setImage(displayImage);
		displayImage.setRoi(newRoi);

		displayImage.updateAndDraw();
	}  


	/**
	 * Delete one of the ROIs
	 *
	 * @param e action event
	 */
	private void deleteSelected(int classId){
		int index = exampleList[classId].getSelectedIndex();
		exampleManager.deleteExample(classId, displayImage.getCurrentSlice(), index);
		drawExamples();
		updateExampleLists();
	}

	

	/**
	 * Repaint all panels
	 */
	public void repaintAll()
	{
		this.imagePanel.repaint();
		getCanvas().repaint();
		this.labelsJPanel.repaint();
		this.scrollPanel.repaint();
		this.all.repaint();
	}

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
			}
			roiOverlay[i].setRoi(rois);
		}

		displayImage.updateAndDraw();
	}



	/**
	 * Update the example lists in the GUI
	 */
	protected void updateExampleLists()	{
		final int currentSlice = displayImage.getCurrentSlice();

		for(int i = 0; i < numberofClasses; i++)
		{
			exampleList[i].removeAll();
			Vector listModel = new Vector();

			for(int j=0; j<exampleManager.getExamples(i, currentSlice).size(); j++){	

				listModel.addElement("trace " + i + " "+ j + " " + currentSlice);
				//exampleList[i].add(new JLabel("trace " + i + " "+ j + " " + currentSlice));
				//	exampleList[i].add("trace " + i + " "+ j + " " + currentSlice);
			}
			/*JList list=new JList(listModel);
			list.setVisibleRowCount(10);
			list.setFixedCellHeight(20);
			list.setFixedCellWidth(140);
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);*/

			exampleList[i].setListData(listModel);
			

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
	private void uploadExamples(int i){
		//get selected pixels

		OpenDialog od = new OpenDialog("Choose data file", OpenDialog.getLastDirectory(), "data.arff");
		if (od.getFileName()==null)
			return;

		final int n = displayImage.getCurrentSlice();

		dataManager.openZip(od.getDirectory() + od.getFileName(), i, n);
		drawExamples();
		updateExampleLists();
	}

}
