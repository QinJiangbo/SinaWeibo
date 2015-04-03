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
 * ����Ԥ������
 * ���ȴ�hotweibomids.txt ��nohotweibomids.txt�ж�ȡ���źͷ�����΢��mid
 * ��������õ���΢��mid�б��ѯ���ݿ��е���ͼ��mid+view��
 * ������ͼ�õ�ĳ��΢����صĲ����������Ӧ���ݿ�
 * @author coderwang
 *
 */
public class Preprocessing {
	
	private ArrayList<String>hotWeiboMids=null;     //����΢���б�
	private ArrayList<String>nHotWeiboMids=null;	 //������΢���б�
	private DAO dao=null;   //���ݿ��������
	private  long endTime=1*60*60*1000; //ʱ����

	
	
	/**
	 * �ڴ���Ĺ����л�ȡ�����������Ϣ�������ص����ݵ�������
	 * �ٶ�+���ٶ�+��ǰ���룬��δ�����ݽ����ھ�
	 * 
	 */
	public void processForMoreInfo()throws IOException{
		Log.addLog("����������΢��");
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
		Log.addLog("��������΢��");
		for(String mid:hotWeiboMids){
			if(mid==null||mid.equals(""))break;
			processOneWeiboByTimeStamp(1, mid.trim(),1);
		}
	}
	
	
	/**
	 * ȡ��ÿ����Ϣ��λʱ���ڵ�����
	 * @param timeStamp ʱ����
	 */
	public void processOneWeiboByTimeStamp(int timeSpan,String mid,int popular){
		CharacteristicData cData=new CharacteristicData();
		ResultSet rSet=dao.getOneRow(mid+"view", mid);
		try {
			while(rSet.next()){//��ȡ����ʱ��			
				cData.setCreateTime(Long.parseLong(rSet.getString("create_time")));//����ʱ��
			}
			long end=endTime;
			end+=cData.getCreateTime();
			int repostCount=dao.getRepostCountByTime(mid, end);
			cData.setRepostCount(repostCount);//��ǰת����
			Graph graph=dao.getGraph(mid+"view", end, mid);//��ȡ����ͼ״�ṹ
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
				cData2.setPopular(popular);//���յ����ж�
				cData2.setTimeStamp(i);//�������ڵ�ʱ������
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
	 * Ϊһ����������ֵ
	 * @param cData ����ֵ�Ķ���
	 * @param index ֵ�������е�λ��
	 * @param featureSeries ����ʱ������
	 */
	public void setDynamicCharacters(int type,CharacteristicData cData,int index ,int []featureSeries,int timeSpan){
		if(type==1){//ת����
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
				cData.setRepostCountV((double)featureSeries[index]/(double)timeSpan);//�ٶ�
				cData.setRepostCountA((cData.getRepostCountV()-(double)featureSeries[index-1]/(double)timeSpan)/timeSpan);//���ٶ�
				
			}else{
				cData.setRepostCountV((double)featureSeries[index]/(double)timeSpan);//�ٶ�
			}
	
		}
		if(type==2){//��V�û���
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
				cData.setvUserCountV((double)featureSeries[index]/(double)timeSpan);//�ٶ�
				cData.setvUserCountA((cData.getvUserCountV()-(double)featureSeries[index-1]/(double)timeSpan)/(double)timeSpan);//���ٶ�
			
			}else{
				cData.setvUserCountV((double)featureSeries[index]/(double)timeSpan);//�ٶ�
			}
		}
		if(type==3){//ת�����
			cData.setWidth(featureSeries[index]);
			if(index>0){
				cData.setWidthV(featureSeries[index]-featureSeries[index-1]/(double)timeSpan);//�ٶ�
			}else{
				cData.setWidthV((double)featureSeries[index]/timeSpan);
			}
			if(index>1){
				cData.setWidthA((cData.getWidthV()-((double)featureSeries[index-1]/timeSpan-(double)featureSeries[index-2]/timeSpan))/(double)timeSpan);
			}
		}
		if(type==4){//ת�����
			cData.setDepth(featureSeries[index]);
			if(index>0){
				cData.setDepthV((double)featureSeries[index]-(double)featureSeries[index-1]/(double)timeSpan);//�ٶ�
			}else{
				cData.setDepthA((double)featureSeries[index]/timeSpan);
			}
			if(index>1){
				cData.setDepthA((cData.getDepthV()-((double)featureSeries[index-1]/timeSpan-(double)featureSeries[index-2]/timeSpan))/(double)timeSpan);
			}
		}
		
	}
	
	/**
	 * Ϊһ����������ֵ
	 * @param cData ����ֵ�Ķ���
	 * @param index ֵ�������е�λ��
	 * @param featureSeries ����ʱ������
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
			cData.setFollowersCountA(1.0*featureSeries[index]/(double)timeSpan);//�ٶ�
			cData.setFollowersCountA((cData.getFollowersCountV()-1.0*featureSeries[index-1]/timeSpan)/(double)timeSpan);//���ٶ�		
		}else{
			cData.setFollowersCountA(1.0*featureSeries[index]/(double)timeSpan);//�ٶ�
		}
	}
	
	/**
	 * ���д���
	 * @throws IOException 
	 */
	public void process() throws IOException{
		Log.addLog("����������΢��");
		for(String mid: nHotWeiboMids){
			if(mid==null||mid.equals(""))break;
			processOneWeibo(mid.trim(),0);
		}
		Log.addLog("");
		Log.addLog("#####################################################");
		Log.addLog("");
		Log.addLog("��������΢��");
		for(String mid:hotWeiboMids){
			if(mid==null||mid.equals(""))break;
			processOneWeibo(mid.trim(),1);
		}
	}
	
	/**
	 * ����һ��΢��
	 * @param mid ΢��id
	 * @throws IOException 
	 */
	@SuppressWarnings("null")
	public void processOneWeibo(String mid,int popular) throws IOException{
		Log.addLog("����"+mid+"ת������");
		//1����ȡ΢���ı������ͷ����߻�������
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
				cData.setHasPic(hasPic);//�Ƿ���ͼƬ
				temp=rSet.getString("content");
				if(temp!=null)
					cData.setContentLength(temp.length());//�ı�����
				cData.setCreateTime(Long.parseLong(rSet.getString("create_time")));//����ʱ��
				cData.setFinalRepostCount(rSet.getInt("repost_count"));//����ת������
				cData.setVerified(rSet.getInt("verified"));//�Ƿ���֤�û�
				cData.setLevel(rSet.getInt("level"));//�û��ȼ�
				cData.setFriendsCount(rSet.getInt("friends_count"));//�û�������
				cData.setFollowerCount(rSet.getInt("followers_count"));//�û���˿��
				cData.setStatusesCount(rSet.getInt("weibos_count"));//�û�����΢����
				
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
		//2����ȡ��������
		endTime+=cData.getCreateTime();
		int repostCount=dao.getRepostCountByTime(mid, endTime);
		cData.setRepostCount(repostCount);//��ǰת����
		long followersCount=dao.getFollowersCountByTime(mid+"users", endTime);
		cData.setFollowersCount(followersCount);//��ǰ��˿����
		int vUserCount=dao.getVusersCountByTime(mid+"users",endTime);
		cData.setvUserCount(vUserCount);
		
		Graph graph=dao.getGraph(mid+"view", endTime, mid);
		graph.bfsForWD();
		System.out.println(graph.toString());
		cData.setDepth(graph.getDepth());
		cData.setWidth(graph.getMaxWidth());
		
		//3����ӷ������� popular
		cData.setPopular(popular);
		//4���������ݴ������ݿ�
		dao.insertVdata(mid, cData, "cdata");
		Log.addLog(cData.toString());
	}
	
	/**
	 * ��ʼ��
	 */
	public void init(){
		dao=new DAO();
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");
		getSeedWeiboMids();
		System.out.println("1����ʼ���ɹ�");	
	}
	
	
	
	/**
	 * ��ȡ����΢��mid
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



