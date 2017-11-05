package activeSegmentation.gui;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
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

		for(int i = 0; i < featureManager.getNumOfClasses(); i++){
			ArrayList< Roi > rois = new ArrayList<Roi>();
			for (Roi r : featureManager.getExamples(i, currentSlice)){
				rois.add(r);
			}
			roiList.add(rois);
		}
		return roiList;
	}

	public void addClass(){
		featureManager.addClass();
	}

	public String getclassLabel(int index){
		return featureManager.getClassLabel(index);
	}

	public int getClassIdofCurrentSlicetraining(int currentSlice){
		return featureManager.getClassIdofCurrentSlicetraining(currentSlice);
	}
	
	public int getClassIdofCurrentSlicetesting(int currentSlice){
		return featureManager.getClassIdofCurrentSlicetesting(currentSlice);
	}
	
	public void deleteExample(int classId,int currentSlice, int index ){
		featureManager.deleteExample(classId, currentSlice, index);
	}
	
	public void deleteImageType(int classId, int sliceNum){
		featureManager.deleteImageType(classId, sliceNum);
	}
	
	public int getClassId(String classNum){
		
		return featureManager.getclassKey(classNum);
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

	public void  setMetadata(boolean filterFlag,boolean featureFlag, boolean learningFlag, String path) {
		System.out.println("filterFlag"+filterFlag);
		System.out.println("featureFlag"+featureFlag);
		System.out.println("learingFlag"+learningFlag);
		dataManager.setPath(path);
		dataManager.getMetaInfo();
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
		return featureManager.getNumOfClasses();
	}
	
	public int getSize(int i, int currentSlice ){
		return featureManager.getSize(i, currentSlice);
	}

	public ArrayList<Integer> getDataImageTypeId(int ClassNum ){
		return featureManager.getDataImageTypeId(ClassNum);
	}
	
	public ArrayList<Integer> getDataImageTestTypeId(int ClassNum){
		return featureManager.getDataImageTestTypeId(ClassNum);
	}
	
	public void addExamples(int id, Roi r, int currentSlice) {
		// TODO Auto-generated method stub
		featureManager.addExample(id, r, currentSlice);	
	}

	public void addImageType(int id, int SliceNo) {
		// TODO Auto-generated method stub
		featureManager.addImageType(id, SliceNo);	
	}
	
	public void addTestImageType(int id, int SliceNo){
		featureManager.addTestImageType(id, SliceNo);
	}
	
	public ImagePlus pixellevelTraining(ImagePlus classifiedImage, String featureType){
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
	
	public HashMap<Integer,Integer> classlevelTraining(String featureType){
		learningManager.trainClassifier();
		List<double[]> classificationResult=learningManager.applyClassifier(featureManager.extractAll(featureType));
		int t=0;
		HashMap<Integer,Integer> indextolabel = new HashMap<Integer, Integer>();
		ArrayList<Integer> testindex = featureManager.getImageTestType();
		for(double[] arr : classificationResult){
			for(int i=0;i<arr.length;i++){
				indextolabel.put(testindex.get(t),((int)arr[i])+1);
				t++;
			}
		}
		return indextolabel;
	}
	
	public ImagePlus computeFeaturespixellevel(String featureType) {
		ImagePlus classifiedImage= null;
		featureManager.extractFeatures(featureType);
		return pixellevelTraining(classifiedImage, featureType);
	}
	
	public HashMap<Integer,Integer> computeFeatureclasslevel(String featureType){
		featureManager.extractFeatures(featureType);
		return classlevelTraining(featureType);
	}

	public void setClassifier(Object classifier){
		if(classifier!=null){
			learningManager.setClassifier(classifier);
		}
		
	}

	public ImagePlus getOriginalImage() {
		return originalImage.duplicate();
	}

	public String getClassLabel(int index) {
		// TODO Auto-generated method stub
		return featureManager.getClassLabel(index);
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
