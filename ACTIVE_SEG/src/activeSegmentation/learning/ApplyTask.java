package activeSegmentation.learning;

import java.util.concurrent.RecursiveAction;

import activeSegmentation.IClassifier;
import weka.core.Instances;

public class ApplyTask extends RecursiveAction{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int workLoad = 10000;
	private Instances instances;
	private double[] classificationResult;
	private IClassifier iClassifier;
	private int mStart;
	private int mLength;

	public ApplyTask(Instances instances,int mStart,int length, double[] classificationResult, 
			IClassifier classifier) {
		this.instances = instances;
		this.classificationResult= classificationResult;
		this.iClassifier=classifier;
		this.mStart= mStart;
		this.mLength= length;


	}

	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		if (mLength < workLoad) {
			classifyPixels();
			return;
		}

		int split = mLength / 2;

		invokeAll(new ApplyTask(instances, mStart, split, classificationResult,iClassifier),
				new ApplyTask(instances, mStart + split, mLength - split, 
						classificationResult,iClassifier));

	}

	private void classifyPixels(){
		IClassifier classifierCopy=null;
		try {
			classifierCopy = (IClassifier) (iClassifier.makeCopy());
		} catch (Exception e) {

			e.printStackTrace();
		}

		System.out.println(mStart+"---"+mLength);
		Instances testInstances= new Instances(instances, mStart, mLength);
		for (int index = 0; index < testInstances.size(); index++)
		{
			try {
				classificationResult[mStart+index]=classifierCopy.
						classifyInstance(testInstances.get(index));
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}


}
