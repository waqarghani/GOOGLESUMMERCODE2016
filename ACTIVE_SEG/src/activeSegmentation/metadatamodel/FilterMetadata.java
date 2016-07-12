package activeSegmentation.metadatamodel;

import ij.gui.Roi;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import activeSegmentation.Common;
import activeSegmentation.IDataManager;
import activeSegmentation.IExampleManager;
import activeSegmentation.IFilterManager;

public class FilterMetadata {

	private IFilterManager filterManager;
	private IExampleManager exampleManager;
	private IDataManager dataManager;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public FilterMetadata(IFilterManager filterManager){
		this.filterManager=filterManager;
	}

	public void createDescription(String path){
		JSONObject descObj = new JSONObject();
		descObj.put(Common.COMMENT,Common.DEFAULTCOMMENT);
		descObj.put(Common.CREATEDATE,dateFormat.format(new Date()));
		descObj.put(Common.MODIFYDATE,dateFormat.format(new Date()));
		descObj.put(Common.PATH,path);
		
	}

	public void saveFilters(String path){
		JSONArray jsonArr=new JSONArray();
	
		for(String key: filterManager.getFilters()){
		JSONObject obj = new JSONObject();
		Map<String,String> filtersetting =filterManager.getFilterSetting(key);
		obj.put(Common.FILTER, key);
		for(String setting: filtersetting.keySet()){
		obj.put(setting, filtersetting.get(setting));		
		}
		jsonArr.add(obj);
		}
		JSONObject finalObj = new JSONObject();
		finalObj.put(Common.FILTERS, jsonArr);
		writeFile(path, finalObj);

	}
	
	public void saveLearning(String path,String arffName, String classifierName){
		JSONArray jsonArr=new JSONArray();		
		JSONObject finalObj = new JSONObject();	
		finalObj.put(Common.ARFF, arffName);
		finalObj.put(Common.CLASSIFIER, classifierName);
		writeFile(path, finalObj);
	}
	
	public void setFilterSettings(String file){
		
		JSONObject filterObj=readFile(file);
		JSONArray filters = (JSONArray) filterObj.get(Common.FILTERS);
		Iterator<Map<String,String>> iterator = filters.iterator();
	
		while (iterator.hasNext()) {
			Map<String,String> filter=iterator.next();
			String filterName=filter.remove(Common.FILTER);
			filterManager.updateFilterSetting(filterName, filter);
		}

	}
	
	
	public void loadExamples(String file){
		
		JSONObject featureList=readFile(file);
		JSONArray classes = (JSONArray) featureList.get(Common.CLASSES);	
		Iterator<String> classIterator = classes.iterator();
		int classId=0;
		while (classIterator.hasNext()) {
			String classLabel= classIterator.next();
			exampleManager.addClass(classId);
			exampleManager.setClassLabel(classId, classLabel);
			classId++;
		}

		JSONArray features = (JSONArray) featureList.get(Common.FEATURESLIST);
		Iterator<Map<String,String>> iterator = features.iterator();
		
		while (iterator.hasNext()) {
			Map<String,String> feature=iterator.next();
			int classNum=Integer.parseInt(feature.get(Common.CLASS));
			int sliceNum=Integer.parseInt(feature.get(Common.SLICE));
			String zipFile= feature.get(Common.ROI_ZIP_PATH);
			List<Roi> rois=dataManager.openZip(zipFile);
			exampleManager.addExampleList(classNum, rois, sliceNum);
		}
	}
	
	

	private void writeFile(String path, JSONObject obj){
		try {

			FileWriter file = new FileWriter(path);
			file.write(obj.toJSONString());
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.print(obj);

	}
	
	
	
	
	private JSONObject readFile(String file){
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(file));

			JSONObject jsonObject = (JSONObject) obj;
			
			return jsonObject;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

}

