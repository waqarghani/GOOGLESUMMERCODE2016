package activeSegmentation.feature;

import java.util.ArrayList;
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
	public Zernike_Feature_Extraction(IFilterManager filterManager, ImagePlus originalImage){
		this.filterManager= filterManager;
	}
	
	@Override
	public String getFeatureName() {
		// TODO Auto-generated method stub
		return featureName;
	}

	@Override
	public void createTrainingInstance(List<String> classLabels, int classes, List<Vector<ArrayList<Roi>>> examples) {
		// TODO Auto-generated method stub
		ArrayList<Attribute> attributes = createFeatureHeader();
		attributes.add(new Attribute(Common.CLASS, addClasstoHeader(classes)));
		
		// create initial set of instances
		trainingData =  new Instances(Common.INSTANCE_NAME, attributes, 1 );
		
		// Set the index of the class attribute
		trainingData.setClassIndex(classes);
		
		for(int sliceNum = 1; sliceNum <= classes; sliceNum++)
		{
			trainingData.add(filterManager.createInstance(featureName, 0, 0, sliceNum, sliceNum));
		}

	}

	private  ArrayList<Attribute> createFeatureHeader(){
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		double[] tempzernikevalues = filterManager.createInstance(featureName, 0, 0, 0, 1).toDoubleArray();
		int degree = tempzernikevalues.length;
		for(int k=0;k<=degree;k++){
			for(int l=0;l<=k;l++){	
				if((k-l)%2==0){
				     attributes.add(new Attribute("Z"+k+","+l));
				     if(l!=0)
				    	 attributes.add(new Attribute("Z"+k+",-"+l));
				}
			}
    	}	
		return attributes;
	}
	
	private ArrayList<String> addClasstoHeader(int numClasses){
		ArrayList<String> classes=null;
			classes = new ArrayList<String>();
			for(int i = 1; i <= numClasses ; i ++)
			{			
				classes.add("class"+i);
			}			
		return classes;
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

}
