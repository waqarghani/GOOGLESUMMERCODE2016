package activeSegmentation;

import java.util.ArrayList;
import java.util.List;

import ij.ImageStack;

public class FeatureType {
	private List<ImageStack> tempStack;
	private ImageStack finalStack;
	private double[] zernikeMoments;
	
	public FeatureType(){
		tempStack= new ArrayList<ImageStack>();
	}
	public void add(ImageStack imageStack){
		this.tempStack.add(imageStack);
	}
	
	public void add(double[] zernikeMoments){
		this.zernikeMoments = zernikeMoments;
	}
	
	public void combineStacks(List<ImageStack> imageStackList)
	{
		finalStack=new ImageStack(imageStackList.get(0).getWidth(),imageStackList.get(0).getHeight());
		for(ImageStack stack: imageStackList){
			for(int i=1; i<=stack.getSize(); i++){
				finalStack.addSlice(stack.getSliceLabel(i), stack.getProcessor(i));
			}
		}
	}

	public List<ImageStack> gettempStack(){
		return tempStack;
	}
	public ImageStack getfinalStack(){
		return finalStack;
	}
}
