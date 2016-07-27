package activeSegmentation;

public interface IFeature {
	
	public String getFeatureName();
	public void createTrainingInstance();
	public IDataSet getDataSet();
	public void setDataset(IDataSet trainingData);
	
}
