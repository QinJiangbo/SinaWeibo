package weibo.classifier;

import java.text.DecimalFormat;


/**
 * 	2014/4/22
 * 	@author coderwang
 *	利用最小二乘法实现的简单一元线性回归
 *	y=ax+b
 */
public class LinerRegression {
	
	private double []x;//横坐标
	private double []y;//纵坐标
	double sumx=0;
	double sumxy=0.0;
	double sumy=0;
	double sumx2=0;

	/*
	 * 构造函数
	 */
	public LinerRegression(){
	
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
		double result=0.0;
		caculateParm();//初始化参数值
		double a=(x.length*sumxy-sumy*sumx)/(x.length*sumx2-(sumx*sumx));
		double b=sumy/x.length-a*(sumx/x.length);
		result=a*predictNum+b;
		return result;
	}
	

	/**
	 * 计算相关参数
	 * @return
	 */
	public void caculateParm(){
	
		for(int i=0;i<x.length;i++){
			sumx+=x[i];
			sumy+=y[i];
			sumxy+=x[i]*y[i];
			sumx2+=x[i]*x[i];
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
		DecimalFormat df = new DecimalFormat("##.000");
		return Double.parseDouble(df.format(x));
	}

	
	
}
