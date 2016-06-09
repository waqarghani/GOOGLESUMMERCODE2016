package activeSegmentation.io;

import ij.IJ;
import ij.Macro;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import ij.io.SaveDialog;
import ij.plugin.frame.Recorder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import activeSegmentation.IClassifier;
import activeSegmentation.IDataManager;
import weka.core.Instances;

public class DataManagerImp implements IDataManager {
	

	
	/**
	 * Read ARFF file
	 * @param filename ARFF file name
	 * @return set of instances read from the file
	 */
	public Instances readDataFromARFF(String filename){
		try{
			BufferedReader reader = new BufferedReader(
					new FileReader(filename));
			try{
				Instances data = new Instances(reader);
				// setting class attribute
				data.setClassIndex(data.numAttributes() - 1);
				reader.close();
				return data;
			}
			catch(IOException e){IJ.showMessage("IOException");}
		}
		catch(FileNotFoundException e){IJ.showMessage("File not found!");}
		return null;
	}
	
	
	
	@Override
	/**
	 * Write current instances into an ARFF file
	 * @param data set of instances
	 * @param filename ARFF file name
	 */
	public boolean writeDataToARFF(Instances data, String filename)
	{
		BufferedWriter out = null;
		try{
			out = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream( filename ) ) );

			final Instances header = new Instances(data, 0);
			out.write(header.toString());

			for(int i = 0; i < data.numInstances(); i++)
			{
				out.write(data.get(i).toString()+"\n");
			}
		}
		catch(Exception e)
		{
			IJ.log("Error: couldn't write instances into .ARFF file.");
			IJ.showMessage("Exception while saving data as ARFF file");
			e.printStackTrace();
			return false;
		}
		finally{
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;

	}

	/**
	 * Write current classifier into a file
	 *
	 * @param filename name (with complete path) of the destination file
	 * @return false if error
	 */
	public boolean saveClassifier(String filename,IClassifier classifier,Instances trainHeader )
	{
		File sFile = null;
		boolean saveOK = true;


		IJ.log("Saving model to file...");

		try {
			sFile = new File(filename);
			OutputStream os = new FileOutputStream(sFile);
			if (sFile.getName().endsWith(".gz"))
			{
				os = new GZIPOutputStream(os);
			}
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
			objectOutputStream.writeObject(classifier);
			if (trainHeader != null)
				objectOutputStream.writeObject(trainHeader);
			objectOutputStream.flush();
			objectOutputStream.close();
		}
		catch (Exception e)
		{
			IJ.error("Save Failed", "Error when saving classifier into a file");
			saveOK = false;
		}
		if (saveOK)
			IJ.log("Saved model into " + filename );

		return saveOK;
	}



	@Override
	public List<Roi> openZip(String path, int classNum, int sliceNumber) {
		// TODO Auto-generated method stub
		
		return openZip1(path, classNum, sliceNumber);
		
	}
	
	private List<Roi> openZip1(String path,int classNum, int n) { 
		Hashtable rois = new Hashtable();
		ZipInputStream in = null; 
		List<Roi> roiList= new ArrayList<Roi>();
		ByteArrayOutputStream out = null; 
		int nRois = 0; 
		try { 
			in = new ZipInputStream(new FileInputStream(path)); 
			byte[] buf = new byte[1024]; 
			int len; 
			ZipEntry entry = in.getNextEntry(); 
			while (entry!=null) { 
				String name = entry.getName();
				if (name.endsWith(".roi")) { 
					out = new ByteArrayOutputStream(); 
					while ((len = in.read(buf)) > 0) 
						out.write(buf, 0, len); 
					out.close(); 
					byte[] bytes = out.toByteArray(); 
					RoiDecoder rd = new RoiDecoder(bytes, name); 
					Roi roi = rd.getRoi(); 
					if (roi!=null) { 
						name = name.substring(0, name.length()-4); 
						name = getUniqueName(name,rois);  
						rois.put(name, roi); 
						roiList.add(roi);
						nRois++;
					} 
				} 
				entry = in.getNextEntry(); 
			} 
			in.close(); 
		} catch (IOException e) {
			
		} finally {
			if (in!=null)
				try {in.close();} catch (IOException e) {}
			if (out!=null)
				try {out.close();} catch (IOException e) {}
		}
		if(nRois==0)
				System.out.println("ERROR OCCURED");
		
		return roiList;
	} 
	
	private String getUniqueName(String name,Hashtable rois) {
		String name2 = name;
		int n = 1;
		Roi roi2 = (Roi)rois.get(name2);
		while (roi2!=null) {
			roi2 = (Roi)rois.get(name2);
			if (roi2!=null) {
				int lastDash = name2.lastIndexOf("-");
				if (lastDash!=-1 && name2.length()-lastDash<5)
					name2 = name2.substring(0, lastDash);
				name2 = name2+"-"+n;
				n++;
			}
			roi2 = (Roi)rois.get(name2);
		}
		return name2;
	}



	@Override
	public boolean saveExamples(String filename, List<Roi> roi) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean loadTrainingData(String fileName) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean loadExamples(String directory) {
		// TODO Auto-generated method stub
		return false;
	}



	
}
