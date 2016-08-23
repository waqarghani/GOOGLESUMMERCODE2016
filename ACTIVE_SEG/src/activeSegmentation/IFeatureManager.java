package activeSegmentation;

import java.util.List;




import java.util.Set;

import ij.gui.Roi;
/**
 * 				
 *   
 * 
 * @author Sumit Kumar Vohra and Dimiter Prodanov , IMEC
 *
 *
 * @contents
 * Feature manager is responsible for loading , saving Features, It is also responsible 
 * for storing , updating ROIS
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
public interface IFeatureManager {

	public void addExample(int classNum, Roi roi, int n) ;
	public void addExampleList(int classNum, List<Roi> roi, int n) ;
	public void deleteExample(int classNum, int nSlice, int index);
	public List<Roi> getExamples(int classNum, int n);
	public int  getclassKey(String classNum);
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
