package activeSegmentation.gui;

import ij.io.OpenDialog;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.learning.SMO;
import weka.classifiers.Classifier;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyPanel;

public class EvaluationPanel  implements Runnable {

	private IDataManager dataManager;

	public static final Font FONT = new Font( "Arial", Font.PLAIN, 10 );

	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent COMPUTE_BUTTON_PRESSED = new ActionEvent( this, 2, "Compute" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent LOAD_BUTTON_PRESSED = new ActionEvent( this, 3, "Load" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	final ActionEvent SAVE_BUTTON_PRESSED = new ActionEvent( this, 4, "Save" );

	public EvaluationPanel(IDataManager dataManager) {
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
		final JFrame frame = new JFrame("EVALUATION");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel controlsBox=new JPanel(new GridBagLayout());
		JPanel controlJPanel=new JPanel(new GridBagLayout());
		controlJPanel.setBorder(BorderFactory.createTitledBorder("CONTROL"));
		JPanel curvesJPanel=new JPanel(new GridBagLayout());
		curvesJPanel.setBorder(BorderFactory.createTitledBorder("PLOTS"));
		JPanel resultJPanel=new JPanel(new GridBagLayout());
		resultJPanel.setBorder(BorderFactory.createTitledBorder("RESULTS"));
		
		JPanel resetJPanel = new JPanel(new GridBagLayout());
		
		
		addButton( "COMPUTE",null ,resetJPanel,
				COMPUTE_BUTTON_PRESSED,new Dimension(100, 25),Util.getGbc(0, 0, 1, false, false));
		addButton( "LOAD",null ,resetJPanel,
				LOAD_BUTTON_PRESSED,new Dimension(100, 25),Util.getGbc(1, 0, 1, false, false) );
		addButton( "SAVE",null ,resetJPanel,
				SAVE_BUTTON_PRESSED,new Dimension(100, 25),Util.getGbc(2, 0, 1, false, false) );

		
		controlsBox.add(resultJPanel, Util.getGbc(0, 0, 1, false, true));
		controlsBox.add(controlJPanel, Util.getGbc(1, 0, 1, false, true));
		controlsBox.add(curvesJPanel, Util.getGbc(0, 1, 1, false, true));
		controlsBox.add(resetJPanel, Util.getGbc(0, 2, 1, false, true));
		
		
		frame.add(controlsBox);
		frame.setSize(520, 420);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);	
	}

	private JButton addButton( final String label, final Icon icon,JComponent panel, 
			final ActionEvent action, Dimension dimension,GridBagConstraints labelsConstraints ){
		final JButton button = new JButton();
		panel.add( button, labelsConstraints);
		button.setText( label );
		button.setIcon( icon );
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
