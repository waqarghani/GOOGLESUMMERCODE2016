package activeSegmentation.gui;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.StackWindow;
import ij.process.ImageProcessor;
import ij.process.LUT;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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






import activeSegmentation.Common;





/**
 * 
 *  
 * @author
 *
 */
public class FeaturePanel extends StackWindow 
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
	private ImagePlus classifiedImage;
	Panel all;
	/** 50% alpha composite */
	final Composite transparency050 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f );

	// Create overlay LUT
	byte[] red = new byte[ 256 ];
	byte[] green = new byte[ 256 ];
	byte[] blue = new byte[ 256 ];

	GuiController controller;
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

	private static final Icon uploadIcon = new ImageIcon(FeaturePanel.class.getResource("/activeSegmentation/images/upload.png"));
	private static final Icon downloadIcon = new ImageIcon(FeaturePanel.class.getResource("/activeSegmentation/images/download.png"));

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 21, "TRAIN" );


	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 23, "Save" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent CONFIGURE_BUTTON_PRESSED = new ActionEvent( this, 24, "CONFIGURE" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent TOGGLE_BUTTON_PRESSED = new ActionEvent( this, 26, "TRAIN" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_CONFIGURE_BUTTON_PRESSED = new ActionEvent( this, 27, "SaveConfigure" );
	final ActionEvent ADD_BUTTON_PRESSED = new ActionEvent( this, 25, "ADDCLASS" );
	final ActionEvent DELETE_BUTTON_PRESSED = new ActionEvent( this, 2, "Delete" );
	/** opacity (in %) of the result overlay image */
	int overlayOpacity = 33;
	/** alpha composite for the result overlay image */
	Composite overlayAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayOpacity / 100f);
	Dimension dimension=new Dimension(100, 25);
	ImageOverlay resultOverlay;


	public FeaturePanel(GuiController controller, ImagePlus image)
	{
		super(image, new CustomCanvas(image));	
		final CustomCanvas canvas = (CustomCanvas) getCanvas();
		this.displayImage= imp;
		this.setTitle("Active Segmentation");
		this.exampleList = new ArrayList<JList>();
		this.allexampleList = new ArrayList<JList>();
		this.controller= controller;	
		colors=Util.setDefaultColors();
		roiOverlayList = new ArrayList<RoiListOverlay>();
		setOverlay();
		// Remove the canvas from the window, to add it later
		removeAll();
		setLut(colors);
		imagePanel = new JPanel(new GridBagLayout());	
		imagePanel.add(ic, Util.getGbc(0, 0, 1, false, false));
		imagePanel.add(zSelector,Util.getGbc(0, 0, 0, false, false));
		imagePanel.add(sliceSelector,Util.getGbc(0, 1, 1, false, true));

		if(null != sliceSelector){
			sliceSelector.setEnabled(true);
			// set slice selector to the correct number
			sliceSelector.setValue( displayImage.getSlice() );
			// add adjustment listener to the scroll bar
			sliceSelector.addAdjustmentListener(new AdjustmentListener() 
			{
				public void adjustmentValueChanged(final AdjustmentEvent e) {
              
					if(e.getSource() == sliceSelector)
					{
						displayImage.killRoi();
						updateGui();
						if(showColorOverlay)
						{
							updateResultOverlay();		
						}						
					}
				}
			});


			KeyListener keyListener = new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {}

				@Override
				public void keyReleased(final KeyEvent e) {

					if(e.getKeyCode() == KeyEvent.VK_LEFT ||
							e.getKeyCode() == KeyEvent.VK_RIGHT ||
							e.getKeyCode() == KeyEvent.VK_LESS ||
							e.getKeyCode() == KeyEvent.VK_GREATER ||
							e.getKeyCode() == KeyEvent.VK_COMMA ||
							e.getKeyCode() == KeyEvent.VK_PERIOD)
					{
						//IJ.log("moving scroll");
						displayImage.killRoi();
						updateGui();
						if(showColorOverlay)
						{
							updateResultOverlay();
						}
					}
				}

				@Override
				public void keyPressed(KeyEvent e) {}
			};
			// add key listener to the window and the canvas
			addKeyListener(keyListener);
			canvas.addKeyListener(keyListener);

		}

		createPanel();
		updateGui();
		Panel all = new Panel();
		BoxLayout box = new BoxLayout(all, BoxLayout.X_AXIS);
		all.setLayout(box);
		all.add(imagePanel);
		all.add(controlsBox);
		add(all);  	      	      	   
		this.pack();	 	    
		this.setVisible(true); 

	}

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
		addButton( controller.getclassLabel(i),null ,labelsJPanel,
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
		allexampleList.get(i).addMouseListener(mouseListener);
		labelsJPanel.add( Util.addScrollPanel(exampleList.get(i),null), 
				Util.getGbc(0,originalJ, 1, false, false));
		labelsJPanel.add( Util.addScrollPanel( allexampleList.get(i),null ),
				Util.getGbc(1,originalJ, 1, false, false));
		originalJ++;
	}


	private void updateGui(){
		drawExamples();
		updateExampleLists();
		updateallExampleLists();	

	}

	private void createPanel(){

		addButton( "CONFIGURE",null ,configureJPanel,
				CONFIGURE_BUTTON_PRESSED,dimension,Util.getGbc(0,0 , 1, false, false),null );

		controlsBox=new JPanel(new GridBagLayout());

		labelsJPanel.setBorder(BorderFactory.createTitledBorder("LABELS"));

		for(int i = 0; i < controller.getNumberofClasses(); i++){
			addSidePanel(i);
		}

		addButton( "COMPUTE",null ,resetJPanel,
				COMPUTE_BUTTON_PRESSED,dimension,Util.getGbc(0, 0, 1, false, false),null);
		addButton( "TOGGLE",null ,resetJPanel,
				TOGGLE_BUTTON_PRESSED,dimension,Util.getGbc(1, 0, 1, false, false),null );
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

		for(int i = 0; i < controller.getNumberofClasses(); i++){

			addclasses(i, originajFrameJ, originalFrameK);

		}

		JPanel resetJPanel = new JPanel(new GridBagLayout());
		addButton( "SAVE",null ,resetJPanel,
				SAVE_CONFIGURE_BUTTON_PRESSED,dimension,Util.getGbc(1, 0, 1, false, false),null );
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
		int currentSlice= displayImage.getCurrentSlice();
		if(event==COMPUTE_BUTTON_PRESSED){
			classifiedImage=controller.computeFeatures("pixelLevel");
			toggleOverlay();
		}
		if(event==SAVE_BUTTON_PRESSED){
			controller.saveMetadata();

		}
		if(event==SAVE_CONFIGURE_BUTTON_PRESSED){
			updateGui();
		}
		if(event==ADD_BUTTON_PRESSED ){
			addClass(event);
		}

		if(event==TOGGLE_BUTTON_PRESSED){

			toggleOverlay();
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
			final Roi r = displayImage.getRoi();
			if (null == r)
				return;
			displayImage.killRoi();
			controller.addExamples(event.getID(),r, currentSlice);			
			updateGui();

		}

		if(event.getActionCommand()== "UploadButton"){	

			controller.uploadExamples(event.getID(),currentSlice);
			updateGui();
		}

		if(event.getActionCommand()== "DownloadButton"){	

			controller.saveRoi(event.getID(), currentSlice);
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
		final Roi newRoi = controller.getRoi(classId, displayImage.getCurrentSlice(), index);			
		// Set selected trace as current ROI
		newRoi.setImage(displayImage);
		displayImage.setRoi(newRoi);
		displayImage.updateAndDraw();
	}  


	private void addClass(final ActionEvent  event) {
		controller.addClass();
		addclasses(controller.getNumberofClasses()-1, originajFrameJ, originalFrameK);
		addSidePanel(controller.getNumberofClasses()-1);
		validateFrame();
	}


	/**
	 * Draw the painted traces on the display image
	 */
	protected void drawExamples(){
		final int currentSlice = displayImage.getCurrentSlice();

		for(int i = 0; i < controller.getRois(currentSlice).size(); i++){
			roiOverlayList.get(i).setColor(colors.get(i));
			roiOverlayList.get(i).setRoi(controller.getRois(currentSlice).get(i));
		}

		displayImage.updateAndDraw();
	}



	/**
	 * Update the example lists in the GUI
	 */
	private void updateExampleLists()	{
		final int currentSlice = displayImage.getCurrentSlice();
		for(int i = 0; i < controller.getNumberofClasses(); i++){
			exampleList.get(i).removeAll();
			Vector listModel = new Vector();
			for(int j=0; j<controller.getSize(i, currentSlice); j++){	
				listModel.addElement(controller.getclassLabel(i)+ " " + i + " "+ j + " " + currentSlice);
			}
			exampleList.get(i).setListData(listModel);
			exampleList.get(i).setForeground(colors.get(i));
		}

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
					System.out.println("ITEM : "+ item);
					String[] arr= item.split(" ");
					int classId = exampleList.get(Integer.parseInt(arr[1])).getSelectedIndex();
					controller.deleteExample(classId, displayImage.getCurrentSlice(), index);
					updateGui();
				}
			}
		}
	};


	/**
	 * Update the example lists in the GUI
	 */
	private void updateallExampleLists(){
		for(int i = 0; i < controller.getNumberofClasses(); i++){
			allexampleList.get(i).removeAll();
			Vector listModel = new Vector();
			for(int currentSlice=1; currentSlice<=displayImage.getStackSize();currentSlice++){
				for(int j=0; j<controller.getSize(i, currentSlice); j++){	
					listModel.addElement(controller.getclassLabel(i)+ " " + i + " "+ j + " " + currentSlice);
				} 
			}
			allexampleList.get(i).setListData(listModel);
			allexampleList.get(i).setForeground(colors.get(i));
		}
	}



	private void addclasses(int i , int j, int k){
		JCheckBox  checkBox = new JCheckBox("Class :"+ (i+1));
		jCheckBoxList.add(checkBox);
		JPanel classPanel= new JPanel();
		JTextArea textArea= new JTextArea();
		textArea.setText(controller.getclassLabel(i) );
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
		displayImage.updateAndDraw();
	}

	/**
	 * Toggle between overlay and original image with markings
	 */
	void toggleOverlay()
	{
		showColorOverlay = !showColorOverlay;
		if (showColorOverlay && null != classifiedImage)
		{
			updateResultOverlay();
		}
		else{
			resultOverlay.setImage(null);
			displayImage.updateAndDraw();
		}
	}

	public void setLut(List<Color> colors ){
		int i=0;
		for(Color color: colors){
			red[i] = (byte) color.getRed();
			green[i] = (byte) color.getGreen();
			blue[i] = (byte) color.getBlue();
			i++;
		}
		overlayLUT = new LUT(red, green, blue);
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
