package weibo.download;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import weibo.util.DownloadUtils;

public class KeepAliveClient {
	
	public CloseableHttpClient   httpclient=null;
	public HttpGet httpGet=null ;//get������ȡ
	public CloseableHttpResponse response = null;
	public HttpEntity entity=null;

	
	public KeepAliveClient(){
		
		httpGet=new HttpGet();
		this.httpclient=HttpClients.createDefault();
	}

	
	public String getOnePage(String url) throws ClientProtocolException, IOException, URISyntaxException{
		String html="";
		URI uri = new URI(url);
		httpGet.setURI(uri);
		DownloadUtils.addGetHeaders(httpGet);	
	    response = httpclient.execute(httpGet);//������ܻ������֤�룬���µ�¼��ҳ�治���ڵ�����
	
	    entity=response.getEntity();
		html=EntityUtils.toString(entity,"utf-8");
		EntityUtils.consume(entity);	
		return html;
	}
}
