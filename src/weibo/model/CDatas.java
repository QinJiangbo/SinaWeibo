package weibo.model;

import java.util.ArrayList;

import weibo.io.DAO;

/**
 * �������ݼ���
 * @author coderwang
 *
 */

public class CDatas implements Cloneable{
	
	private ArrayList<CharacteristicData>cDatas=null;//�������ݼ�
	private String[]featureNames={//����������
			"",//0
			"hasPic",//1                          
			"contentLength",//2
			"createTime",//3
			"repostCount",//4 
			"verified",//5
			"level",//6
			"followerCount",//7	
			"friendsCount",//8
			"statusesCount",//9
			"depth",//10
			"width",//11
			"followersCount",//12
			"linkDensity",//13
			"vUserCount",//14
			"finalRepostCount",//15
			"popular"//16
	};
	public ArrayList<CharacteristicData> getcDatas() {
		return cDatas;
	}


	public void setcDatas(ArrayList<CharacteristicData> cDatas) {
		this.cDatas = cDatas;
	}


	public void setFeaturesToEval(int[] featuresToEval) {
		this.featuresToEval = featuresToEval;
	}


	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}


	public String[] getFeatureNames() {
		return featureNames;
	}


	public void setFeatureNames(String[] featureNames) {
		this.featureNames = featureNames;
	}

	public int[]featuresToEval={//��Ҫ���м��������ֵ�±�
			1,2,4,5,6,7,8,9,10,11,12,13,14
	};
	
	/**
	 * ѡ��ĳ������
	 * @param index
	 */
	public void setFeaturesToEval(int index){
		for(int i=0;i<featuresToEval.length;++i){
			if(featuresToEval[i]==index){
				featuresToEval[i]=-1;
				return ;
			}
		}
	}
	
	private int classIndex=16;
	
	public double[] getFeatures(int featureIndex){
		int size=cDatas.size();
		double features [] =new double[size];
		for(int i=0;i<size;++i){
			features[i]=cDatas.get(i).getAttribute(featureIndex);
		} 
		//�Ӵ�С����
		double temp=0.0;
		for(int i=0;i<size;i++){
			for(int j=i+1;j<size;j++){
				if(features[j]>features[i]){
					temp=features[i];
					features[i]=features[j];
					features[j]=temp;
				}
			}
		}
		return features;
	}
	
	
	public CDatas(){
		
	}
	
	/**
	 * ��ȡ��Ҫ��������������±�ֵ
	 * @return
	 */
	public int[] getFeaturesToEval(){
		return featuresToEval;
	}
	
	/**
	 * ��ȡ�����±�
	 * @return
	 */
	public int getClassIndex(){
		return classIndex;
	}

	
	/**
	 * ��ȡ����������������
	 * @return
	 */
	public String[]getFeatureName(){
		return this.featureNames;
	}
	
	/**
	 * ��ȡ��������
	 * @return
	 */
	public ArrayList<CharacteristicData> getDatas(){
		return this.cDatas;
	}
	
	/**
	 * ��������
	 * @param dao ���ݿ��������
	 */
	public void loadDataFromDB(DAO dao,int num){
		cDatas=dao.getCDataList("cdata",num);
	}
	
	/**
	 * ��ӡ��������
	 */
	public void printCDatas(){
		for(CharacteristicData cData:cDatas){
			System.out.println(cData.toString());
		}
	}
	
	
	public Object clone() {
		Object o = null;
		try {
			o = (CDatas) super.clone();
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	public CDatas getCDatas(){
		CDatas cDatas=new CDatas();
		cDatas.setClassIndex(this.getClassIndex());
		cDatas.setFeatureNames(this.getFeatureName());
		int featureToEval2[]=new int[this.getFeaturesToEval().length];
		for(int i=0;i<featureToEval2.length;++i){
			featureToEval2[i]=this.getFeaturesToEval()[i];
		}
		cDatas.setFeaturesToEval(featureToEval2);
		return cDatas;
	}

}
