package weibo.featuresSelection;
import weibo.io.DAO;
import weibo.model.CDatas;

/**
 *  Ù–‘—°‘ÒÀ„∑®
 * @author coderwang
 *
 */
public class FeaturesSelectionTest {
	public static void main(String []args){
		DAO dao=new DAO();
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");
		CDatas cDatas=new CDatas();
		int numOfData=100000;
		cDatas.loadDataFromDB(dao,numOfData);
		//cDatas.printCDatas();
		GainRatioEval gainRatioEval=new GainRatioEval(cDatas);
		double[] scores=gainRatioEval.getGainRatio(cDatas.getDatas());
		int featuresIndex[]=cDatas.getFeaturesToEval();	
		double temp=0;
		int f=0;
		for(int i=0;i<scores.length;i++){
			for(int j=i+1;j<scores.length;j++){
				if(scores[j]>scores[i]){
					temp=scores[j];
					scores[j]=scores[i];
					scores[i]=temp;		
					f=featuresIndex[j];
					featuresIndex[j]=featuresIndex[i];
					featuresIndex[i]=f;
					
				}
			}
		}
		for(int i=0;i<scores.length;i++){
			System.out.println(cDatas.getFeatureName()[featuresIndex[i]]+" : "+scores[i]);
		}
		
	}
}
