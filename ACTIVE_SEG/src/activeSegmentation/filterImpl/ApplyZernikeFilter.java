package activeSegmentation.filterImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import activeSegmentation.IFilter;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ijaux.scale.ZernikeMoment;
import ijaux.scale.ZernikeMoment.Complex;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class ApplyZernikeFilter extends RecursiveTask<Complex>{
	ImageProcessor imp;
	private ZernikeMoment zm;
	private IFilter filter;
	
	public ApplyZernikeFilter(IFilter filter, ImageProcessor imp){
		this.imp=imp;
		this.filter=filter;
	}
	
	@Override
	protected Complex compute() {
		// TODO Auto-generated method stub
		//zm.count++;
		//zm.ss++;
		/*synchronized (filter) {
            // ensure that zm's initialization is complete
            while (filter==null) {
                // not yet initialized
                try {
					zm.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }*/
//		return zm.extractZernikeMoment(imp);
		return filter.applyFilter(imp);
	}
	
	public static ArrayList<Complex> ComputeValues(ImagePlus originalImage, IFilter filter) {
		// TODO Auto-generated method stub
		Instances Data;
    	ArrayList<Complex> arr= new ArrayList<Complex>();
/*    	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    	for(int k=0;k<degree;k++){
			for(int l=0;l<order;l++){	
				if((k-l)%2==0){
				     attributes.add(new Attribute("Z"+k+","+l));
				}
			}
    	}	
		Data=new Instances("Zernike Moment", attributes, 30);
		
*/    	
    	synchronized(filter) {
    		// synchronize the initialization of zmtemp, because other threads will
            // check it
    		Complex rv = filter.applyFilter(originalImage.getImageStack().getProcessor(1));
    		arr.add(rv);
//    		DenseInstance insta=new DenseInstance(1.0,rv);
//          Data.add(insta);  
    		// wake up any threads waiting on the initialization
    		filter.notifyAll();
    	}
		long as=System.currentTimeMillis();
		
		List<ApplyZernikeFilter> tasks = new ArrayList<>();
		for(int i=2; i<originalImage.getStackSize(); i++){
			ApplyZernikeFilter ezm =new ApplyZernikeFilter(filter, originalImage.getImageStack().getProcessor(i));
            tasks.add(ezm);
			ezm.fork();
		}
		
		if (tasks.size() > 0) {
			for (ApplyZernikeFilter task : tasks) {
                Complex rv=task.join();
        		arr.add(rv);
 //               DenseInstance insta=new DenseInstance(1.0,rv);
  //              Data.add(insta);    
            }
		}
/*		System.out.println(Data);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter("/home/mg/zernike.arff"));
			writer.write(Data.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		 
		long aa=System.currentTimeMillis();
		System.out.println(aa-as);
		return arr;
	}
}
