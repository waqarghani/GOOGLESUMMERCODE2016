package activeSegmentation.gui;


import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.SaveDialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;












import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

















import activeSegmentation.IFilter;
import activeSegmentation.IFilterManager;
import activeSegmentation.filterImpl.FilterManager;



public class TabbedFilterPanel implements Runnable {


	private IFilterManager filterManager;
	private JTabbedPane pane;

	private ImagePlus trainingImage;

	private static final Icon PREV_ICON = new ImageIcon( TabbedFilterPanel.class.getResource( "../images/left.png" ) );

	private static final Icon NEXT_ICON = new ImageIcon( TabbedFilterPanel.class.getResource( "../images/right.png" ) );

	private static final Icon TAB_ICON = new ImageIcon( TabbedFilterPanel.class.getResource( "../images/tabicon.png" ) );

	public static final Font FONT = new Font( "Arial", Font.PLAIN, 10 );

	/** This {@link ActionEvent} is fired when the 'next' button is pressed. */
	final ActionEvent NEXT_BUTTON_PRESSED = new ActionEvent( this, 0, "Next" );

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent PREVIOUS_BUTTON_PRESSED = new ActionEvent( this, 1, "Previous" );

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 2, "Compute" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent LOAD_BUTTON_PRESSED = new ActionEvent( this, 3, "Load" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 4, "Save" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent DEFAULT_BUTTON_PRESSED = new ActionEvent( this, 5, "Default" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent VIEW_BUTTON_PRESSED = new ActionEvent( this, 6, "View" );


	public TabbedFilterPanel(FilterManager filterManager, ImagePlus trainingImage) {

		this.filterManager = filterManager;
		this.trainingImage= trainingImage;
	}




	public void run() {
		final JFrame frame = new JFrame("FILTER");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		pane = new JTabbedPane();
		pane.setFont(FONT);
		pane.setBackground(Color.WHITE);


		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setFont(FONT);

		Set<String> filters= filterManager.getFilters();  
		System.out.println(filters.size());
		int filterSize=1;
		for(String filter: filters){
			pane.addTab(filter,null,createTab(filterManager.getFilterSetting(filter),
					filterManager.getFilter(filter).getImage(), filterSize, filters.size()),filter);

			filterSize++;

		}


		pane.setSize(600, 300);
		addButton( "COMPUTE",null , 20, 320, 100, 50,panel,COMPUTE_BUTTON_PRESSED );
		addButton( "LOAD",null , 130, 320, 100, 50,panel,LOAD_BUTTON_PRESSED );
		addButton( "DEFAULT",null , 240, 320, 100, 50,panel,DEFAULT_BUTTON_PRESSED );
		addButton( "SAVE",null , 350, 320, 100, 50,panel,SAVE_BUTTON_PRESSED );
		addButton( "VIEW",null , 460, 320, 100, 50,panel,VIEW_BUTTON_PRESSED );


		frame.add(pane);
		frame.add(panel);
		frame.setSize(600, 420);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}


	private JPanel createTab( Map<String , String> settingsMap, Image image, int size, int maxFilters) {
		JPanel p = new JPanel();
		p.setLayout(null);
		int x=30, y=10, w=140, h=25;
		if(size!=1)
			addButton( "Previous", null, 10, 90, 28, 38,p,PREVIOUS_BUTTON_PRESSED );
		if(size != maxFilters)
			addButton( "Next", null, 480, 90, 28, 38,p ,NEXT_BUTTON_PRESSED );
		//Icon icon = new ImageIcon( TabbedFilterPanel.class.getResource( "../images/LOG.gif" ) );
		Icon icon = new ImageIcon( image );
		JLabel imagelabel= new JLabel(icon);
		imagelabel.setBounds(x+20, y-10, w+70, h+200);
		p.add(imagelabel);
		for (String key: settingsMap.keySet()){



			JLabel label= new JLabel(key);
			label.setFont(FONT);
			label.setBounds( x+230, y, w, h );
			JTextArea textArea= new JTextArea();
			p.add(label);
			textArea.setText( settingsMap.get(key));
			textArea.setFont(FONT);
			textArea.setBounds( x+300, y, w, h );
			p.add(textArea);   
			y=y+50;
		}

		return p;
	}

	public void doAction( final ActionEvent event )
	{
		System.out.println("IN DO ACTION");
		System.out.println(event.toString());
		if(event == PREVIOUS_BUTTON_PRESSED ){

			System.out.println("BUTTON PRESSED");
			pane.setSelectedIndex(pane.getSelectedIndex()-1);
		}
		if(event==NEXT_BUTTON_PRESSED){

			pane.setSelectedIndex(pane.getSelectedIndex()+1);
		}

		if(event==COMPUTE_BUTTON_PRESSED){

			if(trainingImage != null){
				filterManager.applyFilters(trainingImage);
			}


		}
		if(event==SAVE_BUTTON_PRESSED){

			SaveDialog sd = new SaveDialog("Save Feature...", "FilterImage", ".tiff");
			String name = sd.getFileName();
			ImageStack imageStack= filterManager.getFeatureStack();
			if (name == null & imageStack!=null){
			
			IJ.saveAsTiff( new ImagePlus("FILTERED IMAGE", imageStack),name);
			}
			
		}

		if(event==LOAD_BUTTON_PRESSED){
			ImagePlus loadedImage= IJ.openImage();
			filterManager.setImageStack(loadedImage.getImageStack());

		}
		if(event==DEFAULT_BUTTON_PRESSED){

         filterManager.setDefault();

		}

		if(event==VIEW_BUTTON_PRESSED){
			ImageStack imageStack= filterManager.getFeatureStack();
			new ImagePlus("FILTERED IMAGE", imageStack).show();

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



}