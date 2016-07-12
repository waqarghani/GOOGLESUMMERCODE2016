package activeSegmentation.learning;

import java.util.HashMap;
import java.util.Map;

import activeSegmentation.IClassifier;
import activeSegmentation.IDataSet;

public class ClassifierManager {

	
	
	Map<String,IClassifier> classifierMap= new HashMap<String, IClassifier>();


	public void trainClassifier(IClassifier classifier, IDataSet dataSet){
		
		try {
			classifier.buildClassifier(dataSet);
			classifierMap.put(classifier.getClass().getCanonicalName(), classifier);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	
	
	
	
	
}
