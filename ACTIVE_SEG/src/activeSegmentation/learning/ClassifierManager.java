package activeSegmentation.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import weka.classifiers.AbstractClassifier;
import activeSegmentation.Common;
import activeSegmentation.IClassifier;
import activeSegmentation.IDataManager;
import activeSegmentation.IDataSet;
import activeSegmentation.ILearningManager;
import activeSegmentation.io.MetaInfo;

public class ClassifierManager implements ILearningManager {


	private IClassifier currentClassifier;
	Map<String,IClassifier> classifierMap= new HashMap<String, IClassifier>();
	private IDataManager dataManager;
	private MetaInfo metaInfo;
	private List<String> learningList= new ArrayList<String>();
	private String selectedType=Common.PASSIVELEARNING;
	private IDataSet dataset;


	public ClassifierManager(){

		learningList.add(Common.ACTIVELEARNING);
		learningList.add(Common.PASSIVELEARNING);

	}



	public void trainClassifier(IDataSet dataSet){

		try {
			currentClassifier.buildClassifier(dataSet);
			classifierMap.put(currentClassifier.getClass().getCanonicalName(), currentClassifier);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	@Override
	public void saveLearningMetaData(){	
		Map<String,String> learningMap = new HashMap<String, String>();
		if(dataset!=null){
			learningMap.put(Common.ARFF, Common.ARFFFILENAME);
			dataManager.writeDataToARFF(dataset.getDataset(), Common.ARFFFILENAME);
		}
		
		learningMap.put(Common.CLASSIFIER, Common.CLASSIFIERNAME);  
		learningMap.put(Common.LEARNINGTYPE, selectedType);
		metaInfo.setLearning(learningMap);
		dataManager.writeMetaInfo(metaInfo);		
	}



	@Override
	public void loadLearningMetaData() {
		// TODO Auto-generated method stub
		if(metaInfo.getLearning()!=null){
			dataset= dataManager.readDataFromARFF(metaInfo.getLearning().get(Common.ARFF));
			selectedType=metaInfo.getLearning().get(Common.LEARNINGTYPE);
		}

	}




	@Override
	public void setClassifier(Object classifier) {

		if (classifier instanceof AbstractClassifier) {
			currentClassifier = new WekaClassifier((AbstractClassifier)classifier);		 		
		}

	}





	@Override
	public void trainClassifier() {
		// TODO Auto-generated method stub

	}








}
