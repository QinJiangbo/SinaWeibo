package weibo.model;

import java.math.BigDecimal;

/**
 * 
 * 模型性能评价指标
 * @author coderwang
 *
 */
public class MeasureArguments {
	
	private int tp;//正的类型被正确划分的个数   1->1
	private int tn;//负的类型被正确划分的个数   0->0
	private int fp;//负的被错误划分的个数		 0->1
	private int fn;//正的被错误划分的个数       1->0
	private int p;//正的类型个数 1 num
	private int n;//负的类型个数 0 num
	private int noPredict;
	private final int base=100;
	
	
	private double accuracy=0.0;// or means recognition rate
	private double errorRate=0.0;// misclassification rate
	private double recall=0.0;    //召回率 or name as sensitivity, true positive rate 
	private double specifity=0.0; // true negative rate
	private double precision=0.0 ;
	private double f1=0.0;//or name as f,f-score 
	private double fb=0.0;
	
	
	public String getString(){
		String result=(accuracy+"\t\t"+errorRate+"\t\t"+recall+"\t\t"+specifity+"\t\t"+precision+"\t\t"+f1+"\t\t");
		return result;
	}
	
	/**
	 * 加载基本度量数据
	 * @param testingCDatas
	 */
	public void loadBasicArguments(CDatas testingCDatas){
		for(CharacteristicData data:testingCDatas.getcDatas()){
			if(data.getPopular()==1){
				p++;
				if(data.getPredictResult()==1){
					tp++;
				}else if(data.getPredictResult()==0){
					fp++;
				}else{
					noPredict++;
					p--;
				}	
			}else{
				n++;
				if(data.getPredictResult()==0.0){
					tn++;
				}else if(data.getPredictResult()==1){
					fp++;
				}else {//未有分类
					noPredict++;
					n--;
				}
				
			}
		}
	}
	
	/**
	 * 计算相关评价指数
	 */
	public void caculateMeasures(){
		caculateAccuracy();
		caculateErrorRate();
		caculateRecall();
		caculateRecall();
		caculateSpecifity();
		caculatePrecision();
		caculateF1();
	}
	
	public void caculateFb(double b){
		if(b*b*precision+recall==0)
			fb=0;
		else
		fb=roundDouble(base*((1+b*b)*precision*recall)/(b*b*precision+recall));
	}
	
	
	public void caculateF1(){
		if(precision+recall>0)
			f1=roundDouble((2*precision*recall)/(precision+recall));
	}
	
	
	
	
	public void caculatePrecision(){
		if(tp+fp>0)
			precision=roundDouble(base*(double)tp/(double)(tp+fp));
	}
	
	/**
	 * 计算精确度
	 */
	public void caculateAccuracy(){
		if(p+n>0)
		accuracy=roundDouble((double)(tp+tn)/(double)(p+n));
		accuracy*=roundDouble(base);
		accuracy=roundDouble(accuracy);
	}
	
	/**
	 * 计算出错率
	 */
	public void caculateErrorRate(){
		if(p+n>0)
			errorRate=roundDouble(base*(double)(fp+fn)/(double)(p+n));
	}
	
	/**
	 * 计算召回率
	 */
	public void caculateRecall(){
		if(p==0)
			recall=0;
		else
		recall=roundDouble(base*(double)tp/(double)p);
	}
	
	/**
	 * negative 被正确预测的比例
	 */
	
	public void caculateSpecifity(){
		if(n>0)
			specifity=roundDouble(base*(double)tn/(double)n);
		else {
			specifity=0;
		}
	}
	
	
	
	
	
	/**
	 * 四舍五入
	 * @param x
	 * @return x的四舍五入
	 * 
	 */
	public double roundDouble(double x){
		BigDecimal   b   =   new   BigDecimal(x);  
		return b.setScale(4,BigDecimal.ROUND_HALF_UP)
				.doubleValue();  
	}
	
	
	public MeasureArguments(){
		tp=0;
		tn=0;
		fp=0;
		fn=0;
		noPredict=0;
	}
	
	
	
	public int getTp() {
		return tp;
	}


	public void setTp(int tp) {
		this.tp = tp;
	}


	public int getTn() {
		return tn;
	}


	public void setTn(int tn) {
		this.tn = tn;
	}

	public int getFp() {
		return fp;
	}

	public void setFp(int fp) {
		this.fp = fp;
	}

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getErrorRate() {
		return errorRate;
	}

	public void setErrorRate(double errorRate) {
		this.errorRate = errorRate;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public double getSpecifity() {
		return specifity;
	}

	public void setSpecifity(double specifity) {
		this.specifity = specifity;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getF1() {
		return f1;
	}

	public void setF1(double f1) {
		this.f1 = f1;
	}

	public double getFb() {
		return fb;
	}

	public void setFb(double fb) {
		this.fb = fb;
	}

	public int getNoPredict() {
		return noPredict;
	}

	public void setNoPredict(int noPredict) {
		this.noPredict = noPredict;
	}
	
	

}
