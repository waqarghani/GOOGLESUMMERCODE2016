package activeSegmentation.filter;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.gui.DialogListener;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.Blitter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ijaux.scale.GScaleSpace;
import ijaux.scale.Pair;

import java.awt.*;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import activeSegmentation.IFilter;
import dsp.Conv;

/**
 * @version 	1.6    23 Aug 2016
 *              1.5		date 23 Sept 2013
 *				- isotropic correction
 * 				1.0		date 23 Jul 2013 
 * 				Based on Mexican_Hat_Filter v 2.2
 * 				- common functionality is refactored in a library class
 * 				
 *   
 * 
 * @author Dimiter Prodanov IMEC  & Sumit Kumar Vohra
 *
 *
 * @contents
 * This pluign convolves an image with a Bi-Laplacian of Gaussian (BoG) filter
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

 
public class BoG_Filter_ implements ExtendedPlugInFilter, DialogListener, IFilter {
    @SuppressWarnings("unused")
	private PlugInFilterRunner pfr=null;

	final int flags=DOES_ALL+SUPPORTS_MASKING+KEEP_PREVIEW;
	private String version="1.5";
	@SuppressWarnings("unused")

	private int nPasses=1;
	private int pass;
	
	public final static String SIGMA="LOG_sigma", LEN="G_len",MAX_LEN="G_MAX", ISO="G_iso", ISSEP="G_SEP";

	private static int sz= Prefs.getInt(LEN, 2);
	private  int max_sz= Prefs.getInt(MAX_LEN, 8);
	//private static float sigma=(float) Prefs.getDouble(SIGMA, 2.0f);
	private float[][] kernel=null;
	private int position_id=-1;

	private ImagePlus image=null;
	public static boolean debug=IJ.debugMode;

	public static boolean sep= Prefs.getBoolean(ISSEP, false);
	private static boolean isiso= Prefs.getBoolean(ISO, true);
	private boolean isEnabled=true;

	public boolean isFloat=false;
	
	@SuppressWarnings("unused")
    private boolean hasRoi=false;
	
	
	/* NEW VARIABLES*/

	/** A string key identifying this factory. */
	private final  String FILTER_KEY = "BOG";

	/** The pretty name of the target detector. */
	private final String FILTER_NAME = "Bi-Laplacian of Gaussian";

	private Map< String, String > settings= new HashMap<String, String>();

	private ImageStack imageStack;
	
	public void initialseimageStack(ImageStack img){
		this.imageStack = img;
	}
	/**
	 * 
	 */
	/* (non-Javadoc)
	 * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
	 */
	@Override
	public int setup(String arg, ImagePlus imp) {
		image=imp;
		isFloat= (image.getType()==ImagePlus.GRAY32);
		hasRoi=imp.getRoi()!=null;
		return  flags;
	}

 /*
  * (non-Javadoc)
  * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
  */
	@Override
	public void run(ImageProcessor ip) {
	
		int r = (sz-1)/2;
		//GScaleSpace sp=new GScaleSpace(r);
		GScaleSpace sp=new GScaleSpace(r,3.0f);
		FloatProcessor fp=filter(ip,sp,sep,isiso);
		image.setProcessor(fp);
		image.updateAndDraw();
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
			ImageProcessor fp=filter(ip.duplicate(), sp,sep, isiso);
			imageStack.addSlice( FILTER_KEY+"_" + sigma, fp);		
		}
		initialseimageStack(imageStack);
		return new Pair<Integer,ImageStack>(index, imageStack);	
	}


	public FloatProcessor filter(ImageProcessor ip,GScaleSpace sp, final boolean seperable,final boolean isotropic){
		
		ip.snapshot();
	 	
		if (!isFloat) 
			ip=ip.toFloat(0, null);

		
		pass++;
		
		float[] kernx= sp.gauss1D();
		GScaleSpace.flip(kernx);		

		float[] kern_diff_4= sp.diffNGauss1D(4);
		GScaleSpace.flip(kern_diff_4);
		
		float[] kern_diff_2= sp.diffNGauss1D(2);
		GScaleSpace.flip(kern_diff_2);		
		
		kernel=new float[4][];
		kernel[0]=kernx;
		kernel[1]=kern_diff_4;
		kernel[2]=kern_diff_2;
		float[] kernel2=sp.computeLapNKernel2D(2); // 2D kernel computation
		kernel[3]=kernel2;
			
		int sz= sp.getSize();
		
		if (debug)
			System.out.println("sz " +sz);
		
		float[][] disp= new float[3][];

		disp[0]=GScaleSpace.joinXY(kernel, 0, 1);
		disp[1]=GScaleSpace.joinXY(kernel, 1, 0);
		
		
		if (debug && pass==1) {
			FloatProcessor fp=new FloatProcessor(sz,sz);
			if (isotropic) {
				disp[2]=GScaleSpace.joinXY(kernel, 2, 2);
				for (int i=0; i<sz*sz; i++)
					fp.setf(i, disp[0][i]+ disp[1][i] + 2*disp[2][i]  );
				 
			} else {
				for (int i=0; i<sz*sz; i++)
					fp.setf(i, disp[0][i]+ disp[1][i] );
			}
			new ImagePlus("kernel sep",fp).show();
			if (!seperable) {
				FloatProcessor fp2=new FloatProcessor(sz,sz, kernel2);
				new ImagePlus("kernel 2D",fp2).show();
			}
		}
		long time=-System.nanoTime();	
		
		FloatProcessor fpaux= (FloatProcessor) ip;
	 		
		Conv cnv=new Conv();
		if (seperable) {
			if (isotropic) {
				FloatProcessor fpauxiso=(FloatProcessor) fpaux.duplicate();
								
				cnv.convolveSemiSep(fpaux, kernx, kern_diff_4);	
				for (int i=0; i<kern_diff_2.length; i++)
					kern_diff_2[i]*=Math.sqrt(2.0);
				cnv.convolveFloat1D(fpauxiso, kern_diff_2, 0); //Ox
				cnv.convolveFloat1D(fpauxiso, kern_diff_2, 1); //Oy
				
				fpaux.copyBits(fpauxiso, 0, 0, Blitter.ADD);
				System.out.println("separable & isotropic computation");
			} else {
				cnv.convolveSemiSep(fpaux, kernx, kern_diff_4);	
				System.out.println("separable & non-isotropic computation");
			}
		} else {	
			if (isotropic) {			
				System.out.println("non-separable & isotropic computation");
			} else {
				for (int i=0; i<sz*sz; i++)
					kernel2[i]=disp[0][i]+ disp[1][i];
				System.out.println("non-separable & non-isotropic computation");
			} // end else
			cnv.convolveFloat(fpaux, kernel2, sz, sz);
		} // end else
	 
		time+=System.nanoTime();
		time/=1000.0f;
		System.out.println("elapsed time: " + time +" us");
		fpaux.resetMinAndMax();	
		
		/*if (convert) {
			float[] minmax=findMinAndMax(fpaux);
			final double d1 = -(fmin* minmax[1] - fmax* minmax[0])/(fmin - fmax);
			dr = dr/ (minmax[1] - minmax[0]);
			System.out.println("contrast adjustment y=ax+b \n " +
					" b " +d1 +" a " + dr);
				
			contrastAdjust(fpaux, dr, d1);
		}*/
		
		return fpaux;
	
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
		GenericDialog gd=new GenericDialog("Bi-LoG (BoG) " + version);
		gd.addNumericField("half width", r, 1);
		//gd.addNumericField("sigma", sigma, 1);
		gd.addCheckbox("Show kernel", debug);
		gd.addCheckbox("Separable", sep);
		gd.addCheckbox("isotropic", isiso);
		/*if (hasRoi)
			gd.addCheckbox("Brightness correct", true);*/
		
		gd.addPreviewCheckbox(pfr);
		gd.addDialogListener(this);
		gd.showDialog();
		pixundo=imp.getProcessor().getPixelsCopy();
		if (gd.wasCanceled()) {			
			//image.repaintWindow();
			return DONE;
		}
		/*if (!IJ.isMacro())
			staticSZ = sz;*/
		return IJ.setupDialog(imp, flags);
	}
	
	private Object pixundo;
	//private boolean convert=false;
	
	// Called after modifications to the dialog. Returns true if valid input.
	/* (non-Javadoc)
	 * @see ij.gui.DialogListener#dialogItemChanged(ij.gui.GenericDialog, java.awt.AWTEvent)
	 */
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		int r = (int)(gd.getNextNumber());
		//sigma = (float) (gd.getNextNumber());
		debug = gd.getNextBoolean();
		sep = gd.getNextBoolean();
		isiso = gd.getNextBoolean();
		//convert=gd.getNextBoolean();
		 
		sz = 2*r+1;
		if (gd.wasCanceled()) {
			ImageProcessor proc=image.getProcessor();
			proc.setPixels(pixundo);
			proc.resetMinAndMax();
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
  		prefs.put(ISO, Boolean.toString(isiso));
  		prefs.put(ISSEP, Boolean.toString(sep));
        // prefs.put(SIGMA, Float.toString(sigma));

   }

   @Override
	public Map<String, String> getDefaultSettings() {

		settings.put(LEN, Integer.toString(sz));
		settings.put(MAX_LEN, Integer.toString(max_sz));
		settings.put(ISSEP, Boolean.toString(sep));
		settings.put(ISO, Boolean.toString(isiso));

		return this.settings;
	}

	@Override
	public boolean updateSettings(Map<String, String> settingsMap) {
		sz=Integer.parseInt(settingsMap.get(LEN));
		max_sz=Integer.parseInt(settingsMap.get(MAX_LEN));
		sep=Boolean.parseBoolean(settingsMap.get(ISSEP));
		isiso=Boolean.parseBoolean(settingsMap.get(ISO));

		return true;
	}
	
	@Override
	public boolean reset() {
		// TODO Auto-generated method stub
	sz= Prefs.getInt(LEN, 2);
	max_sz= Prefs.getInt(MAX_LEN, 8);
	sep= Prefs.getBoolean(ISSEP, true);
	isiso=Prefs.getBoolean(ISO, true);
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


	private Double bog(double x){

		return (Math.pow(x, 4)-8*(x*x)+8)* Math.exp(-Math.pow(x, 2)/2) / (2  *Math.sqrt(3.14));
	}
	
	
	@Override
	public Image getImage(){

		final XYSeries series = new XYSeries("Data");
		for(double i=-10;i<=10;i=i+0.5){
			Double y=bog(i);
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
