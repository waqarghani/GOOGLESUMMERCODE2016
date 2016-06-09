package activeSegmentation;


import weka.classifiers.Classifier;
import weka.core.Instance;



public interface IClassifier {
	
	/**
     * It builds the classifier on the instances.
     *
     * @param instances The instances to use
     * @throws Exception The exception that will be launched.
     */
    public void buildClassifier(IDataSet instances) throws Exception;

    /**
     * @param instance The instance
     * @return the distribution for instance
     */
    public double[] distributionForInstance(Instance instance);
    
    /**
    *
    * @param instance The instance to classify.
    * @return The predicted label for the classifier.
    * @throws Exception The exception that will be launched.
    */
    public double classifyInstance(Instance instance) throws Exception;

    /**
    *
    * @param classifier
    */
	public void setClassifier(Classifier classifier);

	
	 /**
     * Evaluates the classifier using the test dataset and stores the evaluation.
     *
     * @param instances The instances to test
     * @return The evaluation
     */
 
     public void testModel(IDataSet instances);
    
    /**
     * @return The copy of the IClassifier used.
     * @throws Exception The exception that will be launched.
     */
    public IClassifier makeCopy() throws Exception;


}
