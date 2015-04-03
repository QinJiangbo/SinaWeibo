package weibo.download;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;
import weibo.io.WeiboWriter;
import weibo.login.WeiboRobotManager;
import weibo.prase.UserHomePagePrase;
import weibo.util.ConstructURL;

/**
 * @author coderwang
 * 2014/2/15
 * 下载用户信息
 * */

public class UserInfoDownload extends Download {
	
	private WeiboWriter writer;//保存内容的对应的写入类
	private WeiboWriter error_writer;//记录出错的uid
	
	
	public UserInfoDownload(){
		super();
	}
	
	
	/**
	 * 开始下载
	 * @param uids 用户id列表
	 * @param mid 转发的微博id
	 * */
	public void startDownload(ArrayList<String>uids,String mid) {
		int countnum=0;
		for(String uid:uids){
			countnum++;
			if(countnum>=0){
			System.out.println(countnum);
			String homeUrl=ConstructURL.getUserHomeUrl(uid);
			System.out.println("下载"+countnum+"_"+homeUrl);
			String content="";
			int count=10;
			try {
				while(content.equals("")||content==null){
					try {
						content = getOneUserInfo(homeUrl);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					count--;
					if(count<0){
						System.out.println(homeUrl+"无法下载");
						error_writer=new WeiboWriter();
						error_writer.setPath(".\\data\\error_info\\user_home_page_download_error.txt");
						error_writer.append(homeUrl);
						break;
					}
				}
				String path=".\\data\\"+mid+"users";
				saveOneUserPage(nextPath(path, countnum+"_"+uid), content);	
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}
		
	}
	
	
	/**
	 * 获取单个用户的信息
	 * @param homeUrl 该用户的主页的url
	 * @return content //用户主页信息（仍然是未经过处理的html 源码）
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws InterruptedException 
	 * */
	public String getOneUserInfo(String homeUrl) throws ClientProtocolException, IOException, InterruptedException{
		 String content="";//页面内容	
		 content=WeiboRobotManager.getRobot().fetchOnePage(homeUrl);
		 if(content.contains("抱歉，你访问的页面地址有误，或者该页面不存在"))
			 return new String("");	
		 UserHomePagePrase uPrase=new UserHomePagePrase();
		 String tmp=uPrase.getMainContent(content);
		 if(tmp.equals("")||tmp==null){
			 System.out.println("获取主要用户信息不存在"+content);
			 int count=5;
			 while(count-->0){
				 content=WeiboRobotManager.getRobot().fetchOnePage(homeUrl);
				 tmp=uPrase.getMainContent(content);
				 if(tmp.length()>10){
					 break;
				 }
			 }
			 
		 }
		 {//资源回收
			 uPrase=null;
			 content="";
			 content=null;	 
		 }
		 return tmp;
	}
	
	/**
	 * 构造用户信息的下个存储路径
	 * @param path 文件所在文件夹路径
	 * @param uid 用户id
	 * @return 返回构造好的存储路径
	 * */
	public String nextPath(String path,String uid){
		File file=new File(path);
		file.mkdir();
		return new String(path+"\\"+uid+".txt");
	}
	
	/**
	 * @param path 文件所在文件夹路径
	 * @param content 用户主页信息
	 * */
	public void saveOneUserPage(String path,String content){
		writer=new WeiboWriter();
		writer.setPath(path);
		try {
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
