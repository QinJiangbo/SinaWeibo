package weibo.examples;

import java.io.IOException;

import weibo.io.WeiboReader;
import weibo.model.User;
import weibo.prase.UserHomePagePrase;

public class UserInfoPraseTest {

	public static void main(String[] args) {
		WeiboReader reader = new WeiboReader("./data/common_test_data/data.txt");
		try {
			String htmlPage = reader.readWholeFile();
			//System.out.println(htmlPage);
			UserHomePagePrase prase=new UserHomePagePrase();
			User user=prase.getUser(htmlPage);
			System.out.println(user.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
