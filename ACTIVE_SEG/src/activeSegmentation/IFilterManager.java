package activeSegmentation;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import weka.core.Instance;
import ij.ImagePlus;
import ij.ImageStack;

public interface IFilterManager {

	public  void loadFilters(String home)throws InstantiationException, IllegalAccessException, 
	IOException, ClassNotFoundException;
	public void applyFilters(ImagePlus image);
	public Set<String> getFilters();
	public Map<String,String> getFilterSetting(String key);
	public boolean updateFilterSetting(String key, Map<String,String> settingsMap);
	public int getNumOfFeatures() ;
	public String getLabel(int index);
	public ImageStack getImageStack(int sliceNum);
	public Instance createInstance(int x, int y, int classIndex, int sliceNum);
	public int getOriginalImageSize();
	public IFilter getFilter(String key);
	public boolean setImageStack(ImageStack featureStack);
	public ImageStack getFeatureStack();
	public boolean setDefault();
	public boolean isFilterEnabled(String key);
	public void enableFilter(String key);
}
