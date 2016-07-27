package activeSegmentation;

import activeSegmentation.io.MetaInfo;



public interface ILearningManager {

	public void setClassifier(Object classifier);
	public void trainClassifier();
	public void saveLearning(String path);
	public void setLearning(MetaInfo metaInfo);
	
}
