package activeSegmentation.feature;

import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;



import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.IDataSet;
import activeSegmentation.IFeatureManager;
import activeSegmentation.IFeature;
import activeSegmentation.io.FeatureInfo;
import activeSegmentation.io.MetaInfo;



/**
 * 				
 *   
 * 
 * @author Sumit Kumar Vohra and Dimiter Prodanov , IMEC
 *
 *
 * @contents
 * Feature Manager to store , update and delete Samples , It also consist of code to load from metafile
 * 
 * 
 * @license This library is free software; you can redistribute it and/or
 *      modify it under the terms of the GNU Lesser General Public
 *      License as published by the Free Software Foundation; either
 *      version 2.1 of the License, or (at your option) any later version.
 *
 *      This library is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *       Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public
 *      License along with this library; if not, write to the Free Software
 *      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
public class FeatureManager implements IFeatureManager {

	/** array of lists of Rois for each slice (vector index) 
	 * and each class (arraylist index) of the training image */
	private List<Vector<ArrayList<Roi>>> examples;
	private List<ArrayList<Integer>> imageType;
	private IDataManager dataManager;
	private MetaInfo metaInfo;
	private Map<String,IFeature> featureMap= new HashMap<String, IFeature>();

	/** maximum number of classes (labels) allowed */
	/** names of the current classes */
	private Map<Integer,String> classLabels = new HashMap<Integer, String>();
	private static RoiManager roiman= new RoiManager();

	/** current number of classes */
	private int numOfClasses = 0;
	private int stackSize=0;

	public FeatureManager(int stackSize, int numOfClasses,IDataManager dataManager)
	{
		this.stackSize=stackSize;
		this.examples= new ArrayList<Vector<ArrayList<Roi>>>();
		this.imageType = new ArrayList<ArrayList<Integer>>();
		this.dataManager= dataManager;	
		// update list of examples
		for(int i=0; i < stackSize; i++)
		{
			examples.add(new Vector<ArrayList<Roi>>());			
			imageType.add(new ArrayList<Integer>());
		}

		for(int i=1; i<=numOfClasses;i++ ){
			addClass();
		}

	}


	public void addExample(int classNum, Roi roi, int n) 
	{

		System.out.println(roi);
		System.out.println("ADD EXAMLE");
		examples.get(n-1).get(classNum).add(roi);
		roiman.addRoi(roi);

	}
	
	public void addImageType(int classNum, int nSlice) 
	{
		for(int i=0;i<imageType.size();i++){
			if(imageType.get(i).contains(nSlice))
				imageType.get(i).remove(imageType.get(i).indexOf(nSlice));
		}
		imageType.get(classNum).add(nSlice);
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

	@Override
	public String getImageStatus(int nSlice){
		/*if(imageType.containsKey(nSlice))
		{
			if(imageType.get(nSlice)==0)
				return "Training";
			return "Testing";
		}*/
		return null;
	}


	@Override
	public int  getclassKey(String classNum){

		for (Map.Entry<Integer,String> e : classLabels.entrySet()) {
			Integer key = e.getKey();
			Object value2 = e.getValue();
			if ((value2.toString()).equalsIgnoreCase(classNum))
			{
				return key;
			}
		} 
		return 0;
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
	 * Remove an slice from dataset. 
	 * 
	 * @param sliceNum the number of the examples' class
	 */
	public void deleteImageType(int classId, int sliceNum)
	{
		imageType.get(classId).remove(imageType.get(classId).indexOf(sliceNum));
	}

	/**
	 * Get the current class labels
	 * @return array containing all the class labels
	 */
	@Override
	public List<String> getClassLabels() 
	{
		return new ArrayList<String>(classLabels.values());
	}

	@Override
	public String getClassLabel(int index) {
		// TODO Auto-generated method stub
		return classLabels.get(index);
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
		//classLabels.add(classNum-1, label);
		classLabels.put(classNum, label);
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
	public void addClass()
	{

		for(int i=1; i <= stackSize; i++)
			examples.get(i-1).add(new ArrayList<Roi>());

		numOfClasses ++;
		classLabels.put(numOfClasses ,new String(Common.CLASS + (numOfClasses)));
		// increase number of available classes

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
		metaInfo= dataManager.getMetaInfo();
		Map<String,String> keywordList= metaInfo.getKeywordList();
		if(keywordList!=null){
			for(String key:keywordList.keySet()){
				Integer classId=Integer.parseInt(key);	
				if(numOfClasses>=classId){
					setClassLabel(classId, keywordList.get(key));	
					System.out.println("classId"+classId);
					System.out.println("No.ofClasses "+numOfClasses);
				}
				else{
					addClass();
					System.out.println("classId 1"+classId);
					System.out.println("No.ofClasses "+numOfClasses);
					setClassLabel(classId, keywordList.get(key));	
				}
			}
		}

		for(FeatureInfo featureInfo : metaInfo.getFeatureList() ){
			int classNum=featureInfo.getClassLabel();
			System.out.println(metaInfo.getPath()+featureInfo.getZipFile());
			List<Roi> classRoiList=dataManager.openZip(metaInfo.getPath()+featureInfo.getZipFile());
			System.out.println(classRoiList.size());
			for( String s: featureInfo.getSliceList().keySet()){
				Integer sliceNum= Integer.parseInt(s.substring(s.length()-1));
				System.out.println("slicenum-"+sliceNum);
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
			System.out.println(name);
			for(Roi roi: classRoiList){
				System.out.println(roi.getName());
				if(roi.getName().equalsIgnoreCase(name)){
					roiList.add(roi);
				}
			}
		}

		return roiList;
	}

	@Override
	public void saveFeatureMetadata(){
		metaInfo= dataManager.getMetaInfo();
		metaInfo.resetFeatureInfo();

		Map<String, String> keywordList = new HashMap<String, String>();
		for(Integer key:classLabels.keySet()){
			keywordList.put(key.toString(), classLabels.get(key));

		}
		metaInfo.setKeywordList(keywordList);		
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

	public ArrayList<String> ClassLabelsForClassLevel(){
		/*
		ArrayList<String> labels = new ArrayList<String>();
		for(Entry<Integer, Integer> map:imageType.entrySet()){
			if(map.getValue()==0)
				labels.add("class"+map.getKey());
		}
		numOfClasses = labels.size();*/
		//return labels;
		return null;
	}
	
	@Override
	public IDataSet extractFeatures(String featureType){
		
		if(featureType.equals("classlevel"))
		{
			featureMap.get(featureType).createTrainingInstance(ClassLabelsForClassLevel(),
				numOfClasses, examples);
			
		}
		else {
			featureMap.get(featureType).createTrainingInstance(new ArrayList<String>(classLabels.values()),
					numOfClasses, examples);
		}
		IDataSet dataset=featureMap.get(featureType).getDataSet();
		dataManager.setData(dataset);
		System.out.println("NUMBER OF INSTANCE"+dataset.toString());
		return dataset;

	}

	@Override
	public List<IDataSet> extractAll(String featureType){
		List<IDataSet> dataset= featureMap.get(featureType).
				createAllInstance(new ArrayList<String>(classLabels.values()),
						numOfClasses);
		return dataset;

	}

	@Override
	public Set<String> getFeatures(){
		return featureMap.keySet();
	}

	@Override
	public void addFeatures(IFeature feature){
		featureMap.put(feature.getFeatureName(), feature);
	}


	@Override
	public int getSize(int i, int currentSlice) {
		// TODO Auto-generated method stub
		return getExamples(i, currentSlice).size();
	}


	@Override
	public ArrayList<Integer> getDataImageTypeId(int ClassNum) {
		// TODO Auto-generated method stub
		
		if(imageType.size()==0)
			return null;
		
		return imageType.get(ClassNum);
	}

}
