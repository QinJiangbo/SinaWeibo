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
 * ����΢���������ҳ����
 * 1��������ȡ��html��������Ҫ�Ĳ���"<script></script>"�е����ݣ�Ҳ����json��ʽ������
 * 2����ȡ��json��ʽ�е�������Ҫ�ĺ��ʵ�����
 * */


public class WeiboSearchPrase implements WeiboPrase {
	
	/**
	 * ��ȡwebҳ���е��������΢�����ڵ�"<script>"�ű�
	 * @param htmlPage webҳ��
	 * @return mainContent html��ʽ��΢���б���
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
	 * ��ȡĳ��ҳ���΢���б�
	 * @param resultPage ��ȡ���İ���΢�����ݵĴ���html����
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
	 * ��htmlƬ������ȡһ��Weibo����
	 * @param weibo �洢��ȡ��΢�����ݶ���
	 * @param content ����΢����Ϣ��htmlԪ��
	 * */
	public void saveWeibo(Weibo weibo,Element content){
		Element feed_list_content=content.getElementsByTag("p").first();
		if(feed_list_content!=null)
				feed_list_content=feed_list_content.getElementsByTag("em").first();
		if(feed_list_content!=null){
			String text=feed_list_content.text();
			weibo.setContent(text);
		}else{
			System.out.println("����΢�������⣬���ݶ���");
		}	
		Element  otherinfo=content.getElementsByTag("p").get(1);//������Ϣ����Դ��ת�������ۣ��޵���Ϣ
		if(otherinfo!=null){
			Elements links=otherinfo.getElementsByTag("a");
			for(Element link :links){
				if(link.attr("action-type").equals("feed_list_forward")){//ת�����
					String zhuanfa=link.text();
					int count=0;
					if(zhuanfa.endsWith(")")){
						int start=zhuanfa.indexOf("(");
						int end=zhuanfa.indexOf(")");
						count=Integer.parseInt(zhuanfa.substring(start+1,end));	
					}
					weibo.setRepostsCount(count);
				}
				if(link.attr("action-type").equals("feed_list_comment")){//�������
					int count=0;
					String pinglun=link.text();
					if(pinglun.endsWith(")")){
						int start=pinglun.indexOf("(");
						int end=pinglun.indexOf(")");
						count=Integer.parseInt(pinglun.substring(start+1,end));
						
					}
					weibo.setCommentsCount(count);
				}
				if(link.attr("node-type").equals("feed_list_item_date")){//����ʱ��
					String url=link.attr("href");
					weibo.setSource(url);
					String date= link.attr("title");
					weibo.setCreatedAt(date);
				}
			}
		}
	}
	
	/**
	 * ��Html Ƭ������ȡ��һ��User����
	 * @param userE htmlҳ���б����û���Ϣ��html Ƭ��
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
	 * ���ر���ץȡҳ�����ҳ��
	 * @param resultPage ץȡ��ĳҳ����ҳ���htmԴ����
	 * @return totalPages �ܹ�������ҳ��
	 * @throws InterruptedException 
	 * */
	public int getSearchResultPages(String resultPage ){
		int totalPages=0;
		Document doc=Jsoup.parse(resultPage);
		Elements pageElements=doc.getElementsByTag("li");
		int number=pageElements.size();
		if(pageElements.get(number-1).text().equals("��һҳ"))
			totalPages=Integer.parseInt( pageElements.get(number-2).text());
		else{
			totalPages=Integer.parseInt(pageElements.get(number-1).text());
		}	
		return totalPages;
	}
	
	/**
	 * ���ر���ץȡҳ�������΢����mid
	 * @param resultPage ץȡ��ĳ����ҳ���htmlԴ����
	 * @return mids ����΢���б�
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
	 * �������һ��΢��������ʱ��
	 * */
	public String getLastWeiboCreateTime(String resultPage){
		Elements ahref=Jsoup.parse(resultPage).getElementsByAttribute("date");
		int size=ahref.size()-1;
		//System.out.println(size);
		Element a=null;
		for(;size>=0;size--){
			a=ahref.get(size);		
			if(a.hasAttr("title")&&(!(a.attr("date").equals("")||a.attr("date")==null))){//��ֹת����΢����ʱ��
				return a.attr("date");	
			}	
		}
		return null;
	}

}
