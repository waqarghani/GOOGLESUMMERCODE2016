package com.modular.filter.core;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import java.util.List;
import java.util.Map;

public interface IFilter {
	
	/**
	 * Returns a new default settings map for the filter 
	 * 
	 * @return a new map.
	 */
	public Map< String, String > getDefaultSettings();
	
	/**
	 * Returns true if setting are update Successfully
	 * @param  settingsMap
	 * @return boolean
	 */
	public boolean updateSettings(Map< String, String > settingsMap);
	
	/**
	 * Returns aplly filterS
	 * 
	 * @return String
	 */	
	public ImageStack applyFilter(ImageProcessor imageProcessor);
	/**
	 * Returns a unique key of filter
	 * 
	 * @return String
	 */
	public String getKey();
	
	/**
	 * Returns a Name of the filter
	 * 
	 * @return Integer
	 */
	public String getName();
	
	/**
	 * Get stack size
	 * @return number of slices in the stack
	 */
	public int getSize();
	/**
	 * Get slice label
	 * @param index slice index (from 1 to max size)
	 * @return slice label
	 */
	public String getSliceLabel(int index);
	/**
	 * Get stack height
	 * @return stack height
	 */
	public int getHeight();
	/**
	 * Get stack width
	 * @return stack width
	 */
	public int getWidth();

	

}
