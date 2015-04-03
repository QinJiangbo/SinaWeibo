package weibo.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import weibo.featuresSelection.GainRatioEval;
import weibo.io.DAO;
import weibo.model.CDatas;
import weibo.model.CharacteristicData;
import weibo.model.MeasureArguments;

/**
 * 基于预测的段时间热点微博发现算法
 * 
 * @author coderwang
 * 
 */
public class DesicionTreeAndLinerRegression {

	static DAO dao = new DAO();
	static CDatas cDatas = new CDatas();
	static ArrayList<CharacteristicData> dynamicDatas=new ArrayList<CharacteristicData>();
	static ArrayList<MeasureArguments> measureArgumentsList;
	static ArrayList<MeasureArguments> statisticArg = new ArrayList<MeasureArguments>();// 用于全局统计
	static int totalGroup=0;//总的测试组数
	/**
	 * @param dao 数据库操作对象
	 * @param cDatas //用于预测分类的数据集
	 * @param measureArgumentsList 评价指标对象
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int numOfData=450;
		init(numOfData);
		int timeCount=5;
		int groupSize=numOfData/5;
		int testTime=1;
		startTest(groupSize, testTime,timeCount);//组大小，测试次数，预测使用时间：分钟
		
		
		/*###############################################################*/
		//String tips[]={"base","predict"};
		System.out.println("\n\n############综合测试结果##################");
		System.out.println(	"accuracy       " +
						   	"errorRate        " +
							"recall        " +
							"specifity        " +
							"precision         " +
							"f1\t           ");
		String tip="";
		if(timeCount==60){
			tip="nopredict";
		}else{
			tip=timeCount+"";
		}
		for(int i=0;i<statisticArg.size();++i){
			MeasureArguments mArguments=statisticArg.get(i);
			String result=roundDouble(mArguments.getAccuracy()/totalGroup)
					+"\t\t"+roundDouble(mArguments.getErrorRate()/totalGroup)
					+"\t\t"+roundDouble(mArguments.getRecall()/totalGroup)
					+"\t\t"+roundDouble(mArguments.getSpecifity()/totalGroup)
					+"\t\t"+roundDouble(mArguments.getPrecision()/totalGroup)
					+"\t\t"+roundDouble(mArguments.getF1()/totalGroup)+"\t"+tip;
			System.out.println(result);
		}
		
		
		/*###############################################################*/
	}

/**
 * 
 * @param num加载数据数目
 */
	public static void init(int num) {
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");
		cDatas.loadDataFromDB(dao,num);
	/*	for(int i=0;i<cDatas.getDatas().size();++i){//测试数据的动态数据
			dynamicDatas.addAll(dao.getDynamicFeaturesByMid(cDatas.getcDatas().get(i).getMid(), "dynamicdata"));
		}*/
		MeasureArguments m1=new MeasureArguments();
		statisticArg.add(m1);

	}

	public static ArrayList<CharacteristicData> getDynamicDataByMid(String mid){
		ArrayList<CharacteristicData> dDatas=new ArrayList<CharacteristicData>();
		for(int i=0;i<dynamicDatas.size();++i){
			CharacteristicData cData=dynamicDatas.get(i);
			if(cData.getMid().endsWith(mid)){
				dDatas.add(cData);
				if(dDatas.size()==60)break;
			}
		}
		return dDatas;
	}
	public static void getPredictFeatures(CharacteristicData cData, int timeSpanCount,
			DAO dao) {
		ArrayList<CharacteristicData> cDatas = dao.getDynamicFeaturesByMid(
		cData.getMid(), "dynamicdata");
		//ArrayList<CharacteristicData> cDatas=getDynamicDataByMid(cData.getMid());
		double timeSeries[] = new double[timeSpanCount];// 时间变量
		double repostSeries[] = new double[timeSpanCount];// 转发数
		double widthSeries[] = new double[timeSpanCount];// 宽度
		double depthSeries[] = new double[timeSpanCount];// 深度
		double vUserCountSeries[] = new double[timeSpanCount];// 加V用户数
		double followersCountSeries[] = new double[timeSpanCount];// 当前粉丝数

		CharacteristicData cdata = null;
		for (int i = 0; i < timeSpanCount; ++i) {
			if(cDatas.size()>=timeSpanCount){
				cdata = cDatas.get(i);
				timeSeries[i] = cdata.getTimeStamp() * 1.0;
				repostSeries[i] = cdata.getRepostCount() * 1.0;
				widthSeries[i] = cdata.getWidth() * 1.0;
				depthSeries[i] = cdata.getDepth() * 1.0;
				vUserCountSeries[i] = cdata.getvUserCount() * 1.0;
				followersCountSeries[i] = cdata.getFollowersCount() * 1.0;
			}else{
				return;
			}
		}
		/** 进行相关变量值的预测 **************************************************/
		LinerRegression lr = new LinerRegression();
		int predictTime = 60;// 预测的时间
		int repostCount = (int) lr.predict(timeSeries, repostSeries,
				predictTime);
		cData.setRepostCount(repostCount);
		int width = (int) lr.predict(timeSeries, widthSeries, predictTime);
		cData.setWidth(width);
		// int depth=(int)lr.predict(timeSeries, depthSeries, predictTime);
		// cData.setDepth(depth);
		cData.setDepth((int) (depthSeries[depthSeries.length - 1] * 2.5));// 无法线性预测
		int vUserCount = (int) lr.predict(timeSeries, vUserCountSeries,
				predictTime);
		cData.setvUserCount(vUserCount);
		// long followersCount=(long)lr.predict(timeSeries,
		// followersCountSeries, predictTime);
		// cData.setFollowersCount(followersCount);
		cData.setFollowersCount((long) (followersCountSeries[followersCountSeries.length - 1] * 2.5));
		cDatas.clear();
	}

	/**
	 * 开始测试
	 * 
	 * @param testTime测试的伦数
	 * @param groupSize
	 *            每组数据大小
	 */
	public static void startTest(int groupSize, int testTime,int timeSpanCount) {
		for (int i = 0; i < testTime; i++) {
			System.out.println("#############第 "+i+" 伦测试#################");
			CompareToBase(groupSize, timeSpanCount);
			for(int k=0;k<measureArgumentsList.size();++k){
				statisticArg.get(k%1).setAccuracy(statisticArg.get(k%1).getAccuracy()+measureArgumentsList.get(k).getAccuracy());
				statisticArg.get(k%1).setErrorRate(statisticArg.get(k%1).getErrorRate()+measureArgumentsList.get(k).getErrorRate());
				statisticArg.get(k%1).setRecall(statisticArg.get(k%1).getRecall()+measureArgumentsList.get(k).getRecall());
				statisticArg.get(k%1).setPrecision(statisticArg.get(k%1).getPrecision()+measureArgumentsList.get(k).getPrecision());
				statisticArg.get(k%1).setSpecifity(statisticArg.get(k%1).getSpecifity()+measureArgumentsList.get(k).getSpecifity());
				statisticArg.get(k%1).setF1(statisticArg.get(k%1).getF1()+measureArgumentsList.get(k).getF1());
			}
			measureArgumentsList.clear();
			measureArgumentsList=null;
		}
	}


	/**
	 * 单伦测试的相关结果分析
	 */
	public static void CompareToBase(int groupSize,int timeSpanCount){
		measureArgumentsList=new ArrayList<>();
		k_fold_cross_validation(groupSize,timeSpanCount);	
		System.out.println("\n\n############单次测试结果##################");
		System.out.println(	"accuracy       " +
						   	"errorRate        " +
							"recall        " +
							"specifity        " +
							"precision         " +
							"f1\t           ");
		String tip;
		if(timeSpanCount==60){
			 tip="nopredict";
		}else{
			 tip=timeSpanCount+"";
		}
		for(int i=0;i<measureArgumentsList.size();++i){
			System.out.println(measureArgumentsList.get(i).getString()+tip);
		}

	}

	/**
	 * 构建决策树，并进行预测
	 * 
	 * @param testSet
	 *            测试集合
	 * @param traingingSet
	 *            预测集合
	 */
	public static void Predict(ArrayList<CharacteristicData> testSet,
			ArrayList<CharacteristicData> traingingSet) {
		CDatas trainingCDatas = cDatas.getCDatas();
		CDatas testingDatas = cDatas.getCDatas();
		testingDatas.setcDatas(testSet);
		trainingCDatas.setcDatas(traingingSet);
		GainRatioEval gainRatioEval = new GainRatioEval(trainingCDatas);
		DecisionTree dTree = new DecisionTree();
		dTree.GenerateRoot(trainingCDatas, gainRatioEval);
		// dTree.bfs();
		for (CharacteristicData cData : testingDatas.getcDatas()) {
			cData.setPredictResult(dTree.predict(cData));
		}
		MeasureArguments measureArguments = new MeasureArguments();
		measureArguments.loadBasicArguments(testingDatas);
		measureArguments.caculateMeasures();
		measureArgumentsList.add(measureArguments);
	}

	/**
	 * 设定group大小进行k轮测试
	 * 
	 * @param groupSize
	 */

	public static void k_fold_cross_validation(int groupSize,int predictTime) {
		System.out.println("################## k-fold Cross-Validation #############");
		int groupNum = cDatas.getDatas().size() / groupSize;
		ArrayList<ArrayList<CharacteristicData>> datas = new ArrayList<ArrayList<CharacteristicData>>();
		for (int i = 0; i < groupNum; ++i) {
			ArrayList<CharacteristicData> datas0 = new ArrayList<>();
			datas.add(datas0);
		}
		int index;
		Random random = new Random();
		for (int i = 0; i < cDatas.getcDatas().size(); ++i) {
			index = random.nextInt(groupNum);
			datas.get(index).add(cDatas.getcDatas().get(i));
		}
		ArrayList<CharacteristicData> trainingSet = new ArrayList<>();
		ArrayList<CharacteristicData> testSet = new ArrayList<>();
		for (int k = 0; k < groupNum; ++k) {
			System.out.println("第 " + (k + 1) + " 组测试");
			totalGroup++;
			for (int i = 0; i < datas.size(); ++i) {
				if (i == k) {
					testSet.addAll(datas.get(i));
				} else {
					trainingSet.addAll(datas.get(i));
				}
			}
			int totalFeature[] = cDatas.getFeaturesToEval();
			cDatas.setFeaturesToEval(totalFeature);
			if(predictTime==60){
				Predict(testSet, trainingSet);//不用预测数据
			}else{
				ArrayList<CharacteristicData>predictDatas=new ArrayList<>();
				CharacteristicData cData=null;
				for(int i=0;i<testSet.size();++i){
				    cData=testSet.get(i).getCData();
					getPredictFeatures(cData, predictTime, dao);
					predictDatas.add(cData);
					
				}
				Predict(predictDatas,trainingSet);
				predictDatas.clear();
				predictDatas=null;
			}
			testSet.clear();
			trainingSet.clear();
		}
	}

	/**
	 * 四舍五入
	 * 
	 * @param x
	 * @return
	 * 
	 */
	public static double roundDouble(double x) {
		DecimalFormat df = new DecimalFormat("##.0000");
		return Double.parseDouble(df.format(x));
	}

}
