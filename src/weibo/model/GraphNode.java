package weibo.model;

/**
 * 
 * 用于存储图结构的节点类
 * @author coderwang
 *
 */


public class GraphNode {
	
	private String mid;
	private String parent_mid;
	private boolean visited;
	long createTime;
	int followersCount;
	int verified;
	
	
	
	public int getVerified() {
		return verified;
	}

	public void setVerified(int verified) {
		this.verified = verified;
	}

	public  GraphNode() {
		mid=null;
		parent_mid=null;
		visited=false;
		createTime=0;
		followersCount=0;
	} 
	
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getParent_mid() {
		return parent_mid;
	}

	public void setParent_mid(String parent_mid) {
		this.parent_mid = parent_mid;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}



}
