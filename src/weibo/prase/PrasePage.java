package weibo.prase;

import java.util.ArrayList;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PrasePage {
	
	/**
	 * @param html 某日热门微博
	 * @return 热门微博的url集合
	 * 
	 */
	
	public ArrayList<String> getWeibos(String html){
		ArrayList<String> weiboUrls =new ArrayList<String>();
		Document doc = Jsoup.parse(html);
		Elements wbs=doc.getElementsByAttributeValue("class", "S_func2 WB_time");
		if(wbs!=null){
			String href="";
			for(Element element :wbs){
				href=element.attr("href").trim();
				weiboUrls.add(href);
			}
		}
		return weiboUrls;	
	}

}
