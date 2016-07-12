package activeSegmentation;

import java.util.List;

public interface IEvaluation {
	
	public List<String> getMetrics();
	public String testModel(IDataSet instances,List<String> selection);
}
