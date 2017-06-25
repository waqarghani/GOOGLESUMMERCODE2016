package ijaux.scale;

import java.util.ArrayList;

import ij.process.ImageProcessor;

public class ZernikeMoment {
	int degree;
	int order;
	int centerX;
	int centerY;
	double radius;
	public static Zps[] zps;

	public ZernikeMoment(int degree, int order){
		this.degree=degree;
		this.order=order;
	}
	
	public double calculateRadial(double r, int m, int n, Zps zps){
		
		if(n<m || n<0 || m<0)
			return 0;
		
		//Check if radial value for m and n already present.
		if(zps.get(m, n)!=0)
			return zps.get(m, n);
		
		if(n==0&&m==0)
			return 1;
		
		if((n-m)%2==0)
			return (r*(calculateRadial(r,Math.abs(m-1),n-1,zps)+calculateRadial(r,m+1,n-1,zps))-calculateRadial(r,m,n-2,zps));	
		
		else
			return 0;
	}
	
	public void calculateRadius(ImageProcessor ip){
		centerX = ip.getWidth() / 2;
        centerY = ip.getHeight() / 2;
        final int max = Math.max(centerX, centerY);
        radius = Math.sqrt(2 * max * max);
	}

	public double[] extractZernikeMoment(ImageProcessor ip){
		System.out.println("Start Zernike moment extraction process");
		calculateRadius(ip);
		
		ArrayList<Double> real = null; 
    	ArrayList<Double> imag = null;
        
    	zps=new Zps[ip.getHeight()*ip.getWidth()];
    	int index=0;
        for(int i=0;i<ip.getHeight();i++){
        	for(int j=0;j<ip.getWidth();j++){
        		final int x = j-centerX;
        		final int y = i-centerY;
        		final double r = Math.sqrt((x * x) + (y * y)) / radius;
        		//For each pixel create zps object
        		zps[index]=new Zps(order,degree);
        		
        		real=new ArrayList<Double>();
        		imag=new ArrayList<Double>();
        		
        		for(int k=0;k<degree;k++){
        			for(int l=0;l<order;l++){
        				
        				if((k-l)%2==0){
        					//Calculate radial_value
        					double radial_value = calculateRadial(r, l, k, zps[index]);
        					final double ang = l * Math.atan2(y, x);
        					double pixel = ip.getPixel(x, y);
        					real.add(pixel * radial_value * Math.cos(ang));
        	        		imag.add(pixel * radial_value * Math.sin(ang));
        					zps[index].set(l, k, radial_value, ang, pixel); 
        				}
        			}
        		}
        		
        		zps[index].setComplex(real, imag);
        		index++;
        		
        		 	
         	}
       }
        
        double[] real_result=new double[real.size()];
        for(int i=0;i<zps.length;i++){
        	ArrayList<Double> temp=zps[i].getReal();
        	for(int j=0;j<temp.size();j++){
        		real_result[j]+=(temp.get(j)* (order + 1)) / Math.PI;
        	}
        }
        for(int i=0;i<real_result.length;i++){
        	System.out.println(real_result[i]);
        }
        
		return real_result;
	}
}
