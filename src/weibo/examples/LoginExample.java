package weibo.examples;

import java.io.IOException;



import org.apache.http.client.ClientProtocolException;
import weibo.login.WeiboRobotManager;



public class LoginExample {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException, InterruptedException {
		
//		WeiboRobot weiboRobot = new WeiboRobot("isswang@live.cn",
//				"wangxiaoyi@1244");
//		weiboRobot.login();
//
//		HttpClient httpClient = HttpClients.custom()
//				.setDefaultCookieStore(weiboRobot.getCookieStore()).build();
//		HttpResponse response = httpClient.execute(new HttpGet(
//				"http://weibo.com/dmonsns"));
		
		WeiboRobotManager.init();
		int count=20;
		while(count-->0){
			System.out.println(WeiboRobotManager.getRobot().getUsername());
		}
		
		
	}

}
