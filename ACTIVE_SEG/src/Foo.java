
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Foo extends JFrame {

    public Foo() {

        setTitle( "Tabs and Cards" );
        setSize( 400, 400 );
        setDefaultCloseOperation( EXIT_ON_CLOSE );

        JTabbedPane tabbedPane = new JTabbedPane();

        // needs to be final to be accessed inside the event handlers
        final JPanel tab1 = new JPanel();
        final JPanel tab2 = new JPanel();
        tab2.setLayout( new CardLayout() );

        tabbedPane.addTab( "Tab 1", tab1 );
        tabbedPane.addTab( "Tab 2", tab2 );


        JPanel tab21 = new JPanel();
        tab21.add( new JLabel( "2.1" ) );

        JPanel tab22 = new JPanel();
        tab22.add( new JLabel( "2.2" ) );

        JPanel tab23 = new JPanel();
        tab23.add( new JLabel( "2.3" ) );

        tab2.add( tab21 );
        tab2.add( tab22 );
        tab2.add( tab23 );


        JButton btnToTab22 = new JButton( "Next!" );
        btnToTab22.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed( ActionEvent evt ) {
                // gets the layout, casts it and call next to go to the next card
                ( ( CardLayout ) tab2.getLayout() ).next( tab2 );
            }
        });
        tab21.add( btnToTab22 );

        JButton btnToTab23 = new JButton( "Next!" );
        btnToTab23.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed( ActionEvent evt ) {
                ( ( CardLayout ) tab2.getLayout() ).next( tab2 );
            }
        });
        tab22.add( btnToTab23 );

        add( tabbedPane, BorderLayout.CENTER );

        setVisible( true );

    }

    public static void main( String[] args ) {
        new Foo();
    }

}