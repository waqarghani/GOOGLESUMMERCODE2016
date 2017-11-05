package activesegmentation.filter;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.gui.DialogListener;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ijaux.scale.*;

import java.awt.*;
import java.io.File;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import activeSegmentation.FeatureType;
import activeSegmentation.IFilter;
import dsp.Conv;

/**
 * @version   	23 Aug 2016
 *              3.0 20 Oct 2013
 * 				- change of philosophy
 * 				- migration to the GScaleSpace package
 * 				2.1 18 Nov 2012
 * 				- fixes in the scaling of the convolution kernels
 * 				2.0	17 Nov 2012
 * 				- semi-separable implementation
 * 				1.0	 8 Nov 2012
 *   
 * 
 * @author Dimiter Prodanov,IMEC , Sumit Kumar Vohra
 *
 *
 * @contents
 * This pluign convolves an image with a Gaussian derivative of order  (n,m).
 * 
 * 
 * @license This library is free software; you can redistribute it and/or
 *      modify it under the terms of the GNU Lesser General Public
 *      License as published by the Free Software Foundation; either
 *      version 2.1 of the License, or (at your option) any later version.
 *
 *      This library is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *       Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public
 *      License along with this library; if not, write to the Free Software
 *      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
public class Gaussian_Derivative_ implements ExtendedPlugInFilter, DialogListener,IFilter {

    @SuppressWarnings("unused")

    private PlugInFilterRunner pfr=null;

	final int flags=DOES_ALL+CONVERT_TO_FLOAT+SUPPORTS_MASKING+KEEP_PREVIEW;
	private String version="2.0";
	   @SuppressWarnings("unused")

	private int nPasses=1;
	    @SuppressWarnings("unused")

	   private int pass=0;
	public final static String SIGMA="LOG_sigma", LEN="G_len" ,MAX_LEN="G_MAX", 
			ISSEP="G_SEP", GN="G_Xn", GM="G_Yn", SCNORM="G_SCNORM";

	private static int sz = Prefs.getInt(LEN, 2);
	private  int max_sz= Prefs.getInt(MAX_LEN, 8);
	private float[][] kernel=null;
	public static boolean debug=IJ.debugMode;

	private static int nn = Prefs.getInt(GN, 1);
	private static int mm = Prefs.getInt(GM, 0);

	private static boolean sep=Prefs.getBoolean(ISSEP, false);
	private static boolean scnorm=Prefs.getBoolean(SCNORM, false);
	private ImagePlus image=null;	
	private boolean isFloat=false;
	private boolean isRGB=false;
	private static int wnd=3;
	private boolean isEnabled=true;
	private int position_id=-1;


	/* NEW VARIABLES*/

	/** A string key identifying this factory. */
	private final  String FILTER_KEY = "GAUSSIAN";

	/** The pretty name of the target detector. */
	private final String FILTER_NAME = "Gaussian Derivatives";

	private Map< String, String > settings= new HashMap<String, String>();

	private ImageStack imageStack;

	/*
	 * @param args - args[0] should point to the folder where the plugins are installed 
	 */
	public static void main(String[] args) {

		try {

			File f=new File(args[0]);

			if (f.exists() && f.isDirectory() ) {
				System.setProperty("plugins.dir", args[0]);
				new ImageJ();
			} else {
				throw new IllegalArgumentException();
			}
		}
		catch (Exception ex) {
			IJ.log("plugins.dir misspecified\n");
			ex.printStackTrace();
		}

	}

	public void initialseimageStack(ImageStack img){
		this.imageStack = img;
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		isFloat= (imp.getType()==ImagePlus.GRAY32);
		isRGB = (imp.getType()==ImagePlus.COLOR_RGB);
		return  flags;
	}

	@Override
	public void run(ImageProcessor ip) {
		try {
			image.close();
		} catch (Exception Ex){

		}

		int r = (sz-1)/2;

		if (wnd<0)
			wnd=-wnd;
		if (wnd>5)
			wnd=5;

		GScaleSpace sp=new GScaleSpace(r, wnd);
		ImageProcessor fpaux=filter(ip, sp, sep, scnorm,nn,mm);
		image=new ImagePlus("Convolved_"+nn+"_"+mm, fpaux);
		//image.setProcessor(fpaux);
		//image.updateAndDraw();
		image.show();
		//ImageJ ij=IJ.getInstance();		
	}




	/**
	 * Apply filter to input image (in place)
	 * @param inputImage input image
	 * @param size kernel size (it must be odd)
	 * @param nAngles number of angles
	 * @return false if error
	 */
	public Pair<Integer,ImageStack> applyFilter(ImageProcessor ip){
		int index = position_id;
		ImageStack imageStack=new ImageStack(ip.getWidth(),ip.getHeight());
		for (int sigma=sz; sigma<= max_sz; sigma *=2){		
			GScaleSpace sp=new GScaleSpace(sigma);
			ImageProcessor fp=filter(ip.duplicate(), sp,sep, scnorm,nn,mm);
			imageStack.addSlice( FILTER_KEY+"_" + sigma, fp);		
		}
		initialseimageStack(imageStack);
		return new Pair<Integer,ImageStack>(index, imageStack);
	}


	public FloatProcessor filter(ImageProcessor ip,GScaleSpace sp, final boolean sep,final boolean scnorm, int n,
			int m){

		ImageProcessor ipaux=ip.duplicate();

		if (!isFloat && !isRGB) 
			ipaux=ipaux.toFloat(0, null);

		pass++;

		//GScaleSpace sp=new GScaleSpace(sigma);
		double sigma=sp.getSigma();
		float[] kernx=null;
		if (n==0) {
			kernx=sp.gauss1D();
		} else {
			kernx=sp.diffNGauss1D(n);
			if (scnorm) {
				scnorm(kernx,sigma,n);
			}
		}
		GScaleSpace.flip(kernx);	

		float[] kerny= null;

		if (m==0) {
			kerny=sp.gauss1D();
		} else {
			kerny=sp.diffNGauss1D(m);
			if (scnorm) {
				scnorm(kerny,sigma,n);
			}
		}
		GScaleSpace.flip(kerny);

		kernel=new float[3][];
		kernel[0]=kernx;
		kernel[1]=kerny;	 

		float[] kernel_xy=GScaleSpace.joinXY(kernel, 0, 1);
		kernel[2]=kernel_xy;

		if (debug ) {
			FloatProcessor fp3=new FloatProcessor(sp.getSize(),sp.getSize(), kernel_xy);
			new ImagePlus("Gd_"+n +"_" +m,fp3).show();	 
		}
		long time=-System.nanoTime();	

		FloatProcessor fpaux= (FloatProcessor) ipaux;
		Conv cnv=new Conv();
		if (sep) {
			cnv.convolveSep(fpaux, kernx, kerny);			
		} else {		 
			cnv.convolveFloat(fpaux, kernel_xy, sp.getSize(), sp.getSize());
		}

		time+=System.nanoTime();
		time/=1000.0f;
		System.out.println("elapsed time: " + time +" us");
		fpaux.resetMinAndMax();

		return fpaux;

	}

	private void scnorm(float[] kern, double sigma, int n) {
		sigma=Math.pow(sigma, n);
		for (int i=0; i<kern.length; i++) {
			kern[i]*=sigma;
		}
	}

	/* Saves the current settings of the plugin for further use
	 * 
	 *
	 * @param prefs
	 */
	public static void savePreferences(Properties prefs) {
		prefs.put(GN, Integer.toString(nn));
		prefs.put(GM, Integer.toString(mm));
		prefs.put(LEN, Integer.toString(sz));
		prefs.put(ISSEP, Boolean.toString(sep));
		prefs.put(SCNORM, Boolean.toString(scnorm));
		// prefs.put(SIGMA, Float.toString(sigma));

	}


	public float[] getKernel(int i) {
		return kernel[i];
	}

	@Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		this.pfr = pfr;
		int r = (sz-1)/2;
		GenericDialog gd=new GenericDialog("Gauss derivative " + version);
		gd.addNumericField("span x sigma", wnd, 3);
		gd.addNumericField("half width", r, 1);
		gd.addNumericField("order in x", nn, 1);
		gd.addNumericField("order in y", mm, 1);
		gd.addCheckbox("Show kernel", debug);
		gd.addCheckbox("Separable", sep);
		gd.addCheckbox("Scale nomalize", scnorm);
		gd.addPreviewCheckbox(pfr);
		gd.addDialogListener(this);
		gd.showDialog();
		if (gd.wasCanceled())
			return DONE;


		return IJ.setupDialog(imp, flags);
	}

	// Called after modifications to the dialog. Returns true if valid input.
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		wnd = (int)(gd.getNextNumber());
		int r = (int)(gd.getNextNumber());
		nn = (int)(gd.getNextNumber());
		mm = (int)(gd.getNextNumber());
		debug = gd.getNextBoolean();
		sep = gd.getNextBoolean();
		scnorm = gd.getNextBoolean();
		sz = 2*r+1;
		if (gd.wasCanceled())
			return false;


		return r>0;
	}


	@Override
	public void setNPasses (int nPasses) {
		this.nPasses = nPasses;
	}

	@Override
	public boolean reset() {
		// TODO Auto-generated method stub
		sz= Prefs.getInt(LEN, 2);
		max_sz= Prefs.getInt(MAX_LEN, 8);
		sep= Prefs.getBoolean(ISSEP, true);
		scnorm=Prefs.getBoolean(SCNORM, false);
		nn = Prefs.getInt(GN, 1);
		mm = Prefs.getInt(GM, 0);
		return true;
	}

	@Override
	public Map<String, String> getDefaultSettings() {

		settings.put(LEN, Integer.toString(sz));
		settings.put(MAX_LEN, Integer.toString(max_sz));
		settings.put(ISSEP, Boolean.toString(sep));
		settings.put(SCNORM, Boolean.toString(scnorm));
		settings.put(GN, Integer.toString(nn));
		settings.put(GM, Integer.toString(mm));

		return this.settings;
	}

	@Override
	public boolean updateSettings(Map<String, String> settingsMap) {
		sz=Integer.parseInt(settingsMap.get(LEN));
		max_sz=Integer.parseInt(settingsMap.get(MAX_LEN));
		sep=Boolean.parseBoolean(settingsMap.get(ISSEP));
		scnorm=Boolean.parseBoolean(settingsMap.get(SCNORM));
		nn=Integer.parseInt(settingsMap.get(GN));
		mm=Integer.parseInt(settingsMap.get(GM));

		return true;
	}



	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return FILTER_KEY;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return FILTER_NAME;
	}

	/**
	 * Get stack size
	 * @param sliceNum
	 * @return number of slices in the stack
	 */
	@Override
	public int getSize(){
		return imageStack.getSize();
	}
	/**
	 * Get slice label
	 * @param index slice index (from 1 to max size)
	 * @return slice label
	 */
	@Override
	public String getSliceLabel(int index){
		return imageStack.getSliceLabel(index);
	}
	/**
	 * Get stack height
	 * @return stack height
	 */
	@Override
	public int getHeight(){
		return imageStack.getHeight();
	}
	/**
	 * Get stack width
	 * @return stack width
	 */
	@Override
	public int getWidth(){
		return imageStack.getWidth();
	}

	private Double gaussian(double x){

		return Math.exp(-Math.pow(x, 2)/2) / (2  *Math.sqrt(3.14));
	}

	@Override
	public Image getImage(){

		final XYSeries series = new XYSeries("Data");
		for(double i=-10;i<=10;i=i+0.5){
			Double y=gaussian(i);
			series.add(i, y);
		}
		final XYSeriesCollection data = new XYSeriesCollection(series);
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"",
				"", 
				"", 
				data,
				PlotOrientation.VERTICAL,
				false,
				false,
				false
				);

		return chart.createBufferedImage(200, 200);
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		// TODO Auto-generated method stub
		this.isEnabled= isEnabled;
	}

	@Override
	public ImageStack getImageStack() {
		return imageStack;
	}


	@Override
	public void setImageStack(ImageStack imageStack) {
		this.imageStack = imageStack;
	}

	@Override
	public void updatePosition(int position) {
		// TODO Auto-generated method stub
		this.position_id = position;
	}

	@Override
	public int getDegree() {
		// TODO Auto-generated method stub
		return 0;
	}


}
