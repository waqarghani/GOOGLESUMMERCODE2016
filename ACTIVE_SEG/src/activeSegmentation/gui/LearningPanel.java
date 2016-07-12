package activeSegmentation.gui;

import ij.io.OpenDialog;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.learning.SMO;
import weka.classifiers.Classifier;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyPanel;

public class LearningPanel  implements Runnable {

	private IDataManager dataManager;
	private JList classifierList;


	public static final Font FONT = new Font( "Arial", Font.PLAIN, 10 );

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 2, "Compute" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent LOAD_BUTTON_PRESSED = new ActionEvent( this, 3, "Load" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 4, "Save" );
	Dimension dimension=new Dimension(100, 25);

	public LearningPanel(IDataManager dataManager) {
		this.dataManager= dataManager;
		this.classifierList= Util.model();
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

		JPanel all = new JPanel(new GridBagLayout());


		JPanel learningJPanel = new JPanel(new GridBagLayout());
		learningJPanel.setBorder(BorderFactory.createTitledBorder("Learning"));	

		// Add Weka panel for selecting the classifier and its options
		GenericObjectEditor m_ClassifierEditor = new GenericObjectEditor();
		PropertyPanel m_CEPanel = new PropertyPanel(m_ClassifierEditor);
		m_ClassifierEditor.setClassType(Classifier.class);
		m_ClassifierEditor.setValue(new SMO());

	

		Object c = (Object)m_ClassifierEditor.getValue();
		String originalOptions = "";
		String originalClassifierName = c.getClass().getName();
		if (c instanceof OptionHandler) 
		{
			originalOptions = Utils.joinOptions(((OptionHandler)c).getOptions());
		}

		// add classifier editor panel
		learningJPanel.add(m_CEPanel,Util.getGbc(0,0, 1, false, false));

		JPanel options = new JPanel(new GridBagLayout());
		options.setBorder(BorderFactory.createTitledBorder("Options"));
		CheckboxGroup checkboxGroup= new CheckboxGroup();
		Checkbox checkbox= new Checkbox("K Cross Validation", checkboxGroup, true);
		Checkbox checkbox1= new Checkbox("Hold out Validation", checkboxGroup, true);
		Checkbox activeLearning= new Checkbox("Active Learning", checkboxGroup, true);
		options.add(checkbox,Util.getGbc(0,0, 1, false, false ));
		options.add(checkbox1,Util.getGbc(1,0, 1, false, false ) );
		options.add(activeLearning,Util.getGbc(0,1, 1, false, false ));


		JPanel resetJPanel = new JPanel(new GridBagLayout());
		addButton( "COMPUTE",null ,resetJPanel,
				COMPUTE_BUTTON_PRESSED,dimension,Util.getGbc(0, 0, 1, false, false),null);
		addButton( "LOAD",null ,resetJPanel,
				LOAD_BUTTON_PRESSED,dimension,Util.getGbc(1, 0, 1, false, false),null );
		addButton( "SAVE",null ,resetJPanel,
				SAVE_BUTTON_PRESSED,dimension,Util.getGbc(2, 0, 1, false, false), null );

		JPanel classfier = new JPanel(new GridBagLayout());
		classfier.setBorder(BorderFactory.createTitledBorder("Classifier"));
		classfier.add(Util.addScrollPanel(classifierList,null),
				Util.getGbc(0, 0, 1, false, true)) ;
		all.add(learningJPanel,Util.getGbc(0, 0, 1, false, true));
		all.add(classfier,Util.getGbc(1, 0, 1, false, true));
		all.add(resetJPanel,Util.getGbc(0, 1, 1, false, true));
		all.add(options,Util.getGbc(1, 1, 1, false, true));


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


}
