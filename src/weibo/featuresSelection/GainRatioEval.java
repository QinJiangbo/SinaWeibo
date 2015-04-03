package weibo.featuresSelection;

import java.text.DecimalFormat;
import java.util.ArrayList;

import weibo.model.CDatas;
import weibo.model.CharacteristicData;

public class GainRatioEval {
	
	
	private CDatas cDatas;
	
	/**
	 * ���캯��
	 * @param cDatas
	 */
	public GainRatioEval(CDatas cDatas){
		this.cDatas=cDatas;
	}

	public double[] getGainRatio(ArrayList<CharacteristicData>dataList){	
		int classContent[]={0,1};
		double infoD=getInfoD(dataList,2, classContent);//δ���������з�ʱ��������Ϣ��
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
	 * �������ֵ
	 * @param features[]//����������ֵ
	 * @param featureIndex ����ֵ���±�
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
			for(int i=0;i<dataList.size();++i){//���ֳ�������
				if((double)dataList.get(i).getAttribute(featureIndex)>tempThreadHold){
					sub1.add(dataList.get(i));
				}else{
					sub2.add(dataList.get(i));
				}
			}
			int classContent[]={0,1};
			int totalRowNum=dataList.size();//������
			double weight1=roundDouble( (double)sub1.size()/(double)totalRowNum );//Ȩ�ؼ���
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
			//Ѱ����һ�����ܵ�threadhold
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
	 * ��ȡgainRatio��ֵ
	 * @param atype ����a������������ֵ������ɢֵ atype=0 :a=0|a=1 ;atype =1 : a<midpoint | a>midpoint
	 * @param dataList ���ݼ�
	 * @param featureIndex �����±�
	 * @return
	 */
	public double getGainRatio(double infoD,int featureIndex,ArrayList<CharacteristicData>dataList){		
		double gainRatio=0.0;
		double infoAD = 0.0;//��A��������֮����Ҫ������Ϣ���з���
		int totalRowNum=dataList.size();//������
		double features[]=cDatas.getFeatures(featureIndex);//������ֵ
		
		
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
		double weight1=roundDouble( (double)sub1.size()/(double)totalRowNum );//Ȩ�ؼ���
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
	 * ��������
	 * @param x
	 * @return
	 * 
	 */
	public double roundDouble(double x){
		DecimalFormat df = new DecimalFormat("##.000000");
		return Double.parseDouble(df.format(x));
	}
	/**
	 * ������2Ϊ�׵Ķ���ֵ
	 * @param x
	 * @return
	 */
	public double log(double x){
		return	Math.log(x)/Math.log(2.0);
	}
	
	/**
	 * ����������Ҫ����Ϣ��
	 * @param classContent �������
	 * @param m ��ĸ���
	 * @return ���������Ҫ����Ϣ��
	 */
	public double getInfoD(ArrayList<CharacteristicData>dataList,int classNum,int [] classContent){
		double infoD=0.0;
		double p[]=new double[classNum];//pΪ��Ӧ����ֵĸ���
		
		int rowNum=dataList.size();
		if(rowNum==0)
			return infoD;
		int classIndex=cDatas.getClassIndex();
		int classContentItem;//������
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
