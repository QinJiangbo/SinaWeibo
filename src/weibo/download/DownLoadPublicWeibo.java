package weibo.download;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;


import weibo.io.DAO;
import weibo.util.ConstructURL;
import weibo.util.GetWeiboUrlId;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 通过微博API 获取公共微博
 * 2014/5/16
 * @author coderwang
 *
 */
public class DownLoadPublicWeibo {
	
	public DownLoadPage downLoad=null;
	public ArrayList<String>tokens =null;
	public DAO dao=null;
	public int indexOfToken=0;
	
	/**
	 * 初始化相关设置
	 */
	public void init(){
		downLoad=new DownLoadPage();
		dao=new DAO();
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root","wangxiaoyi@1244");
		tokens=new ArrayList<>();
		tokens.add("2.00tPxW1E4J7zUD77cbfb370e8rhrGC");
		tokens.add("2.00qnNXYF4J7zUD1aa5b87a480dSu8q");
		tokens.add("2.00opg4YF4J7zUDbbaff3bd80rRu5ND");
		tokens.add("2.00WKOXYF4J7zUD59b343d45eWc_cCC");
		tokens.add("2.00w3OXYF4J7zUD83c9331983JWFpGC");
		tokens.add("2.00e_h4YF4J7zUDed2b9bb375kVgnwB");
		tokens.add("2.004inJZF4J7zUD90f77ca62f0QaiWE");
		tokens.add("2.00HQQTYF4J7zUD0a2d3ffbe4P9yKSB");
		/*tokens.add("");
		tokens.add("");
		tokens.add("");
		tokens.add("");
		tokens.add("");
		tokens.add("");*/
		
	}

	/**
	 * 轮询的方式获取token
	 * @return
	 */
	public String getToken(){
		String token=null;
		int size=tokens.size();
		token=tokens.get(indexOfToken%size);
		indexOfToken=(++indexOfToken)%size;
		return token;
	}
	
	/**
	 * 获取公共微博
	 * @param timestamp
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws InterruptedException 
	 */
	public void getPublicWeibo(long timestamp) throws ClientProtocolException, IOException, InterruptedException{
		String url="";
		ArrayList<String>mids=new ArrayList<>();
		int count=1;
		while (true){
			 url=ConstructURL.getPublicUrl(getToken());
			 System.out.println(count+" : "+url);
			 mids.addAll(getPublicWeibo(url));
			 for(String s:mids){
				 System.out.println(s);
			 }
			 dao.Insert("public_weibo_seeds", mids);
			 mids.clear();
			 Thread.sleep(timestamp);//暂停
			 count ++;
		}
	}

	/**
	 * 
	 * @param url api url 例如 https://api.weibo.com/2/statuses/public_timeline.json?access_token=2.00tPxW1EjP4mME553da261baHdEz3D
	 * @return 返回查询列表
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * 
	 */
	public ArrayList<String> getPublicWeibo(String url) throws ClientProtocolException, IOException{
		ArrayList<String>mids=new ArrayList<>();
		String html=downLoad.getOnePage(url);
		JSONObject object=JSONObject.parseObject(html);
		html=object.getString("statuses");
		JSONArray jArray=JSONArray.parseArray(html);
		JSONObject uObject=null;
		String id="";
		String mid="";
		String uid="";
		if(jArray!=null){
			for(int i=0;i<jArray.size();++i){
				id=((JSONObject)jArray.get(i)).getString("id");//微博id
				mid = GetWeiboUrlId.Id2Mid(id);//将id用64进制进行表示
				uObject=JSONObject.parseObject(((JSONObject)jArray.get(i)).getString("user"));
				uid=uObject.getString("id");//用户id
				String wurl=" http://weibo.com/"+uid+"/"+mid;
				mids.add(wurl);		
			}
		}else{
			System.out.println("api下载出错");
		}
		return mids;
	}
	
	

}
