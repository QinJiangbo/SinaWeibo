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
 * 按日期抓取当天前10条热门微博
 * 20140324
 * @author coderwang
 *
 */
public class HotWeiboCrawlTask implements Runnable {
	
	private int id=0;//线程编号
	private long endDate=0;//截止日期
	private long date =0;//截止日期
	private DAO dao=null;//数据库操作接口
	private final long day=24*60*60*1000;//一天的时间间隔
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
	 * 具体工作区
	 */
	public void run() {
		while(cVariables.date<=endDate){
			synchronized (cVariables) {//同步获取日期
				this.date=cVariables.date;
				cVariables.date+=day;
			}
			try {
				SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
				String url = ConstructURL.getHotWeiboUrl(f.format(
						new Date(date)).trim());
				Log.addLog("线程"+id+"下载" + url);
				downLoadPage.setUrl(url);

				String html = downLoadPage.getOnePage();//下载某一页数据
				ArrayList<String> weiboUrls = prase.getWeibos(html);//解析该页数据
				synchronized (dao) {
					Log.addLog("插入线程"+id+"["+url+"]下载数据");//插入解析的热门微博urls
					dao.Insert("hotWeiboSeeds", weiboUrls);
				}
			}catch (Exception e1) {
				e1.printStackTrace();
			}
			try {//构造随机休眠时间
				Random random=new Random();			
				long delay=random.nextInt(cVariables.delay)+1;
				Thread.sleep(delay);//延时
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
