package weibo.download;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import weibo.io.WeiboWriter;
import weibo.login.WeiboRobotManager;
import weibo.model.RepostUrlComponent;
import weibo.prase.WeiboRepostPrase;
import weibo.util.ConstructRND;
import weibo.util.ConstructURL;

/**
 * @author coderwang 
 * 2014/1/10
 * 下载某条微博的全部转发微博
 * 分页下载
 * */

public class RepostDownload extends Download{
	
	private WeiboWriter writer;//保存内容的对应的写入类
	private int maxNum;//最大下载页面
	
	public RepostDownload(){
		super();
		writer=new WeiboWriter();
	}
	
	/**
	 * 
	 * */
	public void setMaxNum(int maxNum){
		this.maxNum=maxNum;	
	}
	
	/**
	 *开始下载的任务 
	 * @param _wv,id ,max_id,filter链接需要的一些设置
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * */
	public void startDownload(int _wv,String id,String max_id,int filter) throws ClientProtocolException, IOException{
		 for(int pageNum=1;pageNum<=maxNum;++pageNum){
			 
			 url=ConstructURL.getRepostUrl(_wv, id, max_id, filter, pageNum,ConstructRND.getTimeStamp());
			 System.out.println("开始下载第"+pageNum+"页>>>>>>"+url);
			 content=WeiboRobotManager.getRobot().fetchOnePage(url);		 
			 writer.setPath(nextPath(id,pageNum));
		     writer.write(new WeiboRepostPrase().getHtml(content));
		     System.out.println("第"+pageNum+"页下载完毕！");
		 }
		 System.out.println("下载结束");	 
	}
	
	/**
	 * 获取某一页转发的微博数
	 * @param _wv
	 * @param id
	 * @param max_id
	 * @param filter
	 * @param pageNum
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getOneRepostPage(int _wv, String id, String max_id, int filter,int pageNum) throws ClientProtocolException, IOException {
		String repostHtml = "";
		url = ConstructURL.getRepostUrl(_wv, id, max_id, filter, pageNum,
				ConstructRND.getTimeStamp());
		repostHtml=WeiboRobotManager.getRobot().fetchOnePage(url);
		WeiboRepostPrase wPrase=new WeiboRepostPrase();
		repostHtml=wPrase.getHtml(repostHtml);
		
		{//垃圾回收
			wPrase=null;
			url="";
			url=null;
		}
		return repostHtml;
	}
	/**
	 * 构造相应转发页面的存储文件路径
	 * @param mid 对应微博的id
	 * @param pageNum
	 * */
	public String nextPath(String mid,int pageNum){
		File file=new File("./data/weibo"+mid);
		file.mkdirs();
		return new String("./data/weibo"+mid+"/repostPage"+pageNum+".txt");
	}
	
	
	/**
	 * 根据微博的地址抓取微博所在页面
	 * @param weiboUrl 微博地址
	 * @return content 微博页面
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * */
	public String getOneWeiboPage(String weiboUrl) throws ClientProtocolException, IOException{
		 String content="";
		 content=WeiboRobotManager.getRobot().fetchOnePage(weiboUrl);
		 WeiboRepostPrase wPrase=new WeiboRepostPrase();
		 content= wPrase.getMainContent(content);
		 {
			wPrase=null;
		 }
		return content;	
	}
	
	
 	public ArrayList<String> getRepostMids(RepostUrlComponent component) throws ClientProtocolException, IOException{
 		ArrayList<String>mids=new ArrayList<String>();
 		WeiboRepostPrase prase=new WeiboRepostPrase();
 		ArrayList<String> temp=null;
 		System.out.println("获取"+component.getId()+" 转发微博");
		for (int pageNum = 1; pageNum <= component.getMaxPage(); ++pageNum) {
			url = ConstructURL.getRepostUrl(5, component.getId(), component.getMax_id(), 0, pageNum,ConstructRND.getTimeStamp());
			System.out.println(url);
			content=WeiboRobotManager.getRobot().fetchOnePage(url);
			content=prase.getHtml(content);
			temp=prase.getWeiboIds(content);
			for(String mid:temp){
				mids.add(mid);
			}
			{
				content="";
				url="";
				temp.clear();
			}
			
		}
		System.out.println("下载结束");	
		{
			prase=null;
			temp=null;
		}
		return mids;
 		
 	}
	

}
