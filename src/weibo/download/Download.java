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
 * ���صĳ�����
 * */

public abstract class Download {
	
	public CloseableHttpClient   httpclient;
	public HttpGet httpGet ;//get������ȡ
	public CloseableHttpResponse response = null;
	public HttpEntity entity;
	public String content;//����ȡ��������
	public String url="";//��Ӧҳ���url
	
	public Download(){
		this.httpclient=HttpClients.createDefault();
	}
	
	public void startDownload (){//��ʼ����
		
	}
	public void endDownload() throws IOException{//��������
		 response.close();
		 httpclient.close();
	}

}
