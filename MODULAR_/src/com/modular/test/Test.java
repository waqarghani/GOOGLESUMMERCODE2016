package com.modular.test;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import weka.classifiers.functions.SMO;
import weka.core.Instances;

import com.modular.classifier.WekaClassifier;
import com.modular.dataset.WekaDataSet;
import com.modular.filter.Impl.FilterManager;
import com.modular.filter.core.IClassifier;
import com.modular.filter.core.IExampleManager;
import com.modular.filter.core.ITrainingInstance;
import com.modular.instancecreator.CreateTrainingInstance;
import com.modular.instancecreator.ExampleManagerImpl;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {

			String home = "C://Program Files//ImageJ//plugins//test//";
			File f=new File(args[0]);

			if (f.exists() && f.isDirectory() ) {
				System.setProperty("plugins.dir", args[0]);
				new ImageJ();

				/*-------------- LOADING FILTERS* ------------------*/
				System.out.println("-------------- LOADING FILTERS* ------------------");
				FilterManager filterManager=new FilterManager();
				filterManager.loadFilters(home);
				
				/*-------------- GETTING AVAILABLE FILTER LIST* ------------------*/

				Set<String> filterList= filterManager.getFilters();
				System.out.println("-------------- AVAIL FILTERS* --------------------");
				System.out.println(filterList.size());
				System.out.println();
				for(String s: filterList){
					
					System.out.println("-------------- FILTER"+s+" ------------------");					
					System.out.println(" GET DEFAULT SETTINGS");
					Map<String, String> settingsMap= filterManager.getFilterSetting(s);
					for(String key:settingsMap.keySet()){
						System.out.println(key+ ":  "+ settingsMap.get(key) );
					}
				}


				/*--------------LOAD IMAGE---*/
				
				System.out.println("-------------- LOADING IMAGE* ------------------");
				final ImagePlus blob = loadFromResource( "/blobs.tif" );

				int size= blob.getStackSize();
				System.out.println("STACK SIZE -"+size);


				/* -------------LOAD EXAMPLES----*/
				/*- -binary Learning Problem--*/
				System.out.println("-------------- LOADING EXAMPLES * --------------");
				int classes=2;
				IExampleManager exampleManager= new ExampleManagerImpl(size,classes);

				exampleManager.loadZippedExample(Test.class.getResource("/RoiSet_1.zip").getPath(), 0, 1);
				exampleManager.loadZippedExample(Test.class.getResource("/RoiSet_2.zip").getPath(), 1, 1);

				System.out.println("EXAMPLE LOADED");

				/*----------------APPLY FILTER & GENERATE INSTANCES FOR LEARNING------------*/
				System.out.println(" APPLY FILTERS & GENERATE INSTANCES FOR LEARNING * ");
				filterManager.applyFilters(blob);
				ImageStack filterStack= filterManager.getImageStack(1);
				System.out.println(filterStack.size());
				ImagePlus filterImage= new ImagePlus("filter Image", filterStack);

				filterImage.show();

				ITrainingInstance trainingInstance= new CreateTrainingInstance(blob, filterManager, exampleManager);

				Instances instance=trainingInstance.createTrainingInstances();

				System.out.println("INSTANCE SIZE: "+instance.size());	

				/*------------LEARNING----------- */

				System.out.println("-------------- LEARNING * ------------------");
				int percent=80;
				int trainSize = (int) Math.round(instance.numInstances() * percent
						/ 100);
				int testSize = instance.numInstances() - trainSize;
				Instances train = new Instances(instance, 0, trainSize);
				Instances test = new Instances(instance, trainSize, testSize);
				IClassifier classifier= new WekaClassifier();
				classifier.setClassifier(new SMO());
				classifier.buildClassifier(new WekaDataSet(train));    
				
				System.out.println("---------------------------------------------");	
				
				System.out.println("-------------- EVALUATION * ------------------");
				classifier.testModel(new WekaDataSet(test));



			} else {
				throw new IllegalArgumentException();
			}
		}
		catch (Exception ex) {
			IJ.log("plugins.dir misspecified\n");
			ex.printStackTrace();
		}
	}


	private static ImagePlus loadFromResource(final String path) {
		final URL url = Test.class.getResource(path);
		if (url == null) return null;
		if ("file".equals(url.getProtocol())) return new ImagePlus(url.getPath());
		return new ImagePlus(url.toString());
	}


}
