package weibo.model;


import java.util.ArrayList;



/**
 * ת��ͼ���ݽṹ
 * ���ø�ָ�����ӽڵ�
 * @author coderwang
 * 2014/4/7
 */
public class Graph {
	
	private ArrayList<GraphNode> nodes;
	private int []widths;
	private final int maxWidth=100;
	private String rootMid=null;//���ڵ�
	
	private int []repostCountSeries;
	private int []vUserCountSeries;
	private long []followerCountSeries;
	private int []widthSeries;
	private int []depthSeries;
	
	/**
	 * ��ʼ��һ��΢������ʱ����������
	 * @param timeSpan ʱ��� �Է���Ϊ��λ
	 */
	public void initSeries(int timeSpan){
		int groupNum=(60/timeSpan)+1;//����
		repostCountSeries=new int[groupNum];
		vUserCountSeries=new int[groupNum];
		followerCountSeries=new long[groupNum];
		widthSeries=new int [groupNum];
		depthSeries=new int[groupNum];
	}
	
	/**
	 * ����һ��΢������ʱ����ڵ�����
	 * @param timeSpan
	 */
	public void setSeries(int timeSpan,long startTime){
		long time=timeSpan*60*1000;//ʱ�����ĺ�����
		int index=0;
		for(int i=0;i<nodes.size();++i){
			index=(int) ((nodes.get(i).getCreateTime()-startTime)/time);
			repostCountSeries[index]++;
			vUserCountSeries[index]+=nodes.get(i).getVerified();	
			followerCountSeries[index]+=nodes.get(i).getFollowersCount();
		}
		//��ȡ��Ⱥͳ��ȣ��Ƚ��鷳************************************************//
		int groupNum=60/timeSpan +1;
		ArrayList<GraphNode>tempNodes=null;
		for(int i=0;i<groupNum;++i){
			tempNodes=new ArrayList<>();
			for(int j=0;j<nodes.size();++j){
				if(nodes.get(j).getCreateTime()<=startTime+time*(i+1)){
					GraphNode node=new GraphNode();
					node.setCreateTime(nodes.get(j).getCreateTime());
					node.setFollowersCount(nodes.get(j).getFollowersCount());
					node.setMid(nodes.get(j).getMid());
					node.setParent_mid(nodes.get(j).getParent_mid());
					node.setVerified(nodes.get(j).getVerified());
					node.setVisited(nodes.get(j).isVisited());
					tempNodes.add(node);
				}else{
					break;
				}	
			}
			//����ý׶�����
			for(int k=0;k<maxWidth;k++){
				widths[k]=-1;
			}
			bfsForWD(tempNodes);
			widthSeries[i]=getMaxWidth();
			depthSeries[i]=getDepth();
			tempNodes=null;
		}
	}
	
	/**
	 * ��������ͼ
	 * @param nodes
	 * @param rootMid
	 */
	public Graph(ArrayList<GraphNode> nodes,String rootMid){
		this.nodes=nodes;
		this.rootMid=rootMid;
		widths=new int[maxWidth];
		for(int i=0;i<maxWidth;i++){
			widths[i]=-1;//�Կ�Ƚ��г�ʼ��
		}
	}
	
	
	/**
	 * ��ȡͼ�����
	 * @return ���
	 */
	public int  getDepth(){
		int depth=0;
		for(int i=1;i<maxWidth;i++){
			if(widths[i]!=-1){
				++depth;
			}else{
				break;
			}
			
		}	
		return depth;
	}
	
	
	/**
	 * ��ȡͼ�������
	 * @return
	 */
	public int  getMaxWidth(){
		int maxwidth=0;
		int width;//��ʱ���
		for(int i=1;i<maxWidth;i++){
			width=widths[i];
			if(width!=-1){
				if(width>maxwidth){
					 maxwidth=width;
				}
			}else{
				break;
			}
			
		}	
		return  maxwidth;
	}
	
	
	@Override
	public String toString() {
		String swidth="[ ";
		int width=0;
		for(int i=1;i<maxWidth;++i){
			width=widths[i];
			if(width==-1)
			{
				swidth+=" ]";
				break;
			}else{
				swidth+=(width+" ");
			}
		}
		return "Graph [nodes=" + nodes.size() + ", widths=" +swidth
				+ ", maxWidth=" + maxWidth +" , Depth "+getDepth()+ ", rootMid=" + rootMid + "]";
	}


	public int  getNodes(){
		return nodes.size();
	}
	
	/**
	 * ��α������ͼ��ÿһ��Ŀ��
	 */
	public void bfsForWD(ArrayList<GraphNode> tempNodes){
		int level=0;//��δ�1��ʼ
		ArrayList<String>mids=new ArrayList<>();
		mids.add(rootMid);	
		getChildNodes(tempNodes,mids);
		while(mids.size()>0){
			level++;
			widths[level]=mids.size();
			getChildNodes(tempNodes, mids);
		}	
	}
	
	/**
	 * ��α������ͼ��ÿһ��Ŀ��
	 */
	public void bfsForWD(){
		int level=0;//��δ�1��ʼ
		ArrayList<String>mids=new ArrayList<>();
		mids.add(rootMid);	
		getChildNodes(nodes,mids);
		while(mids.size()>0){
			level++;
			widths[level]=mids.size();
			getChildNodes(nodes, mids);
		}
		
	}
	
	/**
	 * ��ȡ��һ��Ľڵ���Ϣ
	 * @param nodes
	 * @param mids
	 * @return
	 */
	public void getChildNodes(ArrayList<GraphNode> graphNodes,ArrayList<String> mids){
		ArrayList<String> mids_t=new ArrayList<>();
		String tempMid=null;
		for(String mid:mids){
			if(mid!=null){
				for(GraphNode node:graphNodes){
					if(node.isVisited()==false){
						tempMid=node.getParent_mid();			
						if(tempMid!=null&&tempMid.equals(mid)){
							mids_t.add(node.getMid());
							node.setVisited(true);
						}
					}
				}
			}
		}
		mids.clear();
		mids.addAll(mids_t);
		mids_t.clear();
		mids_t=null;
		
	}

	public int[] getRepostCountSeries() {
		return repostCountSeries;
	}

	public void setRepostCountSeries(int[] repostCountSeries) {
		this.repostCountSeries = repostCountSeries;
	}

	public int[] getvUserCountSeries() {
		return vUserCountSeries;
	}

	public void setvUserCountSeries(int[] vUserCountSeries) {
		this.vUserCountSeries = vUserCountSeries;
	}

	public long[] getFollowerCountSeries() {
		return followerCountSeries;
	}

	public void setFollowerCountSeries(long[] followerCountSeries) {
		this.followerCountSeries = followerCountSeries;
	}

	public int[] getWidthSeries() {
		return widthSeries;
	}

	public void setWidthSeries(int[] widthSeries) {
		this.widthSeries = widthSeries;
	}

	public int[] getDepthSeries() {
		return depthSeries;
	}

	public void setDepthSeries(int[] depthSeries) {
		this.depthSeries = depthSeries;
	}
	
}
