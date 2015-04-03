package weibo.model;



public class Weibo implements Comparable<Weibo>{
	
	private User user=null;                               //����
	private String createdAt;                             //Weibo����ʱ��
	private String mid;                                   //΢��MID    
	private String url;                                   //΢����ҳ
	private String content;                               //΢������
	private String source;                                //΢��������ҳ��
	private Weibo retweetedWeibo = null;                  //ת���Ĳ��ģ�����ΪWeibo���������ת������û�д��ֶ�
	private int repostsCount;                             //ת����
	private int commentsCount;                            //������
	private int likeCount;                                //����
	private String originalPic;                           //ԭʼͼƬ
	private String  weiboFrom;                            //΢����Դ����Ҫָ�ͻ���
	public String parent;                                 //΢��ת��ǰһ��΢�����û�
	public String forwardMid;                             //ת��΢����Ӧ��id
	public String parent_id;                              //΢��ת��ǰһ��΢�����û���id

	
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
