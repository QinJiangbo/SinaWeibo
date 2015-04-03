package weibo.preprocessing;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import weibo.crawler.Log;
import weibo.io.DAO;
import weibo.io.WeiboReader;
import weibo.model.CharacteristicData;
import weibo.model.Graph;
/**
 * 数据预处理类
 * 首先从hotweibomids.txt 和nohotweibomids.txt中读取热门和非热门微博mid
 * 利用上面得到的微博mid列表查询数据库中的视图（mid+view）
 * 检索视图得到某条微博相关的参数并存入对应数据库
 * @author coderwang
 *
 */
public class Preprocessing {
	
	private ArrayList<String>hotWeiboMids=null;     //热门微博列表
	private ArrayList<String>nHotWeiboMids=null;	 //非热门微博列表
	private DAO dao=null;   //数据库操作对象
	private  long endTime=1*60*60*1000; //时间间隔

	
	
	/**
	 * 在处理的过程中获取更多的数据信息，包括重点数据的增量，
	 * 速度+加速度+当前距离，对未来数据进行挖掘
	 * 
	 */
	public void processForMoreInfo()throws IOException{
		Log.addLog("解析非热门微博");
		int count =0;
		for(String mid: nHotWeiboMids){
			if(mid==null||mid.equals(""))break;
			processOneWeiboByTimeStamp(1, mid.trim(),0);
			count ++;
			if(count%100 == 0){
				try {
					Thread.sleep(3*60*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Log.addLog("");
		Log.addLog("#####################################################");
		Log.addLog("");
		Log.addLog("解析热门微博");
		for(String mid:hotWeiboMids){
			if(mid==null||mid.equals(""))break;
			processOneWeiboByTimeStamp(1, mid.trim(),1);
		}
	}
	
	
	/**
	 * 取出每条信息单位时间内的数据
	 * @param timeStamp 时间间隔
	 */
	public void processOneWeiboByTimeStamp(int timeSpan,String mid,int popular){
		CharacteristicData cData=new CharacteristicData();
		ResultSet rSet=dao.getOneRow(mid+"view", mid);
		try {
			while(rSet.next()){//提取创建时间			
				cData.setCreateTime(Long.parseLong(rSet.getString("create_time")));//创建时间
			}
			long end=endTime;
			end+=cData.getCreateTime();
			int repostCount=dao.getRepostCountByTime(mid, end);
			cData.setRepostCount(repostCount);//当前转发数
			Graph graph=dao.getGraph(mid+"view", end, mid);//获取整个图状结构
			graph.initSeries(timeSpan);
			graph.setSeries(timeSpan, cData.getCreateTime());
			ArrayList<CharacteristicData>cDatas=new ArrayList<>();
			for(int i=0;i<graph.getRepostCountSeries().length;++i){
				CharacteristicData cData2=new CharacteristicData();
				cData2.setMid(mid);
				setDynamicCharacters(1,cData2, i, graph.getRepostCountSeries(), timeSpan);
				setDynamicCharacters(2,cData2, i, graph.getvUserCountSeries(), timeSpan);
				setDynamicCharacters(3,cData2, i, graph.getWidthSeries(), timeSpan);
				setDynamicCharacters(4,cData2, i, graph.getDepthSeries(), timeSpan);
				setDynamicCharacters(cData2, i, graph.getFollowerCountSeries(), timeSpan);
				cData2.setPopular(popular);//最终的流行度
				cData2.setTimeStamp(i);//数据所在的时间区间
				//System.out.println(cData2.predicDynamic());
				cDatas.add(cData2);
			}	
			dao.insertDynamicData("dynamicdata", cDatas);
			cDatas.clear();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try {
				if(rSet!=null)
				rSet.close();
				} catch (SQLException e) {
			}
			rSet=null;
		}	
	}
	
	/**
	 * 为一个特征对象赋值
	 * @param cData 被赋值的对象
	 * @param index 值在数组中的位置
	 * @param featureSeries 特征时间序列
	 */
	public void setDynamicCharacters(int type,CharacteristicData cData,int index ,int []featureSeries,int timeSpan){
		if(type==1){//转发量
			if(index==0)
				cData.setRepostCount(featureSeries[index]);
			else{
				int count=0;
				for(int i=0;i<=index;i++){
					count+=featureSeries[i];
				}
				cData.setRepostCount(count);
			}
			if(index>0){
				cData.setRepostCountV((double)featureSeries[index]/(double)timeSpan);//速度
				cData.setRepostCountA((cData.getRepostCountV()-(double)featureSeries[index-1]/(double)timeSpan)/timeSpan);//加速度
				
			}else{
				cData.setRepostCountV((double)featureSeries[index]/(double)timeSpan);//速度
			}
	
		}
		if(type==2){//加V用户数
			if(index==0)
				cData.setvUserCount(featureSeries[index]);
			else{
				int count=0;
				for(int i=0;i<=index;i++){
					count+=featureSeries[i];
				}
				cData.setvUserCount(count);
			}
			
			if(index>0){
				cData.setvUserCountV((double)featureSeries[index]/(double)timeSpan);//速度
				cData.setvUserCountA((cData.getvUserCountV()-(double)featureSeries[index-1]/(double)timeSpan)/(double)timeSpan);//加速度
			
			}else{
				cData.setvUserCountV((double)featureSeries[index]/(double)timeSpan);//速度
			}
		}
		if(type==3){//转发宽度
			cData.setWidth(featureSeries[index]);
			if(index>0){
				cData.setWidthV(featureSeries[index]-featureSeries[index-1]/(double)timeSpan);//速度
			}else{
				cData.setWidthV((double)featureSeries[index]/timeSpan);
			}
			if(index>1){
				cData.setWidthA((cData.getWidthV()-((double)featureSeries[index-1]/timeSpan-(double)featureSeries[index-2]/timeSpan))/(double)timeSpan);
			}
		}
		if(type==4){//转发深度
			cData.setDepth(featureSeries[index]);
			if(index>0){
				cData.setDepthV((double)featureSeries[index]-(double)featureSeries[index-1]/(double)timeSpan);//速度
			}else{
				cData.setDepthA((double)featureSeries[index]/timeSpan);
			}
			if(index>1){
				cData.setDepthA((cData.getDepthV()-((double)featureSeries[index-1]/timeSpan-(double)featureSeries[index-2]/timeSpan))/(double)timeSpan);
			}
		}
		
	}
	
	/**
	 * 为一个特征对象赋值
	 * @param cData 被赋值的对象
	 * @param index 值在数组中的位置
	 * @param featureSeries 特征时间序列
	 */
	public void setDynamicCharacters(CharacteristicData cData,int index ,long []featureSeries,int timeSpan){		
		if(index==0)
			cData.setFollowersCount(featureSeries[index]);
		else{
			long count=0;
			for(int i=0;i<=index;i++){
				count+=featureSeries[i];
			}
			cData.setFollowersCount(count);
		}
		if(index>0){
			cData.setFollowersCountA(1.0*featureSeries[index]/(double)timeSpan);//速度
			cData.setFollowersCountA((cData.getFollowersCountV()-1.0*featureSeries[index-1]/timeSpan)/(double)timeSpan);//加速度		
		}else{
			cData.setFollowersCountA(1.0*featureSeries[index]/(double)timeSpan);//速度
		}
	}
	
	/**
	 * 进行处理
	 * @throws IOException 
	 */
	public void process() throws IOException{
		Log.addLog("解析非热门微博");
		for(String mid: nHotWeiboMids){
			if(mid==null||mid.equals(""))break;
			processOneWeibo(mid.trim(),0);
		}
		Log.addLog("");
		Log.addLog("#####################################################");
		Log.addLog("");
		Log.addLog("解析热门微博");
		for(String mid:hotWeiboMids){
			if(mid==null||mid.equals(""))break;
			processOneWeibo(mid.trim(),1);
		}
	}
	
	/**
	 * 处理一条微博
	 * @param mid 微博id
	 * @throws IOException 
	 */
	@SuppressWarnings("null")
	public void processOneWeibo(String mid,int popular) throws IOException{
		Log.addLog("解析"+mid+"转发特征");
		//1、获取微博文本特征和发布者基本特征
		CharacteristicData cData=new CharacteristicData();
		ResultSet rSet=dao.getOneRow(mid+"view", mid);
		String temp=null;
		try {
			while(rSet.next()){
				temp=rSet.getString("img_src");
				int hasPic=0;
				if(temp!=null||!temp.equals("")){
					hasPic=1;
				}
				cData.setHasPic(hasPic);//是否有图片
				temp=rSet.getString("content");
				if(temp!=null)
					cData.setContentLength(temp.length());//文本长度
				cData.setCreateTime(Long.parseLong(rSet.getString("create_time")));//创建时间
				cData.setFinalRepostCount(rSet.getInt("repost_count"));//最终转发数量
				cData.setVerified(rSet.getInt("verified"));//是否认证用户
				cData.setLevel(rSet.getInt("level"));//用户等级
				cData.setFriendsCount(rSet.getInt("friends_count"));//用户好友数
				cData.setFollowerCount(rSet.getInt("followers_count"));//用户粉丝数
				cData.setStatusesCount(rSet.getInt("weibos_count"));//用户发布微博数
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{	
				try {
					if(rSet!=null)
					rSet.close();
					} catch (SQLException e) {
				}
				rSet=null;
				temp=null;
		}
		//2、获取传播特征
		endTime+=cData.getCreateTime();
		int repostCount=dao.getRepostCountByTime(mid, endTime);
		cData.setRepostCount(repostCount);//当前转发数
		long followersCount=dao.getFollowersCountByTime(mid+"users", endTime);
		cData.setFollowersCount(followersCount);//当前粉丝总数
		int vUserCount=dao.getVusersCountByTime(mid+"users",endTime);
		cData.setvUserCount(vUserCount);
		
		Graph graph=dao.getGraph(mid+"view", endTime, mid);
		graph.bfsForWD();
		System.out.println(graph.toString());
		cData.setDepth(graph.getDepth());
		cData.setWidth(graph.getMaxWidth());
		
		//3、添加分类特征 popular
		cData.setPopular(popular);
		//4、完整数据存入数据库
		dao.insertVdata(mid, cData, "cdata");
		Log.addLog(cData.toString());
	}
	
	/**
	 * 初始化
	 */
	public void init(){
		dao=new DAO();
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");
		getSeedWeiboMids();
		System.out.println("1、初始化成功");	
	}
	
	
	
	/**
	 * 读取种子微博mid
	 */
	public void getSeedWeiboMids(){
		hotWeiboMids=new ArrayList<>();
		nHotWeiboMids=new ArrayList<>();
		WeiboReader reader=new WeiboReader();
		String path="./data/seedWeiboMids/";
		try {
			hotWeiboMids=reader.readWholeList(path+"hotWeiboMids.txt");
			nHotWeiboMids=reader.readWholeList(path+"nhotWeiboMids.txt");
		} catch (IOException e) {
			
			e.printStackTrace();
		}finally{
			reader.close();
			path="";
		}
	}
	
	

}



