package activeSegmentation;

import java.util.ArrayList;
import java.util.List;

import ij.ImageStack;
import ijaux.scale.ZernikeMoment.Complex;

public class FeatureType {
	private List<ImageStack> tempStack;
	private ImageStack finalStack = null;
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
