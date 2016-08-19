package activeSegmentation;

import java.util.List;




import java.util.Set;

import ij.gui.Roi;

public interface IFeatureManager {

	public void addExample(int classNum, Roi roi, int n) ;
	public void addExampleList(int classNum, List<Roi> roi, int n) ;
	public void deleteExample(int classNum, int nSlice, int index);
	public List<Roi> getExamples(int classNum, int n);
	public List<String> getClassLabels();
	public String getClassLabel(int index);
	public int getSize(int i, int currentSlice);
	public void setClassLabel(int classNum, String label);
	public void setNumOfClasses(int numOfClasses);
	public int getNumOfClasses();
	public void addClass();
	public void setFeatureMetadata();
	public void saveFeatureMetadata();
	public IDataSet extractFeatures(String featureType);
	public Set<String> getFeatures();
	public void addFeatures(IFeature feature);
	public List<IDataSet> extractAll(String featureType);
}
