package activeSegmentation;

import ij.gui.Roi;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public interface IFeature {
	
	public String getFeatureName();
	public void createTrainingInstance(List<String> classLabels,
			int classes, List<Vector<ArrayList<Roi>>> examples);
	public IDataSet getDataSet();
	public void setDataset(IDataSet trainingData);
	public List<IDataSet> createAllInstance(List<String> classLabels, int classes);
	
}
