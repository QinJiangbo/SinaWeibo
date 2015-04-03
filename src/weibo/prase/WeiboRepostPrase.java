package weibo.prase;


import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSON;
import weibo.model.Data;
import weibo.model.RepostResult;
import weibo.model.RepostUrlComponent;
import weibo.model.User;
import weibo.model.Weibo;
import weibo.model.WeiboAndRepost;

/**
 * @author coderwang 
 * 2014/2/28
 * 解析微博转发相关的内容
 * */

public class WeiboRepostPrase {
	
	
	/**
	 * @param content json格式的转发数据
	 * @return html json格式中的html部分
	 * */
	public String getHtml(String content){
		String html="";
		RepostResult repostResult=JSON.parseObject(content,RepostResult.class);
		Data data=JSON.parseObject(repostResult.getData(), Data.class);
		html=data.getHtml();
		
		{//资源回收
			repostResult=null;
			data=null;
			
		}
		return html;
	}
	
	
	/**
	 * @param 整个页面内容
	 * @return mainContent 返回微博主要信息
	 * 
	 * */
	public String getMainContent(String content){
		String mainContent="";
		Document doc=Jsoup.parse(content);
		Elements elements=doc.getElementsByTag("script");
		String tmp=null;
		WeiboAndRepost weiboAndRepost=null;
		for(Element element:elements){
			//System.out.println(element.html());
			if(element.html().startsWith("FM.view({\"ns\":\"pl.content.weiboDetail.index\"")){
				tmp=element.html();
				mainContent=tmp.substring(8,tmp.length()-1);
				weiboAndRepost=JSON.parseObject(mainContent, WeiboAndRepost.class);
				mainContent=weiboAndRepost.getHtml();
			}
			tmp="";
		}
		
		{
			weiboAndRepost=null;
			tmp=null;
			elements=null;
			doc=null;
			
		}
		return mainContent;
	}
	
	/**
	 * 提取获取转发信息的组件
	 * @param  content 微博以及其转发列表所在的html源码
	 * @return repostUrlComponent 构造获取转发信息的组件
	 * */ 
	
	public  RepostUrlComponent  getRepostUrlComponent(String content,Weibo weibo){
		
		RepostUrlComponent  repostUrlComponent=new RepostUrlComponent();
		Document doc=Jsoup.parse(content);
		Element weiboDetail=doc.getElementsByAttributeValue("class", "WB_detail").first();
		if(weiboDetail!=null){
			Element e=weiboDetail.getElementsByAttributeValue("node-type", "feed_list").first();//获取微博id和用户id
			if(e!=null){
				User user=new User();
				String tbinfo=e.attr("tbinfo");
				int index=tbinfo.indexOf("=");
				user.setUid(tbinfo.substring(index+1));
				weibo.setMid(e.attr("mid"));
				weibo.setUser(user);
			} 
			e=weiboDetail.getElementsByAttributeValue("class","WB_text").first();
			if(e!=null){
				weibo.setContent(e.text());//设置微博内容
			}
			e=weiboDetail.getElementsByAttributeValue("class", "WB_media_list clearfix").first();
			//微博中的媒体内容
			if(e!=null){
				e=e.getElementsByAttributeValue("class", "bigcursor").first();//设置微博中的图片地址
				if(e!=null){
					weibo.setOriginalPic(e.attr("src"));
				}
			}
			e=weiboDetail.getElementsByAttributeValue("node-type", "like_counter").first();//赞数
			if(e!=null){
				String temp=e.text();
			    weibo.setLikeCount(getCount(temp));
			}
			e=weiboDetail.getElementsByAttributeValue("node-type", "forward_counter").first();//抓发数
			if(e!=null){
				String temp=e.text();
				weibo.setRepostsCount(getCount(temp));
				
			}
			e=weiboDetail.getElementsByAttributeValue("node-type", "comment_counter").first();//评论数
			if(e!=null){
				String temp=e.text();
				weibo.setCommentsCount(getCount(temp));
			}
			
			e=weiboDetail.getElementsByAttributeValue("class", "WB_from").last();//最后一个，应为转发的微博也有个累死的信息
			if(e!=null){
				Element child=e.getElementsByAttributeValue("class", "S_link2 WB_time").first();
				if(child!=null){
					weibo.setCreatedAt(child.attr("date"));//创建日期
				}
				child=e.getElementsByAttributeValue("action-type", "app_source").first();
				if(child!=null){
					weibo.setWeiboFrom(child.text());//来源
				}
			}
			
			
		}
		Element  forward=doc.getElementsByAttributeValue("node-type", "forward_detail").first();
		//System.out.println(forward.html());
		if(forward!=null){
			Elements  elements=forward.getElementsByAttributeValue("action-type", "feed_list_page");
			String action_data;
			int max_page=0;//临时max_page;
			int count=0;
			for(Element e:elements){
				
				action_data=e.attr("action-data");
				if(action_data!=null&&!action_data.equals("")){
					String[] datas=action_data.split("&");
					int index=0;
					if(count==0){//id max_id
						index=datas[0].indexOf("=");
						repostUrlComponent.setId(datas[0].substring(index+1));	
						index=datas[1].indexOf("=");	
						repostUrlComponent.setMax_id(datas[1].substring(index+1));
					}
					index=datas[3].indexOf("=");//max_page
					int tmp=Integer.parseInt(datas[3].substring(index+1));
					if(tmp>max_page)
						max_page=tmp;
					
				}
				count++;
			}
		repostUrlComponent.setMaxPage(max_page);
		elements=null;
		}
		
		{
			doc=null;
			weiboDetail=null;
		}
		return repostUrlComponent;
		
	}
	
	/**
	 * 获取评论数 赞数 转发数
	 * @param temp 包含统计数据为文本 eg:评论数(30)
	 * @return count 统计数目
	 * */
	public int getCount(String temp){
		int count=0;
		if(temp.contains("(")){
			int start=temp.indexOf("(")+1;
			temp=temp.substring(start,temp.length()-1);
			count=Integer.parseInt(temp);
		}
		return count;
	}
	
	
	/**
	 * 返回转发列表中的转发微博
	 * @param 转发微博所在的html片段
	 * @return weibos 返回微博list对象
	 * */
	public ArrayList<Weibo> getWeiboList(String html) {
		ArrayList<Weibo> weibos = new ArrayList<Weibo>();
		Document doc = Jsoup.parse(html);
		Elements weiboElements = doc.getElementsByAttributeValueContaining("class",
				"comment_list S_line1 clearfix");
		Weibo weibo;
		User user;
		if (weiboElements != null) {
			for (Element element : weiboElements) {
				weibo=new Weibo();
				user=new User();
				weibo.setMid(element.attr("mid"));//微博id
				Element dt=element.getElementsByTag("dt").first();
				if(dt!=null){
					dt=dt.getElementsByTag("img").first();
					if(dt!=null){
						user.setImg(dt.attr("src"));//用户头像地址
						String temp=dt.attr("usercard");
					    int index=temp.indexOf("=");
					    user.setUid(temp.substring(index+1));//用户id
					}
				}
				Element dd=element.getElementsByTag("dd").first();			
				if(dd!=null){
					//System.out.println(dd.text());
					Element e=dd.getElementsByTag("a").first();
					if(e!=null){
						user.setName(e.attr("title"));//用户名
					}
					e=dd.getElementsByTag("em").first();
					if(e!=null){
						weibo.setContent(e.text());//微博内容
						String text=e.text();
						if(text.contains("//@")){
							int start=text.indexOf("//@");
							String temp=text.substring(start);
							int end=temp.indexOf(":");//用户名中不能出现:所以这样解析应该是正确地
							if(end!=-1)
							weibo.setParent(temp.substring(3,end));
						}else{
							weibo.setParent("root");
						}
					}else{
						System.out.println("no em");
					}
					e=dd.getElementsByAttributeValue("action-type", "feed_list_forward").first();
					if(e!=null){
						weibo.setRepostsCount(getCount(e.text()));//转发量
					}
					e=dd.getElementsByAttributeValue("class", "S_link2 WB_time").first();
					if(e!=null){
						weibo.setCreatedAt(e.attr("date"));//创建时间
						weibo.setSource("http://weibo.com"+e.attr("href"));//微博所在地址
					}
				}
				user.setHomePage("http://weibo.com/u/"+user.getUid());
				weibo.setUser(user);
				weibos.add(weibo);
			}
		}
		{
			doc=null;
		}
		return weibos;
	}
	
	
	/**
	 * 返回转发列表中的转发微博
	 * @param 转发微博所在的html片段
	 * @return mids 微博id列表
	 * */
	public ArrayList<String> getWeiboIds(String html){
		ArrayList<String> mids=new ArrayList<>();
		Document doc = Jsoup.parse(html);
		Elements weiboElements = doc.getElementsByAttributeValueContaining("class",
				"comment_list S_line1 clearfix");
		if(weiboElements!=null){
			for(Element element:weiboElements){
				if(element!=null){
					mids.add(element.attr("mid"));
				}
			}
		}
		{//资源回收
			doc=null;
			weiboElements=null;
		}
		return mids;
	}

}
