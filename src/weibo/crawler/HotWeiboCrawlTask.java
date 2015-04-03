package weibo.crawler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import weibo.download.DownLoadPage;
import weibo.io.DAO;
import weibo.prase.PrasePage;
import weibo.util.ConstructURL;

/**
 * ������ץȡ����ǰ10������΢��
 * 20140324
 * @author coderwang
 *
 */
public class HotWeiboCrawlTask implements Runnable {
	
	private int id=0;//�̱߳��
	private long endDate=0;//��ֹ����
	private long date =0;//��ֹ����
	private DAO dao=null;//���ݿ�����ӿ�
	private final long day=24*60*60*1000;//һ���ʱ����
	private PrasePage prase=null;
	private DownLoadPage downLoadPage=null;
	CommanVariables cVariables=null;
	
	public HotWeiboCrawlTask(DAO dao,int id,CommanVariables cVariables){
		this.endDate=cVariables.endDate;
		this.dao=dao;
		this.id=id;
		this.cVariables=cVariables;
		prase=new PrasePage();
		downLoadPage=new DownLoadPage();
	}
	@Override
	/**
	 * ���幤����
	 */
	public void run() {
		while(cVariables.date<=endDate){
			synchronized (cVariables) {//ͬ����ȡ����
				this.date=cVariables.date;
				cVariables.date+=day;
			}
			try {
				SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
				String url = ConstructURL.getHotWeiboUrl(f.format(
						new Date(date)).trim());
				Log.addLog("�߳�"+id+"����" + url);
				downLoadPage.setUrl(url);

				String html = downLoadPage.getOnePage();//����ĳһҳ����
				ArrayList<String> weiboUrls = prase.getWeibos(html);//������ҳ����
				synchronized (dao) {
					Log.addLog("�����߳�"+id+"["+url+"]��������");//�������������΢��urls
					dao.Insert("hotWeiboSeeds", weiboUrls);
				}
			}catch (Exception e1) {
				e1.printStackTrace();
			}
			try {//�����������ʱ��
				Random random=new Random();			
				long delay=random.nextInt(cVariables.delay)+1;
				Thread.sleep(delay);//��ʱ
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
