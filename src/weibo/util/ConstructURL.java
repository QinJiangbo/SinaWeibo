package weibo.util;

/**
 * @author coderwang
 * 2014/1/10
 * 构造自己的url
 * */
public class ConstructURL {
	
	/**
	 * 
	 * 构造对应搜索的url
	 * eg:http://s.weibo.com/wb/北大荒xsort=time&timescope=custom:2013-12-10-18:2014-01-2-18&page=50
	 * %E5%8C%97%E5%A4%A7%E8%8D%92&(自动转码的结果)
	 * @param searchItem 搜索内容
	 * @param sortType 排序类型 时间或者是热度
	 * @param timescope 时间间隔 例如2013-12-10-18:2014-01-2-18“年-月-日-小时”
	 * @param page Num 第几页 搜索会返回该时间间隔内的50页的数据
	 * @return 返回搜索url
	 * */
	public static String getSearchURL(String searchItem,String sortType,String timescope,int pageNum){
		String url="http://s.weibo.com/wb/";
				url+=searchItem+
				"&xsort="+sortType+
				"&timescope=custom:"+timescope+
				"&page="+pageNum;
		return url;
	}
	
	
	/**
	 * 构造某一页转发列表的json格式数据的请求url
	 * eg:http://weibo.com/aj/mblog/info/big?_wv=5&id=3664072912104801&max_id=3664850242417143&filter=0&page=6&__rnd=1389257674022
	 * @param _wv =5
	 * @param id
	 * @param max_id id和max_id 都可以之前从之前的页面中提取
	 * @param filter 热门转发和普通转发列表
	 * @param pageNum 第几页
	 * @param _rnd 时间戳 随机数
	 * 
	 * */
	public static String getRepostUrl(int _wv,String id,String max_id,int filter,int pageNum,long _rnd){
		String url="http://weibo.com/aj/mblog/info/big?" +
				"_wv=" +_wv+
				"&id=" +id+
				"&max_id="+max_id +
				"&filter=" +filter+
				"&page="+pageNum+
				"&__rnd="+_rnd;
		return url;
	}
	
	
	/**
	 * 构造用户主页的url,用于提取用户信息
	 * @param uid 用户id
	 * @return url 用户主页url
	 * */
	public static String getUserHomeUrl(String uid){
		String url="http://weibo.com/u/"+uid;
		return url;
	}
	
	/**
	 * 构造某一天的热门微博网址
	 * @param date
	 * @return
	 */
	public static String getHotWeiboUrl(String date){	
		return "http://hot.weibo.com/daily/"+date;	
	}
	

	/**
	 * 
	 * @param page 页数
	 * @param rnd 随机数-时间戳
	 * @return
	 */
	public static String getHotWeiboUrl(int page,long rnd){
		return "http://hot.weibo.com/ajax/feed?type=h&v=9999&date=&page="+page+"&topic_id=&cur_order=&_t=0&__rnd="+rnd;	
	}
	
	/**
	 * 获取实时公共微博的url
	 * @param token 授权参数
	 * @return
	 */
	
	public static String getPublicUrl(String token){
		return "https://api.weibo.com/2/statuses/public_timeline.json?access_token="+token;
	}

}
