package activeSegmentation.feature;

import ij.gui.Roi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import activeSegmentation.Common;
import activeSegmentation.IExampleManager;



public class ExampleManagerImpl implements IExampleManager {

	/** array of lists of Rois for each slice (vector index) 
	 * and each class (arraylist index) of the training image */
	private Vector<ArrayList<Roi>> examples[];
	
	/** maximum number of classes (labels) allowed */
	private int MAX_NUM_CLASSES = Common.MAX_NUM_CLASSES;
	/** names of the current classes */
	private String[] classLabels = new String[MAX_NUM_CLASSES];
	
	/** current number of classes */
	private int numOfClasses = 0;
	private int stackSize=0;

	public ExampleManagerImpl(int stackSize, int numOfClasses)
	{
		this.stackSize=stackSize;
			
		// update list of examples
		examples = new Vector[stackSize];
		for(int i=0; i < stackSize; i++)
		{
			examples[i] = new Vector<ArrayList<Roi>>(MAX_NUM_CLASSES);
			
			System.out.println("ADDED");
		}
		
		for(int i=0; i<MAX_NUM_CLASSES; i++)
			this.classLabels[ i ] = new String(Common.CLASS + (i+1));
		
		for(int i=0; i<numOfClasses;i++ ){
			addClass();
		}
	}

	
	public void addExample(int classNum, Roi roi, int n) 
	{
		System.out.println(" IN WEKA Test Example"+ n+"class Number"+ classNum);
				
		examples[n-1].get(classNum).add(roi);
		System.out.println(" doneTest Example");
	}
	
	@Override
	public void addExampleList(int classNum, List<Roi> roiList, int n) {
		// TODO Auto-generated method stub
		for(Roi roi: roiList){
			if(processibleRoi(roi)){
				
				addExample(classNum, roi, n);
			}
		
		}
	}

	
	/**
	 * Return the list of examples for a certain class.
	 * 
	 * @param classNum the number of the examples' class
	 * @param n the slice number
	 */
	public List<Roi> getExamples(int classNum, int n) 
	{
		return examples[n-1].get(classNum);
	}
	
	/**
	 * Remove an example list from a class and specific slice
	 * 
	 * @param classNum the number of the examples' class
	 * @param nSlice the slice number
	 * @param index the index of the example list to remove
	 */
	public void deleteExample(int classNum, int nSlice, int index)
	{
		getExamples(classNum, nSlice).remove(index);
	}
	
	/**
	 * Get the current class labels
	 * @return array containing all the class labels
	 */
	@Override
	public String[] getClassLabels() 
	{
		return classLabels;
	}


	/**
	 * Set the name of a class.
	 * 
	 * @param classNum class index
	 * @param label new name for the class
	 */
	@Override
	public void setClassLabel(int classNum, String label) 
	{
		getClassLabels()[classNum] = label;
	}
	
	/**
	 * Set the current number of classes. Should not be used to create new
	 * classes. Use <link>addClass<\link> instead.
	 *
	 * @param numOfClasses the new number of classes
	 */
	@Override
	public void setNumOfClasses(int numOfClasses) {
		this.numOfClasses = numOfClasses;
	}

	/**
	 * Get the current number of classes.
	 *
	 * @return the current number of classes
	 */
	@Override
	public int getNumOfClasses() 
	{
		return numOfClasses;
	}
	
	/**
	 * Add new segmentation class.
	 */
	public void addClass()
	{
		
			for(int i=1; i <= stackSize; i++)
				examples[i-1].add(new ArrayList<Roi>());

		// increase number of available classes
		numOfClasses ++;
	}

	private boolean processibleRoi(Roi roi) {
		boolean ret=(roi!=null && !(roi.getType()==Roi.LINE || 
				roi.getType()==Roi.POLYLINE ||
				roi.getType()==Roi.ANGLE ||
				roi.getType()==Roi.FREELINE ||
				roi.getType()==Roi.POINT
				)
				);

		return ret;

	}


	public int getStackSize() {
		return stackSize;
	}


	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	
}
