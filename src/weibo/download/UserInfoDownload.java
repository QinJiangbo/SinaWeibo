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
 * �����û���Ϣ
 * */

public class UserInfoDownload extends Download {
	
	private WeiboWriter writer;//�������ݵĶ�Ӧ��д����
	private WeiboWriter error_writer;//��¼�����uid
	
	
	public UserInfoDownload(){
		super();
	}
	
	
	/**
	 * ��ʼ����
	 * @param uids �û�id�б�
	 * @param mid ת����΢��id
	 * */
	public void startDownload(ArrayList<String>uids,String mid) {
		int countnum=0;
		for(String uid:uids){
			countnum++;
			if(countnum>=0){
			System.out.println(countnum);
			String homeUrl=ConstructURL.getUserHomeUrl(uid);
			System.out.println("����"+countnum+"_"+homeUrl);
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
						System.out.println(homeUrl+"�޷�����");
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
	 * ��ȡ�����û�����Ϣ
	 * @param homeUrl ���û�����ҳ��url
	 * @return content //�û���ҳ��Ϣ����Ȼ��δ���������html Դ�룩
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws InterruptedException 
	 * */
	public String getOneUserInfo(String homeUrl) throws ClientProtocolException, IOException, InterruptedException{
		 String content="";//ҳ������	
		 content=WeiboRobotManager.getRobot().fetchOnePage(homeUrl);
		 if(content.contains("��Ǹ������ʵ�ҳ���ַ���󣬻��߸�ҳ�治����"))
			 return new String("");	
		 UserHomePagePrase uPrase=new UserHomePagePrase();
		 String tmp=uPrase.getMainContent(content);
		 if(tmp.equals("")||tmp==null){
			 System.out.println("��ȡ��Ҫ�û���Ϣ������"+content);
			 int count=5;
			 while(count-->0){
				 content=WeiboRobotManager.getRobot().fetchOnePage(homeUrl);
				 tmp=uPrase.getMainContent(content);
				 if(tmp.length()>10){
					 break;
				 }
			 }
			 
		 }
		 {//��Դ����
			 uPrase=null;
			 content="";
			 content=null;	 
		 }
		 return tmp;
	}
	
	/**
	 * �����û���Ϣ���¸��洢·��
	 * @param path �ļ������ļ���·��
	 * @param uid �û�id
	 * @return ���ع���õĴ洢·��
	 * */
	public String nextPath(String path,String uid){
		File file=new File(path);
		file.mkdir();
		return new String(path+"\\"+uid+".txt");
	}
	
	/**
	 * @param path �ļ������ļ���·��
	 * @param content �û���ҳ��Ϣ
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
