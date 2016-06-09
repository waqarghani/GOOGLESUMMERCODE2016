package activeSegmentation;

import ij.gui.Roi;

import java.util.List;

import activeSegmentation.IClassifier;
import weka.core.Instances;

public interface IDataManager {
	public boolean writeDataToARFF(Instances data, String filename);
	public boolean saveClassifier(String filename,IClassifier classifier,Instances trainHeader );
	public Instances readDataFromARFF(String filename);
	public List<Roi> openZip(String filename, int classNum, int sliceNumber);
	public boolean saveExamples(String filename,List<Roi> roi);
	public boolean loadTrainingData(String fileName);
	public boolean loadExamples(String directory);
	

}
