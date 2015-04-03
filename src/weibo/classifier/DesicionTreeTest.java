package weibo.classifier;

/**
 * @author coderwang
 * 2014/4/14
 * ��������ģ����� ����һ��Сʱ�����ݽ��еĲ���
 */
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import weibo.featuresSelection.GainRatioEval;
import weibo.io.DAO;
import weibo.model.CDatas;
import weibo.model.CharacteristicData;
import weibo.model.MeasureArguments;

public class DesicionTreeTest {

	
	/**
	 * @param args
	 */
	
	static DAO dao=new DAO();
	static CDatas cDatas=new CDatas();
	static ArrayList<MeasureArguments>measureArgumentsList;
	static ArrayList<MeasureArguments>statisticArg=new ArrayList<>();//����ȫ��ͳ��

	public static void init(int numOfData){
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");
		cDatas.loadDataFromDB(dao,numOfData);
		
	}
	
	public static void main(String[] args) {		
		int numOfData=450;
		init(numOfData);
		int k_fold=5;
		int groupSize=numOfData/k_fold;
		int testTime=1;//���Դ���
		startTest(groupSize,testTime);
		
	}
	
	/**
	 * ��ʼ����
	 * @param testTime���Ե�����
	 * @param groupSize ÿ�����ݴ�С
	 */
	public static void startTest(int groupSize,int testTime){
		for(int i=0;i<testTime;i++){
			CompareToBase(groupSize);
			statisticArg.addAll(measureArgumentsList);
		}	
		System.out.println(statisticArg.size());//�ܵĲ��Խ����
		String tips[]={"base","total","selected"};
		MeasureArguments[] mArguments=new MeasureArguments[3];
		for(int i=0;i<mArguments.length;++i){
			mArguments[i]=new MeasureArguments();
		}
		MeasureArguments measureArguments=null;
		for(int i=0;i<statisticArg.size();++i){
			//System.out.println(measureArgumentsList.get(i).getString()+tips[i%3]);
			measureArguments=statisticArg.get(i);
			mArguments[i%3].setAccuracy(measureArguments.getAccuracy()+mArguments[i%3].getAccuracy());
			mArguments[i%3].setErrorRate(measureArguments.getErrorRate()+mArguments[i%3].getErrorRate());
			mArguments[i%3].setRecall(measureArguments.getRecall()+mArguments[i%3].getRecall());
			mArguments[i%3].setSpecifity(measureArguments.getSpecifity()+mArguments[i%3].getSpecifity());
			mArguments[i%3].setPrecision(measureArguments.getPrecision()+mArguments[i%3].getPrecision());
			mArguments[i%3].setF1(measureArguments.getF1()+mArguments[i%3].getF1());
		}
		System.out.println("\n\n############�ۺϲ��Խ��##################");
		System.out.println(	"accuracy       " +
						   	"errorRate        " +
							"recall        " +
							"specifity        " +
							"precision         " +
							"f1\t           ");
		int size=statisticArg.size()/3;
		for(int i=0;i<mArguments.length;++i){
			String result=roundDouble(mArguments[i].getAccuracy()/size)
					+"\t\t"+roundDouble(mArguments[i].getErrorRate()/size)
					+"\t\t"+roundDouble(mArguments[i].getRecall()/size)
					+"\t\t"+roundDouble(mArguments[i].getSpecifity()/size)
					+"\t\t"+roundDouble(mArguments[i].getPrecision()/size)
					+"\t\t"+roundDouble(mArguments[i].getF1()/size)+"\t"+tips[i];
			System.out.println(result);
		}
	}
	
	/**
	 * ���ײ��Ե���ؽ������
	 */
	public static void CompareToBase(int groupSize){
		measureArgumentsList=new ArrayList<>();
		k_fold_cross_validation(groupSize);	
		System.out.println("\n\n############���β��Խ��##################");
		System.out.println(	"accuracy       " +
						   	"errorRate        " +
							"recall        " +
							"specifity        " +
							"precision         " +
							"f1\t           ");
		String tips[]={"base","total","selected"};
		for(int i=0;i<measureArgumentsList.size();++i){
			System.out.println(measureArgumentsList.get(i).getString()+tips[i%3]);
		}

	}
	
	//public static void evaluate
	
	/**
	 * ������������������Ԥ��
	 * @param testSet ���Լ���
	 * @param traingingSet Ԥ�⼯��
	 */
	public static void Predict(ArrayList<CharacteristicData>testSet,ArrayList<CharacteristicData>traingingSet){	
		CDatas trainingCDatas = cDatas.getCDatas();
		CDatas testingDatas=cDatas.getCDatas();
		testingDatas.setcDatas(testSet);
		trainingCDatas.setcDatas(traingingSet);
		
		
		/*System.out.println("�������ݣ� " + testingDatas.getcDatas().size()
				+ "  ѵ������ :" + trainingCDatas.getDatas().size());*/
		
		GainRatioEval gainRatioEval = new GainRatioEval(trainingCDatas);
		DecisionTree dTree = new DecisionTree();
		dTree.GenerateRoot(trainingCDatas, gainRatioEval);
		//dTree.bfs();
		for (CharacteristicData cData : testingDatas.getcDatas()) {
			cData.setPredictResult(dTree.predict(cData));
		}
		MeasureArguments measureArguments=new MeasureArguments();
		measureArguments.loadBasicArguments(testingDatas);
		measureArguments.caculateMeasures();
		measureArgumentsList.add(measureArguments);
	}
	
	
	/**
	 *�趨group��С����k�ֲ���
	 * @param groupSize
	 */
	
	public static void k_fold_cross_validation(int groupSize) {
		System.out.println("################## k-fold Cross-Validation #############");
		int groupNum = cDatas.getDatas().size() / groupSize;
		ArrayList<ArrayList<CharacteristicData>> datas = new ArrayList<ArrayList<CharacteristicData>>();
		for (int i = 0; i < groupNum; ++i) {
			ArrayList<CharacteristicData> datas0  = new ArrayList<>();
			datas.add(datas0);
		}
		int index;
		Random random = new Random();
		for (int i = 0; i < cDatas.getcDatas().size(); ++i) {
			index = random.nextInt(groupNum);
			datas.get(index).add(cDatas.getcDatas().get(i));
		}
		ArrayList<CharacteristicData> trainingSet=new ArrayList<>();
		ArrayList<CharacteristicData> testSet =new ArrayList<>();		
		for(int k=0;k<groupNum;++k) {
			System.out.println("�� " + (k+1) + " �ֲ��Կ�ʼ");
			for (int i = 0; i < datas.size(); ++i) {
				if (i == k) {
					testSet.addAll(datas.get(i));
				} else {
					trainingSet.addAll(datas.get(i));
				}
			}
			int totalFeature[]=cDatas.getFeaturesToEval();
			int baseFeature[]={  1 ,2, 4, 5, 6, 7, 8, 9 };
			int selectedFeature[]={  4 ,5,10 ,11, 12, 13, 14 };
			int[][] features={
				baseFeature,totalFeature,selectedFeature
			};
			for(int i=0;i<features.length;++i){
				cDatas.setFeaturesToEval(features[i]);
				Predict(testSet,trainingSet);
			}
			
			testSet.clear();
			trainingSet.clear();
		}
	}
	
	/**
	 * ��������
	 * @param x
	 * @return
	 * 
	 */
	public static double roundDouble(double x){
		DecimalFormat df = new DecimalFormat("##.0000");
		return Double.parseDouble(df.format(x));
	}

}
