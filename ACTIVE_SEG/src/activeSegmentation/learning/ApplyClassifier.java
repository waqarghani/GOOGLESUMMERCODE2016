package activeSegmentation.learning;

import ij.IJ;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import weka.core.Instances;
import activeSegmentation.IClassifier;
import activeSegmentation.IDataSet;

public class ApplyClassifier {

	private IClassifier classifier;
	private IDataSet testDataSet;
	
	
	public void applyClassifier(){
		
	}
	
	public void generateProbabilityMask(){
		
	}
	
	
	
	/**
	 * Classify instances concurrently
	 * 
	 * @param data set of instances to classify
	 * @param classifier current classifier
	 * @param counter auxiliary counter to be able to update the progress bar
	 * @return classification result
	 */
	private static Callable<double[][]> classifyInstances(
			final Instances data,
			final IClassifier classifier,
			final AtomicInteger counter)
	{
		if (Thread.currentThread().isInterrupted()) 
			return null;	
		return new Callable<double[][]>(){

			public double[][] call(){

				final int numInstances = data.numInstances();

				final double[][] classificationResult;

					classificationResult = new double[1][numInstances];

				//System.out.println(numInstances);
				for (int i=0; i<numInstances; i++)
				{
					//System.out.println(numInstances);
					try{

						if (0 == i % 4000)
						{
							if (Thread.currentThread().isInterrupted()) 
								return null;
							counter.addAndGet(4000);
						}

												
					classificationResult[0][i] = classifier.classifyInstance(data.get(i));


					}catch(Exception e){

						IJ.showMessage("Could not apply Classifier!");
						e.printStackTrace();
						return null;
					}
				}
				return classificationResult;
			}
		};
	}
}
