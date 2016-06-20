package activeSegmentation.gui;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;



import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.IExampleManager;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.StackWindow;
import ij.io.OpenDialog;
import ij.io.SaveDialog;

public class ExampleWindow extends StackWindow {


	/**
	 * 
	 */
	private IExampleManager exampleManager;
	private IDataManager dataManager;
	private static final long serialVersionUID = 1L;
	private ImagePlus displayImage;
	/** 50% alpha composite */
	final Composite transparency050 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f );
	/** 25% alpha composite */
	//final Composite transparency025 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f );
	/** opacity (in %) of the result overlay image */
	int overlayOpacity = 33;
	/** alpha composite for the result overlay image */
	Composite overlayAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayOpacity / 100f);
	/** current segmentation result overlay */
	ImageOverlay resultOverlay;
	private  int MAX_NUM_CLASSES =10;
	GridBagLayout boxAnnotation = new GridBagLayout();

	private java.awt.List exampleList[];
	/** available colors for available classes */
	private Color[] colors = new Color[]{Color.blue, Color.green, Color.red,
			Color.cyan, Color.magenta};
	private static final Icon uploadIcon = new ImageIcon(ExamplePanel.class.getResource("/activeSegmentation/images/upload.png"));
	//System.out.println("In Buttion ICon" +buttonIcon.getDescription());
	private static final Icon downloadIcon = new ImageIcon(ExamplePanel.class.getResource("/activeSegmentation/images/download.png"));
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 21, "Compute" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent LOAD_BUTTON_PRESSED = new ActionEvent( this, 22, "Load" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 23, "Save" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent DEFAULT_BUTTON_PRESSED = new ActionEvent( this, 24, "Default" );

	private int numberofClasses;

	/** array of roi list overlays to paint the transparent rois of each class */
	RoiListOverlay [] roiOverlay;

	public ExampleWindow(ImagePlus imp, IExampleManager exampleManager, IDataManager dataManager){

		super(imp, new OverlayedImageCanvas(imp) );	

		this.displayImage= imp;
		this.exampleManager = exampleManager;
		this.dataManager= dataManager;

		this.numberofClasses= exampleManager.getNumOfClasses();
		this.exampleList = new java.awt.List[this.numberofClasses];

		roiOverlay = new RoiListOverlay[this.numberofClasses];

		this.exampleList = new java.awt.List[this.numberofClasses];

		roiOverlay = new RoiListOverlay[this.numberofClasses];

		for(int i = 0; i < this.numberofClasses ; i++)
		{
			exampleList[i] = new java.awt.List(5);
			exampleList[i].setForeground(colors[i]);
		}



		// add roi list overlays (one per class)
		for(int i = 0; i < this.numberofClasses; i++)
		{
			roiOverlay[i] = new RoiListOverlay();
			roiOverlay[i].setComposite( transparency050 );
			((OverlayedImageCanvas)ic).addOverlay(roiOverlay[i]);
		}

		
		resultOverlay = new ImageOverlay();
		resultOverlay.setComposite( overlayAlpha );
		((OverlayedImageCanvas)ic).addOverlay(resultOverlay);

		this.setTitle("Active Segmentation");
		JPanel imagePanel = new JPanel();	
		imagePanel.add(ic);

		// Control panel
		ControlJPanel controlPanel = new ControlJPanel(imp,exampleManager, numberofClasses);

		Panel all = new Panel();
		BoxLayout box = new BoxLayout(all, BoxLayout.X_AXIS);
		all.setLayout(box);
		all.add(imagePanel);
		all.add(controlPanel);
		add(all);  	      	      	   

		this.pack();	 	    
		this.setVisible(true); 

	}





	public void doAction( final ActionEvent event )
	{
		if(event==COMPUTE_BUTTON_PRESSED){

		}
		if(event==SAVE_BUTTON_PRESSED){

		}

		if(event==LOAD_BUTTON_PRESSED){

		}
		if(event==DEFAULT_BUTTON_PRESSED){

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





}
