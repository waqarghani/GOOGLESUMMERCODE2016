package activeSegmentation.filterImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ijaux.scale.ZernikeMoment;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class ExtractZernikeMoment extends RecursiveTask<double[]>{
	ImageProcessor imp;
	private ZernikeMoment zm;

	
	public ExtractZernikeMoment(ImageProcessor imp, ZernikeMoment zm){
		this.imp=imp;
		this.zm = zm;
	}
	
	@Override
	protected double[] compute() {
		// TODO Auto-generated method stub
		//zm.count++;
		//zm.ss++;
		synchronized (zm) {
            // ensure that zm's initialization is complete
            while (zm.rv==null) {
                // not yet initialized
                try {
					zm.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
		return zm.extractZernikeMoment(imp);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Instances Data;

		String path="/home/mg/Downloads/tifs/image.tif";
    	ImagePlus imp=IJ.openImage(path);
    	int degree=8;
    	int order=4;
    	ZernikeMoment zmtemp =new ZernikeMoment(8,4);
    	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    	for(int k=0;k<degree;k++){
			for(int l=0;l<order;l++){	
				if((k-l)%2==0){
				     attributes.add(new Attribute("Z"+k+","+l));
				}
			}
    	}	
		Data=new Instances("Zernike Moment", attributes, 30);
		
    	synchronized(zmtemp) {
    		// synchronize the initialization of zmtemp, because other threads will
            // check it
    		double[] rv = zmtemp.extractZernikeMoment(imp.getImageStack().getProcessor(1));
    		DenseInstance insta=new DenseInstance(1.0,rv);
            Data.add(insta);  
    		// wake up any threads waiting on the initialization
    		zmtemp.notifyAll();
    	}
		long as=System.currentTimeMillis();
		
		List<ExtractZernikeMoment> tasks = new ArrayList<>();
		for(int i=2; i<imp.getStackSize(); i++){
			ExtractZernikeMoment ezm =new ExtractZernikeMoment(imp.getImageStack().getProcessor(i), zmtemp);
            tasks.add(ezm);
			ezm.fork();
		}
		
		if (tasks.size() > 0) {
			for (ExtractZernikeMoment task : tasks) {
                double[] rv=task.join();
                System.out.println(rv.length);
                DenseInstance insta=new DenseInstance(1.0,rv);
                Data.add(insta);    
            }
		}
		System.out.println(Data);
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
		 
		long aa=System.currentTimeMillis();
		System.out.println(aa-as);
		

	}
}
