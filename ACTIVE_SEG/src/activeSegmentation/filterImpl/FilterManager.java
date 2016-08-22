package activeSegmentation.filterImpl;


import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.IFilter;
import activeSegmentation.IFilterManager;
import activeSegmentation.io.MetaInfo;
import weka.core.Instance;
import ij.IJ;
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
 * Filter manager is responsible of loading  new filter from jar, 
 * change the setting of filter, generate the filter results
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


public class FilterManager implements IFilterManager {

	private Map<String,IFilter> filterMap= new HashMap<String, IFilter>();
	private Map<Integer,ImageStack> featurStackMap= new HashMap<Integer, ImageStack>();
	
	private FilterUtil filterUtil= new FilterUtil();
	private ImagePlus finalImage;
	private IDataManager dataManager;
	private MetaInfo metaInfo;
	private  boolean colorFeatures;

	private ImagePlus originalImage;

	

	
	private boolean oldColorFormat = false; 

	public FilterManager(IDataManager dataManager, String path){
		this.dataManager= dataManager;
		try {
			loadFilters(path);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public  void loadFilters(String home) throws InstantiationException, IllegalAccessException, 
	IOException, ClassNotFoundException {

		// IN ORIGINAL WILL BE LOADED FROM PROPERTY FILE

		File f=new File(home);
		String[] plugins = f.list();
		List<String> classes=new ArrayList<String>();
		for(String plugin: plugins){
			if(plugin.endsWith(Common.JAR))
			{ 
				classes.addAll(installJarPlugins(home+plugin));
			}
			else if (plugin.endsWith(Common.DOTCLASS)){
				classes.add(plugin);
			}

		}

		ClassLoader classLoader= FilterManager.class.getClassLoader();
		//IJ.log("IN FILTER");

		for(String plugin: classes){		
			Class<?>[] classesList=(classLoader.loadClass(plugin)).getInterfaces();
			for(Class<?> cs:classesList){
				if(cs.getSimpleName().equals(Common.IFILTER)){
					IFilter	thePlugIn =(IFilter) (classLoader.loadClass(plugin)).newInstance(); 
					filterMap.put(thePlugIn.getKey(), thePlugIn);
				}
			}

		}


	}


	public void applyFilters(ImagePlus image){
		//checkColorFeatures(image);
		originalImage=image.duplicate();
		System.out.println(originalImage.getImageStackSize());
		for(int i=1; i<=originalImage.getImageStackSize(); i++){
			List<ImageStack> tempStack= new ArrayList<ImageStack>();
			for(IFilter filter: filterMap.values()){
				if(filter.isEnabled()){
					ImageStack featureStack= filter.applyFilter(originalImage.getImageStack().getProcessor(i));
					//new ImagePlus("temp", featureStack).show();
					tempStack.add(featureStack);
					//	new ImagePlus(" stack", featureStack).show();
				}

			}

			//System.out.println("temp size"+tempStack.size());

			featurStackMap.put(i, combineStacks(tempStack));

		}


	}



	private ImageStack combineStacks(List<ImageStack> imageStackList)
	{
		ImageStack finalStack=new ImageStack(imageStackList.get(0).getWidth(),imageStackList.get(0).getHeight());
		for(ImageStack stack: imageStackList){

			for(int i=1; i<=stack.getSize(); i++){

				finalStack.addSlice(stack.getSliceLabel(i), stack.getProcessor(i));

			}
		}

		return finalStack;
	}


	private void generateFinalImage(){
		ImageStack classified = new ImageStack(originalImage.getWidth(), originalImage.getHeight());
		int numChannels=featurStackMap.get(1).getSize();
		for (int i = 1; i <= originalImage.getStackSize(); i++){
			for (int c = 1; c <= numChannels; c++){
				classified.addSlice(featurStackMap.get(i).getSliceLabel(c), 
						featurStackMap.get(i).getProcessor(c));	
			}
		}

		finalImage = new ImagePlus("Classification result", classified);
		finalImage.setDimensions(numChannels, originalImage.getImageStack().getSize(), originalImage.getNFrames());
		System.out.println(originalImage.getNSlices());
		System.out.println(originalImage.getNFrames());
		if (originalImage.getImageStack().getSize()*originalImage.getNFrames() > 1)
			finalImage.setOpenAsHyperStack(true);

	}

	public Set<String> getFilters(){
		return filterMap.keySet();
	}

	public Map<String,String> getFilterSetting(String key){

		return filterMap.get(key).getDefaultSettings();
	}


	public boolean isFilterEnabled(String key){

		return filterMap.get(key).isEnabled();
	}


	public boolean updateFilterSetting(String key, Map<String,String> settingsMap){

		return filterMap.get(key).updateSettings(settingsMap);
	}


	
	public int getNumOfFeatures() {

		return featurStackMap.get(featurStackMap.size()).getSize();
	}

	/**
	 * Get a specific label of the reference stack
	 * @param index slice index (>=1)
	 * @return label name
	 */
	public String getLabel(int index)
	{
		return  featurStackMap.get(featurStackMap.size()).getSliceLabel(index);
	}



	// Install plugins located in JAR files. 
	private  List<String> installJarPlugins(String home) throws IOException {

		List<String> classNames = new ArrayList<String>();
		ZipInputStream zip = new ZipInputStream(new FileInputStream(home));
		for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
			if (!entry.isDirectory() && entry.getName().endsWith(Common.DOTCLASS)) {
				// This ZipEntry represents a class. Now, what class does it represent?
				String className = entry.getName().replace('/', '.'); // including ".class"
				classNames.add(className.substring(0, className.length() - Common.DOTCLASS.length()));
			}
		}

		return classNames;
	}

	public ImageStack getImageStack(int sliceNum)
	{
		return featurStackMap.get(sliceNum);
	}

	public Instance createInstance(int x, int y, int classIndex, int sliceNum) {

		return filterUtil.createInstance(x, y, classIndex,
				featurStackMap.get(sliceNum), colorFeatures, oldColorFormat);
	}


	@Override
	public int getOriginalImageSize() {
		// TODO Auto-generated method stub
		return originalImage.getImageStackSize();
	}


	@Override
	public ImagePlus getFinalImage() {
		generateFinalImage();
		return finalImage.duplicate();
	}

	@Override
	public void setFinalImage(ImagePlus finalImage) {
		this.finalImage = finalImage;
	}


	@Override
	public boolean setDefault(String key) {
		// TODO Auto-generated method stub
		System.out.println("IN SET DEFAULT");
		if(filterMap.get(key).reset())
			return true;

		return false;
	}


	@Override
	public void enableFilter(String key) {
		// TODO Auto-generated method stub
		if(filterMap.get(key).isEnabled()){
			filterMap.get(key).setEnabled(false);	
		}
		else{
			filterMap.get(key).setEnabled(true);	
		}
	}


	@Override
	public void saveFiltersMetaData(){	
		 metaInfo= dataManager.getMetaInfo();
        
		List<Map<String,String>> filterObj= new ArrayList<Map<String,String>>();
		for(String key: getFilters()){
			Map<String,String> filters = new HashMap<String,String>();
			Map<String,String> filtersetting =getFilterSetting(key);
			filters.put(Common.FILTER, key);
			for(String setting: filtersetting.keySet()){
				filters.put(setting, filtersetting.get(setting));		
			}
			if(filterMap.get(key).getImageStack()!= null && 
					filterMap.get(key).getImageStack().size()>0 ){
				IJ.save(new ImagePlus(key,filterMap.get(key).getImageStack()), metaInfo.getPath()+key+".tif" );
				filters.put(Common.FILTERFILELIST,key+".tif" );
			}
				
			filterObj.add(filters);
		}
			
		metaInfo.setFilters(filterObj);
		dataManager.writeMetaInfo(metaInfo);
	}


	@Override
	public void setFiltersMetaData(){
        metaInfo= dataManager.getMetaInfo();
		List<Map<String,String>> filterObj= metaInfo.getFilters();
		for(Map<String, String> filter: filterObj){
			String filterName=filter.get(Common.FILTER);
			updateFilterSetting(filterName, filter);
			if(null!=filter.get(Common.FILTERFILELIST)){
				String fileName=filter.get(Common.FILTERFILELIST);
				System.out.println(metaInfo.getPath()+fileName);
				ImagePlus image=new ImagePlus(metaInfo.getPath()+fileName);
				image.show();
				filterMap.get(filterName).setImageStack(image.getImageStack());

			}
		}

	}

	@Override
	public Image getFilterImage(String key) {

		return filterMap.get(key).getImage();
	}
	
	@Override
	public ImagePlus getOriginalImage() {
		return originalImage;
	}

	

}
