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
 * ����ĳ��΢����ȫ��ת��΢��
 * ��ҳ����
 * */

public class RepostDownload extends Download{
	
	private WeiboWriter writer;//�������ݵĶ�Ӧ��д����
	private int maxNum;//�������ҳ��
	
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
	 *��ʼ���ص����� 
	 * @param _wv,id ,max_id,filter������Ҫ��һЩ����
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * */
	public void startDownload(int _wv,String id,String max_id,int filter) throws ClientProtocolException, IOException{
		 for(int pageNum=1;pageNum<=maxNum;++pageNum){
			 
			 url=ConstructURL.getRepostUrl(_wv, id, max_id, filter, pageNum,ConstructRND.getTimeStamp());
			 System.out.println("��ʼ���ص�"+pageNum+"ҳ>>>>>>"+url);
			 content=WeiboRobotManager.getRobot().fetchOnePage(url);		 
			 writer.setPath(nextPath(id,pageNum));
		     writer.write(new WeiboRepostPrase().getHtml(content));
		     System.out.println("��"+pageNum+"ҳ������ϣ�");
		 }
		 System.out.println("���ؽ���");	 
	}
	
	/**
	 * ��ȡĳһҳת����΢����
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
		
		{//��������
			wPrase=null;
			url="";
			url=null;
		}
		return repostHtml;
	}
	/**
	 * ������Ӧת��ҳ��Ĵ洢�ļ�·��
	 * @param mid ��Ӧ΢����id
	 * @param pageNum
	 * */
	public String nextPath(String mid,int pageNum){
		File file=new File("./data/weibo"+mid);
		file.mkdirs();
		return new String("./data/weibo"+mid+"/repostPage"+pageNum+".txt");
	}
	
	
	/**
	 * ����΢���ĵ�ַץȡ΢������ҳ��
	 * @param weiboUrl ΢����ַ
	 * @return content ΢��ҳ��
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
 		System.out.println("��ȡ"+component.getId()+" ת��΢��");
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
		System.out.println("���ؽ���");	
		{
			prase=null;
			temp=null;
		}
		return mids;
 		
 	}
	

}
