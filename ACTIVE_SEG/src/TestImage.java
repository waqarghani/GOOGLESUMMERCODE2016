import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;





import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class TestImage extends Canvas {

	public static JFrame frame;
	public static int WIDTH = 200;
	public static int HEIGHT = 200;
	public static void main(String[] a){

		TestImage t=new TestImage();
        frame = new JFrame("WINDOW");
        frame.add(t);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Image image=t.createImage();
        frame.getContentPane().add(new JButton(new ImageIcon(image)));
}

	private Double gaussian(double x){
		
		 return Math.exp(-Math.pow(x, 2)/2) / (2  *Math.sqrt(3.14));
	}
	   
	public Image createImage(){
		
		final XYSeries series = new XYSeries("Data");
		for(double i=-10;i<=10;i=i+0.5){
	    	  Double y=gaussian(i);
	    	  series.add(i, y);
	    	 
		}
		 final XYSeriesCollection data = new XYSeriesCollection(series);
	     final JFreeChart chart = ChartFactory.createXYLineChart(
	            "LOG",
	            "X", 
	            "Y", 
	            data,
	            PlotOrientation.VERTICAL,
	            false,
	            false,
	            false
	        );

	        
	        return chart.createBufferedImage(200, 200);
	}
	

	public Image filterImage(){
		
		
		BufferedImage bufferedImage = new BufferedImage(200,200,BufferedImage.TYPE_INT_RGB);
	      Graphics g = bufferedImage.getGraphics();
	      g.setColor(Color.GREEN);
	      for(int i=-10;i<=10;i++){
	    	  Double y=gaussian(i);
	    	  System.out.println(y);
	    	  g.drawLine(i,y.intValue() , i, y.intValue());
	    	 
	      }
	    
	      return bufferedImage;
	}
}
