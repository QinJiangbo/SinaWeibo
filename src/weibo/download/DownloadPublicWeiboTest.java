package weibo.download;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class DownloadPublicWeiboTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException, InterruptedException {
		// TODO Auto-generated method stub
		DownLoadPublicWeibo dWeibo=new DownLoadPublicWeibo();
		dWeibo.init();
		//dWeibo.getPublicWeibo("https://api.weibo.com/2/statuses/public_timeline.json?access_token=2.00tPxW1EjP4mME553da261baHdEz3D");
		dWeibo.getPublicWeibo(5*1000);
	}

}
