package weibo.model;


/**
 * @author coderwang 
 * 2014/1/11
 * 微博用户的javabean
 * 
 * */
public class User{
	
	
	public User() {
		super();
		this.homePage = "";
		this.name = "";
		this.img = "";
		this.usercardkey = "";
		this.uid = "";
		this.verified =false;
		this.verifiedType =0;
		this.gender ="";
		this.followersCount = 0;
		this.friendsCount =0;
		this.statusesCount = 0;
		this.favouritesCount = 0;
		this.createdAt = 0;
		this.description = "";
		this.tags ="";
		this.constellation = "";
		this.location = "";
		this.edu ="";
		this.job ="";
		this.userType = 0;
		this.level = 0;
	}

	private String homePage="";//用户主页 eg:http://weibo.com/u/2790116834
	private String name="";//用户名
	private String img="";//头像 http://tp3.sinaimg.cn/2790116834/50/5637256872/1
	private String usercardkey=""; // usercardkey=weibo_mp
	private String uid="" ;//用户id
	private boolean verified=false;             //加V标示，是否微博认证用户
	private int verifiedType=0;             //认证类型
	private String gender="";                //性别,m--男，f--女,n--未知 
	private int followersCount=0;           //粉丝数
	private int friendsCount=0;             //关注数
	private int statusesCount=0;            //微博数
	private int favouritesCount=0;          //收藏数
	private long createdAt=0;               //创建时间
	private String description="";           //个人描述
	private String tags="";                  //个人标签
	private String constellation="";         //星座
	public String location="";               //所在城市
	public String edu="";                    //教育程度
	public String job="";                    //公司
	public int userType=0;                  //用户类型 0-个人 1-企业 
	public int level=0;                     //用户等级
	
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getEdu() {
		return edu;
	}
	public void setEdu(String edu) {
		this.edu = edu;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getConstellation() {
		return constellation;
	}
	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHomePage() {
		return homePage;
	}
	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getUsercardkey() {
		return usercardkey;
	}
	public void setUsercardkey(String usercardkey) {
		this.usercardkey = usercardkey;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public int getVerifiedType() {
		return verifiedType;
	}
	public void setVerifiedType(int verifiedType) {
		this.verifiedType = verifiedType;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	public int getFriendsCount() {
		return friendsCount;
	}
	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}
	public int getStatusesCount() {
		return statusesCount;
	}
	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}
	public int getFavouritesCount() {
		return favouritesCount;
	}
	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}
	public long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}
	
	@Override
	public String toString() {
		return "User [homePage=" + homePage + ", name=" + name + ", img=" + img
				+ ", usercardkey=" + usercardkey + ", uid=" + uid
				+ ", verified=" + verified + ", verifiedType=" + verifiedType
				+ ", gender=" + gender + ", followersCount=" + followersCount
				+ ", friendsCount=" + friendsCount + ", statusesCount="
				+ statusesCount + ", favouritesCount=" + favouritesCount
				+ ", createdAt=" + createdAt + ", description=" + description
				+ ", tags=" + tags + ", constellation=" + constellation
				+ ", location=" + location + ", edu=" + edu + ", job=" + job
				+ ", userType=" + userType + ", level=" + level + "]";
	}
	
}