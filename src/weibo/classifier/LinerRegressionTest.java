package weibo.classifier;

import java.util.ArrayList;

import weibo.io.DAO;
import weibo.model.CharacteristicData;

public class LinerRegressionTest {
	
	private static DAO dao;
	
	
	
	
	public static void init(){
		dao=new DAO();
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");
	}
	
	
	
	public  static void  main(String[]args){
		init();
		CharacteristicData cData=new CharacteristicData();
		cData.setMid("3659533857365164");
		getPredictFeatures(cData, 14);

				
		
	}
	
	public static void getPredictFeatures(CharacteristicData cData,int timeSpanCount){
		ArrayList<CharacteristicData>cDatas=dao.getDynamicFeaturesByMid(cData.getMid(), "dynamicdata");
		double timeSeries[]=new double[timeSpanCount];//ʱ�����
		double repostSeries[]=new double[timeSpanCount];//ת����
		double widthSeries[]=new double[timeSpanCount];//���
		double depthSeries[]=new double [timeSpanCount];//���
		double vUserCountSeries[]=new double[timeSpanCount];//��V�û���
		double followersCountSeries[]=new double [timeSpanCount];//��ǰ��˿��
		
		CharacteristicData cdata=null;
		for(int i=0;i<timeSpanCount;++i){
			cdata=cDatas.get(i);
			timeSeries[i]=cdata.getTimeStamp()*1.0;
			repostSeries[i]=cdata.getRepostCount()*1.0;
			widthSeries[i]=cdata.getWidth()*1.0;
			depthSeries[i]=cdata.getDepth()*1.0;
			vUserCountSeries[i]=cdata.getvUserCount()*1.0;
			followersCountSeries[i]=cdata.getFollowersCount()*1.0;
		}
		/**������ر���ֵ��Ԥ��**************************************************/
		LinerRegression lr=new LinerRegression();
		int predictTime=15;//Ԥ���ʱ��
		int repostCount=(int)lr.predict(timeSeries, repostSeries, predictTime);
		cData.setRepostCount(repostCount);
		int width=(int)lr.predict(timeSeries, widthSeries, predictTime);
		cData.setWidth(width);
		int depth=(int)lr.predict(timeSeries, depthSeries, predictTime);
		cData.setDepth(depth);
		int vUserCount=(int)lr.predict(timeSeries, vUserCountSeries, predictTime);
		cData.setvUserCount(vUserCount);
		long followersCount=(long)lr.predict(timeSeries, followersCountSeries, predictTime);
		cData.setFollowersCount(followersCount);
		
		/*�������������ʵֵ*/
		cdata=cDatas.get(15);
		
		for(int i=0;i<timeSpanCount;++i){
			System.out.print(depthSeries[i]+" ");
		}
		System.out.println("\r");
		
		System.out.println(cdata.getRepostCount()+"\t"+cData.getRepostCount());
		System.out.println(cdata.getWidth()+"\t"+cData.getWidth());
		System.out.println(cdata.getDepth()+"\t"+cData.getDepth());
		System.out.println(cdata.getvUserCount()+"\t"+cData.getvUserCount());
		System.out.println(cdata.getFollowersCount()+"\t"+cData.getFollowersCount());
	}
	

}
