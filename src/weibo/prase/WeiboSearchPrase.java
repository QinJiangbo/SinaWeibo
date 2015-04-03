package weibo.prase;
import java.util.ArrayList;
import weibo.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSON;

/**
 * @author coderwang
 * 2014/1/11
 * 
 * 解析微博搜索结果页面类
 * 1、首先提取出html中我们需要的部分"<script></script>"中的内容，也就是json格式的数据
 * 2、提取出json格式中的我们需要的合适的数据
 * */


public class WeiboSearchPrase implements WeiboPrase {
	
	/**
	 * 读取web页面中的搜索结果微博所在的"<script>"脚本
	 * @param htmlPage web页面
	 * @return mainContent html格式的微博列表集合
	 * */
	public String getMainContent(String htmlPage){
		 String mainContent="";
		 Document doc=Jsoup.parse(htmlPage);
	     Elements elements=doc.getElementsByTag("script");
	     for(int i=0;i<elements.size();++i){
	    	Element element= elements.get(i);
	    	if(element.data().startsWith("STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_wb_feedlist\"")){
	    		mainContent=(String) element.data().subSequence(41, element.data().length()-1);
	    		SearchResult searchResult=JSON.parseObject(mainContent,SearchResult.class);	
	    		mainContent=searchResult.getHtml();
	    		break;
	    	}
	     }
		return mainContent;
	}
	
	
	/**
	 * 获取某个页面的微博列表
	 * @param resultPage 提取出的包含微博数据的纯净html界面
	 * */
	
	public ArrayList<Weibo> getWeibos(String resultPage,ArrayList<User>users){
		ArrayList<Weibo> weibos=new ArrayList<Weibo>();
		//Collections.sort(weibos, );
		Document doc=Jsoup.parse(resultPage);
		Elements elements=doc.getElementsByTag("dl");
		Weibo weibo =null;	
		for(Element e: elements){
			weibo=new Weibo();	
			weibo.setMid(e.attr("mid"));
			Element userE=e.getElementsByClass("face").first();
			if(userE!=null)
				userE=userE.getElementsByTag("a").first();
			if(userE!=null){
				User user=new User();
				saveUser(user, userE);
				weibo.setUser(user);	 
			}
			Element content=e.getElementsByClass("content").first();
			if(content!=null)
				saveWeibo(weibo, content);
			if(!(weibo.getMid().equals("")||weibo.getMid()==null)){
				weibos.add(weibo);
				users.add(weibo.getUser());
			}
		}	
		return weibos;
	}
	
	
	/**
	 * 从html片段中提取一个Weibo对象
	 * @param weibo 存储提取的微博内容对象
	 * @param content 包含微博信息的html元素
	 * */
	public void saveWeibo(Weibo weibo,Element content){
		Element feed_list_content=content.getElementsByTag("p").first();
		if(feed_list_content!=null)
				feed_list_content=feed_list_content.getElementsByTag("em").first();
		if(feed_list_content!=null){
			String text=feed_list_content.text();
			weibo.setContent(text);
		}else{
			System.out.println("该条微博有问题，数据丢弃");
		}	
		Element  otherinfo=content.getElementsByTag("p").get(1);//其他信息，来源，转发，评论，赞等信息
		if(otherinfo!=null){
			Elements links=otherinfo.getElementsByTag("a");
			for(Element link :links){
				if(link.attr("action-type").equals("feed_list_forward")){//转发情况
					String zhuanfa=link.text();
					int count=0;
					if(zhuanfa.endsWith(")")){
						int start=zhuanfa.indexOf("(");
						int end=zhuanfa.indexOf(")");
						count=Integer.parseInt(zhuanfa.substring(start+1,end));	
					}
					weibo.setRepostsCount(count);
				}
				if(link.attr("action-type").equals("feed_list_comment")){//评论情况
					int count=0;
					String pinglun=link.text();
					if(pinglun.endsWith(")")){
						int start=pinglun.indexOf("(");
						int end=pinglun.indexOf(")");
						count=Integer.parseInt(pinglun.substring(start+1,end));
						
					}
					weibo.setCommentsCount(count);
				}
				if(link.attr("node-type").equals("feed_list_item_date")){//发布时间
					String url=link.attr("href");
					weibo.setSource(url);
					String date= link.attr("title");
					weibo.setCreatedAt(date);
				}
			}
		}
	}
	
	/**
	 * 从Html 片段中提取出一个User对象
	 * @param userE html页面中保存用户信息的html 片段
	 * */
	public void saveUser(User user,Element userE){
		 user.setHomePage(userE.attr("href"));
		 user.setName(userE.attr("title"));
		 Element img=userE.getElementsByTag("img").first();
		 if(img!=null)
			 user.setImg(img.attr("src"));
		 String usercardid=img.attr("usercard");
		 String[] ids=usercardid.split("&");
		 user.setUid(ids[0].substring(3));
		 user.setUsercardkey(ids[1].substring(12));
	}
	
	
	/**
	 * 返回本次抓取页面的总页数
	 * @param resultPage 抓取的某页搜索页面的htm源代码
	 * @return totalPages 总共的搜索页数
	 * @throws InterruptedException 
	 * */
	public int getSearchResultPages(String resultPage ){
		int totalPages=0;
		Document doc=Jsoup.parse(resultPage);
		Elements pageElements=doc.getElementsByTag("li");
		int number=pageElements.size();
		if(pageElements.get(number-1).text().equals("下一页"))
			totalPages=Integer.parseInt( pageElements.get(number-2).text());
		else{
			totalPages=Integer.parseInt(pageElements.get(number-1).text());
		}	
		return totalPages;
	}
	
	/**
	 * 返回本次抓取页面的所有微博的mid
	 * @param resultPage 抓取的某搜索页面的html源代码
	 * @return mids 返回微博列表
	 * */
	public ArrayList<String> getMids(String resultPage){
		ArrayList<String>mids=new ArrayList<>();
		Elements weiboElements=Jsoup.parse(resultPage).getElementsByTag("dl");
		for(Element weibo:weiboElements){
			String mid=weibo.attr("mid");
			if(!(mid==null||mid.equals("")))
				mids.add(mid);
		}
		return mids;
	} 
	
	/**
	 * 返回最后一条微博发布的时间
	 * */
	public String getLastWeiboCreateTime(String resultPage){
		Elements ahref=Jsoup.parse(resultPage).getElementsByAttribute("date");
		int size=ahref.size()-1;
		//System.out.println(size);
		Element a=null;
		for(;size>=0;size--){
			a=ahref.get(size);		
			if(a.hasAttr("title")&&(!(a.attr("date").equals("")||a.attr("date")==null))){//防止转发的微博的时间
				return a.attr("date");	
			}	
		}
		return null;
	}

}
