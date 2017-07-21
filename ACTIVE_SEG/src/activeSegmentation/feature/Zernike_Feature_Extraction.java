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
	public Zernike_Feature_Extraction(IFilterManager filterManager, ImagePlus originalImage){
		this.filterManager= filterManager;
	}
	
	@Override
	public String getFeatureName() {
		// TODO Auto-generated method stub
		return featureName;
	}

	@Override
	public void createTrainingInstance(HashMap<Integer, Integer> imageType) {
		
	}
	
	@Override
	public void createTrainingInstance(List<String> classLabels, int classes, List<Vector<ArrayList<Roi>>> examples) {
	
			ArrayList<Attribute> attributes = createFeatureHeader();
			attributes.add(new Attribute(Common.CLASS, classLabels));
						
			//create initial set of instances
			trainingData =  new Instances(Common.INSTANCE_NAME, attributes, 1 );
						
			//Set the index of the class attribute
			trainingData.setClassIndex(classes);
						
			for(String imageIndex : classLabels)
			{
				trainingData.add(filterManager.createInstance(featureName, Integer.parseInt(imageIndex.replace("image", ""))));
			}
			System.out.println(trainingData+"aaaaaaaaaa");
	}

	private  ArrayList<Attribute> createFeatureHeader(){
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		double[] tempzernikevalues = filterManager.createInstance(featureName, 1).toDoubleArray();
		int degree = tempzernikevalues.length;
		int count = 0;
		int k=0;
		while(count<degree){
			for(int l=0;l<=k;l++){	
				if((k-l)%2==0){
				     attributes.add(new Attribute("Z"+k+","+l));
				     count++;
				     if(l!=0){
				    	 attributes.add(new Attribute("Z"+k+",-"+l));
				    	 count++;
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



}
