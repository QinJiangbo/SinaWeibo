package weibo.classifier;

import java.util.ArrayList;
import weibo.featuresSelection.GainRatioEval;
import weibo.model.CDatas;
import weibo.model.CharacteristicData;

/**
 * 决策树模型
 * @author coderwang
 * 2014/4/13
 *
 */
public class DecisionTree {
	
	private Node root=null;//树根
	public DecisionTree(){
	}
	
	/**
	 * 预测微博是否流行
	 * @param cData
	 * @return
	 */
	public int predict(CharacteristicData cData){
		int popular=-1;
		Node node=root;
		double nodeValue=0.0;
		String description="";
		while(node.getPopular()==-1){
			description=node.getDescription();
			nodeValue=cData.getAttribute(node.feature);
			if(description.equals("0=1")){
				if(nodeValue==0){
					node=node.leftNode;
				}else{
					node=node.rightNode;
				}
				
			}else if(description.equals("<>")){
				if(nodeValue<=node.getValue()){
					node=node.leftNode;
				}else{
					node=node.rightNode;
				}
			}
			if(node==null){
				break;
			}
		}
		if(node!=null)
			popular=node.getPopular();
		return popular;
	}

	/**
	 * 获取子集中最佳分裂特征
	 * @param gainRatioEval
	 * @param cDatas
	 * @return
	 */
	public Node getBestNode(GainRatioEval gainRatioEval,CDatas cDatas){
		double[] scores=gainRatioEval.getGainRatio(cDatas.getDatas());
		int featuresIndex[]=cDatas.getFeaturesToEval();	
		double temp=0;
		int f=0;
		for(int i=0;i<scores.length;i++){
			for(int j=i+1;j<scores.length;j++){
				if(scores[j]>=scores[i]){//>=和>对结果的影响很大啊
					temp=scores[j];
					scores[j]=scores[i];
					scores[i]=temp;		
					f=featuresIndex[j];
					featuresIndex[j]=featuresIndex[i];
					featuresIndex[i]=f;
					
				}
			}
		}
		if(scores.length!=0&&featuresIndex[0]>-1){
			//System.out.println(cDatas.getFeatureName()[featuresIndex[0]]+" : "+scores[0]);
		}
		Node node=new Node();
		if(featuresIndex[0]!=-1)
		node.setFeature(cDatas.getFeatureName()[featuresIndex[0]]);
		node.setFeatureIndex(featuresIndex[0]);
		//node.setValue(scores[0]); 需要后续添加
		return node;
	}
	
	public boolean isFeatureListEmpty(CDatas cDatas){
		
		boolean flag=true;
		for(int i=0;i<cDatas.getFeaturesToEval().length;++i){
			if(cDatas.getFeaturesToEval()[i]!=-1){
				flag=false;
				break;
			}
		}
		return flag;
		
	}
	
	/**
	 * 决策树构造迭代启动代码
	 * 
	 * @param cDatas
	 * @param gainRatioEval
	 */
	public void GenerateRoot(CDatas cDatas,GainRatioEval gainRatioEval){
		root= ConstructTree(cDatas,gainRatioEval);
	}
	
	/**
	 * 构造决策树
	 * @param cDatas
	 * @param gainRatioEval
	 * @return
	 */
	public Node ConstructTree(CDatas cDatas,GainRatioEval gainRatioEval){
		Node node=new Node();
		if(isAllTheSameClass(cDatas)){//子集中只有一个类的时候
			node.setPopular(getMainClass(cDatas));
			return node;
		}else if(isFeatureListEmpty(cDatas)){
			node.setPopular(getMainClass(cDatas));
			return  node;
		}else{		
			node=getBestNode(gainRatioEval, cDatas);
			if(node.getFeature()==null){//未获取最佳分裂点
				node.setPopular(getMainClass(cDatas));
				return node;
			}
	
			cDatas.setFeaturesToEval(node.featureIndex);//选中该特征
			double features[]=cDatas.getFeatures(node.featureIndex);//特征的值
			
			int type=0;
			if(features[0]==1.0){
				type=0;
			}else {
				type=1;
			}	//特征的值类型		
			int []featureContent={0,1};
			if(type==0){//离散型
				node.setDescription("0=1");
				node.setValue(0.0);
				CDatas cDatas0=new CDatas();
				cDatas0.featuresToEval=cDatas.featuresToEval;
				cDatas0.setFeatureNames(cDatas.getFeatureName());
				cDatas0.setClassIndex(cDatas.getClassIndex());
				ArrayList<CharacteristicData>datas=new ArrayList<>();
				for(int i=0;i<featureContent.length;++i){
					for(int j=0;j<cDatas.getcDatas().size();++j){
						if(cDatas.getcDatas().get(j).getAttribute(node.featureIndex)==featureContent[i]){
							datas.add(cDatas.getcDatas().get(j));
						}
					}
					cDatas0.setcDatas(datas);
					if(datas.size()==0){
						Node tempNode=new Node();
						tempNode.setPopular(getMainClass(cDatas));
						if(i==0){
							node.leftNode=tempNode;
						}else{
							node.rightNode=tempNode;
						}
					}else{
						if(i==0){
							node.leftNode=ConstructTree(cDatas0, gainRatioEval);
						}else{
							node.rightNode=ConstructTree(cDatas0, gainRatioEval);
						}
					}
				}
					
			}else{//连续型
				double mid=features[features.length/2];//之前已经调整过
				node.setDescription("<>");
				node.setValue(mid);
				
				CDatas cDatas0=new CDatas();//<mid
				cDatas0.featuresToEval=cDatas.featuresToEval;
				cDatas0.setFeatureNames(cDatas.getFeatureName());
				cDatas0.setClassIndex(cDatas.getClassIndex());
				CDatas cDatas1=new CDatas();//>mid
				cDatas1.featuresToEval=cDatas.featuresToEval;
				cDatas1.setFeatureNames(cDatas.getFeatureName());
				cDatas1.setClassIndex(cDatas.getClassIndex());
				ArrayList<CharacteristicData>datas0=new ArrayList<>();
				ArrayList<CharacteristicData>datas1=new ArrayList<>();
				for(int j=0;j<cDatas.getcDatas().size();++j){
					if(cDatas.getcDatas().get(j).getAttribute(node.featureIndex)<=mid){
						datas0.add(cDatas.getcDatas().get(j));
					}else{
						datas1.add(cDatas.getcDatas().get(j));
					}
				}
				cDatas0.setcDatas(datas0);
				cDatas1.setcDatas(datas1);
				if(datas0.size()==0){
					Node tempNode=new Node();
					tempNode.setPopular(getMainClass(cDatas));
					node.leftNode=tempNode;
				}else if(datas1.size()==0){
					Node tempNode=new Node();
					tempNode.setPopular(getMainClass(cDatas));
					node.rightNode=tempNode;
				}else{
					node.leftNode=ConstructTree(cDatas0, gainRatioEval);
					node.rightNode=ConstructTree(cDatas1, gainRatioEval);
				}	
			}
			return node;
		}
	}
	
	/**
	 * 获取子集中的主要类别
	 * @param cDatas
	 * @return
	 */
	public int getMainClass(CDatas cDatas){
		int popular=0;
		int count=cDatas.getDatas().size();
		int countOfZero=0;
		for(int i=0;i<count;++i){
			if(cDatas.getDatas().get(i).getPopular()==0){
				countOfZero++;
			}
		}
		if(countOfZero>count/2){
			popular=0;			
		}else{
			popular=1;
		}				
		return popular;
	}
	
	/**
	 * 判断子集中是不是只有一个类别
	 * @param cDatas
	 * @return
	 */
	public boolean isAllTheSameClass(CDatas cDatas){
		boolean flag=false;//子集中的类是不是都是一个
		int count=cDatas.getDatas().size();
		int countOfZero=0;
		for(int i=0;i<count;++i){
			if(cDatas.getDatas().get(i).getPopular()==0){
				countOfZero++;
			}
		}
		if(countOfZero==count||countOfZero==0){
			flag=true;
		}				
		return flag;
	}
	
	
	
	

	
	/**
	 * 层次遍历
	 */
	public void bfs(){
		bfs(root);
	}
	
	public void bfs(Node node){
		ArrayList<Node>nlist=new ArrayList<>();
		ArrayList<Node>nSubList=new ArrayList<>();
		nlist.add(node);
		int index=0;
		System.out.println(node.print());
		while(true){
			node=nlist.get(index);
			index++;
			if(node!=null){
				if(node.leftNode!=null){
					System.out.println(" /");
					System.out.println("/");
					System.out.println(node.leftNode.print());
					nSubList.add(node.leftNode);
				}
				if(node.rightNode!=null){
					System.out.println(" \\");
					System.out.println("  \\");
					System.out.println(node.rightNode.print());
					nSubList.add(node.rightNode);
				}
			}
			if(index>=nlist.size()){
				if(nSubList.size()>0){
					nlist.clear();
					nlist.addAll(nSubList);
					nSubList.clear();
					index=0;
					System.out.println("############################");
				}else{
					break;
				}
			}
		}
	}
	

	/**
	 * 深度遍历
	 */
	public void dfs(){
		dfs(root);
	}
	
	public void dfs(Node node){
		if(node!=null){
			System.out.println(node.feature+"  "+node.description+ "  "+ node.value+"  "+node.getPopular());
		}
		if(node.leftNode!=null)
			dfs(node.getLeftNode());
		if(node.rightNode!=null)
			dfs(node.getRightNode());
	}






/**
 * 数的节点类
 * @author coderwang
 *
 */
	class Node{
		
		private String feature;
		private int featureIndex;
		private double value;
		private String description="leaf";
		private int popular=-1;//初始值为-1 ，0,1 分别表示流行与否
		private Node leftNode=null; 
		private Node rightNode=null;
		
		public String print(){
			return feature+"  "+description+ "  "+ value+"  "+getPopular();
		}
		public String getFeature() {
			return feature;
		}
		public void setFeature(String feature) {
			this.feature = feature;
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		public Node getLeftNode() {
			return leftNode;
		}
		public void setLeftNode(Node leftNode) {
			this.leftNode = leftNode;
		}
		public Node getRightNode() {
			return rightNode;
		}
		public void setRightNode(Node rightNode) {
			this.rightNode = rightNode;
		}
		public int getFeatureIndex() {
			return featureIndex;
		}
		public void setFeatureIndex(int featureIndex) {
			this.featureIndex = featureIndex;
		}
		@Override
		public String toString() {
			return "Node [feature=" + feature + ", featureIndex="
					+ featureIndex + ", value=" + value + ", popular="
					+ popular + ", leftNode=" + leftNode + ", rightNode="
					+ rightNode + "]";
		}
		public int getPopular() {
			return popular;
		}
		public void setPopular(int popular) {
			this.popular = popular;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}

	}

}
