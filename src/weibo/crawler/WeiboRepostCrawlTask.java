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
 * �����ĳ��΢��ת����Ϣ��ץȡ
 * �����û���Ϣץȡ��ת����ÿ��΢����Ϣ��ץȡ
 * ת��ͼ�Ĺ���
 */
public class WeiboRepostCrawlTask implements Runnable {
	int id=0;
	private DAO dao=null;//���ݿ��������
	private CommanVariables variables=null;//�������
	private ArrayList<String>wuls=null;//weibo urls
	private RepostDownload repostDownload=null;//΢��ץ��������
	private WeiboRepostPrase wPrase=null;//΢��ת����Ϣ����
	private UserHomePagePrase uPrase=null;//�û���Ϣ����
	private UserInfoDownload uDownload=null;//�û���Ϣ������	
	private String weiboTableName=null;
	private String userTableName=null;
	private Flag flag=null;
	private RepostUrlComponent component=null;
	
	
	public WeiboRepostCrawlTask(int id,DAO dao,CommanVariables variables,ArrayList<String>wurls){
		this.dao=dao;
		this.variables=variables;
		this.wuls=wurls;
		this.id=id;
		
		//��������ĳ�ʼ��
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
				Log.addLog(id+": 1����ȡ����΢���Լ��䷢���ߵĻ�����Ϣ"+url);
				// 1����ȡ����΢���Լ��䷢���ߵĻ�����Ϣ
				Weibo weibo = new Weibo();
				flag=new Flag();//��־��
				component=saveMainWeiboAndUser(url, weibo);
				flag.endDate = flag.endDate
						+ Long.parseLong(weibo.getCreatedAt()) + flag.timeSpane;
				Log.addLog(id+": 2����������΢��������ת��΢��");
				getRootWeiboRepostByEndTime(flag.endDate, weibo, component);
				int count=dao.getCount(weiboTableName);
				Log.addLog("[################################################]"+weibo.getMid()+" "+weibo.getUrl()+" "+count);
					Log.addLog(id+": 3�����������û���Ϣ");
					flag.utName=userTableName;
					ArrayList<String> uids = getUids();
					if (uids != null) {
						for (int i = 0; i < 1; ++i) {//���߳�
							UserInfoCrawlTask uTask = new UserInfoCrawlTask(id,i,
									dao, uPrase, uDownload, uids, flag);
							new Thread(uTask).start();
						}
					}
					Log.addLog(id+": 4������ת��ͼ");
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
						Log.addLog(id+": 5��������ͼ��"+weibo.getMid()+"view");
						dao.createView(weibo.getMid());
						Log.addLog(id+": 6����������΢��url�еı�־");
						//dao.updateSeeds(weibo.getUrl(), "hotWeiboSeeds");
						dao.updateSeeds(weibo.getUrl(), "public_weibo_seeds");
						writer.append(weibo.getMid());
					System.out.println("#############"+weibo.getUrl()+"���ؽ���#######################################################################################################");
					System.out.println("");
					System.out.println("");
					
					}	
					{//��Դ����
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
			Log.addLog("Thread"+id+"�˳�");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ��ȡ��΢����ת��΢����һ����ʱ��������
	 * @param endDate ��ֹʱ��
	 * @param weibo   root΢������
	 * @param component  ����url�����Ϣ
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
					//��������
					w.setParent_id(weibo.getMid());
					synchronized (dao) {
						dao.insertUser(w.getUser(), userTableName);//ע��˳��
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
			{//��Դ����
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
	 * ��ȡĳ��΢����ת��΢���б�
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
			{//��Դ����
				repostHtml="";
				weibo=null;
				weibos.clear();
				weibos=null;
			}
		}
		{//��Դ����
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
	 * ���촫��ͼ
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
				// update ������Ҫ���¸�д
				if(repostMids!=null)
					dao.updateWeibos(repostMids, mid, weiboTableName);
			}
			
			{//��Դ����
				if(repostMids!=null)
					repostMids.clear();
				component=null;
				content="";	
				weibo=null;
			}
			
		 }	
		{//��Դ����
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
	 * ��ȡδ������Ϣ���û�id
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
	 * ����΢����ҳ�Լ�����user
	 * �ֱ𴴽�mid+weibo mid+user�������ݿ��
	 * ����һ����������΢��ת����Ϣ�Ľṹ��compoent
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
					Log.addLog(id+": �����û���"+userTableName);
					dao.createUserTable(userTableName);
					Log.addLog(id+": ����΢����"+weiboTableName);
					dao.createWeiboTable(weiboTableName, userTableName);
					dao.insertUser(weibo.getUser(),userTableName);
					dao.insertWeibo(weibo,weiboTableName);
				}	
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		{
			//��Դ����
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
	 *���߳̽�����־��
	 */
	class Flag
	{
		
		public boolean userInfoDowloaded=false;
		public boolean repostGraphConstructed=false;
		public int uidIndex=0;//�û�id������±�
		public String utName=userTableName;//�û�����
		public boolean reLogin=false;
		public int delay=100;//�û������߳��ӳ�ʱ��
		private  final long timeSpane=1*60*60*1000;//Ĭ��ʱ��Ϊ�����1��Сʱ
		public long endDate=0;//����ת��΢���Ľ�ֹʱ��
		
		public boolean thisWeiboDown(){
			if(userInfoDowloaded==true&&repostGraphConstructed==true){
				return true;
			}else{
				return false;
			}
		}
		
	}

}
