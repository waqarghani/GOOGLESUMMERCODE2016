package activeSegmentation;

import java.util.List;



import ij.gui.Roi;

public interface IFeatureManager {

	public void addExample(int classNum, Roi roi, int n) ;
	public void addExampleList(int classNum, List<Roi> roi, int n) ;
	public void deleteExample(int classNum, int nSlice, int index);
	public List<Roi> getExamples(int classNum, int n);
	public List<String> getClassLabels();
	public void setClassLabel(int classNum, String label);
	public void setNumOfClasses(int numOfClasses);
	public int getNumOfClasses();
	public void addClass(int classId);
	public void setFeatureMetadata();
	public void saveFeatureMetadata();
}
