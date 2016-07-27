package activeSegmentation.metadatamodel;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import activeSegmentation.Common;
import activeSegmentation.IExampleManager;
import activeSegmentation.IFilterManager;
import activeSegmentation.ILearningManager;

public class FilterMetadata {

	private IFilterManager filterManager;
	private IExampleManager exampleManager;
	private ILearningManager learningManager;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public FilterMetadata(IFilterManager filterManager,IExampleManager exampleManager , ILearningManager learningManager ){
		this.filterManager=filterManager;
		this.exampleManager= exampleManager;
		this.learningManager= learningManager;
	}

	private JSONObject createDescription(String path){
		JSONObject descObj = new JSONObject();
		descObj.put(Common.COMMENT,Common.DEFAULTCOMMENT);
		descObj.put(Common.CREATEDATE,dateFormat.format(new Date()));
		descObj.put(Common.MODIFYDATE,dateFormat.format(new Date()));
		descObj.put(Common.PATH,path);
		return descObj;
	}

	private JSONObject setDescription(JSONObject descObj){
		descObj.put(Common.COMMENT,Common.DEFAULTCOMMENT);
		descObj.put(Common.MODIFYDATE,dateFormat.format(new Date()));
		//descObj.put(Common.PATH,path);
		return descObj;
	}
	
	public void storeMetadata(String path){
	
		JSONObject finalObject = new JSONObject();
		JSONArray finalData = new JSONArray();
		finalData.add(createDescription(path));
		//finalData.add(filterManager.saveFilters(path));
		//finalData.add(exampleManager.saveExamples(path));
	  //  finalData.add(learningManager.saveLearning());
	    
	    finalObject.put("MetaData",finalData);
	    writeFile(path, finalObject);
	}
	
	public void setMetadataFile(String path){
		JSONObject jsonObject= readFile(path);
		
		JSONArray metaData = (JSONArray) jsonObject.get("MetaData");
		Iterator<JSONObject> iterator = metaData.iterator();
		int i=0;
		while (iterator.hasNext()) {
			JSONObject obj=iterator.next();
			if(i==0){
				setDescription(obj);
			}
			if(i==1){
				//filterManager.setFilterSettings(obj);
			}
			
			if(i==2){
			//	exampleManager.loadExamples(obj);
			}
			if(i==3){
				//learningManager.
			}
			i++;
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

