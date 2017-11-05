package activeSegmentation;

import java.util.ArrayList;
import java.util.List;

import ij.ImageStack;
import ijaux.scale.ZernikeMoment.Complex;

/*
 * This Class used for storing Class level features and Pixel level features.
 */
public class FeatureType {
	
	// Hold stack of images which is getting after apply filter on current Slice 
	private List<ImageStack> tempStack;
	
	// Hold all the stack of images which is getting after apply filter on each image.
	private ImageStack finalStack = null;
	
	// Hold Zernike Polynomials Values which is getting after apply filter on each image.
	private Complex zernikeMoments;

	public FeatureType(){
		tempStack= new ArrayList<ImageStack>();
	}
	
	public void add(ImageStack imageStack){
		this.tempStack.add(imageStack);
	}	
	
	public void add(Complex zernikeMoments){
		this.zernikeMoments = zernikeMoments;
	}
	
	/*
	 * It combines stacks into final stack which is getting after applying filter on each images.
	 */
	public void combineStacks(ImageStack currentFilterImageStack){
		if(finalStack==null)
		{	
			finalStack=new ImageStack(currentFilterImageStack.getWidth(),currentFilterImageStack.getHeight());
			for(int i=1; i<=currentFilterImageStack.getSize(); i++){
				finalStack.addSlice(currentFilterImageStack.getSliceLabel(i), currentFilterImageStack.getProcessor(i));
			}
		}else{
			for(int i=1; i<=currentFilterImageStack.getSize(); i++){
				finalStack.addSlice(currentFilterImageStack.getSliceLabel(i), currentFilterImageStack.getProcessor(i));
			}
		}
	}
	
	public List<ImageStack> gettempStack(){
		return tempStack;
	}
	
	public ImageStack getfinalStack(){
		return finalStack;
	}
	
	public Complex getzernikeMoments(){
		return zernikeMoments;
	}
}
