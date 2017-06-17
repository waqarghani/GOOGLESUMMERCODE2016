package ijaux.scale;
import java.util.ArrayList;

/*
 * This classes stores all Radial polynomial upto order m n for individual pixel.
 */
public class Zps {
	double pixel_value;
	double[][] Rmn;
	double angle;
	ArrayList<Double> real=new ArrayList<Double>();
	ArrayList<Double> imag=new ArrayList<Double>();
	public Zps(int m,int n){
		Rmn=new double[m+3][n+3];
		
	}
	public double get(int m,int n){
		return Rmn[m][n];
	}
	public void set(int m, int n, double value, double ang, double pixel_value){
		Rmn[m][n]=value;
		angle=ang;
		this.pixel_value=pixel_value;
	}
	public void setComplex(ArrayList<Double> real,ArrayList<Double> imag){
		this.real=real;
		this.imag=imag;
	}
	public ArrayList<Double> getReal(){
		return real;
	}
}
