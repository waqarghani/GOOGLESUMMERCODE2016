package activeSegmentation.filterImpl;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ijaux.scale.ZernikeMoment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import activeSegmentation.IFilter;

public class ApplyFilter extends RecursiveTask<Map>{

	ImageProcessor imp;
	private IFilter filter;
	int index=0;
	public ApplyFilter(IFilter filter, ImageProcessor imp, int index){
		this.imp=imp;
		this.filter=filter;
		this.index=index;
	}
	
	@Override
	protected Map compute() {
		// TODO Auto-generated method stub
		Map map = new Map(index, (ImageStack)filter.applyFilter(imp));
		return map;
	}
	
	public static HashMap<Integer, ImageStack> ComputeFeatures(ImagePlus originalImage, IFilter filter) {
    	HashMap<Integer, ImageStack> arr= new HashMap<Integer, ImageStack>();
    	List<ApplyFilter> tasks = new ArrayList<ApplyFilter>();
		for(int i=1;i<=originalImage.getStackSize();i++){
			arr.put(i, (ImageStack) filter.applyFilter(originalImage.getImageStack().getProcessor(i)));
		}
    	/*for(int i=1; i<=originalImage.getStackSize(); i++){
			ApplyFilter ezm =new ApplyFilter(filter, originalImage.getImageStack().getProcessor(i), i);
            tasks.add(ezm);
			ezm.fork();
		}
		
		if (tasks.size() > 0) {
			for (ApplyFilter task : tasks) {
                Map map=task.join();
        		arr.put(map.index,map.imgStack);
        		System.out.println("ImageSize"+arr.get(map.index).getSize());
        		
 //               DenseInstance insta=new DenseInstance(1.0,rv);
  //              Data.add(insta);    
            }
		}*/
		return arr;
	}

}
class Map{
	int index;
	ImageStack imgStack;
	Map(int index, ImageStack imgStack){
		this.index = index;
		this.imgStack = imgStack;
	}
}
