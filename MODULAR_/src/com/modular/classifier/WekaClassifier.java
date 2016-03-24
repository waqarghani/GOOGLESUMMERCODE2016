package com.modular.classifier;

import ij.IJ;

import java.util.logging.Level;
import java.util.logging.Logger;












import com.modular.filter.core.IClassifier;
import com.modular.filter.core.IDataSet;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializedObject;

public class WekaClassifier implements IClassifier{

	/**
	 * Classifier from Weka.
	 */
	private Classifier classifier;

	/**
	 * Constructs the learning model from the dataset.
	 *
	 * @param instances The instances to train the classifier
	 * @throws Exception The exception that will be launched.
	 */
	@Override
	public void buildClassifier(IDataSet instances) throws Exception {

		classifier.buildClassifier(instances.getDataset());
		System.out.println(classifier.toString());
		IJ.log(classifier.toString());
	}

	/**
	 *
	 * @param instance The instance to classify.
	 * @return The predicted label for the classifier.
	 * @throws Exception The exception that will be launched.
	 */
	@Override
	public double classifyInstance(Instance instance) throws Exception {
		return classifier.classifyInstance(instance);
	}

	/**
	 * Returns the probability that has the instance to belong to each class.
	 * every instance of belonging to every class that the dataset contains.
	 *
	 * @param instance The instance to test.
	 * @return The probabilities for each class
	 */
	@Override
	public double[] distributionForInstance(Instance instance) {

		try {
			return classifier.distributionForInstance(instance);
		} catch (Exception e) {
			Logger.getLogger(WekaClassifier.class.getName()).log(
					Level.SEVERE, null, e);
		}
		return null;
	}


	/**
	 * Set the classifier to use.
	 *
	 * @param classifier The weka classifier.
	 */
	@Override
	public void setClassifier(Classifier classifier) {
		try {
			this.classifier = weka.classifiers.AbstractClassifier
					.makeCopy(classifier);
		} catch (Exception e) {
			Logger.getLogger(WekaClassifier.class.getName()).log(
					Level.SEVERE, null, e);
		}
	}



	@Override
	public IClassifier getClassifier() throws Exception
	{
		return (IClassifier) new SerializedObject(this).getObject();
	}

	@Override
	public void testModel(IDataSet instances) {


		try {
			Evaluation evaluator;
			evaluator = new Evaluation(new Instances(instances.getDataset(), 0));
			evaluator.evaluateModel(classifier, instances.getDataset());
			System.out.println("SUMMARY  -"+evaluator.toSummaryString());
			IJ.log(evaluator.toSummaryString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

}
