package weibo.model;

import java.util.ArrayList;

import weibo.io.DAO;

/**
 * 特征数据集合
 * @author coderwang
 *
 */

public class CDatas implements Cloneable{
	
	private ArrayList<CharacteristicData>cDatas=null;//特征数据集
	private String[]featureNames={//特征项名字
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

	public int[]featuresToEval={//需要进行计算的特征值下标
			1,2,4,5,6,7,8,9,10,11,12,13,14
	};
	
	/**
	 * 选中某个属性
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
		//从大到小排序
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
	 * 获取需要进行特征计算的下标值
	 * @return
	 */
	public int[] getFeaturesToEval(){
		return featuresToEval;
	}
	
	/**
	 * 获取类型下标
	 * @return
	 */
	public int getClassIndex(){
		return classIndex;
	}

	
	/**
	 * 获取数据特征名字数组
	 * @return
	 */
	public String[]getFeatureName(){
		return this.featureNames;
	}
	
	/**
	 * 获取数据链表
	 * @return
	 */
	public ArrayList<CharacteristicData> getDatas(){
		return this.cDatas;
	}
	
	/**
	 * 加载数据
	 * @param dao 数据库操作对象
	 */
	public void loadDataFromDB(DAO dao,int num){
		cDatas=dao.getCDataList("cdata",num);
	}
	
	/**
	 * 打印数据内容
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
