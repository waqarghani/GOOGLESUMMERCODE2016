package activeSegmentation.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import activeSegmentation.Common;
import activeSegmentation.IDataSet;
import activeSegmentation.IFeature;
import activeSegmentation.IFilterManager;
import activeSegmentation.learning.WekaDataSet;
import ij.ImagePlus;
import ij.gui.Roi;
import weka.core.Attribute;
import weka.core.Instances;

public class Zernike_Feature_Extraction implements IFeature {

	private IFilterManager filterManager;	

	private Instances trainingData;
	private String featureName="classlevel";
	int classindex = 0;
	public Zernike_Feature_Extraction(IFilterManager filterManager, ImagePlus originalImage){
		this.filterManager= filterManager;
	}
	
	@Override
	public void createTrainingInstance(List<String> classLabels, int classes, List<?> features) {
		// TODO Auto-generated method stub

		List<ArrayList<Integer>> imageType = (List<ArrayList<Integer>>) features;
		
		ArrayList<Attribute> attributes = createFeatureHeader();
		attributes.add(new Attribute(Common.CLASS, classLabels));
				
		//create initial set of instances
		trainingData =  new Instances(Common.INSTANCE_NAME, attributes, 1 );
					
		//Set the index of the class attribute
		trainingData.setClassIndex(classindex);
		System.out.println(imageType.get(0).size()+"ssssssssssssss");
		for(int classIndex = 0; classIndex < classes; classIndex++)
		{
			for(int i=0; i<imageType.get(classIndex).size();i++){
				trainingData.add(filterManager.createInstance(featureName, classIndex, imageType.get(classIndex).get(i)));
			}	
		}
		
	}
	
	@Override
	public String getFeatureName() {
		// TODO Auto-generated method stub
		return featureName;
	}

	private  ArrayList<Attribute> createFeatureHeader(){
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		int degree = filterManager.getNumOfFeatures(featureName);
		int k=0;
		while(k<=degree){
			for(int l=0;l<=k;l++){	
				if((k-l)%2==0){
				     attributes.add(new Attribute("Z"+k+","+l));
				     ++classindex;
				     if(l!=0){
				    	 attributes.add(new Attribute("Z"+k+",-"+l));
				     ++classindex;
				     }	 
				}
			}
			k++;
		}
		return attributes;
	}
	
	
	@Override
	public IDataSet getDataSet() {
		// TODO Auto-generated method stub
		return new WekaDataSet(trainingData);
	}

	@Override
	public void setDataset(IDataSet trainingData) {
		// TODO Auto-generated method stub
		this.trainingData= trainingData.getDataset();

	}

	@Override
	public List<IDataSet> createAllInstance(List<String> classLabels, int classes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IDataSet> createTestInstance(List<String> classLabels, int classes, ArrayList<Integer> testimageindex) {
		// TODO Auto-generated method stub
		
		List<IDataSet> dataSets= new ArrayList<IDataSet>();
		for(int i=0; i<testimageindex.size();i++){
			Instances testingData;
			classindex=0;
			ArrayList<Attribute> attributes = createFeatureHeader();
			attributes.add(new Attribute(Common.CLASS,classLabels));
			testingData =  new Instances(Common.INSTANCE_NAME, attributes, 1 );
			// Set the index of the class attribute
			testingData.setClassIndex(classindex);
			testingData.add(filterManager.createInstance(featureName, 0, testimageindex.get(i)));
			dataSets.add(new WekaDataSet(testingData));
		}
		
		return dataSets;        
	}

	

}
