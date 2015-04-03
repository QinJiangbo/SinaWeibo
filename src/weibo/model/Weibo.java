package weibo.model;



public class Weibo implements Comparable<Weibo>{
	
	private User user=null;                               //作者
	private String createdAt;                             //Weibo创建时间
	private String mid;                                   //微博MID    
	private String url;                                   //微博主页
	private String content;                               //微博内容
	private String source;                                //微博发布的页面
	private Weibo retweetedWeibo = null;                  //转发的博文，内容为Weibo，如果不是转发，则没有此字段
	private int repostsCount;                             //转发数
	private int commentsCount;                            //评论数
	private int likeCount;                                //赞数
	private String originalPic;                           //原始图片
	private String  weiboFrom;                            //微博来源，主要指客户端
	public String parent;                                 //微博转发前一个微博的用户
	public String forwardMid;                             //转发微博对应的id
	public String parent_id;                              //微博转发前一个微博的用户的id

	
	public String getParent_id() {
		return parent_id;
	}
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}
	public String getForwardMid() {
		return forwardMid;
	}
	public void setForwardMid(String forwardMid) {
		this.forwardMid = forwardMid;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public String getWeiboFrom() {
		return weiboFrom;
	}
	public void setWeiboFrom(String weiboFrom) {
		this.weiboFrom = weiboFrom;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Weibo getRetweetedWeibo() {
		return retweetedWeibo;
	}
	public void setRetweetedWeibo(Weibo retweetedWeibo) {
		this.retweetedWeibo = retweetedWeibo;
	}
	public int getRepostsCount() {
		return repostsCount;
	}
	public void setRepostsCount(int repostsCount) {
		this.repostsCount = repostsCount;
	}
	public int getCommentsCount() {
		return commentsCount;
	}
	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}
	public String getOriginalPic() {
		return originalPic;
	}
	public void setOriginalPic(String originalPic) {
		this.originalPic = originalPic;
	}
	
	@Override
	public String toString() {
		return "Weibo [user=" + user + ", createdAt=" + createdAt + ", mid="
				+ mid + ", content=" + content + ", source=" + source
				+ ", retweetedWeibo=" + retweetedWeibo + ", repostsCount="
				+ repostsCount + ", commentsCount=" + commentsCount
				+ ", likeCount=" + likeCount + ", originalPic=" + originalPic
				+ ", weiboFrom=" + weiboFrom + ", parent=" + parent
				+ ", forwardMid=" + forwardMid + ", parent_id=" + parent_id
				+ "]";
	}
	@Override
	public int compareTo(Weibo o) {
		return this.createdAt.compareTo(o.createdAt);
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
