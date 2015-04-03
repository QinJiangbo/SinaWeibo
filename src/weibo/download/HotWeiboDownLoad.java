package weibo.download;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;
import weibo.login.WeiboRobotManager;
import weibo.prase.PrasePage;
import weibo.util.ConstructRND;
import weibo.util.ConstructURL;
import com.alibaba.fastjson.JSONObject;

/**
 * 下载一天中的每个小时的所有热门微博
 * 
 * @author coderwang
 *
 */


public class HotWeiboDownLoad {
	private static DownLoadPage downLoadPage=null;
	private  static PrasePage prase=null;
	/**
	 * @param args
	 */
	
	public HotWeiboDownLoad(){
		
	}
	
	public static void init() throws IOException{
		downLoadPage=new DownLoadPage();
		prase=new PrasePage();
		WeiboRobotManager.init();
	}
	public static void main(String[] args) throws ClientProtocolException, IOException {
		init();
		ArrayList<String>urls=new ArrayList<>();
		int count =10;
		String url="";
		for(int page=1;page<=count;++page){
			url=ConstructURL.getHotWeiboUrl(page, ConstructRND.getTimeStamp());
			System.out.println(url);
			urls=getUrls(url);
			System.out.println(urls.size());
			for(String s:urls){
				System.out.println(s);
			}
			urls.clear();
		}

	}

	
	/**
	 * 获取10条微博url 返回最后一条微博的mid
	 * @param urls 返回的微博地址列表
	 * @param url 加载数据的地址
	 * @return 返回最后一条微博的mid
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static ArrayList<String> getUrls(String url) throws ClientProtocolException, IOException{
		downLoadPage.setUrl(url);
		String html=downLoadPage.getOnePage();
		JSONObject json=JSONObject.parseObject(html);
		String data=json.getString("data");
		json=JSONObject.parseObject(data);
		String mainHtml=json.getString("html");
		return prase.getWeibos(mainHtml);
	}

}
