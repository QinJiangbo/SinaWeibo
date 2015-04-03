package weibo.download;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import weibo.login.WeiboRobotManager;
import weibo.util.DownloadUtils;

/**
 * 20140326
 * @author coderwang
 * 下载某页内容
 */
public class DownLoadPage extends Download {
	
	public DownLoadPage(){
		super();
	}
	
	public void setUrl(String url){
		this.url=url;
	}
	/**
	 * 获取某一网页源代码
	 * @return 源代码
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getOnePage() throws ClientProtocolException, IOException{
		 String html="";
		 html=WeiboRobotManager.getRobot().fetchOnePage(url);
		 return html;
	}
	
	
	public String getOnePage(String url) throws ClientProtocolException, IOException{
		 String html="";
		 httpGet = new HttpGet(url);
		 DownloadUtils.addGetHeaders(httpGet);
		 response = httpclient.execute(httpGet);//这里可能会出现验证码，重新登录，页面不存在等问题
		 entity=response.getEntity();
		 html=EntityUtils.toString(entity,"utf-8");
		 EntityUtils.consume(entity);	
		 return html;
	}
	
	
	

}
