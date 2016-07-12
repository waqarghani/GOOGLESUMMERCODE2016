package activeSegmentation.gui;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.StackWindow;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.process.ImageProcessor;
import ij.process.LUT;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;


import weka.core.Instances;
import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.IExampleManager;
import activeSegmentation.IFilterManager;
import activeSegmentation.feature.FeatureExtraction;


/**
 * 
 *  
 * @author
 *
 */
public class ExampleWindow1 extends StackWindow 
{
	/** Generated serial version UID */
	private static final long serialVersionUID = -1037100741242680537L;
	private ImagePlus displayImage;
	/** Lookup table for the result overlay image */

	// GUI components
	final JPanel labelsJPanel=new JPanel(new GridBagLayout());
	final JPanel resetJPanel = new JPanel(new GridBagLayout());
	final JPanel configureJPanel = new JPanel(new GridBagLayout());
	private List<JCheckBox> jCheckBoxList= new ArrayList<JCheckBox>();
	JPanel imagePanel;
	JPanel controlsBox;
	private JFrame frame = new JFrame("CONFIGURE");
	private int numberofClasses;
	private ImagePlus classifiedImage;
	Panel all;
	/** 50% alpha composite */
	final Composite transparency050 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f );

	// Create overlay LUT
	byte[] red = new byte[ 256 ];
	byte[] green = new byte[ 256 ];
	byte[] blue = new byte[ 256 ];

	IExampleManager exampleManager;
	IDataManager dataManager;
	IFilterManager filterManager;
	FeatureExtraction featureExtraction;
	private List<JList> exampleList;
	private List<JList> allexampleList;
	private List<Color> colors ;
	/** flag to display the overlay image */
	private boolean showColorOverlay=false;
	int originalJ=0;
	JPanel classJPanel;
	int originajFrameJ=0, originalFrameK=0;
	LUT overlayLUT;


	/** array of roi list overlays to paint the transparent rois of each class */
	private List<RoiListOverlay> roiOverlayList;

	private static final Icon uploadIcon = new ImageIcon(ExampleWindow1.class.getResource("/activeSegmentation/images/upload.png"));
	private static final Icon downloadIcon = new ImageIcon(ExampleWindow1.class.getResource("/activeSegmentation/images/download.png"));

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 21, "TRAIN" );

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent LOAD_BUTTON_PRESSED = new ActionEvent( this, 22, "Load" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 23, "Save" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent CONFIGURE_BUTTON_PRESSED = new ActionEvent( this, 24, "CONFIGURE" );
	final ActionEvent ADD_BUTTON_PRESSED = new ActionEvent( this, 25, "ADDCLASS" );
	final ActionEvent DELETE_BUTTON_PRESSED = new ActionEvent( this, 2, "Delete" );
	/** opacity (in %) of the result overlay image */
	int overlayOpacity = 33;
	/** alpha composite for the result overlay image */
	Composite overlayAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayOpacity / 100f);
	Dimension dimension=new Dimension(100, 25);
	ImageOverlay resultOverlay;



	public ExampleWindow1(ImagePlus imp,IExampleManager exampleManager, 
			IDataManager dataManager, IFilterManager filterManager)
	{
		super(imp, new OverlayedImageCanvas(imp) );	
		this.displayImage= imp;
		this.exampleManager= exampleManager;
		this.dataManager= dataManager;
		this.filterManager= filterManager;
		this.featureExtraction= new FeatureExtraction(filterManager, exampleManager);
		this.setTitle("Active Segmentation");
		this.exampleList = new ArrayList<JList>();
		this.allexampleList = new ArrayList<JList>();
		this.numberofClasses= 2;
		colors=Util.setDefaultColors();
		roiOverlayList = new ArrayList<RoiListOverlay>();
		setOverlay();
		
		imagePanel = new JPanel(new GridBagLayout());	
		imagePanel.add(ic, Util.getGbc(0, 0, 1, false, false));
		if(null != sliceSelector){

			sliceSelector.setEnabled(true);
			imagePanel.add(zSelector,Util.getGbc(0, 0, 0, false, false));
			imagePanel.add(sliceSelector,Util.getGbc(0, 1, 1, false, true));

			// set slice selector to the correct number
			sliceSelector.setValue( displayImage.getSlice() );
			// add adjustment listener to the scroll bar
			sliceSelector.addAdjustmentListener(new AdjustmentListener() 
			{
				public void adjustmentValueChanged(final AdjustmentEvent e) {

					if(e.getSource() == sliceSelector)
					{
						displayImage.killRoi();
						drawExamples();
						updateExampleLists();
						updateallExampleLists();	
					
						if(showColorOverlay)
						{
							updateResultOverlay();
							displayImage.updateAndDraw();							
						}						
					}

				}
			});

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
		JList current=Util.model();
		current.setForeground(colors.get(i));
		exampleList.add(current);
		JList all=Util.model();
		all.setForeground(colors.get(i));
		allexampleList.add(all);	
		RoiListOverlay roiOverlay = new RoiListOverlay();
		roiOverlay.setComposite( transparency050 );
		((OverlayedImageCanvas)ic).addOverlay(roiOverlay);
		roiOverlayList.add(roiOverlay);
		JPanel buttonsPanel = new JPanel(new GridBagLayout());
		ActionEvent addbuttonAction= new ActionEvent(this, i,"AddButton");
		addButton( exampleManager.getClassLabels().get(i),null ,labelsJPanel,
				addbuttonAction,new Dimension(100, 21),Util.getGbc(0 ,originalJ , 1, false, false),null );

		ActionEvent uploadAction= new ActionEvent(this, i,"UploadButton");
		addButton( null,uploadIcon,buttonsPanel,uploadAction,
				new Dimension(20, 21),Util.getGbc(0, 0, 1, false, false),null);
		ActionEvent downloadAction= new ActionEvent(this, i,"DownloadButton");
		addButton( null,downloadIcon ,buttonsPanel,downloadAction,
				new Dimension(20, 21),Util.getGbc(1, 0, 1, false, false),null);
		labelsJPanel.add(buttonsPanel,Util.getGbc(1,originalJ , 1, false, false) );
		originalJ++;
		exampleList.get(i).addMouseListener(mouseListener);
		labelsJPanel.add( Util.addScrollPanel(exampleList.get(i),null), 
				Util.getGbc(0,originalJ, 1, false, false));
		labelsJPanel.add( Util.addScrollPanel( allexampleList.get(i),null ),
				Util.getGbc(1,originalJ, 1, false, false));
		originalJ++;
	}



	private void createPanel(){

		addButton( "CONFIGURE",null ,configureJPanel,
				CONFIGURE_BUTTON_PRESSED,dimension,Util.getGbc(0,0 , 1, false, false),null );

		controlsBox=new JPanel(new GridBagLayout());

		labelsJPanel.setBorder(BorderFactory.createTitledBorder("LABELS"));

		for(int i = 0; i < numberofClasses; i++){
			addSidePanel(i);
		}

		addButton( "COMPUTE",null ,resetJPanel,
				COMPUTE_BUTTON_PRESSED,dimension,Util.getGbc(0, 0, 1, false, false),null);
		addButton( "LOAD",null ,resetJPanel,
				LOAD_BUTTON_PRESSED,dimension,Util.getGbc(1, 0, 1, false, false),null );
		addButton( "SAVE",null ,resetJPanel,
				SAVE_BUTTON_PRESSED,dimension,Util.getGbc(2, 0, 1, false, false), null );

		controlsBox.add(configureJPanel, Util.getGbc(0, 0, 1, false, true));
		controlsBox.add(Util.addScrollPanel(labelsJPanel, 
				labelsJPanel.getPreferredSize()), Util.getGbc(0, 1, 1, false, true));
		controlsBox.add(resetJPanel, Util.getGbc(0, 2, 1, false, true));
		add(controlsBox, BorderLayout.EAST);
		configureFrame();

	}


	private void configureFrame(){

		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel all = new JPanel(new GridBagLayout());

		JPanel configureJPanel = new JPanel(new GridBagLayout());
		addButton( "ADDCLASS",null ,configureJPanel,
				ADD_BUTTON_PRESSED,dimension,Util.getGbc(0,0 , 1, false, false),null );

		classJPanel = new JPanel(new GridBagLayout());
		classJPanel.setBorder(BorderFactory.createTitledBorder("CLASSES"));	

		for(int i = 0; i < numberofClasses; i++){

			addclasses(i, originajFrameJ, originalFrameK);

		}

		JPanel resetJPanel = new JPanel(new GridBagLayout());
		addButton( "SAVE",null ,resetJPanel,
				SAVE_BUTTON_PRESSED,dimension,Util.getGbc(1, 0, 1, false, false),null );
		addButton( "DELTE",null ,resetJPanel,
				DELETE_BUTTON_PRESSED,dimension,Util.getGbc(2, 0, 1, false, false),null );

		all.add(configureJPanel,Util.getGbc(0, 0, 1, false, true));
		all.add(classJPanel,Util.getGbc(0, 1, 1, false, true));
		all.add(resetJPanel,Util.getGbc(0, 2, 1, false, true));


		frame.add(all);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);	

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
			final ActionEvent action, Dimension dimension,GridBagConstraints labelsConstraints,Color color ){
		final JButton button = new JButton();
		panel.add( button, labelsConstraints);
		button.setText( label );
		button.setIcon( icon );
		if(color!=null){
			button.setBackground(color);
		}
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


	public void validateFrame(){

		frame.invalidate();
		frame.revalidate();
		frame.repaint();
	}

	public void doAction( final ActionEvent event )	{
		if(event==COMPUTE_BUTTON_PRESSED){

			Instances instance=featureExtraction.createTrainingInstance();


		}
		if(event==SAVE_BUTTON_PRESSED){

			//saveInstanceFile();
			SaveDialog sd = new SaveDialog("Save Samples", "data", " ");
			exampleManager.saveExamples(sd.getDirectory(), sd.getFileName());

		}
		if(event==ADD_BUTTON_PRESSED ){
			addClass(event);
		}

		if(event==LOAD_BUTTON_PRESSED){

		}

		if(event.getActionCommand()== "ColorButton"){	

			int id=event.getID();
			Color c;
			c = JColorChooser.showDialog( new JFrame(),
					"CLASS COLOR", colors.get(id));
			colors.add(id, c);
			((Component)event.getSource()).setBackground(c);

		}	


		if(event==CONFIGURE_BUTTON_PRESSED){

			frame.setVisible(true);
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


	private void addClass(final ActionEvent  event) {
		exampleManager.addClass(numberofClasses);
		addclasses(numberofClasses, originajFrameJ, originalFrameK);
		addSidePanel(numberofClasses);
		numberofClasses++;
		validateFrame();

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
		updateallExampleLists();
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
		updateallExampleLists();

	}


	/**
	 * Draw the painted traces on the display image
	 */
	protected void drawExamples(){
		final int currentSlice = displayImage.getCurrentSlice();

		for(int i = 0; i < numberofClasses; i++){
			roiOverlayList.get(i).setColor(colors.get(i));
			final ArrayList< Roi > rois = new ArrayList<Roi>();
			for (Roi r : exampleManager.getExamples(i, currentSlice)){
				rois.add(r);
			}
			roiOverlayList.get(i).setRoi(rois);
		}

		displayImage.updateAndDraw();
	}



	/**
	 * Update the example lists in the GUI
	 */
	private void updateExampleLists()	{
		final int currentSlice = displayImage.getCurrentSlice();
		for(int i = 0; i < numberofClasses; i++){
			exampleList.get(i).removeAll();
			Vector listModel = new Vector();

			for(int j=0; j<exampleManager.getExamples(i, currentSlice).size(); j++){	

				listModel.addElement("trace " + i + " "+ j + " " + currentSlice);
			}
			exampleList.get(i).setListData(listModel);
			exampleList.get(i).setForeground(colors.get(i));
		}

	}


	private boolean saveInstanceFile(){
		String path;
		SaveDialog sd = new SaveDialog("Save Insances...", "data", ".arff");
		String name = sd.getFileName();
		if (name == null)
			return false;
		if (!(name.endsWith(".arff") || name.endsWith(".arff")))
			name = name + ".arff";
		String dir = sd.getDirectory();
		path = dir+name;
		return dataManager.writeDataToARFF(featureExtraction.createTrainingInstance(), path);
	}
	
	/**
	 * Update the example lists in the GUI
	 */
	private void updateallExampleLists(){
		for(int i = 0; i < numberofClasses; i++){
			allexampleList.get(i).removeAll();
			Vector listModel = new Vector();
			for(int currentSlice=1; currentSlice<=displayImage.getStackSize();currentSlice++){
				for(int j=0; j<exampleManager.getExamples(i, currentSlice).size(); j++){	

					listModel.addElement("trace " + i + " "+ j + " " + currentSlice);
				} 
			}

			allexampleList.get(i).setListData(listModel);
			allexampleList.get(i).setForeground(colors.get(i));
		}

	}


	private boolean saveRoi(int  i) {

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
		//get selected pixel
		OpenDialog od = new OpenDialog("Choose data file", OpenDialog.getLastDirectory(), "data.arff");
		if (od.getFileName()==null)
			return;
		List<Roi> rois=dataManager.openZip(od.getDirectory() + od.getFileName());
		exampleManager.addExampleList(i, rois, displayImage.getCurrentSlice());
		drawExamples();
		updateExampleLists();
		updateallExampleLists();
	}

	private void addclasses(int i , int j, int k){
		JCheckBox  checkBox = new JCheckBox("Class :"+ (i+1));
		jCheckBoxList.add(checkBox);
		JPanel classPanel= new JPanel();
		JTextArea textArea= new JTextArea();
		textArea.setText(exampleManager.getClassLabels().get(i) );
		textArea.setSize(dimension);
		classPanel.add(checkBox);
		classPanel.add(textArea);
		JButton button= new JButton();
		button.setBackground(colors.get(i));
		ActionEvent colorAction= new ActionEvent(button, i,"ColorButton");
		addAction(button, colorAction);
		classPanel.add(button);
		classJPanel.add(classPanel,Util.getGbc(k, j, 1, false, false));
		originalFrameK++;
		if(i>0 && originalFrameK==2){
			originalFrameK=0;
			originajFrameJ++;
		}

	}

	/**
	 * Update the result image overlay with the corresponding slice
	 */
	public void updateResultOverlay()
	{
		ImageProcessor overlay = classifiedImage.getImageStack().getProcessor(displayImage.getCurrentSlice()).duplicate();
		overlay = overlay.convertToByte(false);
		overlay.setColorModel(overlayLUT);
		resultOverlay.setImage(overlay);
	}

	public LUT setLut(List<Color> colors ){
		int i=0;
		for(Color color: colors){
			red[i] = (byte) color.getRed();
			green[i] = (byte) color.getGreen();
			blue[i] = (byte) color.getBlue();
			i++;
		}
		overlayLUT = new LUT(red, green, blue);
		return overlayLUT;
	}


	private void addAction(JButton button ,final  ActionEvent action){
		button.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				doAction(action);
			}
		} );

	}

}
