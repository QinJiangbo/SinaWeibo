package weibo.prase;

import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import weibo.model.User;
import weibo.model.UserHomePage;
import com.alibaba.fastjson.JSON;

/**
 * @author coderwang 2014/2/25 解析用户主页获取主要信息
 * */
public class UserHomePagePrase implements WeiboPrase {

	/**
	 * @param content
	 *            用户主页源代码
	 * @return mainContent 用户信息所在的html代码片段
	 * */

	public String getMainContent(String content) {
		String mainContent = "";
		Document doc = Jsoup.parse(content);
		Elements elements = doc.getElementsByTag("script");
		Element element=null;
		UserHomePage uHomePage =null;
		for (int i = 0; i < elements.size(); ++i) {
			element = elements.get(i);
			if(element!=null)
			if (element.data().startsWith("FM.view({\"ns\":\"pl.header.head.index\"")) {
				mainContent = (String) element.data().substring(8,element.data().length() - 1);
				uHomePage = JSON.parseObject(mainContent,UserHomePage.class);
				mainContent = uHomePage.getHtml();
				break;
			}
		}
		
		{
			//资源回收
			element=null;
			elements=null;
			doc=null;
			if(uHomePage!=null){
				uHomePage.setHtml("");
				uHomePage=null;	
			}
		}
		return mainContent;
	}

	/**
	 * 解析个人用户主页
	 * @param content
	 * @return
	 */
	public  User prasePersonalUser(String content){
		User user=new User();
		Document doc=Jsoup.parse(content);
		Element element=null;
		Element pf_name=null;
		Element pf_intro =null;
		Element pf_tags=null;
		Element pf_info_right=null;
		Element user_atten=null;
		Elements aElements=null;
		if(doc!=null){
			
			pf_name=doc.getElementsByAttributeValue("class", "pf_name bsp clearfix").first();
			if(pf_name!=null){
				element=pf_name.getElementsByAttributeValue("class", "name").first();
				if(element!=null){
					user.setName(element.text());//姓名
				}
				
				element=pf_name.getElementsByAttributeValueContaining("title", "当前等级").first();
				if(element!=null){
					 
					 int i= element.attr("title").indexOf("：");//中文分号
					 String level=element.attr("title").substring(i+1);
					 user.setLevel(Integer.parseInt(level));//设置用户等级
					 String id=element.attr("levelcard");
					 int index=id.indexOf("=");
					 id=id.substring(index+1);
					 user.setUid(id);//用户id
				}
				
			}
			pf_intro = doc.getElementsByAttributeValue("class", "pf_intro bsp").first();
			if (pf_intro != null) {	
					user.setDescription(pf_intro.text());//自我介绍
			}

			pf_tags=doc.getElementsByAttributeValue("class", "pf_tags bsp").first();
			if (pf_tags != null) {
				element =pf_tags.getElementsByAttributeValue("class", "layer_menulist_tags S_line3 S_bg5").first();
				if(element !=null){
					user.setTags(element .text());//设置用户标签
				}
				element  =pf_tags.getElementsByAttributeValue("class", "tags").first();
				if(element !=null){
					aElements=element .getElementsByTag("a");
					for(Element href:aElements){
						if(href!=null&&href.attr("href").contains("loc=infgender")){
							user.setGender(href.children().first().attr("title"));//性别
						}
						if(href!=null&&href.attr("href").contains("loc=infsign")){
							user.setConstellation(href.text());//星座
						}
						if(href!=null&&href.attr("href").contains("loc=infplace")){
							user.setLocation(href.text());//所在地
						}						
						if(href!=null&&href.attr("href").contains("loc=infedu")){
							user.setEdu(href.text());//教育
						}
						if(href!=null&&href.attr("href").contains("loc=infjob")){
							user.setJob(href.text());//公司
						}
					}
				}	
			}
			
			pf_info_right = doc.getElementsByAttributeValue("class", "pf_info_right").first();
			user.setVerified(false);
			if (pf_info_right != null) {
					element=pf_info_right.getElementsByAttributeValue("class", "pf_verified bsp").first();					
					if(element!=null){
						user.setVerified(true);//是否为认证用户
					}
			}
			
			user_atten=doc.getElementsByAttributeValueContaining("class", "user_atten clearfix").first();
			if(user_atten!=null){	
				element=user_atten.getElementsByAttributeValueContaining("href", "mod=headfollow").first();
				user.setFriendsCount(getCount(element));
				element=user_atten.getElementsByAttributeValueContaining("href", "mod=headfans").first();
				user.setFollowersCount(getCount(element));
				element=user_atten.getElementsByAttributeValueContaining("href", "mod=headweibo").first();
				user.setStatusesCount(getCount(element));
			}	
		}
		
		{//资源回收
			doc=null;
			element=null;
			pf_name=null;
			pf_intro =null;
			pf_tags=null;
			pf_info_right=null;
			user_atten=null;
			aElements=null;
			
		}
		return user;
	}
	
	/**
	 * 解析企业用户主页
	 * @param content 用户信息所在的html源码
	 * @return user 用户对象
	 * @throws UnsupportedEncodingException 
	 * */
	public User praseBusinessUser(String content) throws UnsupportedEncodingException {
		User user = new User();
		Document doc = Jsoup.parse(content);
		Element element=null;// 临时变量
		Element headAndCount=null;
		Elements elements=null;
		Element userBaseInfo=null;
		Element tmp =null;
		// 解析头像以及计数器
		if (doc != null) {
			headAndCount = doc.getElementsByAttributeValue("class",
					"pf_head S_bg5 S_line1").first();
			if (headAndCount != null) {
				element = headAndCount.getElementsByAttributeValue("class",
						"pf_head_pic").first();
				if (element != null) {
					element = element.getElementsByTag("img").first();
					if (element != null) {
						user.setImg(element.attr("src"));// 头像地址
					}
				}
				element = headAndCount.getElementsByAttributeValue("class",
						"user_atten").first();
				if (element != null&&element.text().contains("关注")&&element.text().contains("粉丝")&&element.text().contains("微博")) {
					elements=headAndCount.getElementsByTag("strong");
					int count=0;
					for(Element e:elements){
						count++;
						if(count==1){
							user.setFriendsCount(Integer.parseInt(e.text()));// 关注数
						}
						if(count==2){
							user.setFollowersCount(Integer.parseInt(e.text()));// 粉丝数
						}
						if(count==3){
							user.setStatusesCount(Integer.parseInt(e.text()));// 微博数
						}
					}
				}
			}

			// 解析用户基本信息
			userBaseInfo = doc.getElementsByAttributeValue("class",
					"pf_info W_fl").first();
			if (userBaseInfo != null) {
				element = userBaseInfo.getElementsByAttributeValue("class",
						"username").first();
				if (element != null) {
					
					user.setVerified(false);
					if (element.html().contains("新浪机构认证")) {// 是否认证用户
						user.setVerified(true);
					}
					tmp = element.getElementsByAttributeValueStarting(
							"title", "当前等级").first();
					if (tmp != null) {
						int i = tmp.attr("title").indexOf("：");// 中文分号
						String level = tmp.attr("title").substring(i + 1);
						user.setLevel(Integer.parseInt(level));// 设置用户等级
					}
					
					
					tmp=element.getElementsByTag("strong").first();
					if(tmp!=null){
						user.setName(tmp.text());
					}
				}
				element = userBaseInfo.getElementsByAttributeValue("class",
						"moreinfo").first();
				if (element != null) {
					//System.out.println(element.text().);
					user.setDescription(element.text());// 用户的附加描述信息
				}

			}
		}
		
		{
			//资源回收
			doc=null;
			element=null;// 临时变量
			headAndCount=null;
			elements=null;
			userBaseInfo=null;
			tmp =null;		
		}
		return user;
	}
	
	
	/**
	 * 解析统计节点
	 * @param element 带解析的节点
	 * @return count 返回关注数，粉丝数和微博数等
	 */
	@SuppressWarnings("null")
	public int getCount(Element element) {
		String text=null;
		Element e=null;
		int count=0;
		if (element != null) {
			e=element.getElementsByTag("strong").first();
			if(e!=null){
				text=e.text();
				if (text != null || text.equals("")) {
					count= Integer.parseInt(text);
				}
			}
		} 
		{//资源回收
			text="";
			text=null;
			e=null;
		}
		return count;
	}
	
	/**
	 * 根据用户信息所在的html片段，解析出用户的基本信息
	 * 
	 * @param content
	 *            用户信息所在的html源代码
	 * @return user 返回用户对象
	 * @throws UnsupportedEncodingException 
	 * */
	public User getUser(String content) throws UnsupportedEncodingException {
		User user=new User();
		if(content.contains("PRF_header")){//企业用户
			user=praseBusinessUser(content);
			user.setUserType(1);
		}else{	
			user=prasePersonalUser(content);//个人用户
			user.setUserType(0);
		}
		return user;
	}

}
