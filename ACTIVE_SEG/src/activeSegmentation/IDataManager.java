package activeSegmentation;

import ij.gui.Roi;

import java.util.List;

import org.json.simple.JSONObject;

import activeSegmentation.IClassifier;
import weka.core.Instances;

public interface IDataManager {
	public boolean writeDataToARFF(Instances data, String filename);
	public boolean saveClassifier(String filename,IClassifier classifier,Instances trainHeader );
	public Instances readDataFromARFF(String filename);
	public List<Roi> openZip(String filename);
	public boolean saveExamples(String filename,List<Roi> roi);
	public boolean loadTrainingData(String fileName);
	public void writeFile(String path, JSONObject obj);
	public JSONObject readFile(String file);
	

}
