package weibo.util;

import org.apache.http.client.methods.HttpGet;
/**
 *@author coderwang 
 *2014/1/10
 *对下载的一些共性的设置 
 * */
public class DownloadUtils {
	
	/**
	 * 增加head参数
	 * @param httpGet get请求
	 * */
     public  static  String cookie="tgc=TGT-Mzk2NzkxMjMyNQ==-1395972121-gz-C1F37C7314FAE6C6C45EF65527F05BAA; domain=login.sina.com.cn; path=/;  SUS=SID-3967912325-1395972121-GZ-ti3jc-4733df6d0055c593a52ed6d4ef1d1b20; path=/; domain=.sina.com.cn;  SUE=es%3D7f3c0f7bf8f2c19f39a334841646ef0a%26ev%3Dv1%26es2%3Dc244e9da180d95e12b702d918cf89bb7%26rs0%3DR4CHeA6FPVs7EqAwZTirEl22JmkZUnDbjYhEanCL9FkNnD13WNidduKURySZHOw1NjyIkwYF%252Fbpgkzb7YlxS2aQnDOKv2m3TCIOxGq264mrqL1o9jOtvPKWg%252BAv%252B1w5czNPqnQ6%252FIrwmZKTB3E%252Bb9KimWzEPBxkxNwa2nVfMwV0%253D%26rv%3D0;path=/;domain=.sina.com.cn; SUP=cv%3D1%26bt%3D1395972121%26et%3D1396058521%26d%3D40c3%26i%3D1b20%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D0%26st%3D0%26lt%3D1%26uid%3D3967912325%26user%3Disswang%2540live.cn%26ag%3D4%26name%3Disswang%2540live.cn%26nick%3D%25E7%25A0%2581%25E5%2586%259C%25E5%2581%259A%25E6%25AF%2595%25E8%25AE%25BE%26sex%3D1%26ps%3D0%26email%3D%26dob%3D%26ln%3Disswang%2540live.cn%26os%3D%26fmp%3D%26lcp%3D;path=/;domain=.sina.com.cnSUB=AR0LgFXE3%2BYCHAPvyojwjImPV1QHbw1hkFf128fUJds%2BKGwkza8zOvLyZL79ZvrXtZcnNnb%2BkJQghzkK3iWMmJO7RyBM0U%2BcGsOXsQkaiVKa%2FcJ1lCoIv4mjnNXCHrfcpEFYcUPMbutziFl1KnPgrqk%3D; expires=Saturday, 28-Mar-15 02:02:01 GMT; path=/; domain=.sina.com.cn;  SUBP=002A2c-gVlwEm1uAWxfgXELuuu1xVxBxAADS8hbG3ka94zhuj7vhE9U; expires=Saturday, 28-Mar-15 02:02:01 GMT; path=/; domain=.sina.com.cnALC=ac%3D0%26bt%3D1395972121%26cv%3D5.0%26et%3D1398564121%26uid%3D3967912325%26vf%3D0%26vt%3D0%26es%3D3dc55f7e3557456ca2b76bc05d97a112; expires=Sunday, 27-Apr-14 02:02:01 GMT; path=/; domain=login.sina.com.cn;  ALF=1398564121; expires=Sunday, 27-Apr-14 02:02:01 GMT; path=/; domain=.sina.com.cnLT=1395972121; path=/; domain=login.sina.com.cnsso_info=v02m6alo5qztKWRk5ylkJOApY6DhKWRk5SljoOYpY6UjKWRk5SljoOEpY6UhKWRk5ilkJSYpY6TlKWRk6ClkJSUpZCklKadlqWkj5OMuY2jnLmMk4izjKOUwA==; expires=Saturday, 28-Mar-15 02:02:01 GMT; path=/; domain=.sina.com.cn";  
     
    public static  void SetCookie(String cookie1){
    	cookie=cookie1;
    }
	public  static void addGetHeaders(HttpGet httpGet){
		 httpGet.addHeader("Connection", "keep-alive");
	     httpGet.addHeader("Content-Type","application/x-www-form-urlencoded");
		 httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
		 httpGet.addHeader("Cookie",cookie);
	}

}
