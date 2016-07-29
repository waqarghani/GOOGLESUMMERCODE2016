package activeSegmentation.feature;

import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.IDataSet;
import activeSegmentation.IFeatureManager;
import activeSegmentation.IFeature;
import activeSegmentation.io.FeatureInfo;
import activeSegmentation.io.MetaInfo;




public class FeatureManager implements IFeatureManager {

	/** array of lists of Rois for each slice (vector index) 
	 * and each class (arraylist index) of the training image */
	private List<Vector<ArrayList<Roi>>> examples;
	private IDataManager dataManager;
	private MetaInfo metaInfo;
	private Map<String,IFeature> featureMap= new HashMap<String, IFeature>();

	/** maximum number of classes (labels) allowed */
	/** names of the current classes */
	private List<String> classLabels = new ArrayList<String>();
	private static RoiManager roiman= new RoiManager();

	/** current number of classes */
	private int numOfClasses = 0;
	private int stackSize=0;

	public FeatureManager(int stackSize, int numOfClasses,IDataManager dataManager, MetaInfo metaInfo)
	{
		this.stackSize=stackSize;
		this.examples= new ArrayList<Vector<ArrayList<Roi>>>();
		this.dataManager= dataManager;
		this.metaInfo= metaInfo;
		
		// update list of examples
		for(int i=0; i < stackSize; i++)
		{
			examples.add(new Vector<ArrayList<Roi>>());			
		}

		for(int i=0; i<numOfClasses;i++ ){
			addClass(i);
		}
		
		setFeatureMetadata();
	}


	public void addExample(int classNum, Roi roi, int n) 
	{

		examples.get(n-1).get(classNum).add(roi);
		roiman.addRoi(roi);

	}

	@Override
	public void addExampleList(int classNum, List<Roi> roiList, int n) {
		// TODO Auto-generated method stub
		for(Roi roi: roiList){
			if(processibleRoi(roi)){
				addExample(classNum, roi, n);
			}

		}
	}


	/**
	 * Return the list of examples for a certain class.
	 * 
	 * @param classNum the number of the examples' class
	 * @param n the slice number
	 */
	public List<Roi> getExamples(int classNum, int n) 
	{
		System.out.println("size"+examples.size());
		System.out.println("class Num"+ classNum+ " slice No"+ n);
		return examples.get(n-1).get(classNum);
	}

	/**
	 * Remove an example list from a class and specific slice
	 * 
	 * @param classNum the number of the examples' class
	 * @param nSlice the slice number
	 * @param index the index of the example list to remove
	 */
	public void deleteExample(int classNum, int nSlice, int index)
	{
		getExamples(classNum, nSlice).remove(index);
	}

	/**
	 * Get the current class labels
	 * @return array containing all the class labels
	 */
	@Override
	public List<String> getClassLabels() 
	{
		return classLabels;
	}


	/**
	 * Set the name of a class.
	 * 
	 * @param classNum class index
	 * @param label new name for the class
	 */
	@Override
	public void setClassLabel(int classNum, String label) 
	{
		classLabels.add(classNum, label);
	}

	/**
	 * Set the current number of classes. Should not be used to create new
	 * classes. Use <link>addClass<\link> instead.
	 *
	 * @param numOfClasses the new number of classes
	 */
	@Override
	public void setNumOfClasses(int numOfClasses) {
		this.numOfClasses = numOfClasses;
	}

	/**
	 * Get the current number of classes.
	 *
	 * @return the current number of classes
	 */
	@Override
	public int getNumOfClasses() 
	{
		return numOfClasses;
	}

	/**
	 * Add new segmentation class.
	 */
	public void addClass(int classId)
	{

		for(int i=1; i <= stackSize; i++)
			examples.get(i-1).add(new ArrayList<Roi>());

		classLabels.add( new String(Common.CLASS + (classId+1)));
		// increase number of available classes
		numOfClasses ++;
	}

	private boolean processibleRoi(Roi roi) {
		boolean ret=(roi!=null && !(roi.getType()==Roi.LINE || 
				roi.getType()==Roi.POLYLINE ||
				roi.getType()==Roi.ANGLE ||
				roi.getType()==Roi.FREELINE ||
				roi.getType()==Roi.POINT
				)
				);

		return ret;

	}


	public int getStackSize() {
		return stackSize;
	}


	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}



	@Override
	/*
	 *
	 */

	public void setFeatureMetadata(){

		for(FeatureInfo featureInfo : metaInfo.getFeatureList() ){
			int classNum=featureInfo.getClassLabel();
			List<Roi> classRoiList=dataManager.openZip(metaInfo.getPath()+featureInfo.getZipFile());
			System.out.println(classRoiList.size());
			for( String s: featureInfo.getSliceList().keySet()){
				Integer sliceNum= Integer.parseInt(s.substring(s.length()-1));
				System.out.println(sliceNum);
				List<String> sliceRois= featureInfo.getSliceList().get(s);	
				addExampleList(classNum, getRois(classRoiList, sliceRois), sliceNum);
			}
		}

		System.out.println(examples.size());
		System.out.println(metaInfo.toString());

	}

	private List<Roi> getRois(List<Roi> classRoiList, List<String> roiNames){
		List<Roi> roiList= new ArrayList<Roi>();
		for(String name: roiNames){
			for(Roi roi: classRoiList){
				if(roi.getName().equalsIgnoreCase(name)){
					roiList.add(roi);
				}
			}
		}

		return roiList;
	}

	@Override
	public void saveFeatureMetadata(){
		metaInfo.resetFeatureInfo();
		for(int classIndex = 0; classIndex <
				getNumOfClasses(); classIndex++)
		{
			FeatureInfo featureInfo= new FeatureInfo();
			List<Roi>  classRois= new ArrayList<Roi>();
			featureInfo.setClassLabel(classIndex);

			for(int sliceNum = 1; sliceNum <= 
					stackSize; sliceNum ++){
				List<Roi> rois=getExamples(classIndex, sliceNum);
				if(rois!=null & rois.size()>0){
					classRois.addAll(rois);
					List<String> roiArr=new ArrayList<String>();
					for(Roi roi: rois){
						roiArr.add(roi.getName());
					}	
					featureInfo.addSlice(Common.SLICE+sliceNum, roiArr);
				}

			}

			String fileName=Common.ROISET+classIndex+Common.FORMAT;
			if(classRois!=null & classRois.size()>0){
				System.out.println("examples");
				dataManager.saveExamples(metaInfo.getPath()+fileName,classRois );
				featureInfo.setZipFile(fileName);
			}

			metaInfo.addFeature(featureInfo);
		}			

		System.out.println("IN");
		System.out.println(metaInfo.toString());
		dataManager.writeMetaInfo(metaInfo);

	}
	

	public IDataSet extractFeatures(String featureType){

		featureMap.get(featureType).createTrainingInstance();
		return featureMap.get(featureType).getDataSet();
		
	}
	
	public Set<String> getFeatures(){
		return featureMap.keySet();
	}

	public void addFeatures(IFeature feature){
		featureMap.put(feature.getFeatureName(), feature);
	}

}
