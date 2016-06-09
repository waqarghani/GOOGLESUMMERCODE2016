package activeSegmentation.feature;


import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;







import activeSegmentation.Common;
import activeSegmentation.IExampleManager;
import activeSegmentation.IFilterManager;
import weka.core.Attribute;
import weka.core.Instances;
import ij.IJ;
import ij.gui.Roi;

public class FeatureExtraction {


	private IFilterManager filterManager;
	private IExampleManager exampleManager;
	private Instances loadedTrainingData;


	private FeatureExtraction(IFilterManager filterManager, IExampleManager exampleManager){

		this.filterManager= filterManager;
		this.exampleManager= exampleManager;
	}
	/**
	 * Create training instances out of the user markings
	 * @return set of instances (feature vectors in Weka format)
	 */
	public Instances createTrainingInstance()
	{
		ArrayList<Attribute> attributes = createFeatureHeader();
		attributes.add(new Attribute(Common.CLASS, addClasstoHeader()));

		// create initial set of instances
		final Instances trainingData =  new Instances(Common.INSTANCE_NAME, attributes, 1 );
		// Set the index of the class attribute
		trainingData.setClassIndex(filterManager.getNumOfFeatures());
		for(int classIndex = 0; classIndex < exampleManager.getNumOfClasses(); classIndex++)
		{
			int nl = 0;

			// Read all lists of examples
			for(int sliceNum = 1; sliceNum <= filterManager.getOriginalImageSize(); sliceNum ++)
				for(int j=0; j < exampleManager.getExamples(classIndex, sliceNum-1).size(); j++)
				{       
					Roi r=  exampleManager.getExamples(classIndex, sliceNum-1).get(j);					
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
        final Polygon poly=r.getPolygon();
		final int x0 = rect.x;
		final int y0 = rect.y;

		final int lastX = x0 + rect.width;
		final int lastY = y0 + rect.height;

		for( int x = x0; x < lastX; x++ )
			for( int y = y0; y < lastY; y++ )				
			{
				
				if(poly.contains(new Point(x0, y0))){
					trainingData.add( filterManager.createInstance(x, y, classIndex, sliceNum-1) );
				}				
				// increase number of instances for this class
				numInstances ++;
			}
		return numInstances;		
	}



	private  ArrayList<Attribute> createFeatureHeader(){
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (int i=1; i<=filterManager.getNumOfFeatures(); i++)
		{
			String attString = filterManager.getLabel(i);
			attributes.add(new Attribute(attString));
		}

		return attributes;
	}

	private ArrayList<String> addClasstoHeader(){
		ArrayList<String> classes=null;

		if(null == this.loadedTrainingData)
		{
			classes = new ArrayList<String>();
			for(int i = 0; i < exampleManager.getNumOfClasses() ; i ++)
			{			
				for(int n=0; n<filterManager.getOriginalImageSize(); n++)
				{
					if(classes.contains(exampleManager.getClassLabels()[i]) == false)
						classes.add(exampleManager.getClassLabels()[i]);
				}
			}			
		}

		return classes;

	}


}
