package weibo.examples;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.dom4j.DocumentException;

import weibo.download.RepostDownload;
import weibo.download.UserInfoDownload;
import weibo.io.DAO;
import weibo.io.WeiboReader;
import weibo.io.WeiboWriter;
import weibo.model.RepostUrlComponent;
import weibo.model.User;
import weibo.model.Weibo;
import weibo.prase.UserHomePagePrase;
import weibo.prase.WeiboRepostPrase;
import weibo.util.ConstructURL;

/**
 * @author coderwang
 * 2014/1/9
 * 进行某条微博的转发微博下载测试
 * */
public class WeiboRepostsDownloadTest {
	
	
	static Weibo weibo=null;//需要搜索的微博对象
	static RepostDownload repostDownload=null;//下载组件对象
	static RepostUrlComponent component=null;//下载时需要的参数
	static WeiboWriter writer=null;//写对象
	static DAO dao=null;//操作数据库对象
	static String weiboTableName=null;
	static String userTableName=null;
	static WeiboReader reader=null;
	
	
	/**
	 * 手动初始化，主要为了分步骤测试
	 */
	
	public static void init(){
		repostDownload=new RepostDownload();
		writer=new WeiboWriter();
		reader=new WeiboReader();
		weibo=new Weibo();
		weibo.setMid("3690154809649921");	
		component=new RepostUrlComponent(); 
		File file=new File("./data/weibo"+weibo.getMid());
		file.mkdirs();
		writer.setPath("./data/weibo"+weibo.getMid());
		dao=new DAO();
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");
		weiboTableName=weibo.getMid()+"repost_weibo";
		userTableName=weibo.getMid()+"users";
	}
	
	/**
	 * 自动完成一些初始化工作
	 * @param weiboSrc 需要分析对应微博的传播特征的微博的地址
	 * */
	
	public static void init(String weiboSrc){//初始化工作
		System.out.println("1.开始初始化");
		repostDownload=new RepostDownload();
		try {
			String content=repostDownload.getOneWeiboPage(weiboSrc+"?type=repost");
			writer=new WeiboWriter();
			reader=new WeiboReader();
			weibo=new Weibo();
			weibo.setSource(weiboSrc);
		    component=new WeiboRepostPrase().getRepostUrlComponent(content,weibo);
		    File file=new File("./data/weibo"+weibo.getMid());
			file.mkdirs();
			writer.setPath("./data/weibo"+weibo.getMid()+"/main_weibo.txt");
			dao=new DAO();
			DAO.loadDriver();
			dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");
			if(weibo.getMid()!=null){
				weiboTableName=weibo.getMid()+"repost_weibo";
				userTableName=weibo.getMid()+"users";
				System.out.println(weiboTableName);
				System.out.println(userTableName);
				
				dao.createUserTable(userTableName);
				dao.createWeiboTable(weiboTableName, userTableName);
				dao.insertUser(weibo.getUser(),userTableName);
				dao.insertWeibo(weibo,weiboTableName);
				System.out.println("初始化成功");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void  downloadRepost(){
		System.out.println("下载转发内容");
        repostDownload.setMaxNum(component.getMaxPage());
		try {
			repostDownload.startDownload(5, component.getId(),component.getMax_id(), 0);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 将下载好的数据转发数据存到数据库
	 * @throws IOException 
	 * 
	 * */
	public static void saveRepostToDB() throws IOException{
		init();
		File files[]=new File(writer.getPath()).listFiles();
		System.out.println("文件总数"+files.length);
		WeiboRepostPrase prase=new WeiboRepostPrase();
		ArrayList<Weibo> weibos=null;
		for(File file:files){
			System.out.println("解析"+file.getName());
			if(file.length()==0){
				System.out.println(file.getPath());
			}
			reader.setPath(file.getPath());
			String content=reader.readWholeFile();
			weibos=prase.getWeiboList(content);
			System.out.println("微博数："+weibos.size());
			for(Weibo weibotmp:weibos){
				 //System.out.println(weibotmp.toString());
				dao.insertUser(weibotmp.getUser(), userTableName);
				dao.insertWeibo(weibotmp, weiboTableName);
			}
		}
	}
	
	/**
	 * 更新用户信息
	 * @param path 用户信息保存的地址
	 * @throws IOException 
	 */
	public static void updateUserInfo(String path) throws IOException{
		File file=new File(path);
		file.mkdir();
		File files[]=file.listFiles();	
		UserHomePagePrase prase=new UserHomePagePrase();
		User user=null;
		String content=null;
		for(File f:files){
			reader.setPath(f.getPath());
			content=reader.readWholeFile();
			if(content!=null){
				user=prase.getUser(content);
				if(user!=null){
					System.out.println(user.toString());
					dao.updateUser(user,userTableName);
					//System.out.println(user.getFriendsCount()+" "+user.getStatusesCount()+" "+user.getFollowersCount());
				}
			}
		}
		
		
	}
	
	
	
	/**
	 * 下载用户信息
	 * @throws SQLException 
	 */
	public static void downloadUserInfo() throws SQLException{
		init();
		int count=dao.getCount(userTableName);
		int scope=500;
		ResultSet rSet=null;
		if(count>scope){//分页下载
			int pageNum=count/scope;
			int lastPageNum=count%scope;
			for(int i=0;i<=pageNum;++i){
				if(i<pageNum){
					rSet=dao.getPageResultSet(userTableName, i*scope, scope);
					download(rSet);
				}
				else{
					rSet=dao.getPageResultSet(userTableName, i*scope, lastPageNum);
					download(rSet);
				}
			}
			
		}else{//直接下载
			rSet=dao.getPageResultSet(userTableName, 0, count);
			download(rSet);
		}	
	}
	/**
	 * 下载某一页用户信息
	 * @param rSet
	 * @throws SQLException 
	 */
	public static void download(ResultSet rSet) throws SQLException{
		ArrayList<String>uids=new ArrayList<String>();
		while(rSet.next()){
			uids.add(rSet.getString(1));
		}
		System.out.println(uids.size());
		new UserInfoDownload().startDownload(uids, weibo.getMid());
	}
	
	/**
	 * 获取更多用户信息
	 * */
	public static void getMoreUserInfo() throws SQLException, IOException{
		init();
		ResultSet rSet=dao.getNoInfoUsers(userTableName);
		download(rSet);	
	}
	
	/**
	 * 下载某条微博的所有转发微博
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public  static void start() throws IOException, SQLException{
		
		 
/*		  init("http://weibo.com/1618051664/Az0Cr1x2f");// 第一步：初始化，包括下载该微博的信息以及创建相关数据库表
		
		  downloadRepost();	  //第二步：下载该微博的所有转发微博信息
		 
		  saveRepostToDB(); //第三步：将下载好的数据存到数据库中去
		
		  ConstructGraph();// 第四步：构造传播关系图
		  
		  init();//继续构造
		  dao.constructGraph(userTableName, weiboTableName);
		  
		 
		  downloadUserInfo(); //第五步：下载用户信息
		 
		  updateUserInfo(".\\data\\"+weibo.getMid()+"users"); //第六步：更新用户信息 传入用户信息保存的位置信息
		  
		  getMoreUserInfo();//第七步:获取更多用户信息，当用户信息下载不够的时候  
		  updateUserInfo(".\\data\\"+weibo.getMid()+"users");
		  
		  
*/	
		
		//  init("http://weibo.com/1741045574/ABPkYEunT");// 第一步：初始化，包括下载该微博的信息以及创建相关数据库表
			
		 
		 
		  saveRepostToDB(); //第三步：将下载好的数据存到数据库中去

		 
		  
		
		
		
		
		
		
	}
	

	public static void main(String[] args) throws ClientProtocolException, IOException, SQLException, DocumentException {

		
		
		start();
		
		
		/*init();
		XMLIO xmlio = new XMLIO(dao);
		ResultSet rSet = null;
		xmlio.createGexfFile(".\\data\\3686655858542013.gexf");
		xmlio.updateGexf("3686655858542013view", ".\\data\\3686655858542013.gexf");*/
		
		
		
	}
	
	
	
	
	/**
	 * 构造传播图
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void ConstructGraph() throws SQLException, ClientProtocolException, IOException{
		 init();	
		 ResultSet rSet= dao.getWeiboHaveRepost(weiboTableName);
		 while(rSet.next()){		
			 String mid=rSet.getString("mid");
			 String url=rSet.getString("src");
			 System.out.println(mid+" : "+rSet.getString("repost_count")+" :" +url);
			 String content=repostDownload.getOneWeiboPage(url+"?type=repost");
			// System.out.println(content);
			 component=new WeiboRepostPrase().getRepostUrlComponent(content,weibo);
			
			
			ArrayList<String> repostMids =null;
			if (component.getMaxPage()!=0) {
				 repostMids = repostDownload
						.getRepostMids(component);
				
			}else{
				repostMids=new WeiboRepostPrase().getWeiboIds(content);
			}
			System.out.println(repostMids.size());
			
			weibo.setMid(mid);
			//System.out.println(weibo.toString());
			dao.updateWholeWeibo(weiboTableName, weibo); // update 函数需要重新改写
			dao.updateWeibos(repostMids, mid, weiboTableName);
			
		 }
		
	}
	
	
	/**
	 * 根据微博地址获取微博信息并保存
	 * @param src
	 */
	public static void getWeibo(String src){
		init();
	}
	
	
	/**
	 * 根据用户uid 获取用户信息并保存
	 * @param uid
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws InterruptedException 
	 */
	public static void getUser(String uid) throws ClientProtocolException, IOException, InterruptedException{
		init();
		UserInfoDownload download=new UserInfoDownload();
		String content=download.getOneUserInfo(ConstructURL.getUserHomeUrl(uid));
		UserHomePagePrase uPrase=new UserHomePagePrase();
		User user= uPrase.getUser(content);
		dao.insertUser(user, userTableName);
	}

}
