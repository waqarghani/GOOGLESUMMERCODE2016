package com.modular.instancecreator;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;




import java.awt.Rectangle;
import java.util.ArrayList;




import com.modular.filter.Impl.FilterManager;
import com.modular.filter.core.IExampleManager;
import com.modular.filter.core.ITrainingInstance;

import weka.core.Attribute;
import weka.core.Instances;

public class CreateTrainingInstance implements ITrainingInstance {



	private ImagePlus image;
	private FilterManager filterManager;
	private IExampleManager exampleManager;
	


	public CreateTrainingInstance(ImagePlus image,FilterManager filterManager, 
			IExampleManager exampleManager ){

		this.image= image;
        this.filterManager=filterManager;
        this.exampleManager=exampleManager;

	}


	/**
	 * Create training instances out of the user markings
	 * @return set of instances (feature vectors in Weka format)
	 */
	public Instances createTrainingInstances()
	{
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (int i=1; i<=filterManager.getNumOfFeatures(); i++)
		{
			String attString = filterManager.getLabel(i);
			attributes.add(new Attribute(attString));
		}

		final ArrayList<String> classes;

		System.out.println("Number of classes "+ exampleManager.getNumOfClasses());

		classes = new ArrayList<String>();
		for(int i = 0; i < exampleManager.getNumOfClasses() ; i ++)
		{			
			for(int n=0; n<this.image.getImageStackSize(); n++)
			{
				if(classes.contains(exampleManager.getClassLabels()[i]) == false)
					classes.add(exampleManager.getClassLabels()[i]);
			}
		}			


		attributes.add(new Attribute("class", classes));

		// create initial set of instances
		final Instances trainingData =  new Instances( "segment", attributes, 1 );
		// Set the index of the class attribute
		trainingData.setClassIndex(filterManager.getNumOfFeatures());

		IJ.log("Training input:");


		// For all classes
		for(int classIndex = 0; classIndex < exampleManager.getNumOfClasses(); classIndex++)
		{
			int nl = 0;
			// Read all lists of examples
			for(int sliceNum = 1; sliceNum <= this.image.getImageStackSize(); sliceNum ++)
				for(int j=0; j < exampleManager.getExamples(classIndex, sliceNum).size(); j++)
				{
					Roi r = exampleManager.getExamples(classIndex, sliceNum).get(j);


					// for regular rectangles
					if ( r.getType() == Roi.RECTANGLE && r.getCornerDiameter() == 0 )					
						nl += addRectangleRoiInstances( trainingData, classIndex, sliceNum, r );					

				}

			IJ.log("# of pixels selected as " + exampleManager.getClassLabels()[classIndex] + ": " +nl);
		}

		if (trainingData.numInstances() == 0)
			return null;		

		return trainingData;
	}





	/**
	 * Add training samples from a rectangular roi
	 * 
	 * @param trainingData set of instances to add to
	 * @param classIndex class index value
	 * @param sliceNum number of 2d slice being processed
	 * @param r shape roi
	 * @return number of instances added
	 */
	private int addRectangleRoiInstances(
			final Instances trainingData, 
			int classIndex,
			int sliceNum, 
			Roi r) 
	{		
		int numInstances = 0;

		final Rectangle rect = r.getBounds();

		final int x0 = rect.x;
		final int y0 = rect.y;

		final int lastX = x0 + rect.width;
		final int lastY = y0 + rect.height;

		//final ImageStack fs = featureStackArray.get( sliceNum - 1 );

		for( int x = x0; x < lastX; x++ )
			for( int y = y0; y < lastY; y++ )				
			{
				trainingData.add( filterManager.createInstance(x, y, classIndex,sliceNum) );

				// increase number of instances for this class
				numInstances ++;
			}
		return numInstances;		
	}


}
