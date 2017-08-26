# Active Segmentation plugin for ImageJ

This project is sponsored by Google Inc. as a part of the Google Summer of Code 2016-2017 program: 

1. **Organization:** [International Neuroinformatics Coordinating Facility(INCF)](http://incf.org)
2. **Mentor:** Dimiter Prodanov, [INCF Belgian Node](http://www.neuroinformatics.be)
3. **Student Developer:** [Mukesh Gupta](https://github.com/mukesh14149), [Sumit Kumar Vohra](https://github.com/sumit3203/)


## Project description
This project is about to provide the general purpose environment that allows biologists and other domain experts to use transparently state-of-the-art techniques in machine learning to improve their image segmentation results.
ImageJ is a public domain Java image processing program extensively used in life sciences. Active Segmentation plugin is the redesign of existing Trainable Weka Segmentation (TWS) of ImageJ. The Active Segmentation provides generic functionality and user friendly interface so that the user can include the state of the art filters and machine learning frameworks from the WEKA library:
  1.  Active learning.
  2.  Multi-instance learning designed by third party in a robust manner

The Generic framework can be used for segmentation and classification.

## Session Screen
Session is a provision for storing project data in the session file. It save the state of the system. First image illustrates the window for Session Screen. Session screen will load the existing(Previous) session of the project. It provide you the option to load the filter, feature and learning data. The "Choose Session File" button is used for loading session file from the local file system. The whole meta-data of the project will be saved in the same directory as selected by the user. The session file must be in "JSON" format.The second image shows the window for browsing session file on the local file system:

![ScreenShot](https://github.com/mukesh14149/GOOGLESUMMERCODE2016/blob/master/other_res/SessionScreen.png) 
![ScreenShot](https://github.com/mukesh14149/GOOGLESUMMERCODE2016/blob/master/other_res/LOADSESSION.png)

Code implementing the loading of session file can be found in the "getMetaInfo" method from the "io" package.
```java
public MetaInfo getMetaInfo() {
		if(metaInfo==null){
			ObjectMapper mapper = new ObjectMapper();
			try {
				metaInfo= mapper.readValue(new File(path+Common.FILENAME), MetaInfo.class);
				originalImage= IJ.openImage(metaInfo.getPath()+Common.TRAININGIMAGE+Common.TIFFORMAT);
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
		}
		return metaInfo;
	}
  ```
As can be seen from the "ObjectMapper" in the code, the session file was mapped using the Jackson API, version 2.3.1.

The following code shows the format of session file for filters. FilterList consists of meta data information related to all the filters which are currently available in active segmentation. Each element in the list will give information about individual Filter. The key field in Filter is FilterName that will be unique for every filter. Filter will also consist of settings in key- value pair format.
```java
	{
		"FilterList": [
		{"Filter":"FilterName1","Setting1":"true","Setting2":"2","Setting3":"false", "FileList": "FilterName1.tif"},
		{"Filter":"FilterName2","Setting1":"true","Setting2":"2",
		"FileList": "FilterName2.tif"}
		]
		
	}
```
## Filter Framework
Filter Framework is responsible for automatic loading of filters.It automatically finds all JAR files in the path and first level of subdirectories and those have implemented "IFilter" interface.

Active Segmentation Filters Screen
![ScreenShot](https://github.com/mukesh14149/GOOGLESUMMERCODE2016/blob/master/other_res/FilterScreen1.png) 
![ScreenShot](https://github.com/mukesh14149/GOOGLESUMMERCODE2016/blob/master/other_res/FilterScreen.png) 

1. The right black marked box is used to enable the unselected filters. User need to double click on filter name to enable it.
2. The settings for each filter is provided in the centre along with the shape of filter.
3. Filters process is started using the "COMPUTE" button.
4. Filters meta data is dumped using the "SAVE" button.
5. The "View" button is used to view the filters result for each image slice.
6. The "Default" button is used to set the set default settings for the filter as provided by the developers.

##  Feature Screen
Feature Screen of the active segmentation is very similar to the TWS(Trainable Weka Segmentation).
In the starting user need to selects Pixel level or Class level feature extraction. 
![ScreenShot](https://github.com/mukesh14149/GOOGLESUMMERCODE2016/blob/master/other_res/MainfeaturesScreen.png)

Active Segmentation Pixel Level Features Screen
![ScreenShot](https://github.com/mukesh14149/GOOGLESUMMERCODE2016/blob/master/other_res/FeatureScreen.png)
<ul>
<li>The Configure button is used to go "Class Screen". The details regarding the class screen is discussed in the next section</li>
<li>The user can add region of interest to class using "class button" i.e BCK, ASTRO etc.</li>
<li>As seen in "Red Box in Feature Screen", the user can also upload or download region of interest to class using "upload or download buttons" </li>
<li>As seen in "Box 1 in Feature Screen", It will show all the ROI's belong to current slice.
The user can delete the region of interest by double click on particular ROI.</li>
<li>As seen in "Box 2 in Feature Screen", It will show all the ROI's belong to class.
The user can delete the region of interest by double click on particular ROI.</li>
<li>Meta Data is dumped using the "SAVE" button.</li>
<li>Learning is started using the "COMPUTE" button. Once the learning is completed, The "overlay image" will be generated</li>
<li>Meta Data is dumped using the "SAVE" button.</li>
<li>The "TOGGLE" button is used to switch between "overlay image" and "original image". </li>
</ul>

Active Segmentation Class Level Features Screen
This is default Screen for selecting features for training purposes
![ScreenShot](https://github.com/mukesh14149/GOOGLESUMMERCODE2016/blob/master/other_res/FeatureClassTrainingScreen.png
)


This is default Screen for selecting features for testing purposes
![ScreenShot](https://github.com/mukesh14149/GOOGLESUMMERCODE2016/blob/master/other_res/FeatureClassTestingScreen.png
)

<ul>
<li>The Configure button is used to go "Class Screen". The details regarding the class screen is discussed in the next section</li>
<li>User selects images and put into given class</li>
</ul>

## Configure Screen
Configure screen is used to configure classes. The user can add,update or delete classes. The default classes will be two.
<ul>
<li>The user can add new class using "ADD CLASS" button.</li>
<li>In order to delete the class,the user have to select the class using checkbox and then click "DELETE" button. </li>
<li>The user can change the color of the class using "COLOR" button.</li>
</ul>

![ScreenShot](https://github.com/mukesh14149/GOOGLESUMMERCODE2016/blob/master/other_res/ClassScreen.png)
![ScreenShot](https://github.com/mukesh14149/GOOGLESUMMERCODE2016/blob/master/other_res/COLOR.png)
