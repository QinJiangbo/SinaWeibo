package weibo.classifier;

import java.text.DecimalFormat;

/**
 * �ع����
 * @author coderwang
 *
 */

public class Regression {
	
	private double []x;//������
	private double []y;//������
	
	private double []x1;//�����굹��
	private double []y1;//�����굹��
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
	 * ��ʼ������ֵ
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
	 * Ԥ���㷨
	 * @param x //�������
	 * @param y //�������
	 * @return //����Ԥ����
	 */
	double predict(double[]x,double[]y,double predictNum){
		this.x=x;
		this.y=y;
		initData();
		double result=0.0;
		caculateParm(x,y);//��ʼ������ֵ �ǵ���
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
 * �������ϵ��
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
	 * ������ز���
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
		
		System.out.println("sumx:��"+sumx +" sumy: "+sumy+ " sumxy :" +sumxy
				+" sumy2: "+sumy2+ " sumx2 :"+sumx2);
		
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
