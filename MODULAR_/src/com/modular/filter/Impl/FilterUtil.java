package com.modular.filter.Impl;

import ij.ImageStack;
import weka.core.DenseInstance;

public class FilterUtil {

	
	
	/**
	 * Create instance (feature vector) of a specific coordinate
	 * 
	 * @param x x- axis coordinate
	 * @param y y- axis coordinate
	 * @param classValue class value to be assigned
	 * @return corresponding instance
	 */
	public DenseInstance createInstance(
			int x, 
			int y, 
			int classValue,
			ImageStack stack,
			boolean colorFeatures,
			boolean oldColorFormat )
	{
		
		final int size=stack.getSize();

		final double[] values = new double[ size + 1 ];
		int n = 0;

		if( colorFeatures == false || oldColorFormat == true)
		{
			for (int z=0; z<size; z++, n++)		
				values[ z ] = stack.getVoxel( x, y, z );
		}
		else
		{
			for (int z=0; z <  size; z++, n++)		
			{
				int c  = (int) stack.getVoxel( x, y, z );
				int r = (c&0xff0000)>>16;
			int g = (c&0xff00)>>8;
		int b = c&0xff;
		values[ z ] = (r + g + b) / 3.0;
			}
		}


		// Assign class
		values[values.length-1] = (double) classValue;

		return new DenseInstance(1.0, values);
	}

	
	
	
	
	
	
	

}
