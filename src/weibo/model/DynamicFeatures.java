package weibo.model;

public class DynamicFeatures {
	
	
	
	

	
	
class DFeature{
	
	/**
	 * 通过选择算法选出来的最佳的5个特征
	 */
	
	private int time=0;//对应时间刻度 
	private int repostCount=0;   //当前转发量
	private int vUserCount=0;    //当前转发者中的加V用户数量
	private int depth =0;          //当前转发树的深度
	private int width=0;          //当前转发的最大宽度
	private long  followersCount=0;  //当前转发者的所有粉丝数
	
	
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
