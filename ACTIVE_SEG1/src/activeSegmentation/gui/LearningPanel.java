package activeSegmentation.gui;

import ij.io.OpenDialog;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import activeSegmentation.IDataManager;
import activeSegmentation.learning.SMO;
import weka.classifiers.Classifier;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyPanel;

public class LearningPanel  implements Runnable {

	private IDataManager dataManager;

	public static final Font FONT = new Font( "Arial", Font.PLAIN, 10 );

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 2, "Compute" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent LOAD_BUTTON_PRESSED = new ActionEvent( this, 3, "Load" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 4, "Save" );

	public LearningPanel(IDataManager dataManager) {
		this.dataManager= dataManager;
	}

	public void doAction( final ActionEvent event )
	{
		if(event==COMPUTE_BUTTON_PRESSED){

		}
		if(event==SAVE_BUTTON_PRESSED){

			//get selected pixels




		}

		if(event==LOAD_BUTTON_PRESSED){


			OpenDialog od = new OpenDialog("Choose data file", OpenDialog.getLastDirectory(), "data.arff");
			if (od.getFileName()!=null){

				dataManager.loadTrainingData(od.getFileName());	
			}
		}



	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		final JFrame frame = new JFrame("LEARNING");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(null);



		// Add Weka panel for selecting the classifier and its options
		GenericObjectEditor m_ClassifierEditor = new GenericObjectEditor();
		PropertyPanel m_CEPanel = new PropertyPanel(m_ClassifierEditor);
		m_ClassifierEditor.setClassType(Classifier.class);
		m_ClassifierEditor.setValue(new SMO());

		m_CEPanel.setBounds( 100, 20, 300, 30 );
		// add classifier editor panel
		panel.add(m_CEPanel);


		Object c = (Object)m_ClassifierEditor.getValue();
		String originalOptions = "";
		String originalClassifierName = c.getClass().getName();
		if (c instanceof OptionHandler) 
		{
			originalOptions = Utils.joinOptions(((OptionHandler)c).getOptions());
		}
		CheckboxGroup checkboxGroup= new CheckboxGroup();
		Checkbox checkbox= new Checkbox("K Cross Validation", checkboxGroup, true);
		checkbox.setBounds( 100, 120, 200, 30 );
		Checkbox checkbox1= new Checkbox("Hold out Validation", checkboxGroup, true);
		checkbox1.setBounds( 300, 120, 200, 30 );
		panel.add(checkbox );
		Checkbox activeLearning= new Checkbox("Active Learning", checkboxGroup, true);
		activeLearning.setBounds( 100, 160, 200, 30 );
		panel.add(activeLearning );
		panel.add(checkbox1 );
		addButton( "COMPUTE",null , 60, 320, 100, 50,panel,COMPUTE_BUTTON_PRESSED );
		addButton( "LOAD",null , 170, 320, 100, 50,panel,LOAD_BUTTON_PRESSED );
		addButton( "SAVE",null , 290, 320, 100, 50,panel,SAVE_BUTTON_PRESSED );

		frame.add(panel);
		frame.setSize(520, 420);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);	
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
