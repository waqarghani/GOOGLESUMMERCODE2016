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
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import activeSegmentation.Common;
import activeSegmentation.IClassifier;
import activeSegmentation.IDataManager;
import activeSegmentation.IDataSet;
import activeSegmentation.learning.WekaDataSet;
import weka.core.Instances;

public class DataManagerImp implements IDataManager {


	private Instances data;
	private String path;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


	/**
	 * Read ARFF file
	 * @param filename ARFF file name
	 * @return set of instances read from the file
	 */
	public IDataSet readDataFromARFF(String filename){
		try{
			BufferedReader reader = new BufferedReader(
					new FileReader(filename));
			try{
				data = new Instances(reader);
				// setting class attribute
				data.setClassIndex(data.numAttributes() - 1);
				reader.close();
				return getDataSet();
			}
			catch(IOException e){IJ.showMessage("IOException");}
		}
		catch(FileNotFoundException e){IJ.showMessage("File not found!");}
		return null;
	}


	public IDataSet getDataSet() {

		IDataSet dataSet= new WekaDataSet(data);
		return  dataSet;
	}


	public void setData(Instances data) {
		this.data = data;
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
							new FileOutputStream( path+filename ) ) );

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
	public List<Roi> openZip(String path) {
		// TODO Auto-generated method stub

		return openZip1(path);

	}

	private List<Roi> openZip1(String path) { 
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
	public boolean saveExamples(String filename, List<Roi> rois) {

		DataOutputStream out = null;
		try {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filename));
			out = new DataOutputStream(new BufferedOutputStream(zos));
			RoiEncoder re = new RoiEncoder(out);
			for (Roi roi:rois) {

				zos.putNextEntry(new ZipEntry(roi.getName()+".roi"));
				re.write(roi);
				out.flush();
			}
			out.close();
		} catch (IOException e) {

			return false;
		} finally {
			if (out!=null)
				try {out.close();} catch (IOException e) {}
		}

		return true;
	}



	@Override
	public boolean loadTrainingData(String fileName) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void writeMetaInfo( MetaInfo metaInfo) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			metaInfo.setModifyDate(dateFormat.format(new Date()));
			if(metaInfo.getCreatedDate()==null){
				metaInfo.setCreatedDate(dateFormat.format(new Date()));
			}
			metaInfo.setPath(path);
			// Convert object to JSON string and save into a file directly
			System.out.println("SAVING");
			mapper.writeValue(new File(path+Common.FILENAME), metaInfo);

			System.out.println("DONE");

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public MetaInfo getMetaInfo() {
		ObjectMapper mapper = new ObjectMapper();
		MetaInfo metaInfo;
		try {
		  metaInfo= mapper.readValue(new File(path+Common.FILENAME), MetaInfo.class);
			//metaInfo.setPath(path);
			return metaInfo;

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		metaInfo= new MetaInfo();
		metaInfo.setPath(path);
		return metaInfo;
	}


	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}



}
