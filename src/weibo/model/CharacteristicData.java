package weibo.model;

/**
 * 特征数据模型，用于构建预测模型的特征数据
 * 2014/4/4
 * @author coderwang
 *
 */
public class CharacteristicData {
	
	/*类别特征***************************************************/
	private String mid;//微博id
	private int timeStamp;//时间界限特征


	/*微博特征***************************************************/
	private int hasPic;          //是否包含图片： 0 /1 
	private int contentLength;   //文本长度  
	private long createTime;     //微博创建时间
	private int repostCount;	  //当前已转发数量
	
	/*发布者特征***************************************************/
	private int verified;         //发布者是否认证用户 0/1  
	private int level;            //发布者等级
	private int followerCount;   //发布者粉丝数
	private int friendsCount;     //发布者关注数
	private int statusesCount;    //发布者微博数
	
	
	/*传播特征**************************************************************/
	private int depth;              //微博传播特定时间最大深度
	private int width;              //微博传播特定时间最大宽度
	private long followersCount;    //传播特定时间的粉丝数
	private double linkDensity;		 //链密度 ，repostCount/followersCount
	private int vUserCount;         //加V用户数量
	
	/*传播运动学特征************************************************************/
	private  double  depthV;//速度
	private  double  depthA;//加速度
	private  double  widthV;//速度
	private  double  widthA;//加速度
	private  double  followersCountV;//速度
	private  double  followersCountA;//加速度
	private  double  repostCountV;//速度
	private  double  repostCountA;//加速度
	private  double  vUserCountV;
	private  double  vUserCountA;
	
	/*结果特征********************************************************************/
	private int finalRepostCount;//最终转发数量
	private int popular;         //是否流行       0/1
	private int predictResult; //预测的结果， -1表示不能预测,0/1
	
	
	public CharacteristicData getCData(){
		CharacteristicData cData=new CharacteristicData();
		cData.setHasPic(hasPic);
		cData.setContentLength(contentLength);
		cData.setCreateTime(createTime);
		cData.setMid(mid);
		cData.setRepostCount(repostCount);
		cData.setLevel(level);
		cData.setVerified(verified);
		cData.setFollowerCount(followerCount);
		cData.setFriendsCount(friendsCount);
		cData.setStatusesCount(statusesCount);
		cData.setDepth(depth);
		cData.setWidth(width);
		cData.setFollowersCount(followersCount);
		cData.setLinkDensity(linkDensity);
		cData.setvUserCount(vUserCount);
		cData.setFinalRepostCount(finalRepostCount);
		cData.setPopular(popular);
		cData.setPredictResult(predictResult);
		return cData;
	}
	
	public CharacteristicData(){
		 depthV=0.0;
		 depthA=0.0;
		 widthV=0.0;
		 widthA=0.0;
		 followersCountV=0.0;
		 followersCountA=0.0;
		 repostCountV=0.0;
		 repostCountA=0.0;
		 hasPic=0;
		 contentLength=0;
		 createTime=0;
		 repostCount=0;
		 verified=0;
		 level=0;
		 followerCount=0;
		 friendsCount=0;
		 statusesCount=0;
		 depth=0;
		 width=0;
		 followersCount=0;
		 linkDensity=0.0;
		 vUserCount=0;
		 finalRepostCount=0;
		 popular=0;
	}
	
	/**
	 * 根据属性名 获取属性值
	 * @param featureName
	 * @return
	 */
	public double getAttribute(String featureName){
		double result=0.0;
		if(featureName.equals("hasPic")){
			result= getHasPic()*1.0;
		}
		if(featureName.equals("contentLength")){
			result= getContentLength()*1.0;
		}
		if(featureName.equals("createTime")){
			result=getCreateTime()*1.0;
		}
		if(featureName.equals("repostCount")){
			result=getRepostCount()*1.0;
		}
		if(featureName.equals("verified")){
			result=getVerified()*1.0;
		}
		if(featureName.equals("level")){
			result=getLevel()*1.0;
		}
		if(featureName.equals("followerCount")){
			result=getFollowerCount()*1.0;
		}
		if(featureName.equals("friendsCount")){
			result=getFriendsCount()*1.0; 
		}
		if(featureName.equals("statusesCount")){
			result=getStatusesCount()*1.0;
		}
		if(featureName.equals("depth")){
			result=getDepth()*1.0;
		}
		if(featureName.equals("width")){
			result=getWidth()*1.0;
		}
		if(featureName.equals("followersCount")){
			result=getFollowersCount()*1.0;
		}
		if(featureName.equals("linkDensity")){
			result=getLinkDensity();
		}
		if(featureName.equals("vUserCount")){
			result=getvUserCount()*1.0; 
		}
		if(featureName.equals("finalRepostCount")){
			result=getFinalRepostCount()*1.0;
		}
		if(featureName.equals("popular")){
			result=getPopular()*1.0;
		}
		return result;
	}
	
	public double getAttribute(int index){
		double result = 0.0;
		if(index<1||index>16){
			System.out.println("Atrribute at index " +index+" non exist!" );
			return 0.0;
		}
		if(index==1){
			result=1.0*hasPic;
		}else if(index==2){
			result=1.0*contentLength;
		}else if(index==3){
			result=1.0*createTime;
		}else if(index==4){
			result=1.0*repostCount;
		}else if(index==5){
			result=1.0*verified;
		}else if(index==6){
			result=1.0*level;
		}else if(index==7){
			result=1.0*followerCount;
		}else if(index==8){
			result=1.0*friendsCount;
		}else if(index==9){
			result=1.0*statusesCount;
		}else if(index==10){
			result=1.0*depth;
		}else if(index==11){
			result=1.0*width;
		}else if(index==12){
			result=1.0*followersCount;
		}if(index==13){
			result=1.0*linkDensity;
		}if(index==14){
			result=1.0*vUserCount;
		}if(index==15){
			result=1.0*finalRepostCount;
		}if(index==16){
			result=1.0*popular;
		}else if(index==17){
			result=depthV;
		}else if(index==18){
			result=depthA;
		}else if(index==19){
			result=widthV;
		}else if(index==20){
			 result=widthA;
		}else if(index==21){
			result=followersCountV;
		}else if(index==22){
			result=followersCountA;
		}else if(index==23){
			result=repostCountV;
		}else if(index==24){
			 result=repostCountA;
		}else if(index==25){
			result=vUserCountV;
		}else if(index==26){
			result=vUserCountA;
		}
		return result;
	}
	
	
	
	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}

	public double getDepthV() {
		return depthV;
	}

	public void setDepthV(double depthV) {
		this.depthV = depthV;
	}

	public double getDepthA() {
		return depthA;
	}

	public void setDepthA(double depthA) {
		this.depthA = depthA;
	}

	public double getWidthV() {
		return widthV;
	}

	public void setWidthV(double widthV) {
		this.widthV = widthV;
	}

	public double getWidthA() {
		return widthA;
	}

	public void setWidthA(double widthA) {
		this.widthA = widthA;
	}

	public double getFollowersCountV() {
		return followersCountV;
	}

	public void setFollowersCountV(double followersCountV) {
		this.followersCountV = followersCountV;
	}

	public double getFollowersCountA() {
		return followersCountA;
	}

	public void setFollowersCountA(double followersCountA) {
		this.followersCountA = followersCountA;
	}

	public double getRepostCountV() {
		return repostCountV;
	}

	public void setRepostCountV(double repostCountV) {
		this.repostCountV = repostCountV;
	}

	public double getRepostCountA() {
		return repostCountA;
	}

	public void setRepostCountA(double repostCountA) {
		this.repostCountA = repostCountA;
	}
	
	public int getHasPic() {
		return hasPic;
	}
	public void setHasPic(int hasPic) {
		this.hasPic = hasPic;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getRepostCount() {
		return repostCount;
	}
	public void setRepostCount(int repostCount) {
		this.repostCount = repostCount;
	}
	public int getVerified() {
		return verified;
	}
	public void setVerified(int verified) {
		this.verified = verified;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getFollowerCount() {
		return followerCount;
	}
	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
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
	public double getLinkDensity() {
		return linkDensity;
	}
	public void setLinkDensity(double linkDensity) {
		this.linkDensity = linkDensity;
	}
	public int getvUserCount() {
		return vUserCount;
	}
	public void setvUserCount(int vUserCount) {
		this.vUserCount = vUserCount;
	}
	public int getFinalRepostCount() {
		return finalRepostCount;
	}
	public void setFinalRepostCount(int finalRepostCount) {
		this.finalRepostCount = finalRepostCount;
	}
	public int getPopular() {
		return popular;
	}
	public void setPopular(int popular) {
		this.popular = popular;
	}
	public double getvUserCountV() {
		return vUserCountV;
	}

	public void setvUserCountV(double vUserCountV) {
		this.vUserCountV = vUserCountV;
	}

	public double getvUserCountA() {
		return vUserCountA;
	}

	public void setvUserCountA(double vUserCountA) {
		this.vUserCountA = vUserCountA;
	}

	@Override
	public String toString() {
		return "mid : "+ mid+"CharacteristicData [hasPic=" + hasPic + ", contentLength="
				+ contentLength + ", createTime=" + createTime
				+ ", repostCount=" + repostCount + ", verified=" + verified
				+ ", level=" + level + ", followerCount=" + followerCount
				+ ", friendsCount=" + friendsCount + ", statusesCount="
				+ statusesCount + ", depth=" + depth + ", width=" + width
				+ ", followersCount=" + followersCount + ", linkDensity="
				+ linkDensity + ", vUserCount=" + vUserCount
				+ ", finalRepostCount=" + finalRepostCount + ", popular="
				+ popular + "]";
	}

	public int getPredictResult() {
		return predictResult;
	}


	public String predicDynamic() {
		return "mid : "+ mid+" CharacteristicData [timeStamp=" + timeStamp + ", repostCount="
				+ repostCount + ", depth=" + depth + ", width=" + width
				+ ", followersCount=" + followersCount + ", vUserCount="
				+ vUserCount + ", depthV=" + depthV + ", depthA=" + depthA
				+ ", widthV=" + widthV + ", widthA=" + widthA
				+ ", followersCountV=" + followersCountV + ", followersCountA="
				+ followersCountA + ", repostCountV=" + repostCountV
				+ ", repostCountA=" + repostCountA + ", vUserCountV="
				+ vUserCountV + ", vUserCountA=" + vUserCountA
				+ ", finalRepostCount=" + finalRepostCount + ", popular="
				+ popular  +"]";
	}

	public void setPredictResult(int predictResult) {
		this.predictResult = predictResult;
	}

	

}
