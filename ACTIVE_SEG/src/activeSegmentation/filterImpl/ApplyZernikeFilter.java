package activeSegmentation.filterImpl;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import activeSegmentation.IFilter;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ijaux.scale.Pair;
import ijaux.scale.ZernikeMoment.Complex;

public class ApplyZernikeFilter extends RecursiveTask<Pair<Integer,Complex>>{
	ImageProcessor imp;
	private IFilter filter;
	private int index;
	public ApplyZernikeFilter(IFilter filter, ImageProcessor imp, int index){
		this.imp=imp;
		this.filter=filter;
		this.index=index;
	}
	
	@Override
	protected Pair<Integer,Complex> compute() {
		//Update index of a given image 
		filter.updatePosition(index);
		return filter.applyFilter(imp);
	}
	
	public static ArrayList<Pair<Integer,Complex>> ComputeValues(ImagePlus originalImage, IFilter filter) {
    	ArrayList<Pair<Integer,Complex>> arr= new ArrayList<Pair<Integer,Complex>>();    	
    	synchronized(filter) {
    		filter.updatePosition(1);
    		Pair<Integer,Complex> rv = filter.applyFilter(originalImage.getImageStack().getProcessor(1));
    		arr.add(rv);
    		filter.notifyAll();
    	}
		long as=System.currentTimeMillis();		
		List<ApplyZernikeFilter> tasks = new ArrayList<>();
		for(int i=2; i<originalImage.getStackSize(); i++){
			ApplyZernikeFilter ezm =new ApplyZernikeFilter(filter, originalImage.getImageStack().getProcessor(i),i);
            tasks.add(ezm);
			ezm.fork();
		}
		if (tasks.size() > 0) {
			for (ApplyZernikeFilter task : tasks) {
				Pair<Integer,Complex> rv=task.join();
        		arr.add(rv);
            }
		}
		long aa=System.currentTimeMillis();
		System.out.println(aa-as);
		return arr;
	}
}