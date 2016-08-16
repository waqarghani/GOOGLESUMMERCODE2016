package activeSegmentation.filterImpl;


import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.IFilter;
import activeSegmentation.IFilterManager;
import activeSegmentation.io.MetaInfo;
import weka.core.Instance;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;



public class FilterManager implements IFilterManager {

	private Map<String,IFilter> filterMap= new HashMap<String, IFilter>();
	private Map<Integer,ImageStack> featurStackMap= new HashMap<Integer, ImageStack>();
	
	private FilterUtil filterUtil= new FilterUtil();
	private ImagePlus finalImage;
	private IDataManager dataManager;
	private MetaInfo metaInfo;
	/** flag to specify the use of color features */
	private  boolean colorFeatures;

	private ImagePlus originalImage;

	

	/** flag to specify the use of the old color format (using directly the RGB values as float) */
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
			if(plugin.endsWith(".jar"))
			{ 
				classes.addAll(installJarPlugins(home+plugin));
			}
			else if (plugin.endsWith(".class")){
				classes.add(plugin);
			}

		}

		ClassLoader classLoader= FilterManager.class.getClassLoader();
		//IJ.log("IN FILTER");

		for(String plugin: classes){		
			Class<?>[] classesList=(classLoader.loadClass(plugin)).getInterfaces();
			for(Class<?> cs:classesList){
				if(cs.getSimpleName().equals("IFilter")){
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

			System.out.println("temp size"+tempStack.size());

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
				classified.addSlice("", featurStackMap.get(i).getProcessor(c));	
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


	/**
	 * Get the number of features of the reference stack (consistent all along the array)
	 * @return number of features on each feature stack of the array
	 */
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
			if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
				// This ZipEntry represents a class. Now, what class does it represent?
				String className = entry.getName().replace('/', '.'); // including ".class"
				classNames.add(className.substring(0, className.length() - ".class".length()));
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
		return this.finalImage;
	}

	@Override
	public void setFinalImage(ImagePlus finalImage) {
		this.finalImage = finalImage;
	}


	@Override
	public boolean setDefault(String key) {
		// TODO Auto-generated method stub
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
