package activeSegmentation.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import activeSegmentation.IDataSet;
import activeSegmentation.IExampleManager;
import activeSegmentation.IFeature;
import activeSegmentation.IFilterManager;

public class FeatureManager {
	private Map<String,IFeature> featureMap= new HashMap<String, IFeature>();
	private IFilterManager filterManager;
	private IExampleManager exampleManager;
	private String featureType;


	private void setDefaultFilters(){
		IFeature feature= new FeatureExtraction(filterManager, exampleManager);
		featureType= feature.getFeatureName();
		featureMap.put(feature.getFeatureName(), feature);
	}

	public IDataSet extractFeatures(){

		featureMap.get(featureType).createTrainingInstance();
		return featureMap.get(featureType).getDataSet();
		
	}
	
	public Set<String> getFeatures(){
		return featureMap.keySet();
	}

}
