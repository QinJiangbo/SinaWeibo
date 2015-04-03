package weibo.download;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import weibo.io.WeiboWriter;
import weibo.prase.WeiboSearchPrase;
import weibo.util.BareBonesBrowserLaunch;
import weibo.util.ConstructURL;
import weibo.util.DownloadUtils;

/**
 * @author coderwang 
 * 2014/1/10
 * ����ĳ����Ϣ���������
 * 
 * */
public class SearchResultsDownload extends Download{
	

	private WeiboWriter writer;//�������ݵĶ�Ӧ��д����
	private int maxPageNum=50;//������������ҳ��Ϊ50��ÿҳĬ��20������
	private int pageCount=0;//���ڼ����ܹ�������page��Ŀ
	
	
	public SearchResultsDownload(int pageCount){
		super();
		this.pageCount+=pageCount;	
	}
	/**
	 * ����ÿ���������õ��Ľ��ҳ��
	 * */
	public void setMaxPageNum(int maxPageNum){
		this.maxPageNum=maxPageNum;
	}
	
	public void startDownload(String searchItem,String sortType,String timescope) throws IOException, InterruptedException{	
	    String lastContent="";
		url=ConstructURL.getSearchURL(searchItem, sortType, timescope, 2);//���ڻ�ȡ�ܹ�������ҳ��
		this.maxPageNum=getSearchPagesNum(url);
		if(maxPageNum==0)return;
		System.out.println("maxPageNum : "+maxPageNum);
		boolean no_more_result=false;
		if(maxPageNum<50)
			no_more_result=true;
		int pageNum;
		//String lastSuccessPage="";
		WeiboSearchPrase prase=new WeiboSearchPrase();
		for( pageNum=1;pageNum<=maxPageNum;++pageNum){
			url=ConstructURL.getSearchURL(searchItem, sortType, timescope, pageNum);
			System.out.println("����"+url);
			content=getOnePageResult(url);		
			if(!content.contains("search_noresult")){	
				String mainContent=prase.getMainContent(content);
				//����û����Ͳ�����
				lastContent=mainContent;
				saveResult(mainContent,searchItem,++pageCount);	
			}else{
				int count=0;
				while(content.contains("search_noresult")){
					if(count>5){
						System.out.println("no result"+(pageCount+1));
						saveResult("no result ",searchItem,++pageCount);
						break;
					}
					count++;
					content=getOnePageResult(url);
					if(!content.contains("search_noresult")){
						lastContent=content;
						break;
					}
				}	
			}	
		}
		//���timescope�е�ʱ���Ƿ������
		if(!no_more_result){
			//����timescope		
			SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd-HH");  
			long addHour=3600000l;//����һ��Сʱ��ʱ����
			String lastSearchWeiboTime=dateformat.format(new Date(addHour+Long.parseLong(prase.getLastWeiboCreateTime(lastContent))));		
			System.out.println(lastSearchWeiboTime);
			String newTimeScope=getUpdatedTimescope(timescope, lastSearchWeiboTime);
			if(newTimeScope.endsWith(timescope)){
				endDownload();//��������
				return;
			}
			startDownload(searchItem, sortType, newTimeScope);		
		}
	}
	
	/**
	 * ����ץȡ�Ľ��
	 * @param content ���
	 * @param searchItem ������
	 * @param pageNumCount �ܵı����ҳ��
	 * */
	public void saveResult(String content,String searchItem,int pageNumCount) throws IOException{
		writer=new WeiboWriter();
		writer.setPath(nextPath(searchItem, pageNumCount));
		writer.write(content);
	}
	
	/**
	 * ������Ӧת��ҳ��Ĵ洢�ļ�·��
	 * @param mid ��Ӧ΢����id
	 * @param pageNum
	 * */
	public String nextPath(String searchItem,int pageNum){
		File file=new File("./data/"+searchItem+"�������");
		file.mkdir();
		return new String("./data/"+searchItem+"�������/resultPage"+pageNum+".txt");
	}
	
	/**
	 * ����timescope,ʹ����oldTimescope�г��ֵ�����������ܹ���������
	 * @param oldTimescope
	 * @param lastSearchWeiboTime ��һ�������Ĺ��������һ��΢��������ʱ��
	 * */
	public String getUpdatedTimescope(String oldTimeScope,String lastSearchWeiboTime){
		
		String times[]=oldTimeScope.split(":");
		String timescope=times[0];
		timescope=timescope+":"+lastSearchWeiboTime;
		return timescope;
	}
	
	/**
	 * ��ȡĳһҳ���������
	 * @param url��Ӧ������url
	 * @return content ���ظ�ҳ�Ľ��
	 * @throws InterruptedException 
	 * */
	public String getOnePageResult(String url) throws IOException, InterruptedException{
		 System.out.println("��ʼ����"+url);
		 httpGet = new HttpGet(url);
		 DownloadUtils.addGetHeaders(httpGet);	
		 response = httpclient.execute(httpGet);
		 entity=response.getEntity();
		 content=EntityUtils.toString(entity,"utf-8");
		 while(content.contains("yzm_input")){//����Ҫ������֤���ʱ���˹����룬ò���е�̫�������ˣ��Ժ���ʱ�䳢��ȥ�ƽ��
			 System.out.println("��ȥ������֤�룬20��ʱ��");
			 BareBonesBrowserLaunch.openURL(url+"?sudaref=s.weibo.com");
			 Thread.sleep(20000);
			 response=httpclient.execute(httpGet);
			 content=EntityUtils.toString(response.getEntity());
		 }
		 EntityUtils.consume(entity);	
		 return content;
	}
	
	
	/**
	 * ��ȡ���ҳ��
	 * @param url �����Ľ��ҳ������
	 * */
	public int getSearchPagesNum(String url) throws IOException{
	    
		 System.out.println("��ʼ����: "+url);
		 httpGet = new HttpGet(url);
		 DownloadUtils.addGetHeaders(httpGet);
		 response = httpclient.execute(httpGet);
		 entity=response.getEntity();
		 content=EntityUtils.toString(entity,"utf-8");
		 if(content.contains("search_noresult")){
			 return 0;//û���
		 }
		 while(content.contains("yzm_input")){//����Ҫ������֤���ʱ���˹����룬ò���е�̫�������ˣ��Ժ���ʱ�䳢��ȥ�ƽ��
			 System.out.println("��ȥ������֤�룬20��ʱ��");
			 BareBonesBrowserLaunch.openURL(url+"?sudaref=s.weibo.com");
			 try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 response=httpclient.execute(httpGet);
			 content=EntityUtils.toString(response.getEntity());
		 }
		 EntityUtils.consume(entity);	
		 return getPageNum(new WeiboSearchPrase().getMainContent(content));	
	}
	
	/**
	 * ����ץȡ����ҳ������������������е��������
	 * @param content ץȡ��ҳ����
	 * */
	public int getPageNum(String content){		
		//����ҳ���ȡ�������
		maxPageNum=new WeiboSearchPrase().getSearchResultPages(content);
		return maxPageNum;
	}

}
