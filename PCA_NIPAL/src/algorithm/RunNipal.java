package algorithm;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;




public class RunNipal {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		File file =new File(RunNipal.class.getResource("pcadata.txt").getFile());
		System.out.println(file.exists());
		Scanner input = new Scanner (file);

		int m = 10;
		int n = 5;
		double[][] a = new double [m][n];
		while (input.hasNext()){

			for (int i=0;i<m;i++){
				for (int j=0;j<n;j++)
					a[i][j]= Double.parseDouble(input.next());
			}   

		}
		//print the input matrix
		System.out.println("The input matrix is : ");
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){
				System.out.print(a[i][j]+" ");
			}
			System.out.println();
		}


		System.out.println("-----------------------------");
		NipalPca pca= new NipalPca();
		pca.runNipal(3, a);




	}

}
