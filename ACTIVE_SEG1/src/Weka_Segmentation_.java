


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;



import activeSegmentation.IDataManager;
import activeSegmentation.IEvaluation;
import activeSegmentation.IFeatureManager;
import activeSegmentation.IFilterManager;
import activeSegmentation.ILearningManager;
import activeSegmentation.evaluation.EvaluationMetrics;
import activeSegmentation.feature.FeatureExtraction;
import activeSegmentation.feature.FeatureManager;
import activeSegmentation.filterImpl.FilterManager;
import activeSegmentation.gui.GenericDialogPlus;
import activeSegmentation.gui.Gui;
import activeSegmentation.gui.GuiController;
import activeSegmentation.io.DataManagerImp;
import activeSegmentation.learning.ClassifierManager;




public class Weka_Segmentation_ implements PlugIn {

	private ImagePlus trainingImage;
	private static String path;
	private static String metaFileName;
	private static boolean[] defaultValue = new boolean[3];
	/** main GUI panel (containing the buttons panel on the left,
	 *  the image in the center and the annotations panel on the right */
	Panel all = new Panel();
	public Weka_Segmentation_(){
		
	}

	public void getImage(){
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
				if(showSettingsDialog()){

					IDataManager dataManager= new DataManagerImp();
					dataManager.setPath(path);
					dataManager.getMetaInfo();
					Weka_Segmentation_ test_Gui_ = new Weka_Segmentation_();
					if(dataManager.getOriginalImage()!=null){
						test_Gui_.setTrainingImage(dataManager.getOriginalImage());
					}
					else{
					test_Gui_.getImage();
					dataManager.setOriginalImage(test_Gui_.getTrainingImage());
					}
					IFilterManager filterManager=new FilterManager(dataManager, home);

					IEvaluation evaluation= new EvaluationMetrics();
					IFeatureManager featureManager = new FeatureManager(
							test_Gui_.getTrainingImage().getImageStackSize(),2,dataManager);
					ILearningManager  learningManager= new ClassifierManager(dataManager);
					featureManager.addFeatures(new FeatureExtraction(filterManager,dataManager.getOriginalImage()));

					GuiController guiController= new GuiController(filterManager, featureManager,
							learningManager,dataManager);
					guiController.setMetadata(defaultValue[0], defaultValue[1], defaultValue[2]);
					Gui gui= new Gui(guiController);
					gui.showGridBagLayoutDemo();
				}
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
		//String path = "C://Users//HP//Desktop//DataImages//aav_samples//json//";
		try {

			IDataManager dataManager= new DataManagerImp();
			dataManager.setPath(path);
			dataManager.getMetaInfo();
			if(dataManager.getOriginalImage()!=null){
				trainingImage= dataManager.getOriginalImage();
			}
			else
			getImage();
			IFilterManager filterManager=new FilterManager(dataManager, home);
			IEvaluation evaluation= new EvaluationMetrics();
			IFeatureManager featureManager = new FeatureManager(
					trainingImage.getImageStack().getSize(),2,dataManager);
			featureManager.addFeatures(new FeatureExtraction(filterManager,trainingImage.duplicate()));

			ILearningManager  learningManager= new ClassifierManager(dataManager);
			GuiController guiController= new GuiController(filterManager, featureManager, learningManager,dataManager);
			guiController.setMetadata(defaultValue[0], defaultValue[1], defaultValue[2]);
			Gui gui= new Gui(guiController);
			gui.showGridBagLayoutDemo();
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
	public static boolean showSettingsDialog()
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

		return true;

	}

	private static void loadSessionFile(){
		//get selected pixel
		OpenDialog od = new OpenDialog("Choose Session file", OpenDialog.getLastDirectory(), "data.json");
		if (od.getFileName()==null)
			return ;

		path=od.getDirectory();
		metaFileName= od.getFileName();

	}
	public ImagePlus getTrainingImage() {
		return trainingImage;
	}

	public void setTrainingImage(ImagePlus trainingImage) {
		this.trainingImage = trainingImage;
	}


}
