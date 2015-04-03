package weibo.crawler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import weibo.crawler.WeiboRepostCrawlTask.Flag;
import weibo.download.UserInfoDownload;
import weibo.io.DAO;
import weibo.model.User;
import weibo.prase.UserHomePagePrase;
import weibo.util.ConstructURL;

/**
 * 下载用户信息线程
 * 下载用户信息并将信息存入数据库
 * @author coderwang
 *
 */
public class UserInfoCrawlTask implements Runnable {
	
	
	private DAO dao=null;//数据库操作对象
	private UserHomePagePrase uPrase=null;//用户信息解析
	private UserInfoDownload uDownload=null;//用户信息下载类
	private ArrayList<String> uids=null;
	private Flag flag=null;
	private int id;
	private int pid;
	

	public UserInfoCrawlTask(int pid,int id,DAO dao, UserHomePagePrase uPrase,
			UserInfoDownload uDownload, ArrayList<String> uids, Flag flag) {
		super();
		this.id=id;
		this.pid=pid;
		this.dao = dao;
		this.uPrase = uPrase;
		this.uDownload = uDownload;
		this.uids = uids;
		this.flag = flag;
	}

	@Override
	public void run() {
		String uid="";
		String userHtml="";
		String temp="";
		User user =null;
		Random random=null;
		long delay=0;
		while (flag.uidIndex < uids.size()) {
			uid = getUid();
			try {
				System.out.println("Thread "+pid+"_"+id+" : get user home page "+(flag.uidIndex-1)+" uid: "+ uid);		
				userHtml = uDownload.getOneUserInfo(ConstructURL.getUserHomeUrl(uid));
				if(userHtml.equals("")||userHtml==null){
					System.out.println("Thread "+pid+"_"+id+" userhtml为空");
				}
				temp=new String(userHtml);
				user = uPrase.getUser(userHtml);
				if(user.getLevel()!=0||user.getStatusesCount()>0){
					synchronized (dao) {
						dao.updateUser(user, flag.utName);
					}
				}else{
					System.out.println("Thread "+pid+"_"+id+" "+user.toString()+"updating");
					if(temp!=null||!temp.equals("")){
						System.out.println(temp);
					}
				}
				
				{//资源回收
					uid="";
					userHtml="";
					temp="";
					user=null;
					random=null;
				}
				random=new Random();			
				delay=random.nextInt(flag.delay)+1;
				Thread.sleep(delay);//延时
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		flag.userInfoDowloaded=true;//用户信息下载完毕
		try {
			Log.addLog("Thread "+pid+"_"+id+" : "+flag.utName+"用户信息下载结束");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		{
			//资源回收
			uids.clear();
			uids=null;
			uPrase=null;
			uDownload=null;
			flag=null;
			dao=null;
			uid="";
			uid=null;
			userHtml="";
			userHtml=null;
			temp="";
			temp=null;
			user =null;
			random=null;
			delay=0;
			
		}
	}
	
	
	public synchronized String getUid(){
		String uid = uids.get(flag.uidIndex);
		flag.uidIndex++;
		return uid;
	}
	
	
	


}
