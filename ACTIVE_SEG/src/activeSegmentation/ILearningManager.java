package activeSegmentation;





public interface ILearningManager {

	public void setClassifier(Object classifier);
	public void trainClassifier();
	public void saveLearningMetaData();
	public void loadLearningMetaData();
	
}
