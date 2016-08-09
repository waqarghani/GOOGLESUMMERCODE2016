package activeSegmentation.gui;



import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.ImagePlus;








import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import activeSegmentation.IDataManager;
import activeSegmentation.IEvaluation;
import activeSegmentation.IFeatureManager;
import activeSegmentation.IFilterManager;
import activeSegmentation.feature.FeatureExtraction;

public class Gui {
	private JFrame mainFrame;
	private JPanel controlPanel;
	private IFilterManager filterManager;
	private IEvaluation evaluation;
	private ImagePlus trainingImage;
	private GuiController controller;

	/** This {@link ActionEvent} is fired when the 'next' button is pressed. */
	final ActionEvent FEATURE_BUTTON_PRESSED = new ActionEvent( this, 0, "Feature" );

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent FILTER_BUTTON_PRESSED = new ActionEvent( this, 1, "Filter" );
	/** This {@link ActionEvent} is fired when the 'next' button is pressed. */
	final ActionEvent LEARNING_BUTTON_PRESSED = new ActionEvent( this, 2, "Learning" );

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent EVALUATION_BUTTON_PRESSED = new ActionEvent( this, 3, "Evaluation" );


	final static String LOOKANDFEEL = "Metal";
	final static String THEME = "Test";
	public static final Font FONT = new Font( "Arial", Font.BOLD, 13 );
	public Gui(IFilterManager filterManager,GuiController controller, ImagePlus trainingImage){

		this.filterManager= filterManager;
		this.trainingImage= trainingImage;
		this.controller= controller;
		prepareGUI();
		
	}


	private static void initLookAndFeel() {
		String lookAndFeel = null;

		if (LOOKANDFEEL != null) {
			if (LOOKANDFEEL.equals("Metal")) {
				lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();

			}

			else if (LOOKANDFEEL.equals("System")) {
				lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			} 

			else if (LOOKANDFEEL.equals("Motif")) {
				lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			} 

			else if (LOOKANDFEEL.equals("GTK")) { 
				lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
			} 

			else {
				System.err.println("Unexpected value of LOOKANDFEEL specified: "
						+ LOOKANDFEEL);
				lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
			}

			try {

				UIManager.setLookAndFeel(lookAndFeel);

				if (LOOKANDFEEL.equals("Metal")) {
					if (THEME.equals("DefaultMetal"))
						MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
					else 
						MetalLookAndFeel.setCurrentTheme(new OceanTheme());

					UIManager.setLookAndFeel(new MetalLookAndFeel()); 
				}   

			} 

			catch (ClassNotFoundException e) {
				System.err.println("Couldn't find class for specified look and feel:"
						+ lookAndFeel);
				System.err.println("Did you include the L&F library in the class path?");
				System.err.println("Using the default look and feel.");
			} 

			catch (UnsupportedLookAndFeelException e) {
				System.err.println("Can't use the specified look and feel ("
						+ lookAndFeel
						+ ") on this platform.");
				System.err.println("Using the default look and feel.");
			} 

			catch (Exception e) {
				System.err.println("Couldn't get specified look and feel ("
						+ lookAndFeel
						+ "), for some reason.");
				System.err.println("Using the default look and feel.");
				e.printStackTrace();
			}
		}
	}


	public void doAction( final ActionEvent event )
	{
		System.out.println("IN DO ACTION");
		System.out.println(event.toString());
		if(event ==FILTER_BUTTON_PRESSED ){
			FilterPanel filterPanel=new FilterPanel(filterManager,trainingImage);
			SwingUtilities.invokeLater(filterPanel);

		}
		if(event==FEATURE_BUTTON_PRESSED){
			new FeaturePanel(trainingImage.duplicate(),controller);
		}

		if(event==LEARNING_BUTTON_PRESSED){
			LearningPanel learningPanel = new LearningPanel();
			SwingUtilities.invokeLater(learningPanel);
		}
		
		if(event==EVALUATION_BUTTON_PRESSED){
			
		//	EvaluationPanel evaluationPanel = new EvaluationPanel(dataManager, evaluation);
		//	SwingUtilities.invokeLater(evaluationPanel);
		}

	}



	private void prepareGUI(){
		initLookAndFeel();
		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		mainFrame = new JFrame("ACTIVE SEGMENTATION");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(500,300);



		trainingImage.show();

		controlPanel = new JPanel();
		controlPanel.setLayout(null);
		addButton( "FILTERS", null, 25, 50, 200, 50, controlPanel,FILTER_BUTTON_PRESSED  );
		addButton( "FEATURE EXTRACTION", null, 275, 50, 200, 50, controlPanel,FEATURE_BUTTON_PRESSED);
		addButton( "LEARNING", null, 25, 150, 200, 50, controlPanel, LEARNING_BUTTON_PRESSED );
		addButton( "EVALUATION", null, 275, 150, 200, 50, controlPanel, EVALUATION_BUTTON_PRESSED );

		// postioning

		controlPanel.setLocation(0, 0);
		mainFrame.add(controlPanel);
		mainFrame.setVisible(true);  

	}


	private JButton addButton( final String label, final Icon icon, final int x,
			final int y, final int width, final int height,JPanel panel,final ActionEvent action)
	{
		final JButton button = new JButton();
		panel.add( button );
		button.setText( label );
		button.setIcon( icon );
		button.setFont( FONT );
		button.setBounds( x, y, width, height );
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


	public void showGridBagLayoutDemo(){


		mainFrame.setVisible(true);  
	}
}