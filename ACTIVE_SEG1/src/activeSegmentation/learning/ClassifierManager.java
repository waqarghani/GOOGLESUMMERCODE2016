package activeSegmentation.learning;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import java.util.concurrent.ForkJoinPool;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.functions.SMO;
import activeSegmentation.Common;
import activeSegmentation.IClassifier;
import activeSegmentation.IDataManager;
import activeSegmentation.IDataSet;
import activeSegmentation.ILearningManager;
import activeSegmentation.io.MetaInfo;

public class ClassifierManager implements ILearningManager {


	private IClassifier currentClassifier= new WekaClassifier(new SMO());
	Map<String,IClassifier> classifierMap= new HashMap<String, IClassifier>();
	private IDataManager dataManager;
	private MetaInfo metaInfo;
	private List<String> learningList= new ArrayList<String>();
	private String selectedType=Common.PASSIVELEARNING;
	private IDataSet dataset;
	private ForkJoinPool pool; 
	


	public ClassifierManager(IDataManager dataManager){
		learningList.add(Common.ACTIVELEARNING);
		learningList.add(Common.PASSIVELEARNING);
		this.dataManager= dataManager;
		pool=  new ForkJoinPool();

	}


    @Override
	public void trainClassifier(){

		try {
			currentClassifier.buildClassifier(dataManager.getDataSet());
			System.out.println(currentClassifier.toString());
			classifierMap.put(currentClassifier.getClass().getCanonicalName(), currentClassifier);
		} catch (Exception e) {
		
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
	public List<double[]> applyClassifier(List<IDataSet> testDataSet){
		List<double[]> results= new ArrayList<double[]>();
		for(IDataSet dataSet: testDataSet){
			double[] classificationResult = new double[testDataSet.get(0).getNumInstances()];		
			ApplyTask applyTask= new ApplyTask(dataSet, 0, dataSet.getNumInstances(), 
					classificationResult, currentClassifier);
			pool.invoke(applyTask);
			results.add(classificationResult);			
		}
		
		
		return results;
	}





}
