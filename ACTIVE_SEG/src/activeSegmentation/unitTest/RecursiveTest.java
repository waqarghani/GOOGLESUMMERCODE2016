package activeSegmentation.unitTest;

import java.util.concurrent.ForkJoinPool;

public class RecursiveTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ForkJoinPool forkJoinPool = new ForkJoinPool(4);
		MyRecursiveTask myRecursiveTask = new MyRecursiveTask(128);

		long mergedResult = forkJoinPool.invoke(myRecursiveTask);

		System.out.println("mergedResult = " + mergedResult);  
	}

}
