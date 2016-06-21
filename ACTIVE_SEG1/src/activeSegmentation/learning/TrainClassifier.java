package activeSegmentation.learning;

import activeSegmentation.IClassifier;
import activeSegmentation.IDataSet;

public class TrainClassifier {

	private IClassifier classifier;
	private IDataSet dataSet;
	
	
	
	public TrainClassifier(IClassifier classifier, IDataSet dataSet) {
		super();
		this.classifier = classifier;
		this.dataSet = dataSet;
	}



	public void trainClassifier(){
		
		try {
			classifier.buildClassifier(dataSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	
	
	public IClassifier getClassifier() {
		return classifier;
	}



	public void setClassifier(IClassifier classifier) {
		this.classifier = classifier;
	}



	public IDataSet getDataSet() {
		return dataSet;
	}



	public void setDataSet(IDataSet dataSet) {
		this.dataSet = dataSet;
	}
	
	
	
}
