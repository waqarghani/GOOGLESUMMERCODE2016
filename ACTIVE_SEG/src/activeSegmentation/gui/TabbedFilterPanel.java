package activeSegmentation.gui;


import java.awt.Font;
import java.awt.Insets;
import java.util.Map;







import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;


import activeSegmentation.filterImpl.FilterManager;



public class TabbedFilterPanel implements Runnable {

	JButton jButtonPrev;

	JButton jButtonNext;
	private FilterManager filterManager;

	private static final Icon PREV_ICON = new ImageIcon( TabbedFilterPanel.class.getResource( "../images/left.png" ) );

	private static final Icon NEXT_ICON = new ImageIcon( TabbedFilterPanel.class.getResource( "../images/right.png" ) );

	private static final Icon TAB_ICON = new ImageIcon( TabbedFilterPanel.class.getResource( "../images/tabicon.png" ) );

	public static final Font FONT = new Font( "Arial", Font.PLAIN, 10 );
	public TabbedFilterPanel(FilterManager filterManager) {
		super();
		this.filterManager = filterManager;
	}

	public void run() {
		final JFrame frame = new JFrame("FILTER");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTabbedPane pane = new JTabbedPane();

		for(String filter: filterManager.getFilters()){
			pane.addTab(filter,null,createTab(filterManager.getFilterSetting(filter)),filter);
			pane.setFont(FONT);

		}
		

		frame.add(pane);
		frame.setSize(550, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private JPanel createTab( Map<String , String> settingsMap) {
		JPanel p = new JPanel();
		p.setLayout(null);
		int x=70, y=10, w=140, h=25;
		addButton( "Previous", PREV_ICON, 10, 90, 28, 38,p  );
		addButton( "Next", NEXT_ICON, 480, 90, 28, 38,p  );
		Icon icon = new ImageIcon( TabbedFilterPanel.class.getResource( "../images/LOG.gif" ) );

		JLabel imagelabel= new JLabel(icon);
		imagelabel.setBounds(x, y, w, h+200);
		p.add(imagelabel);
		for (String key: settingsMap.keySet()){

			

			JLabel label= new JLabel(key);
			label.setFont(FONT);
			label.setBounds( x+170, y, w, h );
			JTextArea textArea= new JTextArea();
			p.add(label);
			textArea.setText( settingsMap.get(key));
			textArea.setFont(FONT);
			textArea.setBounds( x+250, y, w, h );
			p.add(textArea);   
			y=y+50;
		}

		return p;
	}



	private JButton addButton( final String label, final Icon icon, final int x,
			final int y, final int width, final int height,JComponent panel)
	{
		final JButton button = new JButton();
		panel.add( button );
		button.setText( label );
		button.setIcon( icon );
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFont( FONT );
		button.setBounds( x, y, width, height );

		return button;
	}



}