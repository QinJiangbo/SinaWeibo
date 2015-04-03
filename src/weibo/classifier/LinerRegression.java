package weibo.classifier;

import java.text.DecimalFormat;


/**
 * 	2014/4/22
 * 	@author coderwang
 *	������С���˷�ʵ�ֵļ�һԪ���Իع�
 *	y=ax+b
 */
public class LinerRegression {
	
	private double []x;//������
	private double []y;//������
	double sumx=0;
	double sumxy=0.0;
	double sumy=0;
	double sumx2=0;

	/*
	 * ���캯��
	 */
	public LinerRegression(){
	
	}
	/**
	 * Ԥ���㷨
	 * @param x //�������
	 * @param y //�������
	 * @return //����Ԥ����
	 */
	double predict(double[]x,double[]y,double predictNum){
		this.x=x;
		this.y=y;
		double result=0.0;
		caculateParm();//��ʼ������ֵ
		double a=(x.length*sumxy-sumy*sumx)/(x.length*sumx2-(sumx*sumx));
		double b=sumy/x.length-a*(sumx/x.length);
		result=a*predictNum+b;
		return result;
	}
	

	/**
	 * ������ز���
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
	 * ��������
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
