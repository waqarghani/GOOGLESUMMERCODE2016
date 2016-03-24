package com.modular.instancecreator;

import ij.gui.Roi;
import ij.io.RoiDecoder;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.modular.filter.core.IExampleManager;





public class ExampleManagerImpl implements IExampleManager {

	/** array of lists of Rois for each slice (vector index) 
	 * and each class (arraylist index) of the training image */
	private Vector<ArrayList<Roi>> examples[];
	/** maximum number of classes (labels) allowed */
	public static final int MAX_NUM_CLASSES = 10;
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
			this.classLabels[ i ] = new String("class " + (i+1));
		
		for(int i=0; i<numOfClasses;i++ ){
			addClass();
		}
	}

	
	public void addExample(int classNum, Roi roi, int n) 
	{
		//System.out.println(" IN WEKA Test Example"+ n+"class Number"+ classNum);
				
		examples[n-1].get(classNum).add(roi);
		//roiman.addRoi(roi);
	//	System.out.println(" doneTest Example");
	}
	
	@Override
	public void addExampleList(int classNum, List<Roi> roiList, int n) {
		// TODO Auto-generated method stub
		for(Roi roi: roiList){
			addExample(classNum, roi, n);
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


	

	@Override
	public List<Roi> loadZippedExample(String path, int classNum, int sliceNumber) {
		// TODO Auto-generated method stub
		
		List<Roi> roiList=openZip1(path, classNum, sliceNumber);
		addExampleList(classNum, roiList, sliceNumber);
		return roiList;
	}
	
	private List<Roi> openZip1(String path,int classNum, int n) { 
		Hashtable rois = new Hashtable();
		ZipInputStream in = null; 
		List<Roi> roiList= new ArrayList<Roi>();
		ByteArrayOutputStream out = null; 
		int nRois = 0; 
		try { 
			in = new ZipInputStream(new FileInputStream(path)); 
			byte[] buf = new byte[1024]; 
			int len; 
			ZipEntry entry = in.getNextEntry(); 
			while (entry!=null) { 
				String name = entry.getName();
				if (name.endsWith(".roi")) { 
					out = new ByteArrayOutputStream(); 
					while ((len = in.read(buf)) > 0) 
						out.write(buf, 0, len); 
					out.close(); 
					byte[] bytes = out.toByteArray(); 
					RoiDecoder rd = new RoiDecoder(bytes, name); 
					Roi roi = rd.getRoi(); 
					if (roi!=null) { 
						name = name.substring(0, name.length()-4); 
						name = getUniqueName(name,rois);  
						rois.put(name, roi); 
						roiList.add(roi);
						nRois++;
					} 
				} 
				entry = in.getNextEntry(); 
			} 
			in.close(); 
		} catch (IOException e) {
			
		} finally {
			if (in!=null)
				try {in.close();} catch (IOException e) {}
			if (out!=null)
				try {out.close();} catch (IOException e) {}
		}
		if(nRois==0)
				System.out.println("ERROR OCCURED");
		
		return roiList;
	} 
	
	private String getUniqueName(String name,Hashtable rois) {
		String name2 = name;
		int n = 1;
		Roi roi2 = (Roi)rois.get(name2);
		while (roi2!=null) {
			roi2 = (Roi)rois.get(name2);
			if (roi2!=null) {
				int lastDash = name2.lastIndexOf("-");
				if (lastDash!=-1 && name2.length()-lastDash<5)
					name2 = name2.substring(0, lastDash);
				name2 = name2+"-"+n;
				n++;
			}
			roi2 = (Roi)rois.get(name2);
		}
		return name2;
	}



	
}
