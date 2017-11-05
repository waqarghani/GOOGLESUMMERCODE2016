package activeSegmentation;

import java.awt.Image;
import java.io.IOException;
import java.util.Map;
import java.util.Set;








import weka.core.Instance;
import ij.ImagePlus;
import ij.ImageStack;

/**
 * 				
 *   
 * 
 * @author Sumit Kumar Vohra and Dimiter Prodanov , IMEC
 *
 *
 * @contents
 * Interface for Filter manager, It is responsible of doing all the Saving, loading
 * the filter
 * 
 * 
 * @license This library is free software; you can redistribute it and/or
 *      modify it under the terms of the GNU Lesser General Public
 *      License as published by the Free Software Foundation; either
 *      version 2.1 of the License, or (at your option) any later version.
 *
 *      This library is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *       Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public
 *      License along with this library; if not, write to the Free Software
 *      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
public interface IFilterManager {

	/**
	 * This method will load list of filter from particular
	 * directory, It can load filters from jar file 
	 * @param diretory of filter
	 *
	 */
	public  void loadFilters(String home)throws InstantiationException, IllegalAccessException, 
	IOException, ClassNotFoundException;
	
	/**
	 * This method will apply list of filters on particular 
	 * image 
	 * @param image on which filter is applied
	 *
	 */
	public void applyFilters(ImagePlus image);
	
	/**
	 * This method will set of filters loaded by the plugin  
	 * 
	 * @return set of loaded or available filters
	 *
	 */
	public Set<String> getFilters();
	
	/**
	 * This method will give the setting of the particular filters 
	 * 
	 * @param key of the filter
	 *
	 */
	public Map<String,String> getFilterSetting(String key);
	
	/**
	 * This method will update the setting of the particular filters 
	 * given by key
	 * @param key of the filter
	 *@param  updated setting map
	 */
	public boolean updateFilterSetting(String key, Map<String,String> settingsMap);
	
	public int getNumOfFeatures(String featureName) ;
	
	public String getLabel(int index);
	
	public ImageStack getImageStack(int sliceNum);
	
	/**
	 * This method will create pixel level training  
	 * instance. It might be changed in future to handle other feature type
	 *@param xlocation of pixel
	 *@param  ylocation of Pixel
	 *@param class of pixel
	 *@param slice of Image
	 *@return Instance of pixel
	 */
	public Instance createInstance(String featureName, int x, int y, int classIndex, int sliceNum);
	
	/**
	 * This method will create class level training  
	 *@param slice of Image
	 *@return Instance of pixel
	 */
	public Instance createInstance(String featureName, int classIndex, int sliceNum);
	
	/**
	 * This method will return number of slices
	 * @return return number of slices
	 */
	public int getOriginalImageSize();
	
	/**
	 * This method will return processed image by particular filter
	 * @param  filter key
	 * @return extracted Image
	 */
	public Image getFilterImage(String key);
	
	/**
	 * This method will change filter settings to default
	 * @param  filter key
	 * @return success flag
	 */
	public boolean setDefault(String key);
	
	/**
	 * This method is to check whether filter is enabled or not
	 * @param  filter key
	 * @return success flag
	 */
	public boolean isFilterEnabled(String key);
	
	/**
	 * This method is to enable the filter
	 * @param  filter key
	 */
	public void enableFilter(String key);
	
	/**
	 * 
	 * @return  classified image
	 */
	public ImagePlus getFinalImage();
	
	/**
	 * 
	 * @param  set classified image
	 */
	public void setFinalImage(ImagePlus finalImage);
	
	/**
	 * 
	 *   set filters Meta Data using MetaInfo
	 */
	public void setFiltersMetaData();
	
	/**
	 * 
	 *   save filters Meta data using MetaInfo
	 */
	public void saveFiltersMetaData();
	
	/**
	 * 
	 *   @return give original training Image
	 */
	public ImagePlus getOriginalImage();

}
