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
 * @author coderwang 新浪微博模拟登陆机器人 2014/1/6 10:45 20140328日搞定，主要问题是登录之后重定向页面也需要抓取
 * */

public class WeiboRobot {

	private CloseableHttpClient client;
	private String username; // 登录帐号(明文)
	private String password; // 登录密码(明文)
	private String su = ""; // 登录帐号(Base64加密)
	private String sp; // 登录密码(各种参数RSA加密后的密文)
	private long servertime; // 初始登录时，服务器返回的时间戳,用以密码加密以及登录用
	private String nonce; // 初始登录时，服务器返回的一串字符，用以密码加密以及登录用
	private String rsakv; // 初始登录时，服务器返回的一串字符，用以密码加密以及登录用
	private String pubkey; // 初始登录时，服务器返回的RSA公钥
	private String errInfo; // 登录失败时的错误信息
	
	public boolean good=true;//robot是可用的
	private BasicCookieStore cookieStore = null;// 存储cookie用
	
	
	public WeiboRobot(WeiboRobot robot){
		 cookieStore=robot.getCookieStore();
		 client = HttpClients.custom().setDefaultCookieStore(cookieStore)
				.build();
		this.username = robot.getUsername();
		this.password = robot.getPassword();
	}

	public WeiboRobot(String username, String password) {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();//多线程
	    cm.setMaxTotal(5);
		cookieStore = new BasicCookieStore();
		client = HttpClients.custom().setDefaultCookieStore(cookieStore)
				.setConnectionManager(cm)
				.build();
		
		this.username = username;
		this.password = password;
	}

	/**
	 * 从respose 中获取响应文本信息
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
			entity=null;//防止内存泄露
			response=null;
		}
		return content;
	}

	/**
	 * 从respose 中获取响应文本信息
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
				entity=null;//防止内存泄露
				response=null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

	/**
	 * 初始登录信息<br>
	 * 返回false说明初始失败
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
			JSONObject json = JSONObject.parseObject(content);// 获取相关的参数用于加密登录
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
	 * 登录
	 * 
	 * @return true:登录成功
	 */

	public boolean login() {
		if (preLogin()) {

			String url = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.11)";
			// 设置参数
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
				 * 第一次访问
				 */
				CloseableHttpResponse response = postRequest(client, url.trim(), parms);
				String html = getContentGBK(response);
				//Log.addLog(html);
				// 获取重定向的url
				Pattern p = Pattern.compile("location.replace\\(.*?\\)");
				Matcher m=null ;
				if(html!=null)
					m = p.matcher(html);
				String redirect_uri = "";
				if (m!=null&&m.find()) {
					redirect_uri = m.group(0); // retcode=0 说明正常，继续访问重定向url
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
	 * 密码进行RSA加密<br>
	 * 返回false说明加密失败
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
				Log.addLog("加密脚本encoder.js未找到");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		errInfo = "密码加密失败";
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
	 * 获取时间戳
	 * 
	 * @return
	 */

	private long getTimestamp() {
		Date now = new Date();
		return now.getTime();
	}

	/**
	 * 正常GET方式HTTP请求
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
	 * 正常POST方式HTTP请求
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
	 * 获取某个页面的内容
	 * 
	 * @param url
	 *            网页原地址
	 * @return html页面
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String fetchOnePage(String url) {
		String content = "";
		try {
			System.out.println(this.username + " 获取" + url);
			content = getContentUTF(getRequest(client, url.trim()));
			if (content.contains("location.replace(\"http://weibo.com/sso/login.php")) {// 包含重定向页面
				String html = content;
				// 获取重定向的url
				Pattern p = Pattern.compile("location.replace\\(.*?\\)");
				Matcher m = p.matcher(html);
				String redirect_uri = "";
				if (m.find()) {
					redirect_uri = m.group(0); // retcode=0 说明正常，继续访问重定向url
					int start_index = redirect_uri.indexOf('\"');
					redirect_uri = redirect_uri.substring(start_index + 1,
							redirect_uri.length() - 2);
					Log.addLog("重定向页面：" + redirect_uri);
				}
				if (redirect_uri.indexOf("retcode=0") > -1) {
					content = getContentUTF(getRequest(client, redirect_uri));
				}
			}
			if (content.contains("验证码")) {
				Log.addLog("[验证码]" + this.getUsername() + " " + url + "输入验证码");
			} else if (content.contains("您的帐号存在异常")) {
				Log.addLog("[robot error]" + this.getUsername() + "帐号异常，暂时无法访问");
				this.good = false;// 账号异常
			} else if (content.contains("抱歉，你访问的页面地址有误，或者该页面不存在")) {
				Log.addLog(this.getUsername() + url + " : "
						+ "抱歉，你访问的页面地址有误，或者该页面不存在");
			} else if (content.contains("您当前访问的帐号异常")) {
				Log.addLog(this.getUsername() + url + " : " + "您当前访问的帐号异常");
			} else if (content.contains("--注册登录header--")) {
				Log.addLog("[robot error]" + this.getUsername() + "重新登陆");
				cookieStore.clear();
				if (!login()) {
					Log.addLog("[robot error]" + this.getUsername() + "重新登录失败");
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
			// 进行内存回收
		}
		return content;
	}
}
