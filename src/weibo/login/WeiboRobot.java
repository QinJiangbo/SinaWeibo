package weibo.login;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import weibo.crawler.Log;
import com.alibaba.fastjson.JSONObject;

/**
 * @author coderwang ����΢��ģ���½������ 2014/1/6 10:45 20140328�ո㶨����Ҫ�����ǵ�¼֮���ض���ҳ��Ҳ��Ҫץȡ
 * */

public class WeiboRobot {

	private CloseableHttpClient client;
	private String username; // ��¼�ʺ�(����)
	private String password; // ��¼����(����)
	private String su = ""; // ��¼�ʺ�(Base64����)
	private String sp; // ��¼����(���ֲ���RSA���ܺ������)
	private long servertime; // ��ʼ��¼ʱ�����������ص�ʱ���,������������Լ���¼��
	private String nonce; // ��ʼ��¼ʱ�����������ص�һ���ַ���������������Լ���¼��
	private String rsakv; // ��ʼ��¼ʱ�����������ص�һ���ַ���������������Լ���¼��
	private String pubkey; // ��ʼ��¼ʱ�����������ص�RSA��Կ
	private String errInfo; // ��¼ʧ��ʱ�Ĵ�����Ϣ
	
	public boolean good=true;//robot�ǿ��õ�
	private BasicCookieStore cookieStore = null;// �洢cookie��
	
	
	public WeiboRobot(WeiboRobot robot){
		 cookieStore=robot.getCookieStore();
		 client = HttpClients.custom().setDefaultCookieStore(cookieStore)
				.build();
		this.username = robot.getUsername();
		this.password = robot.getPassword();
	}

	public WeiboRobot(String username, String password) {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();//���߳�
	    cm.setMaxTotal(5);
		cookieStore = new BasicCookieStore();
		client = HttpClients.custom().setDefaultCookieStore(cookieStore)
				.setConnectionManager(cm)
				.build();
		
		this.username = username;
		this.password = password;
	}

	/**
	 * ��respose �л�ȡ��Ӧ�ı���Ϣ
	 * 
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	public String getContentGBK(CloseableHttpResponse response) throws IOException {
		HttpEntity entity=null;
		if(response.getStatusLine().getStatusCode()>=200&&response.getStatusLine().getStatusCode()<300)
		  entity = response.getEntity();
		String content = null;
		try {
			if(entity!=null){
				content = EntityUtils.toString(entity, "GBK");
				EntityUtils.consume(entity);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(entity!=null){
				entity.getContent().close();
			}
			response.close();
			entity=null;//��ֹ�ڴ�й¶
			response=null;
		}
		return content;
	}

	/**
	 * ��respose �л�ȡ��Ӧ�ı���Ϣ
	 * 
	 * @param response
	 * @return
	 */
	public synchronized String  getContentUTF(CloseableHttpResponse response) {
		HttpEntity entity=null;
		int code=response.getStatusLine().getStatusCode();
		if(code>=200&&code<300)
		  entity = response.getEntity();
		String content = null;
		try {
			if(entity!=null){
				content = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(entity!=null){
					entity.getContent().close();
				}
				response.close();
				entity=null;//��ֹ�ڴ�й¶
				response=null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

	/**
	 * ��ʼ��¼��Ϣ<br>
	 * ����false˵����ʼʧ��
	 * 
	 * @return
	 */

	public boolean preLogin() {
		boolean flag = false;
		try {
			su = Base64.encodeBase64String(URLEncoder.encode(username, "UTF-8")
					.getBytes());
			String url = "http://login.sina.com.cn/sso/prelogin.php?"
					+ "entry=weibo"
					+ "&callback=sinaSSOController.preloginCallBack" + "&su="
					+ su + "&rsakt=mod&client=ssologin.js(v1.4.11)&_="
					+ getTimestamp();
			String content;
			content = getContentGBK(getRequest(client, url.trim()));
			int startIndex = content.indexOf("(");
			content = content.substring(startIndex + 1, content.length() - 1);
			JSONObject json = JSONObject.parseObject(content);// ��ȡ��صĲ������ڼ��ܵ�¼
			servertime = json.getLongValue("servertime");
			nonce = json.getString("nonce");
			rsakv = json.getString("rsakv");
			pubkey = json.getString("pubkey");
			flag = encodePwd();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * ��¼
	 * 
	 * @return true:��¼�ɹ�
	 */

	public boolean login() {
		if (preLogin()) {

			String url = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.11)";
			// ���ò���
			List<NameValuePair> parms = new ArrayList<NameValuePair>();
			parms.add(new BasicNameValuePair("entry", "weibo"));
			parms.add(new BasicNameValuePair("gateway", "1"));
			parms.add(new BasicNameValuePair("from", ""));
			parms.add(new BasicNameValuePair("savestate", "7"));
			parms.add(new BasicNameValuePair("useticket", "1"));
			parms.add(new BasicNameValuePair(
					"pagerefer",
					"http://login.sina.com.cn/sso/logout.php?entry=miniblog&r=http%3A%2F%2Fweibo.com%2Flogout.php%3Fbackurl%3D%252F"));
			parms.add(new BasicNameValuePair("vsnf", "1"));
			parms.add(new BasicNameValuePair("su", su));
			parms.add(new BasicNameValuePair("service", "miniblog"));
			parms.add(new BasicNameValuePair("servertime", servertime + ""));
			parms.add(new BasicNameValuePair("nonce", nonce));
			parms.add(new BasicNameValuePair("pwencode", "rsa2"));
			parms.add(new BasicNameValuePair("rsakv", rsakv));
			parms.add(new BasicNameValuePair("sp", sp));
			parms.add(new BasicNameValuePair("encoding", "UTF-8"));
			parms.add(new BasicNameValuePair("prelt", "187"));
			parms.add(new BasicNameValuePair(
					"url",
					"http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
			parms.add(new BasicNameValuePair("returntype", "META"));

			try {
				/*
				 * ��һ�η���
				 */
				CloseableHttpResponse response = postRequest(client, url.trim(), parms);
				String html = getContentGBK(response);
				//Log.addLog(html);
				// ��ȡ�ض����url
				Pattern p = Pattern.compile("location.replace\\(.*?\\)");
				Matcher m=null ;
				if(html!=null)
					m = p.matcher(html);
				String redirect_uri = "";
				if (m!=null&&m.find()) {
					redirect_uri = m.group(0); // retcode=0 ˵�����������������ض���url
					int start_index = redirect_uri.indexOf('\'');
					redirect_uri = redirect_uri.substring(start_index + 1,
							redirect_uri.length() - 1);
				}
				if (redirect_uri.indexOf("retcode=0") > -1) {
					HttpGet get = new HttpGet(redirect_uri);
					response = client.execute(get);
					return true;
				}
				response.close();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				
			}
		}
		return false;
	}

	/**
	 * �������RSA����<br>
	 * ����false˵������ʧ��
	 * 
	 * @return
	 */

	private boolean encodePwd() {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("javascript");
		try {
			se.eval(new FileReader("./\\encoder\\encoder.js"));
			Invocable invocableEngine = (Invocable) se;
			String callbackvalue = (String) invocableEngine.invokeFunction(
					"encodePwd", pubkey, servertime, nonce, password);
			sp = callbackvalue;
			return true;
		} catch (FileNotFoundException e) {
			try {
				Log.addLog("���ܽű�encoder.jsδ�ҵ�");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		errInfo = "�������ʧ��";
		return false;
	}

	public String getErrInfo() {
		return errInfo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * ��ȡʱ���
	 * 
	 * @return
	 */

	private long getTimestamp() {
		Date now = new Date();
		return now.getTime();
	}

	/**
	 * ����GET��ʽHTTP����
	 * 
	 * @param client
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public CloseableHttpResponse getRequest(CloseableHttpClient client, String url)
			throws ClientProtocolException, IOException {
		

		RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(600000)
                .setConnectTimeout(600000)
                .setConnectionRequestTimeout(600000)
                .build();
		
		HttpGet get = new HttpGet(url.trim());
		get.setConfig(requestConfig);
		get.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
		
		CloseableHttpResponse response = client.execute(get);
		return response;
	}

	/**
	 * ����POST��ʽHTTP����
	 * 
	 * @param client
	 * @param url
	 * @param parms
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public CloseableHttpResponse postRequest(CloseableHttpClient client, String url,
			List<NameValuePair> parms) throws ClientProtocolException,
			IOException {
		HttpPost post = new HttpPost(url.trim());
		post.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(parms, "GBK");
		post.setEntity(postEntity);
		CloseableHttpResponse response = client.execute(post);
		post.releaseConnection();
		return response;
	}

	public BasicCookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(BasicCookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	/**
	 * ��ȡĳ��ҳ�������
	 * 
	 * @param url
	 *            ��ҳԭ��ַ
	 * @return htmlҳ��
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String fetchOnePage(String url) {
		String content = "";
		try {
			System.out.println(this.username + " ��ȡ" + url);
			content = getContentUTF(getRequest(client, url.trim()));
			if (content.contains("location.replace(\"http://weibo.com/sso/login.php")) {// �����ض���ҳ��
				String html = content;
				// ��ȡ�ض����url
				Pattern p = Pattern.compile("location.replace\\(.*?\\)");
				Matcher m = p.matcher(html);
				String redirect_uri = "";
				if (m.find()) {
					redirect_uri = m.group(0); // retcode=0 ˵�����������������ض���url
					int start_index = redirect_uri.indexOf('\"');
					redirect_uri = redirect_uri.substring(start_index + 1,
							redirect_uri.length() - 2);
					Log.addLog("�ض���ҳ�棺" + redirect_uri);
				}
				if (redirect_uri.indexOf("retcode=0") > -1) {
					content = getContentUTF(getRequest(client, redirect_uri));
				}
			}
			if (content.contains("��֤��")) {
				Log.addLog("[��֤��]" + this.getUsername() + " " + url + "������֤��");
			} else if (content.contains("�����ʺŴ����쳣")) {
				Log.addLog("[robot error]" + this.getUsername() + "�ʺ��쳣����ʱ�޷�����");
				this.good = false;// �˺��쳣
			} else if (content.contains("��Ǹ������ʵ�ҳ���ַ���󣬻��߸�ҳ�治����")) {
				Log.addLog(this.getUsername() + url + " : "
						+ "��Ǹ������ʵ�ҳ���ַ���󣬻��߸�ҳ�治����");
			} else if (content.contains("����ǰ���ʵ��ʺ��쳣")) {
				Log.addLog(this.getUsername() + url + " : " + "����ǰ���ʵ��ʺ��쳣");
			} else if (content.contains("--ע���¼header--")) {
				Log.addLog("[robot error]" + this.getUsername() + "���µ�½");
				cookieStore.clear();
				if (!login()) {
					Log.addLog("[robot error]" + this.getUsername() + "���µ�¼ʧ��");
				}
			} else if (content.equals("")) {
				cookieStore.clear();
				int count = 5;
				while (count-- > 0) {
					content = getContentUTF(getRequest(client, url.trim()));
					if (content.length() > 10) {
						break;
					}
				}
				if (content.length() < 10) {
					if (!login()) {
						good = false;
					} else {
						Log.addLog("[relogin]" + getUsername() + "success");
						content = getContentUTF(getRequest(client, url.trim()));
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// �����ڴ����
		}
		return content;
	}
}
