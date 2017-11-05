package activeSegmentation.filter;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.gui.DialogListener;
import ij.measure.Calibration;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ijaux.scale.*;

import java.awt.*;
import java.util.*;

import javax.print.attribute.standard.PrinterLocation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import activeSegmentation.IFilter;
import dsp.Conv;

/**
 * @version 	1.2 23 Aug 2016
 *              1.1 27 Jun 2015
 * 				1.0  6 Oct 2014 
 * 				
 *   
 * 
 * @author Dimiter Prodanov, IMEC , Sumit Kumar Vohra , Kuleuven
 *
 *
 * @contents
 * The plugin performs anisotropic LoG filtering. The principle is based on Michael Broadhead
 * http://works.bepress.com/cgi/viewcontent.cgi?article=1017&context=michael_broadhead 
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


public class ALoG_Filter_ implements ExtendedPlugInFilter, DialogListener, IFilter {
    @SuppressWarnings("unused")

	private PlugInFilterRunner pfr=null;

	final int flags=DOES_ALL+KEEP_PREVIEW+ NO_CHANGES;
	private String version="2.0";
    @SuppressWarnings("unused")

	private int nPasses=1;
	private int pass;
	private int position_id;

	public final static String SIGMA="LOG_sigma",MAX_LEN="G_MAX",FULL_OUTPUT="Full_out",LEN="G_len";

	private static int sz= Prefs.getInt(LEN, 2);
	private  int max_sz= Prefs.getInt(MAX_LEN, 8);
	private boolean isEnabled=true;

	private float[][] kernel=null;

	private ImagePlus image=null;
	public static boolean debug=IJ.debugMode;

	public boolean fulloutput=false;

	private boolean isFloat=false;
    @SuppressWarnings("unused")

	private boolean hasRoi=false;


	/* NEW VARIABLES*/

	/** A string key identifying this factory. */
	private final  String FILTER_KEY = "ALOG";

	/** The pretty name of the target detector. */
	private final String FILTER_NAME = "Anisotropic Laplace of Gaussian";

	/** It stores the settings of the Filter. */
	private Map< String, String > settings= new HashMap<String, String>();
	
	/** It is the result stack*/
	private ImageStack imageStack;

	/**
	 * This method is to setup the PlugInFilter using image stored in ImagePlus 
	 * and arguments of filter
	 */
	@Override
	public int setup(String arg, ImagePlus imp) {
		image=imp;
		isFloat= (image.getType()==ImagePlus.GRAY32);
		hasRoi=imp.getRoi()!=null;
		cal=image.getCalibration();
		return  flags;
	}

	final int Ox=0, Oy=1, Oz=2;

	// It is used to check whether to calibiration or not
	private boolean doCalib = false;
	/*
	 * This variable is to calibrate the Image Window
	 */
	private Calibration cal=null;
	
	public void initialseimageStack(ImageStack img){
		this.imageStack = img;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
	 */
	@Override
	public void run(ImageProcessor ip) {
		int r = (sz-1)/2;
		GScaleSpace sp=new GScaleSpace(r);


		imageStack=new ImageStack(ip.getWidth(),ip.getHeight());

		imageStack = filter(ip,sp,sz,imageStack);


		image=new ImagePlus("ALoG result hw="+((sz-1)/2),imageStack);
		image.show();
	}

	/**
	 * 
	 * This method Apply filter to input image (in place)
	 * @param inputImage input image
	 * @param size kernel size (it must be odd)
	 * @param nAngles number of angles
	 * @return false if error
	 */
	@Override
	public Pair<Integer,ImageStack> applyFilter(ImageProcessor ip){
		int index = position_id;
		ImageStack imageStack=new ImageStack(ip.getWidth(),ip.getHeight());
		for (int sigma=sz; sigma<= max_sz; sigma *=2){		
			GScaleSpace sp=new GScaleSpace(sigma);
			imageStack = filter(ip.duplicate(), sp,sigma,imageStack);
		}
		initialseimageStack(imageStack);
		return new Pair<Integer,ImageStack>(index, imageStack);
	}
	


	
	/**
	 * 
	 * This method is helper function for both applyFilter and run method
	 * @param ip input image
	 * @param sp gaussian scale space
	 * @param sigma filter sigma
	 */
	private ImageStack filter(ImageProcessor ip,GScaleSpace sp, float sigma, ImageStack imageStack){

		ip.snapshot();

		if (!isFloat) 
			ip=ip.toFloat(0, null);

		pass++;
		System.out.println(settings.get(LEN)+"MG");
		//GScaleSpace sp=new GScaleSpace(sigma);
		float[] kernx= sp.gauss1D();
		System.out.println("kernx :"+kernx.length);
		GScaleSpace.flip(kernx);		
		float[] kern_diff2= sp.diff2Gauss1D();
		GScaleSpace.flip(kern_diff2);
		System.out.println("kernx2 :"+kern_diff2.length);
		float[] kern_diff1=sp.diffGauss1D();
		System.out.println("kernx1:"+kern_diff1.length);
		GScaleSpace.flip(kern_diff1);

		kernel=new float[4][];
		kernel[0]=kernx;
		kernel[1]=kern_diff2;
		kernel[2]=kern_diff1;

		float[] kernel2=sp.computeDiff2Kernel2D();
		kernel[3]=kernel2;
		GScaleSpace.flip(kernel2);  // symmetric but this is the correct way

		int sz= sp.getSize();
		if (debug && pass==1) {
			FloatProcessor fpkern2=new FloatProcessor(sz,sz);

			float[][] disp= new float[2][];

			disp[0]=GScaleSpace.joinXY(kernel, 0, 1);
			disp[1]=GScaleSpace.joinXY(kernel, 1, 0);

			for (int i=0; i<sz*sz; i++)
				fpkern2.setf(i, disp[0][i]+ disp[1][i]);

			new ImagePlus("kernel sep",fpkern2).show();


		}

		FloatProcessor fpaux= (FloatProcessor) ip;

		Conv cnv=new Conv();

		FloatProcessor gradx=(FloatProcessor) fpaux.duplicate();
		FloatProcessor grady=(FloatProcessor) fpaux.duplicate();
		FloatProcessor lap_xx=(FloatProcessor) fpaux.duplicate();
		FloatProcessor lap_yy=(FloatProcessor) fpaux.duplicate();
		FloatProcessor lap_xy=(FloatProcessor) fpaux.duplicate();

		cnv.convolveFloat1D(gradx, kern_diff1, Ox);
		cnv.convolveFloat1D(gradx, kernx, Oy);

		cnv.convolveFloat1D(grady, kern_diff1, Oy);
		cnv.convolveFloat1D(grady, kernx, Ox);

		cnv.convolveFloat1D(lap_xx, kern_diff2, Ox);
		cnv.convolveFloat1D(lap_xx, kernx, Oy);

		cnv.convolveFloat1D(lap_yy, kern_diff2, Oy);
		cnv.convolveFloat1D(lap_yy, kernx, Ox);

		cnv.convolveFloat1D(lap_xy, kern_diff1, Oy);
		cnv.convolveFloat1D(lap_xy, kern_diff1, Ox);
		int width=ip.getWidth();
		int height=ip.getHeight();

		FloatProcessor lap_t=new FloatProcessor(width, height); // tangential component
		FloatProcessor lap_o=new FloatProcessor(width, height); // orthogonal component

		for (int i=0; i<width*height; i++) {
			double gx=gradx.getf(i);
			double gy=grady.getf(i);

			double gxy=lap_xy.getf(i);

			double gxx=lap_xx.getf(i);
			double gyy=lap_yy.getf(i);

			double lx=2.0f*gx*gy*gxy;

			gx*=gx;
			gy*=gy;		
			double dt=gy*gxx+gx*gyy;
			double dx=gx*gxx+gy*gyy;
			//double amp=2.0*(gx+gy);			
			double amp=(gx+gy);	

			float lt=(float)((dt-lx)/amp);
			float ot=(float)((dx+lx)/amp);
			lap_t.setf(i, lt);
			lap_o.setf(i, ot);

		}

		if (fulloutput) {
			imageStack.addSlice(FILTER_KEY+"X diff"+sigma, gradx);
			imageStack.addSlice(FILTER_KEY+"Y diff"+sigma, grady);
			imageStack.addSlice(FILTER_KEY+"XX diff"+sigma, lap_xx);
			imageStack.addSlice(FILTER_KEY+"YY diff"+sigma, lap_yy);
			imageStack.addSlice(FILTER_KEY+"XY diff"+sigma, lap_xy);
		}

		imageStack.addSlice(FILTER_KEY+"Lap T"+sigma, lap_t);
		lap_o.resetMinAndMax();
		imageStack.addSlice(FILTER_KEY+"Lap O"+sigma, lap_o);
		System.out.println("ALOG_FILTER");
		return imageStack;
	}



	/**
	 * @param i
	 * @return
	 */
	public float[] getKernel(int i) {
		return kernel[i];
	}

	/* (non-Javadoc)
	 * @see ij.plugin.filter.ExtendedPlugInFilter#showDialog(ij.ImagePlus, java.lang.String, ij.plugin.filter.PlugInFilterRunner)
	 */
	@Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		this.pfr = pfr;
		int r = (sz-1)/2;
		GenericDialog gd=new GenericDialog("Anisotropic LoG " + version);

		gd.addNumericField("half width", r, 2);
		//gd.addNumericField("sigma", sigma, 1);
		gd.addCheckbox("Show kernel", debug);
		gd.addCheckbox("Full output", fulloutput);	
		if (cal!=null) {
			if (!cal.getUnit().equals("pixel"))
				gd.addCheckbox("units ( "+cal.getUnit() + " )", doCalib); 
		}		

		gd.addPreviewCheckbox(pfr);
		gd.addDialogListener(this);
		gd.setResizable(false);
		gd.showDialog();

		//pixundo=imp.getProcessor().getPixelsCopy();
		if (gd.wasCanceled()) {			
			return DONE;
		}

		return IJ.setupDialog(imp, flags);
	}




	// Called after modifications to the dialog. Returns true if valid input.
	/* (non-Javadoc)
	 * @see ij.gui.DialogListener#dialogItemChanged(ij.gui.GenericDialog, java.awt.AWTEvent)
	 */
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		double r = (int)(gd.getNextNumber());
		//sigma = (float) (gd.getNextNumber());
		debug = gd.getNextBoolean();
		fulloutput = gd.getNextBoolean();
		if (cal!=null)
			doCalib=gd.getNextBoolean();

		if (doCalib) {
			r= (r/cal.pixelWidth);
		}
		sz =  (2*(int)r+1);
		if (gd.wasCanceled()) {

			return false;
		}
		return r>0;
		//return sigma>0;
	}


	/* (non-Javadoc)
	 * @see ij.plugin.filter.ExtendedPlugInFilter#setNPasses(int)
	 */
	@Override
	public void setNPasses (int nPasses) {
		this.nPasses = nPasses;
	}

	/* Saves the current settings of the plugin for further use
	 * 
	 *
	 * @param prefs
	 */
	public static void savePreferences(Properties prefs) {
		prefs.put(LEN, Integer.toString(sz));
		// prefs.put(SIGMA, Float.toString(sigma));

	}

	@Override
	public Map<String, String> getDefaultSettings() {

		settings.put(LEN, Integer.toString(sz));
		settings.put(MAX_LEN, Integer.toString(max_sz));
		settings.put(FULL_OUTPUT, Boolean.toString(fulloutput));


		return this.settings;
	}

	@Override
	public boolean reset() {
		// TODO Auto-generated method stub
		sz= Prefs.getInt(LEN, 2);
		max_sz= Prefs.getInt(MAX_LEN, 8);
		fulloutput= Prefs.getBoolean(FULL_OUTPUT, true);
		return true;
	}

	@Override
	public boolean updateSettings(Map<String, String> settingsMap) {
		sz=Integer.parseInt(settingsMap.get(LEN));
		max_sz=Integer.parseInt(settingsMap.get(MAX_LEN));
		fulloutput= Boolean.parseBoolean(settingsMap.get(FULL_OUTPUT));

		return true;
	}

	@Override
	public String getKey() {
		return this.FILTER_KEY;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.FILTER_NAME;
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

	private Double log(double x){

		return (x*x-2)* Math.exp(-Math.pow(x, 2)/2) / (2  *Math.sqrt(3.14));
	}


	@Override
	public Image getImage(){

		final XYSeries series = new XYSeries("Data");
		for(double i=-10;i<=10;i=i+0.5){
			Double y=log(i);
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
		this.position_id=position;
	}

	@Override
	public int getDegree() {
		// TODO Auto-generated method stub
		return 0;
	}

}
