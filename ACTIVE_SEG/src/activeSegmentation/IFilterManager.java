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
 * Interface for Filter manager, It is responsible of doing all the Saving , loading
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
	public Image getFilterImage(String key);
	public boolean setDefault(String key);
	public boolean isFilterEnabled(String key);
	public void enableFilter(String key);
	public ImagePlus getFinalImage();
	public void setFinalImage(ImagePlus finalImage);
	public void setFiltersMetaData();
	public void saveFiltersMetaData();
	public ImagePlus getOriginalImage();
}
