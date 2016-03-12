package algorithm;


import Jama.Matrix;




public class NipalPca {

	final double NIPAL_THRESHOLD = 0.00001;
	
	

	/**
	 * PCA implemented using the NIPALS algorithm. The return value is a double[][], where each
	 * double[] j is an array of the scores of the jth data point corresponding to the desired
	 * number of principal components.
	 * @param input			input raw data array
	 * @param numComponents	desired number of PCs
	 */
	public void runNipal(int numComponents,double[][] data){

		Matrix X= computeMeanCenteredC(new Matrix(data));
		System.out.println("----------------------------------");
		System.out.println(" MEAN CENTERED MATRIX");
		print(X.getArray());
		System.out.println("-----------------------------------");

		Matrix E=X;

		for(int i = 0; i < numComponents; i++) {
			double eigenOld = 0;
			double eigenNew = 0;
			Matrix tMatrix= E.getMatrix(0,E.getRowDimension()-1, 0,0);

			Matrix p;

			do {
				eigenOld = eigenNew;
				// Projection
				double  tMult=1/tMatrix.transpose().times(tMatrix).getArray()[0][0];
				p= E.transpose().times(tMatrix).times(tMult);       

				// Normalize
				double pNormal=p.transpose().times(p).getArray()[0][0];
				p= p.times(1/Math.sqrt(pNormal));

				//
				double pMultiply= 1/p.transpose().times(p).getArray()[0][0];
				tMatrix=E.times(p).times(pMultiply);
				eigenNew= tMatrix.transpose().times(tMatrix).getArray()[0][0];

			} while(Math.abs(eigenNew- eigenOld) > NIPAL_THRESHOLD*eigenNew);


			System.out.println("--------T"+(i+1)+"----------");

			print(tMatrix.getArray());

			System.out.println("--------P"+(i+1)+"----------");
			print(p.getArray());

			E= E.minus(tMatrix.times(p.transpose()));


		}


	}



	/**
	 * Prints the input matrix
	 * @param matrix
	 */
	private void print(double[][] matrix) {
		for(int i=0;i<matrix.length;i++){
			for(int j=0;j<matrix[0].length;j++){
				System.out.print(matrix[i][j]+" ");
			}
			System.out.println();
		}

		System.out.println("");
	}


	
	public Matrix computeRowCentre(Matrix m) {
		final double[][] data = m.getArray();

		final double[] mean = new double[data.length];

		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data[0].length; j++){
				mean[j] += data[i][j];
			}

		}


		for (int i = 0; i < data.length; i++)
			mean[i] /= data[0].length;

		final Matrix mat = new Matrix(data.length, data[0].length);
		final double[][] matdat = mat.getArray();

		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data[0].length; j++){
				matdat[i][j] = (data[i][j] - mean[i]);
			}

		}

		System.out.println("MEAN CENTER R");
		print(matdat);

		return new Matrix(matdat);
	}


	/**
	 * Mean centered Matrix the input matrix so that each column is centered at 0.
	 * @param Matrix
	 * @return matrix mean centered matrix
	 */
	public  Matrix computeMeanCenteredC(Matrix m) {
		final double[][] data = m.getArray();
		final double[] mean = new double[data[0].length];

		for (int j = 0; j < data[0].length; j++)
			for (int i = 0; i < data.length; i++)
				mean[j] += data[i][j];

		for (int i = 0; i < data[0].length; i++)
			mean[i] /= data.length;

		final Matrix mat = new Matrix(data.length, data[0].length);
		final double[][] matdat = mat.getArray();

		for (int j = 0; j < data[0].length; j++)
			for (int i = 0; i < data.length; i++)
				matdat[i][j] = data[i][j] - mean[j];


		return new Matrix(matdat);
	}





}
