package activeSegmentation;

import ij.ImagePlus;
import ij.gui.Roi;

import java.util.List;

import activeSegmentation.io.MetaInfo;
import weka.core.Instances;

/**
 * 				
 *   
 * 
 * @author Sumit Kumar Vohra and Dimiter Prodanov , IMEC
 *
 *
 * @contents
 * Interface for data, It is responsible of doing all the major IO Operations
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
public interface IDataManager {
	public boolean writeDataToARFF(Instances data, String filename);
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
