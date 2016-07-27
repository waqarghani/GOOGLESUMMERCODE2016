package activeSegmentation.io;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaInfo {

	private String comment="Default Comment";
	private String createdDate;
	private String modifyDate;
	private String path;
	private List<Map<String,String>> filters= new ArrayList<Map<String,String>>();
	private Map<String,String> keywordList= new HashMap<String, String>();
	private List<FeatureInfo> featureList= new ArrayList<FeatureInfo>();
	private Map<String,String> learning;


	//private int stage;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<Map<String, String>> getFilters() {
		return filters;
	}
	public void setFilters(List<Map<String, String>> filters) {
		this.filters = filters;
	}
	public List<FeatureInfo> getFeatureList() {
		return featureList;
	}
	public void setFeatureList(List<FeatureInfo> featureList) {
		this.featureList = featureList;
	}
	public Map<String, String> getKeywordList() {
		return keywordList;
	}
	public void setKeywordList(Map<String, String> keywordList) {
		this.keywordList = keywordList;
	}
	public Map<String, String> getLearning() {
		return learning;
	}
	public void setLearning(Map<String, String> learning) {
		this.learning = learning;
	}

	public void addFeature(FeatureInfo featureInfo){

		featureList.add(featureInfo);
	}

	public void resetFeatureInfo(){

		featureList.clear();
	}
	@Override
	public String toString() {
		return "MetaInfo [comment=" + comment + ", createdDate=" + createdDate
				+ ", modifyDate=" + modifyDate + ", path=" + path
				+ ", filters=" + filters + ", keywordList=" + keywordList
				+ ", featureList=" + featureList + ", learning=" + learning
				+ "]";
	}



}
