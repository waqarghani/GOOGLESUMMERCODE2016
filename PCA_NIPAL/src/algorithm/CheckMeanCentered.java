package algorithm;

import java.io.FileNotFoundException;
import Jama.Matrix;




public class CheckMeanCentered {

	public static void main(String[] args) throws FileNotFoundException {
		
		
		double[][] b={{1, 4, 7},{2, 5, 8},{3, 6, 9}};
		
		NipalPca pca= new NipalPca();
		


		System.out.println("-----------Row CENTERED---------------");
		pca.computeRowCentre(new Matrix(b));
		
		System.out.println("-----------CCentered---------------");
		pca.computeMeanCenteredC(new Matrix(b));

		
	}

}
