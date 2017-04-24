package learning.solutions.advanced.matrix.driver;

import learning.solutions.advanced.matrix.kafka.MatrixReceptor;

public class MatrixListeningPod {
	public static void main(String[] args) {
		System.out.println("Listening to the empty void of space .. ");
		MatrixReceptor soundPod;
		try {
			soundPod = new MatrixReceptor();
			soundPod.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
