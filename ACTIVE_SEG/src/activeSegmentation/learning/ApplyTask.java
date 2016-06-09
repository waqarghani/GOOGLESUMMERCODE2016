package activeSegmentation.learning;

import java.util.concurrent.RecursiveTask;

public class ApplyTask extends RecursiveTask<double[][]>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long workLoad = 0;

    public ApplyTask(long workLoad) {
        this.workLoad = workLoad;
    }

	@Override
	protected double[][] compute() {
		// TODO Auto-generated method stub
		
		 if(this.workLoad > 4000) {
			 
		 }
		return null;
	}

	

}
