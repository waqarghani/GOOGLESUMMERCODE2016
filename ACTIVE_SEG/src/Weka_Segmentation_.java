import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import activeSegmentation.IDataManager;
import activeSegmentation.IFeatureManager;
import activeSegmentation.IFilterManager;
import activeSegmentation.ILearningManager;
import activeSegmentation.feature.PixelLevel_FeatureExtraction;
import activeSegmentation.feature.FeatureManager;
import activeSegmentation.feature.ClassLevel_FeatureExtraction;
import activeSegmentation.filterImpl.FilterManager;
import activeSegmentation.gui.GenericDialogPlus;
import activeSegmentation.gui.Gui;
import activeSegmentation.gui.GuiController;
import activeSegmentation.io.DataManagerImp;
import activeSegmentation.learning.ClassifierManager;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

public class Weka_Segmentation_ implements PlugIn {

	private ImagePlus trainingImage;
	private static String path;
	//private static String metaFileName;
	private static boolean[] defaultValue = new boolean[3];

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
		String home = System.getProperty("plugins.dir");
		try {
			if(showSettingsDialog()){
				IDataManager dataManager= new DataManagerImp();
				dataManager.setPath(path);
				dataManager.getMetaInfo();
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
				guiController.setMetadata(defaultValue[0], defaultValue[1], defaultValue[2]);
				Gui gui= new Gui(guiController);
				gui.showGridBagLayoutDemo();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	/**
	 * Show advanced settings dialog
	 *
	 * @return false when canceled
	 */
	private static boolean showSettingsDialog()
	{
		List<String> settings= new ArrayList<String>();
		settings.add("FILTER");
		settings.add("FEATURES");
		settings.add("LEARNING");

		GenericDialogPlus gd = new GenericDialogPlus("Session settings");
		gd.addButton("Choose Session File", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loadSessionFile();
			}
		});
		final int rows = (int)Math.round(settings.size()/2.0);
		gd.addCheckboxGroup(rows, 2,settings.toArray(new String[0]),defaultValue);
		gd.showDialog();

		if (gd.wasCanceled())
			return false;
		for(int i = 0; i < settings.size(); i++)
		{
			defaultValue[i] = gd.getNextBoolean();
		}	

		if(path==null){
			loadSessionFile();
		}
		return true;

	}

	private static void loadSessionFile(){
		//get loaded file
		OpenDialog od = new OpenDialog("Choose Session file", OpenDialog.getLastDirectory(), "data.json");
		if (od.getFileName()==null)
			return ;

		path=od.getDirectory();

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