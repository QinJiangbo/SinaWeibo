package weibo.preprocessing;

import java.io.IOException;

public class PreprocessingTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Preprocessing preprocessing=new Preprocessing();
		preprocessing.init();
		//preprocessing.process();
		preprocessing.processForMoreInfo();
		

	}

}
