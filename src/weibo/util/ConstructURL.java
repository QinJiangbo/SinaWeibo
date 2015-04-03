package weibo.util;

/**
 * @author coderwang
 * 2014/1/10
 * �����Լ���url
 * */
public class ConstructURL {
	
	/**
	 * 
	 * �����Ӧ������url
	 * eg:http://s.weibo.com/wb/�����xsort=time&timescope=custom:2013-12-10-18:2014-01-2-18&page=50
	 * %E5%8C%97%E5%A4%A7%E8%8D%92&(�Զ�ת��Ľ��)
	 * @param searchItem ��������
	 * @param sortType �������� ʱ��������ȶ�
	 * @param timescope ʱ���� ����2013-12-10-18:2014-01-2-18����-��-��-Сʱ��
	 * @param page Num �ڼ�ҳ �����᷵�ظ�ʱ�����ڵ�50ҳ������
	 * @return ��������url
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
	 * ����ĳһҳת���б��json��ʽ���ݵ�����url
	 * eg:http://weibo.com/aj/mblog/info/big?_wv=5&id=3664072912104801&max_id=3664850242417143&filter=0&page=6&__rnd=1389257674022
	 * @param _wv =5
	 * @param id
	 * @param max_id id��max_id ������֮ǰ��֮ǰ��ҳ������ȡ
	 * @param filter ����ת������ͨת���б�
	 * @param pageNum �ڼ�ҳ
	 * @param _rnd ʱ��� �����
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
	 * �����û���ҳ��url,������ȡ�û���Ϣ
	 * @param uid �û�id
	 * @return url �û���ҳurl
	 * */
	public static String getUserHomeUrl(String uid){
		String url="http://weibo.com/u/"+uid;
		return url;
	}
	
	/**
	 * ����ĳһ�������΢����ַ
	 * @param date
	 * @return
	 */
	public static String getHotWeiboUrl(String date){	
		return "http://hot.weibo.com/daily/"+date;	
	}
	

	/**
	 * 
	 * @param page ҳ��
	 * @param rnd �����-ʱ���
	 * @return
	 */
	public static String getHotWeiboUrl(int page,long rnd){
		return "http://hot.weibo.com/ajax/feed?type=h&v=9999&date=&page="+page+"&topic_id=&cur_order=&_t=0&__rnd="+rnd;	
	}
	
	/**
	 * ��ȡʵʱ����΢����url
	 * @param token ��Ȩ����
	 * @return
	 */
	
	public static String getPublicUrl(String token){
		return "https://api.weibo.com/2/statuses/public_timeline.json?access_token="+token;
	}

}
