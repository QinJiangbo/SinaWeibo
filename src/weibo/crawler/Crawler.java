package weibo.crawler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import weibo.io.DAO;
import weibo.login.WeiboRobotManager;


/**
 * 20140326 
 * @author coderwang
 *���߳�����������
 */
public class Crawler {
	
	public static DAO dao=null;
	
	public Crawler(){
		
	}
	
	public static void init(){
		try {
			WeiboRobotManager.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dao=new DAO();
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");
		
	}

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InterruptedException, ParseException, IOException {
		// TODO Auto-generated method stub
		init();
		getWeiboReposts("public_weibo_seeds");
			
		//Log.addLog("��������΢������");
		//getHotWeiboSeeds();
		
		
		
		
	}
	
	/**
	 * ��ȡ����δ��ץȡ������΢����ת����Ϣ
	 * 1�������ݿ��м���δ��ץȡ��weibo url
	 */

	public static void getWeiboReposts(String tableName){
		try {
		
			ArrayList<String> wurls=new ArrayList<String>();
			wurls.add("http://weibo.com/1563926367/AiQxqEgPM");
			wurls.add("http://weibo.com/1456494087/AivPPinh4");
			wurls.add("http://weibo.com/1751158793/AjBiECN9O");
			wurls.add("http://weibo.com/1086233511/Ak5zqnGz0");
			wurls.add("http://weibo.com/1559236912/AjXu1qa49");
			wurls.add("http://weibo.com/2706896955/AjXWlhx2G");
			wurls.add("http://weibo.com/3217179555/AjUXOF5JH");
			wurls.add("http://weibo.com/1938284521/AkfvElBbN");
		

			//ArrayList<String> wurls=dao.getWeiboUrls(tableName);	
			
			
			CommanVariables variables=new CommanVariables();
			Log.addLog("��"+tableName+"���ػ�δץȡ��΢��urls");
			Log.addLog("����΢��url : "+wurls.size()+"��");
			for(int i=0;i<1;++i){
				WeiboRepostCrawlTask wTask=new WeiboRepostCrawlTask(i,dao, variables, wurls);
				new Thread(wTask).start();
			}
			while(variables.index < wurls.size()){
				System.out.println("Crawler sleeping");
				Thread.sleep(5000);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��ȡ����΢������
	 * @throws ParseException 
	 * @throws InterruptedException 
	 */
	public static void getHotWeiboSeeds() throws ParseException, InterruptedException{
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
    	Date start = f.parse("2013-01-01");
    	CommanVariables variables=new CommanVariables();
    	variables.date=start.getTime();
    	Date end=f.parse("2013-12-31");
    	variables.endDate=end.getTime();	
		for(int i=0;i<5;i++){
			HotWeiboCrawlTask hTask=new HotWeiboCrawlTask(dao,i,variables);
			new Thread(hTask).start();
		}	
		while(variables.date<variables.endDate){
			Thread.sleep(10000);
		}
	}

}
