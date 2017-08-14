import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import activeSegmentation.Common;
import activeSegmentation.gui.ImageOverlay;
import activeSegmentation.gui.OverlayedImageCanvas;
import activeSegmentation.gui.RoiListOverlay;
import activeSegmentation.gui.Util;
import activeSegmentation.learning.LVQ;
import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.NewImage;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.process.FloatProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import ij.process.LUT;
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
	LUT overlayLUT;
	// Create overlay LUT
		byte[] red = new byte[ 256 ];
		byte[] green = new byte[ 256 ];
		byte[] blue = new byte[ 256 ];

	public void setLut(List<Color> colors ){
		int i=0;
		for(Color color: colors){
			red[i] = (byte) color.getRed();
			green[i] = (byte) color.getGreen();
			blue[i] = (byte) color.getBlue();
			i++;
		}
		overlayLUT = new LUT(red, green, blue);
	}
	
	public static void main(String[] args) throws Exception{
		
		LUT[] luts;
	//	ImagePlus t= IJ.openImage("/home/mg/Downloads/tifs/image.tif");
		 ImagePlus imp = IJ.openImage("/home/mg/Downloads/tifs/image.tif");
		 imp.show();
		 ImageStack ss = imp.getStack();
		 imp.getProcessor().setOverColor(180, 180, 180);;
		 imp.show();
		 
		 /*if (imp instanceof CompositeImage) {
			 System.out.println("ss");
			    luts = imp.getLuts();
			    luts[0] = LUT.createLutFromColor(Color.CYAN);
			    luts[1] = LUT.createLutFromColor(Color.MAGENTA);
			    
			    imp.setLut(luts[0]);
			    imp.updateAndDraw(); 
			}*/
		    //IJ.saveAs("Tiff", "/Users/wayne/blobs2.tif"); 
	/*	ImageStack c = new ImageStack(t.getWidth(), t.getHeight());
		c.addSlice(t.getStack().getProcessor(1));
		*/
		//NewImage ne = new NewImage();
		
	/*	ImagePlus ip= NewImage.createImage("Ch", 500, 500, 12, 8, 0);
		//ip.getProcessor().setColor(Color.BLUE);
		//ip.getProcessor().fill();p.show();
		example ss =new example();
		ss.setLut(Util.setDefaultColors());
		ip.show();
		    
			ip.getStack().getProcessor(1).setColorModel(ss.overlayLUT);
		 WindowManager.setTempCurrentImage(img);
		    IJ.run("Fire");
		ip.getStack().getProcessor(1).fill();
		ip.updateAndDraw();
	*/
		/*ImagePlus ss = new ImagePlus("sss",c);
		ss.show();
		ss.getProcessor().convertToRGB().setColor(Color.RED);;
		ss.getProcessor().fill();
		ss.updateAndDraw();*/
		/*
		
		example ss =new example();
		ss.setLut(Util.setDefaultColors());
		
		ImagePlus sd = new ImagePlus("sss",c);
		sd.setColor(Color.RED);
		sd.show();
		Overlay ov = new Overlay();
		ov.setFillColor(Color.RED);
		
		sd.setOverlay(ov);
		
		sd.updateAndDraw();
*/		
		/*ImagePlus classifiedImage= null;

		ImageStack classStack = new ImageStack(t.getWidth(), t.getHeight());
		ImagePlus tempImage = t.duplicate();
		
		for (int i=1;i<=tempImage.getStackSize(); i++)
		{
			if(i==3||i==23){
				tempImage.getStack().getProcessor(i).setColor(Color.RED);
				tempImage.getStack().getProcessor(i).fill();
				tempImage.updateAndDraw();
			}	
			classStack.addSlice(tempImage.getStack().getSliceLabel(i), tempImage.getStack().getProcessor(i));
		}
		
		classifiedImage= new ImagePlus("Classified Image", classStack);
		classifiedImage.setCalibration(t.getCalibration());
		classifiedImage.show();*/
/*		//drawOutline(t);
		
		//ImageConverter ii = new ImageConverter(t);
		//ii.convertoriginalImageToRGB();
		//t.setColor(Color.blue);
		t.show();
		
		ImageProcessor ip2 = t.getProcessor();
		ip2.convertToRGB();
		ip2.setColor(Color.RED);;	
		ip2.fill();
		t.updateAndDraw();	
			//t.show();
		ImageStack c = new ImageStack(t.getWidth(), t.getHeight());
		
		System.out.println(Util.setDefaultColors().get(1));
		for(int i=1;i<=t.getStackSize();i++){
		   //System.out.println(t.getStack().getSliceLabel(i));
			if(i==14)	
			{t.getStack().getProcessor(i).setColorModel(overlayLUT);
				t.getStack().getProcessor(i).fill();
				t.updateAndDraw();
			}
			c.addSlice((t.getStack().getProcessor(i)));
		}
		System.out.println(c.size());
		ImagePlus ci= new ImagePlus("Classified Image", c);
		ci.show();
			*/
	}	
	
	static void drawOutline(ImagePlus imp) {
		ImageCanvas ic = imp.getCanvas();
		Overlay overlay = imp.getOverlay();
		
		if (overlay==null)
			overlay = new Overlay();
		Roi roi = null;
		final Composite transparency050 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f );
		imp.show();
		roi = new Roi(0, 0, imp.getWidth(), imp.getHeight());
		ArrayList<Roi> aa= new ArrayList<Roi>();
		aa.add(roi);
		//	roi.setStrokeColor(Color.red);
			RoiListOverlay roiOverlay = new RoiListOverlay();
			roiOverlay.setComposite( transparency050 );
			roiOverlay.setRoi(aa);
			roiOverlay.setColor(Color.red);
		//	overlay.add(roi);
			
			imp.updateAndDraw();
			
		//imp.setOverlay(roi, strokeColor, strokeWidth, fillColor);
		
	}
	
	
}
