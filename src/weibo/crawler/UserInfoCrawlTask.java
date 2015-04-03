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
 * �����û���Ϣ�߳�
 * �����û���Ϣ������Ϣ�������ݿ�
 * @author coderwang
 *
 */
public class UserInfoCrawlTask implements Runnable {
	
	
	private DAO dao=null;//���ݿ��������
	private UserHomePagePrase uPrase=null;//�û���Ϣ����
	private UserInfoDownload uDownload=null;//�û���Ϣ������
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
					System.out.println("Thread "+pid+"_"+id+" userhtmlΪ��");
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
				
				{//��Դ����
					uid="";
					userHtml="";
					temp="";
					user=null;
					random=null;
				}
				random=new Random();			
				delay=random.nextInt(flag.delay)+1;
				Thread.sleep(delay);//��ʱ
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		flag.userInfoDowloaded=true;//�û���Ϣ�������
		try {
			Log.addLog("Thread "+pid+"_"+id+" : "+flag.utName+"�û���Ϣ���ؽ���");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		{
			//��Դ����
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
