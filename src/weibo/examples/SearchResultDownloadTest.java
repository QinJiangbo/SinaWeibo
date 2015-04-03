package weibo.examples;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import weibo.download.SearchResultsDownload;


public class SearchResultDownloadTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub	   	    
		    //获取当前时间
			SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd-HH");   
			String endTime=dateformat.format(new Date(System.currentTimeMillis()));	
			long timespan=2592000000l;//时间间隔一个月
			//一个月之前的时间
			String startTime=  dateformat.format(new Date(System.currentTimeMillis()-timespan));
			int pageCount=0; //总的下载页数
			String searchItem="打的软件";
			
			//下载
			SearchResultsDownload searchResultsDownload=new SearchResultsDownload(pageCount);	
			searchResultsDownload.startDownload(searchItem, "time", startTime+":"+endTime);
			//解析
			
	}

}
