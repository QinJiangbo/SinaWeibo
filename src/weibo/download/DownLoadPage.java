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
 * ����ĳҳ����
 */
public class DownLoadPage extends Download {
	
	public DownLoadPage(){
		super();
	}
	
	public void setUrl(String url){
		this.url=url;
	}
	/**
	 * ��ȡĳһ��ҳԴ����
	 * @return Դ����
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
		 response = httpclient.execute(httpGet);//������ܻ������֤�룬���µ�¼��ҳ�治���ڵ�����
		 entity=response.getEntity();
		 html=EntityUtils.toString(entity,"utf-8");
		 EntityUtils.consume(entity);	
		 return html;
	}
	
	
	

}
