


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;

import java.awt.Panel;
import java.awt.BufferCapabilities.FlipContents;
import java.io.File;
import java.net.URL;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import activeSegmentation.IDataManager;
import activeSegmentation.IExampleManager;
import activeSegmentation.IFilterManager;
import activeSegmentation.feature.ExampleManagerImpl;
import activeSegmentation.filterImpl.FilterManager;
import activeSegmentation.gui.Gui;
import activeSegmentation.gui.TabbedFilterPanel;
import activeSegmentation.io.DataManagerImp;




public class Weka_Segmentation_ implements PlugIn {

	private ImagePlus trainingImage;
	JTextArea textArea= new JTextArea();
	/** main GUI panel (containing the buttons panel on the left,
	 *  the image in the center and the annotations panel on the right */
	Panel all = new Panel();
	public Weka_Segmentation_(){
		if (null == WindowManager.getCurrentImage())
		{
			//IJ.error( "", "Please open an image before running Active Segmentation." );
			trainingImage= IJ.openImage();
			
		}
		else{
			trainingImage = WindowManager.getCurrentImage();
		}
		
		
	}
	
	/**
	 * Main method to test and debug the Trainable Weka
	 * Segmentation GUI
	 *  
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			String home = "C://Program Files//ImageJ//plugins//activeSegmentation//";
			File f=new File(args[0]);

			if (f.exists() && f.isDirectory() ) {
				System.setProperty("plugins.dir", args[0]);
				new ImageJ();
				Weka_Segmentation_ test_Gui_ = new Weka_Segmentation_();
				IFilterManager filterManager=test_Gui_.runProcess(home);
				IDataManager dataManager= new DataManagerImp();
				IExampleManager exampleManager = new ExampleManagerImpl(test_Gui_.getTrainingImage().getImageStackSize(),2);
				Gui gui= new Gui(filterManager,exampleManager,dataManager,test_Gui_.getTrainingImage() );
				gui.showGridBagLayoutDemo();
				
				
				
			} else {
				throw new IllegalArgumentException();
			}
		}
		catch (Exception ex) {
			IJ.log("plugins.dir misspecified\n");
			ex.printStackTrace();
		}

	}

	@Override
	public void run(String arg0) {
		// TODO Auto-generated method stub
		String home = "C://Program Files//ImageJ//plugins//activeSegmentation//";
		try {
			IFilterManager filterManager=runProcess(home);
			
			IDataManager dataManager= new DataManagerImp();
			IExampleManager exampleManager = new ExampleManagerImpl(trainingImage.getImageStackSize(),2);
			Gui gui= new Gui(filterManager,exampleManager,dataManager,trainingImage );
			gui.showGridBagLayoutDemo();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public  IFilterManager runProcess(String home) throws Exception{
		
		try {
			
			/*-------------- LOADING FILTERS* ------------------*/
			System.out.println("-------------- LOADING FILTERS* ------------------");
			IFilterManager filterManager=new FilterManager();
			filterManager.loadFilters(home);
			
			
			/*-------------- GETTING AVAILABLE FILTER LIST* ------------------*/

			Set<String> filterList= filterManager.getFilters();
			System.out.println("-------------- AVAIL FILTERS* --------------------");
			System.out.println(filterList.size());
			System.out.println();
			
			
			IJ.log(String.valueOf(filterList.size()));
			return filterManager;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			IJ.log(e.getMessage());
		}
		return null;	

	}
	
	private  ImagePlus loadFromResource(final String path) {
		final URL url = Weka_Segmentation_.class.getResource(path);
		if (url == null) return null;
		if ("file".equals(url.getProtocol())) return new ImagePlus(url.getPath());
		return new ImagePlus(url.toString());
	}

	public ImagePlus getTrainingImage() {
		return trainingImage;
	}

	public void setTrainingImage(ImagePlus trainingImage) {
		this.trainingImage = trainingImage;
	}


}
