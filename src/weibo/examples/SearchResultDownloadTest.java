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
		    //��ȡ��ǰʱ��
			SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd-HH");   
			String endTime=dateformat.format(new Date(System.currentTimeMillis()));	
			long timespan=2592000000l;//ʱ����һ����
			//һ����֮ǰ��ʱ��
			String startTime=  dateformat.format(new Date(System.currentTimeMillis()-timespan));
			int pageCount=0; //�ܵ�����ҳ��
			String searchItem="������";
			
			//����
			SearchResultsDownload searchResultsDownload=new SearchResultsDownload(pageCount);	
			searchResultsDownload.startDownload(searchItem, "time", startTime+":"+endTime);
			//����
			
	}

}
