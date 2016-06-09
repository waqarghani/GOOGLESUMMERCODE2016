package activeSegmentation.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

public class Gui {
	private JFrame mainFrame;
	private JPanel controlPanel;
	JButton jButtonFeature;
	JButton jButtonLearning;
	JButton jButtonEvaluation;
	final static String LOOKANDFEEL = "Metal";
	final static String THEME = "Test";
	public static final Font FONT = new Font( "Arial", Font.BOLD, 13 );
	public Gui(){
		prepareGUI();
	}

	public static void main(String[] args){
		Gui swingLayoutDemo = new Gui();  
		swingLayoutDemo.showGridBagLayoutDemo();       
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
	private void prepareGUI(){
		initLookAndFeel();
		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		mainFrame = new JFrame("ACTIVE SEGMENTATION");
		mainFrame.setSize(500,300);

		controlPanel = new JPanel();
		controlPanel.setLayout(null);
		jButtonFeature = addButton( "FEATURE EXTRACTION", null, 25, 50, 200, 50, controlPanel );
		jButtonLearning = addButton( "LEARNING", null, 275, 50, 200, 50, controlPanel );
		jButtonEvaluation=addButton( "EVALUATION", null, 25, 150, 200, 50, controlPanel );
		jButtonEvaluation=addButton( "SETTINGS", null, 275, 150, 200, 50, controlPanel );

		// postioning

		controlPanel.setLocation(0, 0);
		mainFrame.add(controlPanel);
		mainFrame.setVisible(true);  
	}


	private JButton addButton( final String label, final Icon icon, final int x,
			final int y, final int width, final int height,JPanel panel)
	{
		final JButton button = new JButton();
		panel.add( button );
		button.setText( label );
		button.setIcon( icon );
		button.setFont( FONT );
		button.setBounds( x, y, width, height );

		return button;
	}


	public void showGridBagLayoutDemo(){


		mainFrame.setVisible(true);  
	}
}