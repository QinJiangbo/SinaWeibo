package weibo.crawler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;
import weibo.download.RepostDownload;
import weibo.download.UserInfoDownload;
import weibo.io.DAO;
import weibo.io.WeiboWriter;
import weibo.model.RepostUrlComponent;
import weibo.model.User;
import weibo.model.Weibo;
import weibo.prase.UserHomePagePrase;
import weibo.prase.WeiboRepostPrase;
import weibo.util.ConstructURL;

/**
 * 20140326
 * @author coderwang
 * 负责对某条微博转发信息的抓取
 * 包括用户信息抓取，转发的每条微博信息的抓取
 * 转发图的构建
 */
public class WeiboRepostCrawlTask implements Runnable {
	int id=0;
	private DAO dao=null;//数据库操作对象
	private CommanVariables variables=null;//共享变量
	private ArrayList<String>wuls=null;//weibo urls
	private RepostDownload repostDownload=null;//微博抓发下载类
	private WeiboRepostPrase wPrase=null;//微博转发信息解析
	private UserHomePagePrase uPrase=null;//用户信息解析
	private UserInfoDownload uDownload=null;//用户信息下载类	
	private String weiboTableName=null;
	private String userTableName=null;
	private Flag flag=null;
	private RepostUrlComponent component=null;
	
	
	public WeiboRepostCrawlTask(int id,DAO dao,CommanVariables variables,ArrayList<String>wurls){
		this.dao=dao;
		this.variables=variables;
		this.wuls=wurls;
		this.id=id;
		
		//相关类对象的初始化
		repostDownload=new RepostDownload();
		uDownload=new UserInfoDownload();
		uPrase=new UserHomePagePrase();
		wPrase=new WeiboRepostPrase();		
	}
	@Override
	public void run() {
		String path="./data/seedWeiboMids/nhotWeiboMids.txt";
		WeiboWriter writer=new WeiboWriter();
		writer.setPath(path);
	
		String url = null;
		while (variables.index < wuls.size()) {
			synchronized (wuls) {
				url = wuls.get(variables.index);
				variables.index++;
			}
			if (url != null && url != "") {
				try {
				Log.addLog(id+": 1、获取种子微博以及其发布者的基本信息"+url);
				// 1、获取种子微博以及其发布者的基本信息
				Weibo weibo = new Weibo();
				flag=new Flag();//标志类
				component=saveMainWeiboAndUser(url, weibo);
				flag.endDate = flag.endDate
						+ Long.parseLong(weibo.getCreatedAt()) + flag.timeSpane;
				Log.addLog(id+": 2、下载种子微博的所有转发微博");
				getRootWeiboRepostByEndTime(flag.endDate, weibo, component);
				int count=dao.getCount(weiboTableName);
				Log.addLog("[################################################]"+weibo.getMid()+" "+weibo.getUrl()+" "+count);
					Log.addLog(id+": 3、下载所有用户信息");
					flag.utName=userTableName;
					ArrayList<String> uids = getUids();
					if (uids != null) {
						for (int i = 0; i < 1; ++i) {//单线程
							UserInfoCrawlTask uTask = new UserInfoCrawlTask(id,i,
									dao, uPrase, uDownload, uids, flag);
							new Thread(uTask).start();
						}
					}
					Log.addLog(id+": 4、构造转发图");
					dao.updateRootSubWeibo(weibo, weiboTableName);
					ConstructGraph(flag.endDate);
					dao.constructGraph(userTableName, weiboTableName);
					flag.repostGraphConstructed=true;
					//flag.userInfoDowloaded=true;
					Log.addLog(id+": flag.repostGraphConstructed=true");
					while(!flag.thisWeiboDown()){
						System.out.println(id+"sleep 5 s");
						Thread.sleep(5000);
					}
					//Thread.sleep(20000);
					synchronized (dao) {
						Log.addLog(id+": 5、创建视图："+weibo.getMid()+"view");
						dao.createView(weibo.getMid());
						Log.addLog(id+": 6、更新种子微博url中的标志");
						//dao.updateSeeds(weibo.getUrl(), "hotWeiboSeeds");
						dao.updateSeeds(weibo.getUrl(), "public_weibo_seeds");
						writer.append(weibo.getMid());
					System.out.println("#############"+weibo.getUrl()+"下载结束#######################################################################################################");
					System.out.println("");
					System.out.println("");
					
					}	
					{//资源回收
						uids=null;
						weibo=null;
						flag=null;
						weiboTableName=null;
						userTableName=null;
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
					
			}
		}
		try {
			Log.addLog("Thread"+id+"退出");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取根微博的转发微博在一定的时间期限内
	 * @param endDate 截止时间
	 * @param weibo   root微博对象
	 * @param component  构造url相关信息
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws InterruptedException 
	 */
	public void getRootWeiboRepostByEndTime(long endDate,Weibo weibo,RepostUrlComponent component) throws ClientProtocolException, IOException, InterruptedException{
		String mid=component.getId();
		String maxMid=component.getMax_id();
		int maxPage=component.getMaxPage();
		String repostHtml=null;
		ArrayList<Weibo>weibos=null;
		boolean end=false;
		while(maxPage-->0){
			repostHtml=repostDownload.getOneRepostPage(5, mid, maxMid, 0, maxPage);
			weibos=wPrase.getWeiboList(repostHtml);
			System.out.println(maxPage+":"+weibos.size());	
			for(Weibo w:weibos){
				if(Long.parseLong(w.getCreatedAt())>endDate){
					end=true;
				}else{
					//插入数据
					w.setParent_id(weibo.getMid());
					synchronized (dao) {
						dao.insertUser(w.getUser(), userTableName);//注意顺序
						dao.insertWeibo(w,weiboTableName);
					}
					
				}
			}
			if(end==true){
				if(weibos!=null)
					weibos.clear();
				weibos=null;	
				break;
			}
			{//资源回收
				repostHtml="";
				weibos.clear();
				weibos=null;	
			}
			
		}	
		
		{
			mid="";
			mid=null;
			maxMid="";
			maxMid=null;
			repostHtml=null;
			weibos=null;
		}
	}
	/**
	 * 获取某条微博的转发微博列表
	 * @param endDate
	 * @param weibo
	 * @param component
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public ArrayList<String> getRootWeiboRepostMidsByEndTime(long endDate,Weibo weibo,RepostUrlComponent component) throws ClientProtocolException, IOException, InterruptedException{
		ArrayList<String>mids=new ArrayList<String>();
		String mid=component.getId();
		String maxMid=component.getMax_id();
		int maxPage=component.getMaxPage();
		String repostHtml=null;
		ArrayList<Weibo>weibos=null;
		boolean end=false;
		while(maxPage-->0){
			repostHtml=repostDownload.getOneRepostPage(5, mid, maxMid, 0, maxPage);
			weibos=wPrase.getWeiboList(repostHtml);
			
			for(Weibo w:weibos){
				if(Long.parseLong(w.getCreatedAt())>endDate){
					end=true;
				}else{
					mids.add(w.getMid());
				}
			}
			if(end==true){
				if(weibos!=null)
					weibos.clear();
				weibos=null;	
				break;
			}
			{//资源回收
				repostHtml="";
				weibo=null;
				weibos.clear();
				weibos=null;
			}
		}
		{//资源回收
			mid="";
			mid=null;
			maxMid="";
			maxMid=null;
			repostHtml="";
			repostHtml=null;
			weibos=null;
		}
		return mids;
	}
	
	
	/**
	 * 构造传播图
	 * @throws SQLException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public  void ConstructGraph(long endDate) throws SQLException, ClientProtocolException, IOException, InterruptedException{	 
		 ResultSet rSet= dao.getWeiboHaveRepost(weiboTableName);
		 Weibo weibo=null;
		 String mid=null;
		 String url=null;
		 String content=null;
		 RepostUrlComponent component=null;
		 ArrayList<String> repostMids =null;
		 while(rSet.next()){
			 weibo=new Weibo();
			 mid=rSet.getString("mid");
			 url=rSet.getString("src");
			 content=repostDownload.getOneWeiboPage(url+"?type=repost");
			 weibo.setMid(mid);
			 weibo.setUrl(url);
			 component=wPrase.getRepostUrlComponent(content,weibo);	
			if (component.getMaxPage()>0) {
				 repostMids = getRootWeiboRepostMidsByEndTime(endDate, weibo, component);
			}else{
				 repostMids=wPrase.getWeiboIds(content);
			}
			synchronized (dao) {
				dao.updateWholeWeibo(weiboTableName, weibo); 
				// update 函数需要重新改写
				if(repostMids!=null)
					dao.updateWeibos(repostMids, mid, weiboTableName);
			}
			
			{//资源回收
				if(repostMids!=null)
					repostMids.clear();
				component=null;
				content="";	
				weibo=null;
			}
			
		 }	
		{//资源回收
				rSet.close();
				rSet=null;
				weibo=null;
				mid="";
				mid=null;
				url="";
				url=null;
				content="";
				content=null;
				component=null;
				if(repostMids!=null)
				repostMids.clear();
				repostMids =null;
			 
		}
	}
	
	
	/**
	 * 获取未下载信息的用户id
	 * @param rSet
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public  ArrayList<String> getUids() throws SQLException, IOException{
		ResultSet resultSet=dao.getNoInfoUsers(userTableName);
		ArrayList<String>uids=new ArrayList<String>();
		while(resultSet.next()){
			uids.add(resultSet.getString(1));
		}
		Log.addLog(id+" : "+userTableName+" size :"+ uids.size());
		resultSet.close();
		resultSet=null;
		return uids;
	}
		
	/**
	 * 下载微博主页以及它的user
	 * 分别创建mid+weibo mid+user两个数据库表
	 * 返回一个用于下载微博转发信息的结构体compoent
	 * @param url
	 */
	public RepostUrlComponent saveMainWeiboAndUser(String url,Weibo weibo){
		RepostUrlComponent component=null;
		String weiboContent="";
		User user=null;
		String userHtml="";
		try {
			weiboContent=repostDownload.getOneWeiboPage(url+"?type=repost");
			component=wPrase.getRepostUrlComponent(weiboContent,weibo);
			user=weibo.getUser();
			userHtml=uDownload.getOneUserInfo(ConstructURL.getUserHomeUrl(user.getUid()));
			user=uPrase.getUser(userHtml);
			user.setHomePage(ConstructURL.getUserHomeUrl(user.getUid()));
			weibo.setUser(user);
			weibo.setSource(url);
			weibo.setUrl(url);
			if(weibo.getMid()!=null){
				weiboTableName=weibo.getMid()+"repost_weibo";
				userTableName=weibo.getMid()+"users";
				synchronized (dao) {
					Log.addLog(id+": 创建用户表"+userTableName);
					dao.createUserTable(userTableName);
					Log.addLog(id+": 创建微博表"+weiboTableName);
					dao.createWeiboTable(weiboTableName, userTableName);
					dao.insertUser(weibo.getUser(),userTableName);
					dao.insertWeibo(weibo,weiboTableName);
				}	
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		{
			//资源回收
			weiboContent="";
			weiboContent=null;
			user=null;
			userHtml="";
			userHtml=null;
		}
		return component;
	}
	
	/**
	 * 
	 * 20140327
	 * @author coderwang
	 *子线程结束标志类
	 */
	class Flag
	{
		
		public boolean userInfoDowloaded=false;
		public boolean repostGraphConstructed=false;
		public int uidIndex=0;//用户id链表的下标
		public String utName=userTableName;//用户表名
		public boolean reLogin=false;
		public int delay=100;//用户下载线程延迟时间
		private  final long timeSpane=1*60*60*1000;//默认时间为最初的1个小时
		public long endDate=0;//下载转发微博的截止时间
		
		public boolean thisWeiboDown(){
			if(userInfoDowloaded==true&&repostGraphConstructed==true){
				return true;
			}else{
				return false;
			}
		}
		
	}

}
