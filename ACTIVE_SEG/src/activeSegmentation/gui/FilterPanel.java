package activeSegmentation.gui;


import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.OpenDialog;
import ij.io.SaveDialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;












import java.util.Set;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import activeSegmentation.Common;
import activeSegmentation.IFilterManager;
import activeSegmentation.filterImpl.FilterManager;



public class FilterPanel implements Runnable {


	private IFilterManager filterManager;
	private JTabbedPane pane;
	private JList filterList;

	private Map<String,List<JTextField>> textMap;
	private ImagePlus trainingImage;



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


	public FilterPanel(IFilterManager filterManager, ImagePlus trainingImage) {

		this.filterManager = filterManager;
		this.trainingImage= trainingImage;
		this.filterList =Util.model();
		this.filterList.setForeground(Color.GREEN);
		textMap= new HashMap<String, List<JTextField>>();
	}


	@Override
	public void run() {
		final JFrame frame = new JFrame("FILTER");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pane = new JTabbedPane();
		pane.setFont(Common.FONT);
		pane.setBackground(Color.WHITE);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setFont(Common.FONT);

		Set<String> filters= filterManager.getFilters();  
		System.out.println(filters.size());
		int filterSize=1;
		for(String filter: filters){
			pane.addTab(filter,null,createTab(filterManager.getFilterSetting(filter),
					filterManager.getFilterImage(filter), 
					filterSize, filters.size(),filter, filterManager.isFilterEnabled(filter)));
			filterSize++;

		}


		pane.setSize(600, 300);
		JScrollPane scrollPane = Util.addScrollPanel(filterList,null);
		scrollPane.setBounds(605,20,100,280);
		panel.add(scrollPane);
		updateFiterList();
		addButton( new JButton(),"COMPUTE",null , 20, 320, 100, 50,panel,COMPUTE_BUTTON_PRESSED,null );
		addButton(new JButton(), "LOAD",null , 130, 320, 100, 50,panel,LOAD_BUTTON_PRESSED,null );
		addButton(new JButton(), "DEFAULT",null , 240, 320, 100, 50,panel,DEFAULT_BUTTON_PRESSED,null );
		addButton(new JButton(), "SAVE",null , 350, 320, 100, 50,panel,SAVE_BUTTON_PRESSED,null );
		addButton(new JButton(), "VIEW",null , 460, 320, 100, 50,panel,VIEW_BUTTON_PRESSED,null );


		frame.add(pane);
		frame.add(panel);
		frame.setSize(730, 420);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}


	private JPanel createTab( Map<String , String> settingsMap, Image image, 
			int size, int maxFilters,String filter, boolean enabled) {
		JPanel p = new JPanel();
		p.setLayout(null);
		int  y=10;
		if(size!=1)
			addButton( new JButton(), "Previous", null, 10, 90, 70, 38,p,PREVIOUS_BUTTON_PRESSED , null);
		if(size != maxFilters)
			addButton( new JButton(), "Next", null, 480, 90, 70, 38,p ,NEXT_BUTTON_PRESSED , null);
		Icon icon = new ImageIcon( image );
		JLabel imagelabel= new JLabel(icon);
		imagelabel.setBounds(100, 3,210,225);
		p.add(imagelabel);

		List<JTextField> jtextList= new ArrayList<JTextField>();
		for (String key: settingsMap.keySet()){

			JLabel label= new JLabel(key);
			label.setFont(Common.FONT);
			label.setBounds( 330, y, 70, 25 );
			p.add(label);

			JTextField textArea= new JTextField(settingsMap.get(key));
			textArea.setFont(Common.FONT);
			textArea.setBounds(400, y, 70, 25 );
			p.add(textArea);   
			jtextList.add(textArea);
			y=y+50;
		}

		textMap.put(filter, jtextList);
		JButton button= new JButton();
		ActionEvent event = new ActionEvent( button,1 , filter);
		if(enabled)		
			addButton( button,Common.ENABLED, null, 480,220 , 50, 20,p ,event, Color.GREEN);
		else
			addButton( button,Common.DISABLED, null, 480,220 , 50, 20,p ,event, Color.RED );

		return p;
	}

	public void doAction( final ActionEvent event )
	{
		System.out.println("IN DO ACTION");
		System.out.println(event.toString());

		Set<String> filters= filterManager.getFilters();  
		for(String filter : filters){
			if(event.getActionCommand()== filter){

				filterManager.enableFilter(filter);
				Color	color=((Component)event.getSource()).getBackground();
				if(color == Color.RED){
					((JButton)event.getSource()).setBackground(Color.GREEN);
					((JButton)event.getSource()).setText(Common.ENABLED);
				}
				else{
					((JButton)event.getSource()).setBackground(Color.RED);
					((JButton)event.getSource()).setText(Common.DISABLED);
				}

				updateFiterList();
			}
		}
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

			System.out.println("");
			String key= pane.getTitleAt( pane.getSelectedIndex());
			int i=0;
			Map<String,String> settingsMap= new HashMap<String, String>();
			for (String settingsKey: filterManager.getFilterSetting(key).keySet()){
				settingsMap.put(settingsKey, textMap.get(key).get(i).getText());	
				i++;
			}
			filterManager.updateFilterSetting(key, settingsMap);		
			filterManager.saveFiltersMetaData();

		}

		if(event==LOAD_BUTTON_PRESSED){
			OpenDialog od = new OpenDialog("Choose filter File", OpenDialog.getLastDirectory(), "filter.txt");
			if (od.getFileName()==null)
				return;
			for(String filter: filterManager.getFilters()){
				updateTabbedGui(filter);
			}
			

		}
		if(event==DEFAULT_BUTTON_PRESSED){

			String key= pane.getTitleAt( pane.getSelectedIndex());
			filterManager.setDefault(key);
			updateTabbedGui(key);
			

		}

		if(event==VIEW_BUTTON_PRESSED){
	       filterManager.getFinalImage().show();

		}

	}


	private void updateTabbedGui(String key){
		int i=0;
		Map<String,String> settingsMap=filterManager.getFilterSetting(key);
		for (String settingsKey: settingsMap.keySet() ){
			
			 textMap.get(key).get(i).setText(settingsMap.get(settingsKey));
			 i++;
		}

	}
	private void updateFiterList() {
		// TODO Auto-generated method stub
		Set<String> filters= filterManager.getFilters();  
		Vector listModel = new Vector();
		for(String filter : filters){
			if(filterManager.isFilterEnabled(filter)){

				listModel.addElement(filter);
			}
		}
		filterList.setListData(listModel);
		filterList.setForeground(Color.GREEN);

	}


	private JButton addButton(final JButton button ,final String label, final Icon icon, final int x,
			final int y, final int width, final int height,
			JComponent panel, final ActionEvent action,final Color color )
	{
		panel.add( button );
		button.setText( label );
		button.setIcon( icon );
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFont( Common.FONT );
		if(color!=null){
			button.setBackground(color);
		}
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