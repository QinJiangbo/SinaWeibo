package weibo.featuresSelection;

import java.text.DecimalFormat;
import java.util.ArrayList;

import weibo.model.CDatas;
import weibo.model.CharacteristicData;

public class GainRatioEval {
	
	
	private CDatas cDatas;
	
	/**
	 * 构造函数
	 * @param cDatas
	 */
	public GainRatioEval(CDatas cDatas){
		this.cDatas=cDatas;
	}

	public double[] getGainRatio(ArrayList<CharacteristicData>dataList){	
		int classContent[]={0,1};
		double infoD=getInfoD(dataList,2, classContent);//未进行特征切分时候所需信息量
		int featuresIndex[]=cDatas.getFeaturesToEval();	
		double scores[]=new double[featuresIndex.length];
		double score=0;
		for(int index=0;index<featuresIndex.length;++index){
			int fi=featuresIndex[index];
			if(fi!=-1){
				score=getGainRatio(infoD,fi , dataList);
				scores[index]=score;
			}
		}
		return scores;
	}
	
	
	
	/**
	 * 计算出阈值
	 * @param features[]//连续特征的值
	 * @param featureIndex 特征值的下标
	 * @param dataList
	 * @return
	 */
	public double getThreadHold(int featureIndex,double features[],ArrayList<CharacteristicData>dataList){
		double threadhold=0.0;
		int threadIndex=0;
		ArrayList<CharacteristicData>sub1=new ArrayList<>();
		ArrayList<CharacteristicData>sub2=new ArrayList<>();
		double tempThreadHold=0.0;
		double min=100000;
		for(int k=2;k<features.length-1;){
			tempThreadHold =features[k];
			for(int i=0;i<dataList.size();++i){//划分成两部分
				if((double)dataList.get(i).getAttribute(featureIndex)>tempThreadHold){
					sub1.add(dataList.get(i));
				}else{
					sub2.add(dataList.get(i));
				}
			}
			int classContent[]={0,1};
			int totalRowNum=dataList.size();//总行数
			double weight1=roundDouble( (double)sub1.size()/(double)totalRowNum );//权重计算
			double weight2=roundDouble( (double)sub2.size()/(double)totalRowNum );
			double infoAD=0.0;
			infoAD=roundDouble( infoAD+weight1*getInfoD(sub1, 2, classContent) );
			infoAD=roundDouble( infoAD+weight2*getInfoD(sub2, 2, classContent) );
			if (infoAD<min){
				min=infoAD;
				threadhold=tempThreadHold;
				threadIndex=k;
			}
			sub1.clear();
			sub2.clear();
			//寻找下一个可能的threadhold
			int nextk=k+1;
			for(;nextk<features.length-1;++nextk){
				if(features[nextk]!=features[k]){
					break;
				}
			}
			k=nextk;
		}
		double temp=features[threadIndex];
		features[features.length/2]=threadhold;
		features[threadIndex]=temp;
		return threadhold;
	}
	
	/**
	 * 获取gainRatio的值
	 * @param atype 特征a的类型是连续值还是离散值 atype=0 :a=0|a=1 ;atype =1 : a<midpoint | a>midpoint
	 * @param dataList 数据集
	 * @param featureIndex 特征下标
	 * @return
	 */
	public double getGainRatio(double infoD,int featureIndex,ArrayList<CharacteristicData>dataList){		
		double gainRatio=0.0;
		double infoAD = 0.0;//在A特征分完之后还需要多少信息进行分类
		int totalRowNum=dataList.size();//总行数
		double features[]=cDatas.getFeatures(featureIndex);//特征的值
		
		
		int type=0;
		if(features[0]==1.0){
			type=0;
		}else {
			type=1;
		}	
		ArrayList<CharacteristicData>sub1=new ArrayList<>();
		ArrayList<CharacteristicData>sub2=new ArrayList<>();
		if(type==1){
			double threadHold=getThreadHold(featureIndex, features, dataList);
			//>threadHold <threadHold
			for(int i=0;i<dataList.size();++i){
				if((double)dataList.get(i).getAttribute(featureIndex)>threadHold){
					sub1.add(dataList.get(i));
				}else{
					sub2.add(dataList.get(i));
				}
			}
		}else{
			//==0 ==1
			for(int i=0;i<dataList.size();++i){
				if((int)dataList.get(i).getAttribute(featureIndex)==1){
					sub1.add(dataList.get(i));
				}else{
					sub2.add(dataList.get(i));
				}
			}		
		}
		int classContent[]={0,1};
		double weight1=roundDouble( (double)sub1.size()/(double)totalRowNum );//权重计算
		double weight2=roundDouble( (double)sub2.size()/(double)totalRowNum );
		infoAD=roundDouble( infoAD+weight1*getInfoD(sub1, 2, classContent) );
		infoAD=roundDouble( infoAD+weight2*getInfoD(sub2, 2, classContent) );
		double splitinfoa=0.0;
		if(weight1>0){
			splitinfoa+=weight1*log(weight1);
		}
		if(weight2>0){
			splitinfoa+=weight2*log(weight2);
		}
		splitinfoa=roundDouble(splitinfoa);
		if(splitinfoa!=0.0)
			gainRatio= roundDouble((infoD-infoAD)/(-splitinfoa));
		return gainRatio;
	}
	
	/**
	 * 四舍五入
	 * @param x
	 * @return
	 * 
	 */
	public double roundDouble(double x){
		DecimalFormat df = new DecimalFormat("##.000000");
		return Double.parseDouble(df.format(x));
	}
	/**
	 * 计算以2为底的对数值
	 * @param x
	 * @return
	 */
	public double log(double x){
		return	Math.log(x)/Math.log(2.0);
	}
	
	/**
	 * 计算总体需要的信息量
	 * @param classContent 类的内容
	 * @param m 类的个数
	 * @return 总体分类需要的信息量
	 */
	public double getInfoD(ArrayList<CharacteristicData>dataList,int classNum,int [] classContent){
		double infoD=0.0;
		double p[]=new double[classNum];//p为对应类出现的概率
		
		int rowNum=dataList.size();
		if(rowNum==0)
			return infoD;
		int classIndex=cDatas.getClassIndex();
		int classContentItem;//类特征
		int countForContentItem=0;
		for(int i=0;i<classNum;i++){
			classContentItem=classContent[i];
			countForContentItem=0;
			for(int j=0;j<rowNum;j++){
				if((int)dataList.get(j).getAttribute(classIndex)==classContentItem)
					countForContentItem++;	
			}
			p[i]=roundDouble( (double)countForContentItem/(double)rowNum );
		}
		double temp=0.0;
		for(int i=0;i<classNum;++i){
			if(p[i]>0)
				temp=p[i]*log(p[i]);
			infoD+=temp;
		}
		infoD=0-infoD;
		infoD=roundDouble(infoD);
		return infoD;
	}

}
