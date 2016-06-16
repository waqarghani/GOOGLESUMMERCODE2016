package activeSegmentation.gui;

import ij.ImagePlus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import activeSegmentation.IExampleManager;


public class ExamplePanel implements Runnable {

	private IExampleManager exampleManager;
	private ImagePlus displayImage;
	
	private int numberofClasses;
	private java.awt.List exampleList[];
	/** available colors for available classes */
	private Color[] colors = new Color[]{Color.blue, Color.green, Color.red,
			Color.cyan, Color.magenta};

	private static final Icon buttonIcon = new ImageIcon(ExamplePanel.class.getResource("/activeSegmentation/images/upload.png"));
	//System.out.println("In Buttion ICon" +buttonIcon.getDescription());
	private static final Icon downloadButtonIcon = new ImageIcon(ExamplePanel.class.getResource("/activeSegmentation/images/download.png"));



	public static final Font FONT = new Font( "Arial", Font.PLAIN, 10 );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 2, "Compute" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent LOAD_BUTTON_PRESSED = new ActionEvent( this, 3, "Load" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 4, "Save" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent DEFAULT_BUTTON_PRESSED = new ActionEvent( this, 4, "Default" );


	public ExamplePanel(IExampleManager exampleManager, ImagePlus displayImage) {
		this.exampleManager = exampleManager;
		this.displayImage= displayImage;
		this.numberofClasses= exampleManager.getNumOfClasses();
		
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
	

	public void addExamples(){

		final JFrame frame = new JFrame("FEATURE");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		int start=100, width =100, gap=50;
		for(int i = 0; i < exampleManager.getNumOfClasses(); i++)
		{
			addButton( exampleManager.getClassLabels()[i],null , start, 100, width, 50,panel,COMPUTE_BUTTON_PRESSED );
			start= start+width+gap;
		}
		
		
		
		JComboBox c = new JComboBox();
		for(int i = 1; i <= displayImage.getImageStackSize(); i++)
		{
			c.addItem("slice"+i);
		}
		
		c.setBounds( 200, 20, 100, 30 );
		panel.add(c);
		
		
		
		
		
		addButton( "COMPUTE",null , 20, 320, 100, 50,panel,COMPUTE_BUTTON_PRESSED );
		addButton( "LOAD",null , 130, 320, 100, 50,panel,LOAD_BUTTON_PRESSED );
		addButton( "DEFAULT",null , 240, 320, 100, 50,panel,DEFAULT_BUTTON_PRESSED );
		addButton( "SAVE",null , 350, 320, 100, 50,panel,SAVE_BUTTON_PRESSED );


		
		frame.add(panel);
		frame.setSize(520, 420);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);	

	}


	public void doAction( final ActionEvent event )
	{
		
	}

	private JButton addButton( final String label, final Icon icon, final int x,
			final int y, final int width, final int height,JComponent panel, final ActionEvent action)
	{
		final JButton button = new JButton();
		panel.add( button );
		button.setText( label );
		button.setIcon( icon );
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFont( FONT );
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



	@Override
	public void run() {
		// TODO Auto-generated method stub
		addExamples();
	}


}
