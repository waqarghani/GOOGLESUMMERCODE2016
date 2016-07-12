package activeSegmentation.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import activeSegmentation.Common;
import activeSegmentation.IExampleManager;

public class ConfigureExample   {


	private int numberofClasses;
	private IExampleManager exampleManager;
	private JFrame frame = new JFrame("CONFIGURE");
	private List<JCheckBox> jCheckBoxList= new ArrayList<JCheckBox>();
	JPanel classJPanel;
	int originajJ=0, originalK=0;
	private static ConfigureExample instance;
	private boolean configureFlag=false;

	
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 1, "Save" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent DELETE_BUTTON_PRESSED = new ActionEvent( this, 2, "Delete" );
	final ActionEvent ADD_BUTTON_PRESSED = new ActionEvent( this, 25, "ADDCLASS" );
	private List<Color> colors ;
	
	public static  ConfigureExample getConfigureInstance( IExampleManager exampleManager){
		if(instance==null){
			instance= new ConfigureExample(exampleManager);
		}
		
		return instance;
	}

	private ConfigureExample( IExampleManager exampleManager){

		this.exampleManager= exampleManager;
		this.numberofClasses= 2;
		setDefaultColors();
	}
	
	public void show()
	{
		if(configureFlag==false){
			run();
			configureFlag=true;
		}
		else{
			frame.setVisible(true);
		}
		
	}

	private void run(){
		
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel all = new JPanel(new GridBagLayout());

		JPanel configureJPanel = new JPanel(new GridBagLayout());
		JLabel maxClassesLabel= new JLabel("MAX CLASSES :");
		
		configureJPanel.add(maxClassesLabel,Util.getGbc(0, 0, 1, false, false));

		addButton( "ADDCLASS",null ,configureJPanel,
				ADD_BUTTON_PRESSED,new Dimension(100, 25),Util.getGbc(2,0 , 1, false, false),null );

		classJPanel = new JPanel(new GridBagLayout());
		classJPanel.setBorder(BorderFactory.createTitledBorder("CLASSES"));	
	
		for(int i = 0; i < numberofClasses; i++){
			
			addclasses(i, originajJ, originalK);
			
		}

		JPanel resetJPanel = new JPanel(new GridBagLayout());
		addButton( "SAVE",null ,resetJPanel,
				SAVE_BUTTON_PRESSED,new Dimension(100, 25),Util.getGbc(1, 0, 1, false, false),null );
		addButton( "DELTE",null ,resetJPanel,
				DELETE_BUTTON_PRESSED,new Dimension(100, 25),Util.getGbc(2, 0, 1, false, false),null );

		all.add(configureJPanel,Util.getGbc(0, 0, 1, false, true));
		all.add(classJPanel,Util.getGbc(0, 1, 1, false, true));
		all.add(resetJPanel,Util.getGbc(0, 2, 1, false, true));


		frame.add(all);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);	

	}


	private void addclasses(int i , int j, int k){
		JCheckBox  checkBox = new JCheckBox();
		jCheckBoxList.add(checkBox);
		JPanel classPanel= new JPanel();
		JLabel classLabel= new JLabel("Class :"+ (i+1));
		JTextArea textArea= new JTextArea();
		textArea.setText(exampleManager.getClassLabels().get(i) );
		textArea.setSize(new Dimension(100, 25));
		classPanel.add(checkBox);
		classPanel.add(classLabel);
		classPanel.add(textArea);
		JButton button= new JButton();

		button.setBackground(colors.get(i));
		ActionEvent colorAction= new ActionEvent(button, i,"ColorButton");
		addAction(button, colorAction);
		classPanel.add(button);
		classJPanel.add(classPanel,Util.getGbc(k, j, 1, false, false));
		originalK++;
		if(i>0 && originalK==2){
			originalK=0;
			originajJ++;
		}

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
	
	public void validate(){
		//frame.removeAll();//or remove(JComponent)
		frame.invalidate();
		frame.revalidate();
		frame.repaint();
	}
	
	public void doAction( final ActionEvent event )	{
		if(event==DELETE_BUTTON_PRESSED){


		}
		if(event==SAVE_BUTTON_PRESSED){


		}
		if(event==ADD_BUTTON_PRESSED){
	           
		  addClass(event);
		  
		}
		if(event.getActionCommand()== "ColorButton"){	

			int id=event.getID();
			System.out.println(id);
			Color c;
			c = JColorChooser.showDialog( new JFrame(),
					"CLASS COLOR", colors.get(id));
			colors.add(id, c);
			((Component)event.getSource()).setBackground(c);
			
		}		
	}

	private void addClass(final ActionEvent  event) {
		System.out.println("ADD CLASS"+ event);
	    addclasses(numberofClasses, originajJ, originalK);
	    numberofClasses++;
	    validate();
		
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

	private void setDefaultColors(){
		colors= new ArrayList<Color>();
		colors.add(Color.blue);
		colors.add(Color.green);
		colors.add(Color.red);
		colors.add(Color.cyan);
		colors.add(Color.magenta);
	}

	public List<Color> getColors() {
		return colors;
	}

	public void setColors(List<Color> colors) {
		this.colors = colors;
	}

	public int getNumberofClasses() {
		return numberofClasses;
	}

	public void setNumberofClasses(int numberofClasses) {
		this.numberofClasses = numberofClasses;
	}

	
	

}
