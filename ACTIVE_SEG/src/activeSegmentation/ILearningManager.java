package activeSegmentation;


import java.util.List;





public interface ILearningManager {

	public void setClassifier(Object classifier);
	public void trainClassifier();
	public void saveLearningMetaData();
	public void loadLearningMetaData();
	public  List<double[]> applyClassifier(List<IDataSet> testDataSet);
	
}
