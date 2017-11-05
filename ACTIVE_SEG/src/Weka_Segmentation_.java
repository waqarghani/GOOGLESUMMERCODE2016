import java.awt.Panel;

import activeSegmentation.IDataManager;
import activeSegmentation.IFeatureManager;
import activeSegmentation.IFilterManager;
import activeSegmentation.ILearningManager;
import activeSegmentation.feature.PixelLevel_FeatureExtraction;
import activeSegmentation.feature.FeatureManager;
import activeSegmentation.feature.ClassLevel_FeatureExtraction;
import activeSegmentation.filterImpl.FilterManager;
import activeSegmentation.gui.Gui;
import activeSegmentation.gui.GuiController;
import activeSegmentation.io.DataManagerImp;
import activeSegmentation.learning.ClassifierManager;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;

public class Weka_Segmentation_ implements PlugIn {

	private ImagePlus trainingImage;
	

	/** main GUI panel (containing the buttons panel on the left,
	 *  the image in the center and the annotations panel on the right */
	Panel all = new Panel();
	public Weka_Segmentation_(){
		
	}
	
	public void getImage(){
		if (null == WindowManager.getCurrentImage())
			trainingImage= IJ.openImage();
		else
			trainingImage = WindowManager.getCurrentImage();
	}
	
	/**
	 * This method will be an entry point into the Plugin. All the
	 * dependency are inject through this class. This method is written according to 
	 * ImageJ plugin loading requirements
	 * @param parameter for imageJ
	 *
	 */
	@Override
	public void run(String arg0) {
		IJ.log(System.getProperty("plugins.dir"));
		String home = System.getProperty("plugins.dir")+"\\plugins\\activeSegmentation\\";
		
		System.out.println(home);
		//String home = System.getProperty("plugins.dir");

		try {
				IDataManager dataManager= new DataManagerImp();
				if(dataManager.getOriginalImage()!=null)
					trainingImage= dataManager.getOriginalImage();
				else{
					getImage();
					dataManager.setOriginalImage(trainingImage);
				}
				IFilterManager filterManager=new FilterManager(dataManager, home);
				//IEvaluation evaluation= new EvaluationMetrics();
				IFeatureManager featureManager = new FeatureManager(
						trainingImage.getImageStack().getSize(),2,dataManager);
				featureManager.addFeatures(new PixelLevel_FeatureExtraction(filterManager,dataManager.getOriginalImage()));
				featureManager.addFeatures(new ClassLevel_FeatureExtraction(filterManager,dataManager.getOriginalImage()));
				
				ILearningManager  learningManager= new ClassifierManager(dataManager);
				GuiController guiController= new GuiController(filterManager, featureManager, learningManager,dataManager);
				Gui gui= new Gui(guiController);
				gui.showGridBagLayoutDemo();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public ImagePlus getTrainingImage() {
		return trainingImage;
	}

	public void setTrainingImage(ImagePlus trainingImage) {
		this.trainingImage = trainingImage;
	}
	
	public static void main(String[] args) {
	       // new ij.ImageJ();
		   
	        new Weka_Segmentation_().run("");
	}

}