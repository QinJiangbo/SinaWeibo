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
 * 下载某个信息的搜索结果
 * 
 * */
public class SearchResultsDownload extends Download{
	

	private WeiboWriter writer;//保存内容的对应的写入类
	private int maxPageNum=50;//搜索结果的最大页面为50，每页默认20条数据
	private int pageCount=0;//用于计数总共的搜索page数目
	
	
	public SearchResultsDownload(int pageCount){
		super();
		this.pageCount+=pageCount;	
	}
	/**
	 * 设置每次搜索所得到的结果页面
	 * */
	public void setMaxPageNum(int maxPageNum){
		this.maxPageNum=maxPageNum;
	}
	
	public void startDownload(String searchItem,String sortType,String timescope) throws IOException, InterruptedException{	
	    String lastContent="";
		url=ConstructURL.getSearchURL(searchItem, sortType, timescope, 2);//用于获取总共的搜索页数
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
			System.out.println("下载"+url);
			content=getOnePageResult(url);		
			if(!content.contains("search_noresult")){	
				String mainContent=prase.getMainContent(content);
				//搜索没结果就不保存
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
		//检查timescope中的时间是否检查完毕
		if(!no_more_result){
			//更新timescope		
			SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd-HH");  
			long addHour=3600000l;//增加一个小时的时间间隔
			String lastSearchWeiboTime=dateformat.format(new Date(addHour+Long.parseLong(prase.getLastWeiboCreateTime(lastContent))));		
			System.out.println(lastSearchWeiboTime);
			String newTimeScope=getUpdatedTimescope(timescope, lastSearchWeiboTime);
			if(newTimeScope.endsWith(timescope)){
				endDownload();//结束下载
				return;
			}
			startDownload(searchItem, sortType, newTimeScope);		
		}
	}
	
	/**
	 * 保存抓取的结果
	 * @param content 结果
	 * @param searchItem 搜索项
	 * @param pageNumCount 总的保存的页数
	 * */
	public void saveResult(String content,String searchItem,int pageNumCount) throws IOException{
		writer=new WeiboWriter();
		writer.setPath(nextPath(searchItem, pageNumCount));
		writer.write(content);
	}
	
	/**
	 * 构造相应转发页面的存储文件路径
	 * @param mid 对应微博的id
	 * @param pageNum
	 * */
	public String nextPath(String searchItem,int pageNum){
		File file=new File("./data/"+searchItem+"搜索结果");
		file.mkdir();
		return new String("./data/"+searchItem+"搜索结果/resultPage"+pageNum+".txt");
	}
	
	/**
	 * 更新timescope,使得在oldTimescope中出现的搜索结果都能够被检索到
	 * @param oldTimescope
	 * @param lastSearchWeiboTime 在一次搜索的过程中最后一条微博发布的时间
	 * */
	public String getUpdatedTimescope(String oldTimeScope,String lastSearchWeiboTime){
		
		String times[]=oldTimeScope.split(":");
		String timescope=times[0];
		timescope=timescope+":"+lastSearchWeiboTime;
		return timescope;
	}
	
	/**
	 * 获取某一页的搜索结果
	 * @param url对应的搜索url
	 * @return content 返回该页的结果
	 * @throws InterruptedException 
	 * */
	public String getOnePageResult(String url) throws IOException, InterruptedException{
		 System.out.println("开始下载"+url);
		 httpGet = new HttpGet(url);
		 DownloadUtils.addGetHeaders(httpGet);	
		 response = httpclient.execute(httpGet);
		 entity=response.getEntity();
		 content=EntityUtils.toString(entity,"utf-8");
		 while(content.contains("yzm_input")){//当需要输入验证码的时候人工输入，貌似有点太不智能了，以后有时间尝试去破解吧
			 System.out.println("快去输入验证码，20秒时间");
			 BareBonesBrowserLaunch.openURL(url+"?sudaref=s.weibo.com");
			 Thread.sleep(20000);
			 response=httpclient.execute(httpGet);
			 content=EntityUtils.toString(response.getEntity());
		 }
		 EntityUtils.consume(entity);	
		 return content;
	}
	
	
	/**
	 * 获取结果页数
	 * @param url 搜索的结果页面链接
	 * */
	public int getSearchPagesNum(String url) throws IOException{
	    
		 System.out.println("开始搜索: "+url);
		 httpGet = new HttpGet(url);
		 DownloadUtils.addGetHeaders(httpGet);
		 response = httpclient.execute(httpGet);
		 entity=response.getEntity();
		 content=EntityUtils.toString(entity,"utf-8");
		 if(content.contains("search_noresult")){
			 return 0;//没结果
		 }
		 while(content.contains("yzm_input")){//当需要输入验证码的时候人工输入，貌似有点太不智能了，以后有时间尝试去破解吧
			 System.out.println("快去输入验证码，20秒时间");
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
	 * 根据抓取的首页的搜索结果解析出所有的搜索结果
	 * @param content 抓取的页面结果
	 * */
	public int getPageNum(String content){		
		//解析页面获取搜索结果
		maxPageNum=new WeiboSearchPrase().getSearchResultPages(content);
		return maxPageNum;
	}

}
