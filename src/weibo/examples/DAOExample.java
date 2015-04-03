package weibo.examples;

import java.io.IOException;
import java.util.ArrayList;

import weibo.io.DAO;
import weibo.io.WeiboReader;


public class DAOExample {

	static ArrayList<String> hotWeiboMids=null;
	static ArrayList<String> nHotWeiboMids=null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		//CountOfUserAndRepostWeibo();
		dropTables();

	}
	
	/**
	 * 删除数据库表
	 */
	public static void dropTables(){
		getSeedWeiboMids();
		DAO dao=new DAO();
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");	
		for(String mid:hotWeiboMids){
			System.out.println(dao.dropTable(mid.trim()+"view"));
			//System.out.println(dao.dropTable(mid.trim()+"repost_weibo"));
			//System.out.println(dao.dropTable(mid.trim()+"users"));
			
		}
		for(String mid:nHotWeiboMids){
			System.out.println(dao.dropView(mid.trim()+"view"));
			//System.out.println(dao.dropTable(mid.trim()+"repost_weibo"));
			//System.out.println(dao.dropTable(mid.trim()+"users"));
			
		}
	}
	
	/**
	 * 统计转发用户数和转发的微博数
	 */
	public static void CountOfUserAndRepostWeibo(){
		getSeedWeiboMids();
		DAO dao=new DAO();
		DAO.loadDriver();
		dao.getConnectionObject("sina_weibo", "root", "wangxiaoyi@1244");	
		int countOfUser=0;
		int countOfRepost=0;
		System.out.println("hotWeiboCount : "+hotWeiboMids.size());
		for(String mid:hotWeiboMids){
			countOfRepost=countOfRepost+dao.getCount(mid.trim()+"repost_weibo")-1;
			countOfUser=countOfUser+dao.getCount(mid.trim()+"users")-1;
			System.out.println("countOfRepost : "+countOfRepost+" countOfUser"+countOfUser);
		}
		System.out.println("nhotWeiboCount : "+nHotWeiboMids.size());
		for(String mid:nHotWeiboMids){
			countOfRepost=countOfRepost+dao.getCount(mid.trim()+"repost_weibo")-1;
			countOfUser=countOfUser+dao.getCount(mid.trim()+"users")-1;
			System.out.println("countOfRepost : "+countOfRepost+" countOfUser"+countOfUser);
		}
		
	}
	
	/**
	 * 读取种子微博mid
	 */
	public static void getSeedWeiboMids(){
		hotWeiboMids=new ArrayList<>();
		nHotWeiboMids=new ArrayList<>();
		WeiboReader reader=new WeiboReader();
		String path="./data/seedWeiboMids/";
		try {
			hotWeiboMids=reader.readWholeList(path+"hotWeiboMids.txt");
			nHotWeiboMids=reader.readWholeList(path+"nhotWeiboMids.txt");
		} catch (IOException e) {
			
			e.printStackTrace();
		}finally{
			reader.close();
			path="";
		}
	}

}
