


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;

import java.awt.Panel;
import java.io.File;
import java.net.URL;
import java.util.Set;

import javax.swing.JTextArea;

import activeSegmentation.IDataManager;
import activeSegmentation.IEvaluation;
import activeSegmentation.IFeatureManager;
import activeSegmentation.IFilterManager;
import activeSegmentation.ILearningManager;
import activeSegmentation.evaluation.EvaluationMetrics;
import activeSegmentation.feature.FeatureExtraction;
import activeSegmentation.feature.FeatureManager;
import activeSegmentation.filterImpl.FilterManager;
import activeSegmentation.gui.Gui;
import activeSegmentation.gui.GuiController;
import activeSegmentation.gui.LearningPanel;
import activeSegmentation.io.DataManagerImp;
import activeSegmentation.io.MetaInfo;
import activeSegmentation.learning.ClassifierManager;




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

			String home = "C:\\Program Files\\ImageJ\\plugins\\activeSegmentation\\";
			File f=new File(args[0]);

			if (f.exists() && f.isDirectory() ) {
				System.setProperty("plugins.dir", args[0]);
				new ImageJ();
				Weka_Segmentation_ test_Gui_ = new Weka_Segmentation_();
				IDataManager dataManager= new DataManagerImp();
				dataManager.setPath(home);
				MetaInfo metaInfo= dataManager.getMetaInfo();
				IFilterManager filterManager=new FilterManager(dataManager,metaInfo, home);
				
				IEvaluation evaluation= new EvaluationMetrics();
				IFeatureManager featureManager = new FeatureManager(
						test_Gui_.getTrainingImage().getImageStackSize(),2,dataManager,metaInfo);
				ILearningManager  learningManager= new ClassifierManager(dataManager);
				featureManager.addFeatures(new FeatureExtraction(filterManager,test_Gui_.getTrainingImage().duplicate()));
				
				GuiController guiController= new GuiController(filterManager, featureManager,
						learningManager,test_Gui_.getTrainingImage());
				Gui gui= new Gui(filterManager,guiController,test_Gui_.getTrainingImage() );
				gui.showGridBagLayoutDemo();
				
			//	filterMetadata.saveFilters();
				
							
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
		String home = "C:\\Program Files\\ImageJ\\plugins\\activeSegmentation\\";
		String path = "C://Users//HP//Desktop//DataImages//aav_samples//json//";
		try {

			IDataManager dataManager= new DataManagerImp();
			dataManager.setPath(home);
			MetaInfo metaInfo= dataManager.getMetaInfo();
			IFilterManager filterManager=new FilterManager(dataManager,metaInfo, home);
			IEvaluation evaluation= new EvaluationMetrics();
			IFeatureManager featureManager = new FeatureManager(
					trainingImage.getImageStack().getSize(),2,dataManager,metaInfo);
			featureManager.addFeatures(new FeatureExtraction(filterManager,trainingImage.duplicate()));
			
			ILearningManager  learningManager= new ClassifierManager(dataManager);
			GuiController guiController= new GuiController(filterManager, featureManager, learningManager,trainingImage);
			Gui gui= new Gui(filterManager,guiController,trainingImage);
			gui.showGridBagLayoutDemo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
