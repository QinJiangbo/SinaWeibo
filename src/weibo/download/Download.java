package weibo.download;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author coderwang
 * 2014/2/25
 * 下载的抽象类
 * */

public abstract class Download {
	
	public CloseableHttpClient   httpclient;
	public HttpGet httpGet ;//get方法获取
	public CloseableHttpResponse response = null;
	public HttpEntity entity;
	public String content;//所获取到的内容
	public String url="";//对应页面的url
	
	public Download(){
		this.httpclient=HttpClients.createDefault();
	}
	
	public void startDownload (){//开始下载
		
	}
	public void endDownload() throws IOException{//结束下载
		 response.close();
		 httpclient.close();
	}

}
