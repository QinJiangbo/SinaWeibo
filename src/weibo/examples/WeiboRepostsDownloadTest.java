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
 * ����ĳ��΢����ת��΢�����ز���
 * */
public class WeiboRepostsDownloadTest {
	
	
	static Weibo weibo=null;//��Ҫ������΢������
	static RepostDownload repostDownload=null;//�����������
	static RepostUrlComponent component=null;//����ʱ��Ҫ�Ĳ���
	static WeiboWriter writer=null;//д����
	static DAO dao=null;//�������ݿ����
	static String weiboTableName=null;
	static String userTableName=null;
	static WeiboReader reader=null;
	
	
	/**
	 * �ֶ���ʼ������ҪΪ�˷ֲ������
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
	 * �Զ����һЩ��ʼ������
	 * @param weiboSrc ��Ҫ������Ӧ΢���Ĵ���������΢���ĵ�ַ
	 * */
	
	public static void init(String weiboSrc){//��ʼ������
		System.out.println("1.��ʼ��ʼ��");
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
				System.out.println("��ʼ���ɹ�");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void  downloadRepost(){
		System.out.println("����ת������");
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
	 * �����غõ�����ת�����ݴ浽���ݿ�
	 * @throws IOException 
	 * 
	 * */
	public static void saveRepostToDB() throws IOException{
		init();
		File files[]=new File(writer.getPath()).listFiles();
		System.out.println("�ļ�����"+files.length);
		WeiboRepostPrase prase=new WeiboRepostPrase();
		ArrayList<Weibo> weibos=null;
		for(File file:files){
			System.out.println("����"+file.getName());
			if(file.length()==0){
				System.out.println(file.getPath());
			}
			reader.setPath(file.getPath());
			String content=reader.readWholeFile();
			weibos=prase.getWeiboList(content);
			System.out.println("΢������"+weibos.size());
			for(Weibo weibotmp:weibos){
				 //System.out.println(weibotmp.toString());
				dao.insertUser(weibotmp.getUser(), userTableName);
				dao.insertWeibo(weibotmp, weiboTableName);
			}
		}
	}
	
	/**
	 * �����û���Ϣ
	 * @param path �û���Ϣ����ĵ�ַ
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
	 * �����û���Ϣ
	 * @throws SQLException 
	 */
	public static void downloadUserInfo() throws SQLException{
		init();
		int count=dao.getCount(userTableName);
		int scope=500;
		ResultSet rSet=null;
		if(count>scope){//��ҳ����
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
			
		}else{//ֱ������
			rSet=dao.getPageResultSet(userTableName, 0, count);
			download(rSet);
		}	
	}
	/**
	 * ����ĳһҳ�û���Ϣ
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
	 * ��ȡ�����û���Ϣ
	 * */
	public static void getMoreUserInfo() throws SQLException, IOException{
		init();
		ResultSet rSet=dao.getNoInfoUsers(userTableName);
		download(rSet);	
	}
	
	/**
	 * ����ĳ��΢��������ת��΢��
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public  static void start() throws IOException, SQLException{
		
		 
/*		  init("http://weibo.com/1618051664/Az0Cr1x2f");// ��һ������ʼ�����������ظ�΢������Ϣ�Լ�����������ݿ��
		
		  downloadRepost();	  //�ڶ��������ظ�΢��������ת��΢����Ϣ
		 
		  saveRepostToDB(); //�������������غõ����ݴ浽���ݿ���ȥ
		
		  ConstructGraph();// ���Ĳ������촫����ϵͼ
		  
		  init();//��������
		  dao.constructGraph(userTableName, weiboTableName);
		  
		 
		  downloadUserInfo(); //���岽�������û���Ϣ
		 
		  updateUserInfo(".\\data\\"+weibo.getMid()+"users"); //�������������û���Ϣ �����û���Ϣ�����λ����Ϣ
		  
		  getMoreUserInfo();//���߲�:��ȡ�����û���Ϣ�����û���Ϣ���ز�����ʱ��  
		  updateUserInfo(".\\data\\"+weibo.getMid()+"users");
		  
		  
*/	
		
		//  init("http://weibo.com/1741045574/ABPkYEunT");// ��һ������ʼ�����������ظ�΢������Ϣ�Լ�����������ݿ��
			
		 
		 
		  saveRepostToDB(); //�������������غõ����ݴ浽���ݿ���ȥ

		 
		  
		
		
		
		
		
		
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
	 * ���촫��ͼ
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
			dao.updateWholeWeibo(weiboTableName, weibo); // update ������Ҫ���¸�д
			dao.updateWeibos(repostMids, mid, weiboTableName);
			
		 }
		
	}
	
	
	/**
	 * ����΢����ַ��ȡ΢����Ϣ������
	 * @param src
	 */
	public static void getWeibo(String src){
		init();
	}
	
	
	/**
	 * �����û�uid ��ȡ�û���Ϣ������
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
