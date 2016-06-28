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
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;




import javax.swing.SwingUtilities;

import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.IExampleManager;


/**
 * 
 *  
 * @author
 *
 */
public class ExampleWindow extends StackWindow implements ActionListener
{
	/** Generated serial version UID */
	private static final long serialVersionUID = -1037100741242680537L;
	private ImagePlus displayImage;
	// GUI components
	final JPanel labelsJPanel=new JPanel(new GridBagLayout());
	final JPanel resetJPanel = new JPanel(new GridBagLayout());
	final JPanel configureJPanel = new JPanel(new GridBagLayout());
	JPanel imagePanel;
	JPanel controlsBox;
	JScrollPane scrollPanel;
	Panel all;
	/** 50% alpha composite */
	final Composite transparency050 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f );

	IExampleManager exampleManager;
	IDataManager dataManager;
	private List<JList> exampleList;
	private List<JList> allexampleList;
	int originalJ=0;

	/** array of roi list overlays to paint the transparent rois of each class */
	private List<RoiListOverlay> roiOverlayList;
	private static final Icon uploadIcon = new ImageIcon(ExampleWindow.class.getResource("/activeSegmentation/images/upload.png"));
	private static final Icon downloadIcon = new ImageIcon(ExampleWindow.class.getResource("/activeSegmentation/images/download.png"));

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 21, "TRAIN" );

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent LOAD_BUTTON_PRESSED = new ActionEvent( this, 22, "Load" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 23, "Save" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent CONFIGURE_BUTTON_PRESSED = new ActionEvent( this, 24, "CONFIGURE" );
	final ActionEvent ADD_BUTTON_PRESSED = new ActionEvent( this, 25, "ADDCLASS" );

	/** opacity (in %) of the result overlay image */
	int overlayOpacity = 33;
	/** alpha composite for the result overlay image */
	Composite overlayAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayOpacity / 100f);

	ImageOverlay resultOverlay;
	private ConfigureExample configureExample;



	public ExampleWindow(ImagePlus imp,IExampleManager exampleManager, IDataManager dataManager)
	{
		super(imp, new OverlayedImageCanvas(imp) );	
		this.displayImage= imp;
		this.exampleManager= exampleManager;
		this.dataManager= dataManager;
		configureExample= ConfigureExample.getConfigureInstance(exampleManager);
		this.setTitle("Active Segmentation");
		this.exampleList = new ArrayList<JList>();
		this.allexampleList = new ArrayList<JList>();

		roiOverlayList = new ArrayList<RoiListOverlay>();
		setOverlay();
		imagePanel = new JPanel(new GridBagLayout());	
		imagePanel.add(ic, Util.getGbc(0, 0, 1, false, false));
		if(null != sliceSelector){

			sliceSelector.setEnabled(true);
			imagePanel.add(zSelector,Util.getGbc(0, 0, 0, false, false));
			imagePanel.add(sliceSelector,Util.getGbc(0, 1, 1, false, true));
		}

		for(int i = 0; i < configureExample.getMaxNoClasses() ; i++){

			JList current=Util.model();
			current.setForeground(configureExample.getColors().get(i));
			exampleList.add(current);
			JList all=Util.model();
			all.setForeground(configureExample.getColors().get(i));
			allexampleList.add(all);		

		}

		createPanel();

		Panel all = new Panel();
		BoxLayout box = new BoxLayout(all, BoxLayout.X_AXIS);
		all.setLayout(box);
		all.add(imagePanel);
		all.add(controlsBox);
		add(all);  	      	      	   

		this.pack();	 	    
		this.setVisible(true); 

	}// end ControlJPanel constructor

	private void setOverlay(){

		resultOverlay = new ImageOverlay();
		resultOverlay.setComposite( overlayAlpha );
		((OverlayedImageCanvas)ic).addOverlay(resultOverlay);
	}


	private void addSidePanel(int i){

		RoiListOverlay roiOverlay = new RoiListOverlay();
		roiOverlay.setComposite( transparency050 );
		((OverlayedImageCanvas)ic).addOverlay(roiOverlay);
		roiOverlayList.add(roiOverlay);
		JPanel buttonsPanel = new JPanel(new GridBagLayout());
		ActionEvent addbuttonAction= new ActionEvent(this, i,"AddButton");
		addButton( exampleManager.getClassLabels().get(i),null ,labelsJPanel,
				addbuttonAction,new Dimension(100, 21),Util.getGbc(0 ,originalJ , 1, false, false) );

		ActionEvent uploadAction= new ActionEvent(this, i,"UploadButton");
		addButton( null,uploadIcon,buttonsPanel,uploadAction,
				new Dimension(20, 21),Util.getGbc(0, 0, 1, false, false));
		ActionEvent downloadAction= new ActionEvent(this, i,"DownloadButton");
		addButton( null,downloadIcon ,buttonsPanel,downloadAction,
				new Dimension(20, 21),Util.getGbc(1, 0, 1, false, false));
		labelsJPanel.add(buttonsPanel,Util.getGbc(1,originalJ , 1, false, false) );
		originalJ++;
		exampleList.get(i).addMouseListener(mouseListener);
		// Add the listbox to a scrolling pane
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPane.getViewport().add( exampleList.get(i) );
		labelsJPanel.add( scrollPane, Util.getGbc(0,originalJ, 1, false, false));
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPane1.getViewport().add( allexampleList.get(i) );
		labelsJPanel.add( scrollPane1, Util.getGbc(1,originalJ, 1, false, false));
		originalJ++;
	}


	private void createPanel(){

		addButton( "CONFIGURE",null ,configureJPanel,
				CONFIGURE_BUTTON_PRESSED,new Dimension(100, 25),Util.getGbc(0,0 , 1, false, false) );

		controlsBox=new JPanel(new GridBagLayout());

		labelsJPanel.setBorder(BorderFactory.createTitledBorder("LABELS"));

		for(int i = 0; i < configureExample.getNumberofClasses(); i++)
		{
			addSidePanel(i);
		}

		//Scroll panel for the label panel
		scrollPanel = new JScrollPane( labelsJPanel );
		scrollPanel.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPanel.setMinimumSize( labelsJPanel.getPreferredSize() );

		addButton( "COMPUTE",null ,resetJPanel,
				COMPUTE_BUTTON_PRESSED,new Dimension(100, 25),Util.getGbc(0, 0, 1, false, false));
		addButton( "LOAD",null ,resetJPanel,
				LOAD_BUTTON_PRESSED,new Dimension(100, 25),Util.getGbc(1, 0, 1, false, false) );
		addButton( "SAVE",null ,resetJPanel,
				SAVE_BUTTON_PRESSED,new Dimension(100, 25),Util.getGbc(2, 0, 1, false, false) );

		controlsBox.add(configureJPanel, Util.getGbc(0, 0, 1, false, true));
		controlsBox.add(scrollPanel, Util.getGbc(0, 1, 1, false, true));
		controlsBox.add(resetJPanel, Util.getGbc(0, 2, 1, false, true));
		add(controlsBox, BorderLayout.EAST);

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

	@Override
	public void actionPerformed( final ActionEvent event )
	{
		if(event==configureExample.ADD_BUTTON_PRESSED ){

			System.out.println("EVENt");
		}

	}

	public void doAction( final ActionEvent event )	{
		if(event==COMPUTE_BUTTON_PRESSED){


		}
		if(event==SAVE_BUTTON_PRESSED){


		}

		if(event==LOAD_BUTTON_PRESSED){

		}




		if(event==CONFIGURE_BUTTON_PRESSED){

			configureExample.show();

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
		int index=exampleList.get(classId).getSelectedIndex();
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
		int index = exampleList.get(classId).getSelectedIndex();
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

		for(int i = 0; i < configureExample.getNumberofClasses(); i++)
		{
			roiOverlayList.get(i).setColor(configureExample.getColors().get(i));
			final ArrayList< Roi > rois = new ArrayList<Roi>();
			for (Roi r : exampleManager.getExamples(i, currentSlice))
			{
				rois.add(r);
			}
			roiOverlayList.get(i).setRoi(rois);
		}

		displayImage.updateAndDraw();
	}



	/**
	 * Update the example lists in the GUI
	 */
	protected void updateExampleLists()	{
		final int currentSlice = displayImage.getCurrentSlice();

		for(int i = 0; i < configureExample.getNumberofClasses(); i++)
		{
			exampleList.get(i).removeAll();
			Vector listModel = new Vector();

			for(int j=0; j<exampleManager.getExamples(i, currentSlice).size(); j++){	

				listModel.addElement("trace " + i + " "+ j + " " + currentSlice);
			}
			exampleList.get(i).setListData(listModel);
			exampleList.get(i).setForeground(configureExample.getColors().get(i));
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
