package weibo.classifier;

import java.text.DecimalFormat;

/**
 * 回归分析
 * @author coderwang
 *
 */

public class Regression {
	
	private double []x;//横坐标
	private double []y;//纵坐标
	
	private double []x1;//横坐标倒数
	private double []y1;//纵坐标倒数
	private double []y2;
	double sumx=0;
	double sumxy=0.0;
	double sumy=0;
	double sumx2=0;
	double sumy2=0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Math.sqrt(9));
	}

	/**
	 * 初始化倒数值
	 */
	public void initData(){
		x1=new double[x.length];
		y1=new double[y.length];
		y2=new double[y.length];
		for(int i=0;i<x.length;++i){
			if(x[i]>0)
				x1[i]=roundDouble(1/x[i]);
			if(y[i]>0)
				y1[i]=roundDouble(1/y[i]);
			y2[i]=Math.pow(y[i], 2);
		}
		
	}
	
	
	
	/**
	 * 预测算法
	 * @param x //横轴参数
	 * @param y //纵轴参数
	 * @return //返回预测结果
	 */
	double predict(double[]x,double[]y,double predictNum){
		this.x=x;
		this.y=y;
		initData();
		double result=0.0;
		caculateParm(x,y);//初始化参数值 非倒数
		double a1=(x.length*sumxy-sumy*sumx)/(x.length*sumx2-(sumx*sumx));
		@SuppressWarnings("unused")
		double b1=sumy/x.length-a1*(sumx/x.length);
		double r1=caculateR();	
		caculateParm(x1,y1);
		double r2=caculateR();
		
		System.out.println(r1+" "+r2);		
		
		
		//result=a*predictNum+b;
		return result;
	}
	
/**
 * 计算相关系数
 * @return
 */
	public double caculateR(){
		double r=0;
		int n=x.length;
		System.out.println(Math.sqrt(n*sumx2-sumx*sumx));
		
		r=(n*sumxy-sumx*sumy)/
		(
				roundDouble((Math.sqrt(n*sumx2-sumx*sumx)))
				*
				roundDouble((Math.sqrt(n*sumy2-sumy*sumy)))
		);
		System.out.println(r+"r");
		r=roundDouble(r);
		return r;
	}
	/**
	 * 计算相关参数
	 * @return
	 */
	public void caculateParm(double[]x,double[]y){
	
		for(int i=0;i<x.length;i++){
			sumx+=x[i];
			sumy+=y[i];
			sumxy+=x[i]*y[i];
			sumx2+=x[i]*x[i];
			sumy2+=roundDouble(Math.pow(y[i], 2));
		}
		sumx=roundDouble(sumx);
		sumy=roundDouble(sumy);
		sumxy=roundDouble(sumx2);
		sumx2=roundDouble(sumx2);
		sumy2=roundDouble(sumy2);
		
		System.out.println("sumx:　"+sumx +" sumy: "+sumy+ " sumxy :" +sumxy
				+" sumy2: "+sumy2+ " sumx2 :"+sumx2);
		
	}
	
	/**
	 * 四舍五入
	 * 
	 * @param x
	 * @return
	 * 
	 */
	public static double roundDouble(double x) {
		DecimalFormat df = new DecimalFormat("##.000");
		return Double.parseDouble(df.format(x));
	}
}
