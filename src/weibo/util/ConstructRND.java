package weibo.util;

import java.util.Date;


/**
 * @author coderwang
 * 2014/1/9
 * 构造随机数
 * */
public class ConstructRND {
	
	public ConstructRND(){}
	
	
	/**
	 * @return 返回时间戳 作为分页抓取转发数据时的url的最后一个参数的一部分
	 * 例如 “&_rnd=1389252369559”
	 * */
	public static long getTimeStamp(){
		return new Date().getTime();
	}
	

}
