import java.util.ArrayList;

import activeSegmentation.Common;
import activeSegmentation.learning.LVQ;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class example {
	private static  ArrayList<Attribute> createFeatureHeader(){
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		int degree = 4;
		int k=0;
		while(k<=degree){
			for(int l=0;l<=k;l++){	
				if((k-l)%2==0){
				     attributes.add(new Attribute("Z"+k+","+l));
				     //++classindex;
				     if(l!=0){
				    	 attributes.add(new Attribute("Z"+k+",-"+l));
				   //  ++classindex;
				     }	 
				}
			}
			k++;
		}
		return attributes;
	}
	
	public static void main(String[] args) throws Exception{
		ArrayList<Attribute> attributes = createFeatureHeader();
		
		ArrayList<String> label = new ArrayList<String>();
		label.add("class1");
		label.add("class2");
		label.add("class3");
		attributes.add(new Attribute(Common.CLASS, label));
		Instances trainingData = new Instances(Common.INSTANCE_NAME, attributes, 1 );
		//trainingData.setClassIndex(15);
		trainingData.add(clac1());
		trainingData.add(clac2());
		trainingData.add(clac1());
		
		LVQ l=new LVQ();
		//System.out.println(trainingData);
		l.buildClusterer(trainingData);
		//
		System.out.println(l.clusterInstance(clac1()));
	}
	
	public static DenseInstance clac1(){
		double[] arr=new double[16];

		arr[0]=15336178.274083;
		arr[1]=5453928.416547;
		arr[2]=5501378.704379;
		arr[3]=-5041502.610245;
		arr[4]=-47346.645001;
		arr[5]=3890940.541617;
		arr[6]=-4152380.174964;
		arr[7]=-4157357.336523;
		arr[8]=-1412307.427417;
		arr[9]=1357285.088363;
		arr[10]=-1306894.082555;
		arr[11]=39602.468131;
		arr[12]=-3988073.88832;
		arr[13]=-1052640.22706;
		arr[14]=-26084.611457;
		arr[15]=(double)0;
		return new DenseInstance(1.0,arr);
	}
	
	public static DenseInstance clac2(){
		double[] arr=new double[16];

		arr[0]=13429884.027705;
		arr[1]=4722636.043672;
		arr[2]=4773664.662441;
		arr[3]=-4546146.598138;
		arr[4]=-64253.916547;
		arr[5]=3361818.536303;
		arr[6]=-3633031.005658;
		arr[7]=-3643419.67225;
		arr[8]=-1252078.605175;
		arr[9]=1140934.350451;
		arr[10]=-968617.5636;
		arr[11]=50430.485514;
		arr[12]=-3466354.648281;
		arr[13]=-910600.031379;
		arr[14]=-77227.251272;
		arr[15]=(double)2;
		return new DenseInstance(1.0,arr);
	}
	
	public static DenseInstance clac3(){
		double[] arr=new double[16];

		arr[0]=13429884.027705;
		arr[1]=4722636.043672;
		arr[2]=4773664.662441;
		arr[3]=-4546146.598138;
		arr[4]=-64253.916547;
		arr[5]=3361818.536303;
		arr[6]=-3633031.005658;
		arr[7]=-3643419.67225;
		arr[8]=-1252078.605175;
		arr[9]=1140934.350451;
		arr[10]=-968617.5636;
		arr[11]=50430.485514;
		arr[12]=-3466354.648281;
		arr[13]=-910600.031379;
		arr[14]=-77227.251272;
		arr[15]=(double)0;
		return new DenseInstance(1.0,arr);
	}
	
	public static DenseInstance clac4(){
		double[] arr=new double[16];

		arr[0]=15336178.274083;
		arr[1]=5453928.416547;
		arr[2]=5501378.704379;
		arr[3]=-5041502.610245;
		arr[4]=-47346.645001;
		arr[5]=3890940.541617;
		arr[6]=-4152380.174964;
		arr[7]=-4157357.336523;
		arr[8]=-1412307.427417;
		arr[9]=1357285.088363;
		arr[10]=-1306894.082555;
		arr[11]=39602.468131;
		arr[12]=-3988073.88832;
		arr[13]=-1052640.22706;
		arr[14]=-26084.611457;
		//arr[15]=(double)1;
		return new DenseInstance(1.0,arr);
	}
	
}
