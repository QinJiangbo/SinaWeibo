package weibo.model;

public class DynamicFeatures {
	
	
	
	

	
	
class DFeature{
	
	/**
	 * ͨ��ѡ���㷨ѡ��������ѵ�5������
	 */
	
	private int time=0;//��Ӧʱ��̶� 
	private int repostCount=0;   //��ǰת����
	private int vUserCount=0;    //��ǰת�����еļ�V�û�����
	private int depth =0;          //��ǰת���������
	private int width=0;          //��ǰת���������
	private long  followersCount=0;  //��ǰת���ߵ����з�˿��
	
	
	public int getRepostCount() {
		return repostCount;
	}
	public void setRepostCount(int repostCount) {
		this.repostCount = repostCount;
	}
	public int getvUserCount() {
		return vUserCount;
	}
	public void setvUserCount(int vUserCount) {
		this.vUserCount = vUserCount;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public long getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(long followersCount) {
		this.followersCount = followersCount;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
}

}
