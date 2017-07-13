package activeSegmentation.filterImpl;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ijaux.scale.Pair;
import ijaux.scale.ZernikeMoment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import activeSegmentation.IFilter;

public class ApplyFilter extends RecursiveTask<Pair<Integer,ImageStack>>{

	ImageProcessor imp;
	private IFilter filter;
	int index=0;
	public ApplyFilter(IFilter filter, ImageProcessor imp, int index){
		this.imp=imp;
		this.filter=filter;
		this.index=index;
	}
	
	@Override
	protected Pair<Integer,ImageStack> compute() {
		// TODO Auto-generated method stub
		filter.updatePosition(index);
		Pair<Integer,ImageStack> pr=filter.applyFilter(imp);
		return pr;
	}
	
	public static ArrayList<Pair<Integer,ImageStack>> ComputeFeatures(ImagePlus originalImage, IFilter filter) {
		ArrayList<Pair<Integer,ImageStack>> arr= new ArrayList<Pair<Integer,ImageStack>>();
    	List<ApplyFilter> tasks = new ArrayList<ApplyFilter>();
		/*
    	for(int i=1;i<=originalImage.getStackSize();i++){
			arr.put(i, (ImageStack) filter.applyFilter(originalImage.getImageStack().getProcessor(i)));
					//System.out.println("In apply filter testing"+img.getSize());

		}*/
    	
    	Long aa=System.currentTimeMillis();
    	
    	for(int i=1; i<=originalImage.getStackSize(); i++){
			ApplyFilter ezm =new ApplyFilter(filter, originalImage.getImageStack().getProcessor(i), i);
            tasks.add(ezm);
			ezm.fork();
		}
		
		if (tasks.size() > 0) {
			for (ApplyFilter task : tasks) {
                Pair<Integer,ImageStack> pr=task.join();
        		arr.add(pr);
            }
		}
		Long bb=System.currentTimeMillis();
    	System.out.println("Timing"+(bb-aa));
    	
		return arr;
	}

}
