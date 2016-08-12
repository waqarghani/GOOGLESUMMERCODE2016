package activeSegmentation.gui;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




import activeSegmentation.IDataManager;
import activeSegmentation.IFeatureManager;
import activeSegmentation.IFilterManager;
import activeSegmentation.ILearningManager;

public class GuiController {

	private IFilterManager filterManager;
	private IFeatureManager featureManager;
	private IDataManager dataManager;
	private  ILearningManager learningManager;
	private int numberofClasses=2;
	private ImagePlus originalImage;

	public GuiController(IFilterManager filterManager, 
			IFeatureManager featureManager, ILearningManager learningManager,IDataManager dataManager){
		this.filterManager=filterManager;
		this.featureManager= featureManager;
		this.learningManager= learningManager;
		this.dataManager= dataManager;
		this.originalImage= dataManager.getOriginalImage();

	}


	public List<ArrayList<Roi>> getRois(int currentSlice){
		List<ArrayList< Roi >> roiList= new ArrayList<ArrayList<Roi>>();

		for(int i = 0; i < numberofClasses; i++){
			ArrayList< Roi > rois = new ArrayList<Roi>();
			for (Roi r : featureManager.getExamples(i, currentSlice)){
				rois.add(r);
			}
			roiList.add(rois);
		}
		return roiList;
	}

	public void addClass(){
		featureManager.addClass(numberofClasses);
		numberofClasses++;
	}

	public String getclassLabel(int index){
		return featureManager.getClassLabel(index);
	}

	public void deleteExample(int classId,int currentSlice, int index ){
		featureManager.deleteExample(classId, currentSlice, index);
	}

	/**
	 * Add examples defined by the user to the corresponding list
	 * in the GUI and the example list in the segmentation object.
	 * 
	 * @param i GUI list index
	 */
	public void uploadExamples(int i, int currentSlice){
		//get selected pixel
		OpenDialog od = new OpenDialog("Choose data file", OpenDialog.getLastDirectory(), "data.arff");
		if (od.getFileName()==null)
			return;
		List<Roi> rois=dataManager.openZip(od.getDirectory() + od.getFileName());
		featureManager.addExampleList(i, rois,currentSlice );

	}

	public Roi getRoi(int classId, int slice, int index){
		return featureManager.getExamples(classId, slice)
				.get(index);
	}

	public void  setMetadata(boolean filterFlag,boolean featureFlag, boolean learningFlag) {
		System.out.println("filterFlag"+filterFlag);
		System.out.println("featureFlag"+featureFlag);
		System.out.println("learingFlag"+learningFlag);
		if(filterFlag)
		filterManager.setFiltersMetaData();
		if(featureFlag)
		featureManager.setFeatureMetadata();
		if(learningFlag)
		learningManager.loadLearningMetaData();
	}
	public void  saveMetadata() {
		filterManager.saveFiltersMetaData();
		featureManager.saveFeatureMetadata();
		learningManager.saveLearningMetaData();
	}
	public boolean saveRoi(int  i, int n) {

		String path;
		SaveDialog sd = new SaveDialog("Save ROIs...", "RoiSet", ".zip");
		String name = sd.getFileName();
		if (name == null)
			return false;
		if (!(name.endsWith(".zip") || name.endsWith(".ZIP")))
			name = name + ".zip";
		String dir = sd.getDirectory();
		path = dir+name;

		return dataManager.saveExamples(path, featureManager.getExamples(i, n));

	}
	public int getNumberofClasses() {
		return numberofClasses;
	}
	public void setNumberofClasses(int numberofClasses) {
		this.numberofClasses = numberofClasses;
	}

	public int getSize(int i, int currentSlice ){
		return featureManager.getSize(i, currentSlice);
	}

	public void addExamples(int id, Roi r, int currentSlice) {
		// TODO Auto-generated method stub
		featureManager.addExample(id, r, currentSlice);	
	}


	public ImagePlus computeFeatures(String featureType) {
		ImagePlus classifiedImage= null;
		featureManager.extractFeatures(featureType);
		learningManager.trainClassifier();
		List<double[]> classificationResult=learningManager.applyClassifier(featureManager.extractAll(featureType));
		ImageStack classStack = new ImageStack(originalImage.getWidth(), originalImage.getHeight());
		int i=1;
		for (double[] result: classificationResult)
		{
			ImageProcessor classifiedSliceProcessor = new FloatProcessor(originalImage.getWidth(),
					originalImage.getHeight(), result);				
			
			classStack.addSlice(originalImage.getStack().getSliceLabel(i), classifiedSliceProcessor);
			i++;
		}
		classifiedImage= new ImagePlus("Classified Image", classStack);
		classifiedImage.setCalibration(originalImage.getCalibration());
		classifiedImage.show();
		return classifiedImage;

	}


	public ImagePlus getOriginalImage() {
		return originalImage.duplicate();
	}


	public void setOriginalImage(ImagePlus originalImage) {
		this.originalImage = originalImage;
	}


	public IFilterManager getFilterManager() {
		return filterManager;
	}


	public void setFilterManager(IFilterManager filterManager) {
		this.filterManager = filterManager;
	}


}
