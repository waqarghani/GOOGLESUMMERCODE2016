package activeSegmentation;

import ij.ImagePlus;
import ij.gui.Roi;

import java.util.List;









import org.json.simple.JSONObject;

import activeSegmentation.IClassifier;
import activeSegmentation.io.MetaInfo;
import weka.core.Instances;

public interface IDataManager {
	public boolean writeDataToARFF(Instances data, String filename);
	public boolean saveClassifier(String filename,IClassifier classifier,Instances trainHeader );
	public IDataSet readDataFromARFF(String filename);
	public List<Roi> openZip(String filename);
	public boolean saveExamples(String filename,List<Roi> roi);
	public boolean loadTrainingData(String fileName);
	public void writeMetaInfo(MetaInfo metaInfo);
	public MetaInfo getMetaInfo();
	public String getPath();
	public void setPath(String path);
	public void setData(IDataSet data);
	public IDataSet getDataSet();
	public ImagePlus getOriginalImage();
	public void setOriginalImage(ImagePlus originalImage);
	

}
