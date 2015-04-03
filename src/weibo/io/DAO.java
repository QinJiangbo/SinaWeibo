package weibo.io;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import weibo.crawler.Log;
import weibo.model.CharacteristicData;
import weibo.model.Graph;
import weibo.model.GraphNode;
import weibo.model.User;
import weibo.model.Weibo;


/**
 * 处理和数据库交互的相关操作
 * @author coderwang 
 * 2014/3/2
 * */
public class DAO {
	
    static 	Connection conn = null;
    
	/**
	 * 加载驱动器
	 * */
	public static void loadDriver(){
		 try {
	            // The newInstance() call is a work around for some
	            // broken Java implementations
	            Class.forName("com.mysql.jdbc.Driver").newInstance();
	           // Log.addLog("数据库驱动加载成功");
	        } catch (Exception ex) {
	            // handle the error
	        	System.out.println(ex.getMessage());
	        }
	}
	
	
	
	/**
	 * 链接数据库
	 * @param dbname 数据库名
	 * @param username 用户名
	 * @param password 用户密码
	 * */
	public void getConnectionObject(String dbname,String username,String password){
		try {
		    conn =DriverManager.getConnection("jdbc:mysql://localhost:3306/" +dbname+
		  "?user="+username+"&password="+password);  
		  // Log.addLog("数据库连接成功");
		} catch (Exception ex) {
		    ex.printStackTrace();
		}	
	}
	
	public void insertVdata(String mid,CharacteristicData cData,String tablename){
		PreparedStatement pstmt=null;
		String sql="insert into "+ tablename +" values ( "+
		" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, mid);
			pstmt.setInt(2, cData.getHasPic());
			pstmt.setInt(3, cData.getContentLength());
			pstmt.setLong(4, cData.getCreateTime());
			pstmt.setInt(5, cData.getRepostCount());
			pstmt.setInt(6, cData.getVerified());
			pstmt.setInt(7, cData.getLevel());
			pstmt.setInt(8, cData.getFollowerCount());
			pstmt.setInt(9, cData.getFriendsCount());
			pstmt.setInt(10, cData.getStatusesCount());
			pstmt.setInt(11, cData.getDepth());
			pstmt.setInt(12, cData.getWidth());
			pstmt.setLong(13, cData.getFollowersCount());
			pstmt.setDouble(14, cData.getLinkDensity());
			pstmt.setInt(15, cData.getvUserCount());
			pstmt.setInt(16, cData.getFinalRepostCount());
			pstmt.setInt(17, cData.getPopular());
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 向数据库中的某个用户表中插入一个用户信息
	 * @param user 用户对象
	 * @param tablename 某个用户表名
	 * */
	public void insertUser(User user,String tablename){
		
		Statement stmt = null;
		ResultSet rSet=null;
		String presql="";
		String sql="";
		PreparedStatement pstmt=null;
		int v=0;//是否认证
		if(user.isVerified()){
			v=1;
		}	
		presql="select uid from "+tablename+" where uid= '"+user.getUid()+"'";
		try {
			stmt = conn.createStatement();
		    rSet=stmt.executeQuery(presql);	
			if (!rSet.next()) {
				try {				
					sql = "insert into "
							+ tablename
							+ "("
							+ "uid,name,home_page,img_src,verified,"
							+ "gender,user_type,followers_count,friends_count,weibos_count,favorates_count,"
							+ "create_time,description,tags,constellation,location,edu,job,level)"
							+ "values( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ?)";
							
					pstmt=conn.prepareStatement(sql);
					pstmt.setString(1, user.getUid());
					pstmt.setString(2, user.getName());
					pstmt.setString(3, user.getHomePage());
					pstmt.setString(4, user.getImg());
					pstmt.setInt(5, v);
					pstmt.setString(6, user.getGender());
					pstmt.setInt(7, user.getUserType());
					pstmt.setInt(8, user.getFollowersCount());
					pstmt.setInt(9, user.getFriendsCount());
					pstmt.setInt(10, user.getStatusesCount());
					pstmt.setInt(11, user.getFavouritesCount());
					pstmt.setLong(12, user.getCreatedAt());		
					pstmt.setString(13, user.getDescription());
					pstmt.setString(14, user.getTags());
					pstmt.setString(15, user.getConstellation());
					pstmt.setString(16, user.getLocation());
					pstmt.setString(17, user.getEdu());
					pstmt.setString(18, user.getJob());
					pstmt.setInt(19, user.getLevel());
							
					pstmt.execute();	

				} catch (SQLException ex) {
					// handle any errors
					System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());
				} finally {
					if (stmt != null) {
						try {
							pstmt.close();
						} catch (SQLException sqlEx) {						
						} // ignore	
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(pstmt!=null){
					pstmt.close();
				}
				if(stmt!=null){
					stmt.close();
				}
				if(rSet!=null){
					rSet.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 
	 * 在数据库中创建tablename的用户信息表
	 * @param 需要创建的用户表的名称
	 * 
	 * */
	public void createUserTable(String tablename){
		Statement stmt = null;
		try {
		    stmt = conn.createStatement();	    
		    String sql=" CREATE TABLE " +tablename+
		    		"(" +
		    		"`uid` varchar(15) NOT NULL," +
		    		"`name` varchar(100) DEFAULT '\"\"'," +
		    		"`home_page` varchar(100) DEFAULT '\"\"'," +
		    		"`img_src` varchar(300) DEFAULT '\"\"'," +
		    		"`verified` int(11) DEFAULT '0'," +
		    		"`gender` varchar(5) DEFAULT '男'," +
		    		"`user_type` int(11) DEFAULT '0'," +
		    		"`followers_count` int(11) DEFAULT '0'," +
		    		"`friends_count` int(11) DEFAULT '0'," +
		    		"`weibos_count` int(11) DEFAULT '0'," +
		    		"`favorates_count` int(11) DEFAULT '0'," +
		    		"`create_time` bigint(20) DEFAULT '0'," +
		    		"`description` varchar(200) DEFAULT '\"\"'," +
		    		"`tags` varchar(100) DEFAULT '\"\"'," +
		    		"`constellation` varchar(20) DEFAULT '\"\"'," +
		    		"`location` varchar(20) DEFAULT '\"\"'," +
		    		"`edu` varchar(100) DEFAULT '\"\"'," +
		    		" `job` varchar(100) DEFAULT '\"\"'," +
		    		"`level` int(11) DEFAULT '0'," +
		    		"PRIMARY KEY (`uid`),"+
		    		"UNIQUE KEY `home_page_UNIQUE` (`home_page`)"+
		    		")ENGINE=InnoDB DEFAULT CHARSET=utf8";	    
		    stmt.execute(sql);
		}
		catch (SQLException ex){
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		finally {
		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore
		        stmt = null;
		    }
		}
	}
	
	/**
	 * 创建用户表
	 * @param tablename 表名称
	 * */
	public void createWeiboTable(String tablename,String userTableName) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql = " CREATE TABLE " + tablename + "("
					+ "`mid` varchar(20) NOT NULL,"
					+ "`uid` varchar(20) NOT NULL,"
					+ "`src` varchar(300) DEFAULT '\"\"',"
					+ "`content` text,"
					+ "`repost_count` int(11) DEFAULT '0',"
					+ "`comment_count` int(11) DEFAULT '0',"
					+ "`like_count` int(11) DEFAULT '0',"
					+ "`original_pic` varchar(400) DEFAULT '\"\"',"
					+ "`parent_mid` varchar(20) DEFAULT '\"\"',"
					+ "`parent_uname` varchar(100) DEFAULT '\"\"',"
					+ "`create_time` bigint(20) DEFAULT '0',"
					+ "`weibo_from` varchar(50) DEFAULT '\"\"',"
					+ "PRIMARY KEY (`mid`),"
					+"FOREIGN KEY (`uid`) REFERENCES "
					+ "`"+userTableName+"` (`uid`) ON DELETE NO ACTION ON UPDATE NO ACTION"
					+ ")ENGINE=InnoDB DEFAULT CHARSET=utf8";
			stmt.execute(sql);
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore
				stmt = null;
			}
		}

	}
		

	/**
	 * 
	 * 在数据库中更新tablename的用户信息表
	 * @param tablename 需要更新的用户信息表
	 * @param user用户信息
	 * 
	 * */
	
//	User [homePage=, name=Ta也有梦, img=, usercardkey=, uid=1744912511, verified=true, verifiedType=0, gender=女, followersCount=398, friendsCount=169, statusesCount=3150, favouritesCount=0, createdAt=0, description=📍Wechat： danson_mm, tags=标签： 名人明星 平常心 吃货 囧 90后 基督教 蜜糖 唐禹哲控, constellation=白羊座, location=其他, edu=, job=, userType=0, level=8]
//	User [homePage=, name=黑白_中野彩奈, img=, usercardkey=, uid=1857900534, verified=true, verifiedType=0, gender=男, followersCount=1040, friendsCount=541, statusesCount=21260, favouritesCount=0, createdAt=0, description=已脱团。我是黑白中野彩奈、我的梦想是成为偶像出道、为了梦想我在周围打工、认识了经纪人后很多工作都接过来！Yvonne酱😘, tags=标签： 吐糟男 弹幕 機戰 求包养 利物浦 纯M 蹭的累 求交往 基友 ACG, constellation=双子座, location=广东 佛山, edu=, job=秘密, userType=0, level=10
//	User [homePage=, name=蘑菇mmmmonty, img=, usercardkey=, uid=2442605872, verified=false, verifiedType=0, gender=女, followersCount=100, friendsCount=324, statusesCount=278, favouritesCount=0, createdAt=0, description=😐😐爱上一匹野马 可我的家里没有草原, tags=, constellation=处女座, location=江苏 无锡, edu=, job=, userType=0, level=7]

	
	public void updateUser(User user,String tablename){////📍😘😐😐
		Statement stmt=null;
		String sql="";
		String presql="";
		ResultSet rSet=null;
		PreparedStatement pstmt=null;
		try {
			stmt=conn.createStatement();
		    presql="select uid from "+tablename+" where uid= '"+user.getUid()+"'";
			rSet=stmt.executeQuery(presql);
			if(rSet.next()){
				int v=0;//是否认证
				if(user.isVerified()){
					v=1;
				}
				String add="";
				sql="update "+tablename
                        + " set verified= ? , gender= ? , user_type= ? , followers_count= ? ,"
						+ "friends_count= ? , weibos_count= ? ,favorates_count= ? ," 
						+ " create_time= ? , description= ? , tags= ? , constellation= ? , location= ? , "
						+ "edu= ? , job= ? , level= ? "+add+" where uid= ? ";	
				//System.out.println(user.getUid()+" update>>>>>");
				pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1, v);
				pstmt.setString(2, user.getGender());
				pstmt.setInt(3, user.getUserType());
				pstmt.setInt(4, user.getFollowersCount());
				pstmt.setInt(5, user.getFriendsCount());
				pstmt.setInt(6, user.getStatusesCount());
				pstmt.setInt(7, user.getFavouritesCount());
				pstmt.setLong(8, user.getCreatedAt());
				pstmt.setString(9, user.getDescription());
				pstmt.setString(10,user.getTags());
				pstmt.setString(11, user.getConstellation());
				pstmt.setString(12,  user.getLocation());
				pstmt.setString(13, user.getEdu());
				pstmt.setString(14,  user.getJob());
				pstmt.setInt(15, user.getLevel());	
				//pstmt.setString(16, user.getImg());
				pstmt.setString(16,  user.getUid());
				pstmt.execute();

			}else{
				int v=0;//是否认证
				if(user.isVerified()){
					v=1;
				}
					sql="update "+tablename
                        + " set verified= ? , gender= ? , user_type= ? , followers_count= ? ,"
						+ "friends_count= ? , weibos_count= ? ,favorates_count= ? ," 
						+ " create_time= ? , description= ? , tags= ? , constellation= ? , location= ? , "
						+ "edu= ? , job= ? , level= ? where name= ? ";
			    pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1, v);
				pstmt.setString(2, user.getGender());
				pstmt.setInt(3, user.getUserType());
				pstmt.setInt(4, user.getFollowersCount());
				pstmt.setInt(5, user.getFriendsCount());
				pstmt.setInt(6, user.getStatusesCount());
				pstmt.setInt(7, user.getFavouritesCount());
				pstmt.setLong(8, user.getCreatedAt());
				pstmt.setString(9, user.getDescription());
				pstmt.setString(10,user.getTags());
				pstmt.setString(11, user.getConstellation());
				pstmt.setString(12,  user.getLocation());
				pstmt.setString(13, user.getEdu());
				pstmt.setString(14,  user.getJob());
				pstmt.setInt(15, user.getLevel());			
				pstmt.setString(16,  user.getName());
				pstmt.execute();
			}
		} catch (SQLException e) {
			System.out.println(tablename);
			System.out.println(user.toString());
			e.printStackTrace();
		}finally{
			try {
				if(stmt!=null)
					stmt.close();
				if(pstmt!=null)
					pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stmt=null;
			presql="";
			presql=null;
			rSet=null;
			sql="";
			sql=null;		
			pstmt=null;
		}
	}
	
	/**
	 * 插入一条微博信息
	 * @param weibo 微博对象
	 * @param tablename 需要插入的表名
	 * */
	public void insertWeibo(Weibo weibo,String tablename){
		
		Statement stmt=null;
		String presql="";
		String sql="";
		ResultSet rSet=null;
		PreparedStatement pstmt=null;
		try {
			stmt=conn.createStatement();
			presql="select mid from "+tablename+" where mid='"+weibo.getMid()+"'";
			rSet=stmt.executeQuery(presql);	
			long create_time=0;
			if(weibo.getCreatedAt()!=null||!weibo.equals("")){
				create_time=Long.parseLong(weibo.getCreatedAt());
			}
			if(!(rSet.next())){
							
						sql = "insert into "
						+ tablename
						+ "(mid,uid,src,content,repost_count,comment_count,"
						+ "like_count,original_pic,parent_mid,parent_uname,create_time,"
						+ "weibo_from) values(?,?,?,?,?,?,?,?,?,?,?,?)";
						
				pstmt=conn.prepareStatement(sql);
				pstmt.setString(1, weibo.getMid());
				pstmt.setString(2, weibo.getUser().getUid());
				pstmt.setString(3, weibo.getSource());
				pstmt.setString(4, weibo.getContent());
				pstmt.setInt(5, weibo.getRepostsCount());
				pstmt.setInt(6, weibo.getCommentsCount());
				pstmt.setInt(7, weibo.getLikeCount());
				pstmt.setString(8, weibo.getOriginalPic());
				pstmt.setString(9, weibo.getForwardMid());
				pstmt.setString(10, weibo.getParent());
				pstmt.setLong(11, create_time);
				pstmt.setString(12, weibo.getWeiboFrom());
	
				pstmt.execute();
			}else{
				System.out.println(weibo.getMid()+"微博已存在");
			}		
		} catch (Exception e) {
			System.out.println(weibo.getMid()+":"+weibo.getContent());
			System.out.println(e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if(pstmt!=null)
					pstmt.close();	
				if(stmt!=null)
					stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			pstmt=null;
			stmt=null;
			sql="";
			sql=null;
			presql="";
			presql=null;
		}		
	}

	
	
	/**
	 * 更新微博的forwardmid
	 * @param weibo
	 * @param tablename
	 */
	public void updateWeibo(Weibo weibo,String tablename){
		Statement stmt=null;
		String presql="";
		ResultSet rSet=null;
		String sql ="";
		try {
			stmt=conn.createStatement();
			presql="select mid from "+tablename+" where mid='"+weibo.getMid()+"'";
			rSet=stmt.executeQuery(presql);	
			if((rSet.next())){
					sql = "update "+tablename+" set parent_mid ='"+weibo.getForwardMid()+"'"+
						"where mid = "+"'"+ weibo.getMid()+"'";
				System.out.println(sql);
				stmt.execute(sql);		
			}else{
				System.out.println(weibo.getMid()+"微博不存在");
			}		
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}finally{
			//内存回收
			try {
				if(stmt!=null)
					stmt.close();
				if(rSet!=null)
					rSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt=null;
			presql="";
			presql=null;
			sql="";
			sql=null;		
			rSet=null;			
		}		
	}
	
	/**
	 * 分页查询
	 * @param tableName 需要查询的表名
	 * @param startIndex 查询从该行的下一行开始查询
	 * @param numPerPage 查询的行数
	 * @return rSet 查询结果集
	 * */
	
	public ResultSet getPageResultSet(String tableName,int startIndex,int numPerPage){
		ResultSet rSet=null;
		Statement statement=null;
		String query="";
		try {
			statement=conn.createStatement();	
			if(statement!=null){
				query="select * from "+tableName+" where  friends_count= 0 and level = 0 and  weibos_count= 0 limit "+startIndex+" ,"+numPerPage;
				rSet=statement.executeQuery(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{		
			statement=null;
			query="";
			query=null;		
		}
		
		return rSet ;
	}
	
	/**
	 * 查询视图的某些行数
	 * @param viewName
	 * @param startIndex
	 * @param numPerPage
	 * @return
	 */
	public ResultSet getViewResultSet(String viewName, int startIndex,
			int numPerPage) {
		ResultSet rSet = null;
		Statement statement =null;
		String query ="";
		try {
			statement = conn.createStatement();

			if (statement != null) {
				query = "select * from " + viewName + " limit "
						+ startIndex + " ," + numPerPage;
				rSet = statement.executeQuery(query);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			statement=null;
			query="";
			query=null;
		}

		return rSet;
	}
	
	
	
	/**
	 * 获取tableName的行数
	 * @param tableName
	 * */
	public int getCount(String tableName){
		int count=0;
		Statement statement=null;
		ResultSet rSet=null;
		String query="";
		try {
			statement=conn.createStatement();
			if(statement!=null){
				query="select count(*) as count from "+tableName;
				rSet= statement.executeQuery(query);
				if(rSet.next()){
					count=rSet.getInt("count");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(statement!=null)
					statement.close();
				if(rSet!=null)
					rSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rSet=null;
			query="";
			query=null;
			statement=null;
		}
		return count;
	}
	
	/**
	 * 获取用户信息不足的用户
	 * @param tablename
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getNoInfoUsers(String tablename) throws SQLException{
		String sql="select * from "+tablename +" where friends_count= 0 and level = 0 and  weibos_count= 0";
		PreparedStatement statement=conn.prepareStatement(sql);
		ResultSet reSet =statement.executeQuery();
		statement=null;
		sql="";
		sql=null;
		return reSet;
	}
	
	/**
	 * 返回具有转发量的微博 按转发量的从大到小排列 出去root微博
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getWeiboHaveRepost(String tableName) throws SQLException{
		String sql="select * from "+tableName +" where repost_count>0  and parent_uname != ? order by repost_count desc";
		//String sql="select * from "+tableName +" where repost_count>0 and repost_count<4400  order by repost_count desc";
		PreparedStatement statement=conn.prepareStatement(sql);
		statement.setString(1, "null");
		ResultSet rSet=statement.executeQuery();
		//垃圾回收
		sql="";
		sql=null;
		statement=null;
		return rSet;
	}
	
	/**
	 * 构造传播图
	 * @param tableName
	 * @throws SQLException 
	 */
	public void constructGraph(String userTable ,String weiboTable) throws SQLException{
		
		String sql="select * from "+weiboTable +" where mid != ? and parent_mid  is null and parent_uname != ? ";
		PreparedStatement pstmt=conn.prepareStatement(sql);
		pstmt.setString(1, "");
		pstmt.setString(2, "root");
		ResultSet allWeibos=pstmt.executeQuery();
		String sql_tmp=null;
		String parent_uname=null;
		ResultSet rSet=null;
		String content=null;
		String mid=null;
		String header=null;//本微博转发的内容
		String temp="";
		String mid_t="";
		while(allWeibos.next()){
		
			 parent_uname=allWeibos.getString("parent_uname");	 
			 mid=allWeibos.getString("mid");
			if (!(parent_uname.equals("null")) && !(parent_uname .equals( "root"))) {
				System.out.println("转自： "+parent_uname);
				content = allWeibos.getString("content");
				header="//@"+parent_uname;
				int index=content.indexOf(header);
				content=content.substring(index+header.length()+1);
				
				System.out.println("转的内容："+content);
				
					sql_tmp = "select * from  " + weiboTable
							+ "  where uid in ( select uid from " + userTable
							+ " where name= ? )";
					pstmt = conn.prepareStatement(sql_tmp);
					pstmt.setString(1, parent_uname);
					rSet = pstmt.executeQuery();
					int count=0;
					boolean update=false;
					while (rSet.next()) {
						mid_t=rSet.getString("mid");
						count++;
						System.out.println(rSet.getString("mid")+"的微博：  "+rSet.getString("content"));
						temp=rSet.getString("content");
						if (temp.contains(content)) {
							sql_tmp = "update " + weiboTable
									+ " set parent_mid = ? where mid = ?";
							System.out.println(sql_tmp);
							update=true;
							pstmt=conn.prepareStatement(sql_tmp);
							pstmt.setString(1, rSet.getString("mid"));
							pstmt.setString(2, mid);
							pstmt.execute();
						}
					}
					if(count>=1&&update==false&&!mid_t.equals("")){
						sql_tmp = "update " + weiboTable
								+ " set parent_mid = ? where mid = ?";
						System.out.println(sql_tmp);
						pstmt=conn.prepareStatement(sql_tmp);
						pstmt.setString(1, mid_t);
						pstmt.setString(2, mid);
						pstmt.execute();
					}
				
			}
			 
		}
		
		//垃圾回收
		{
			sql="";
			sql=null;
			if(pstmt!=null)
				pstmt.close();
			pstmt=null;
			sql_tmp="";
			sql_tmp=null;
			parent_uname=null;
			if(allWeibos!=null)
				allWeibos.close();
			allWeibos=null;
			if(rSet!=null)
				rSet.close();
			rSet=null;
			content=null;
			mid=null;
			header=null;//本微博转发的内容
		}	
	}
	
	
	/**
	 * 跟新整个微博信息
	 * @param weiboTable
	 * @param weibo
	 * @throws SQLException 
	 */
	public void updateWholeWeibo(String weiboTable,Weibo weibo){
		PreparedStatement stmt=null;
		String sql="update "+weiboTable +" set comment_count = ? , like_count = ? , Original_pic = ?" +
				", weibo_from = ?  where mid = ?";
		try{
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, weibo.getCommentsCount());
			stmt.setInt(2, weibo.getLikeCount());
			stmt.setString(3, weibo.getOriginalPic());
			stmt.setString(4, weibo.getWeiboFrom());
			stmt.setString(5, weibo.getMid());
			stmt.execute();	
		}catch(SQLException  e){
			e.printStackTrace();
		}finally{
			//垃圾回收
			try {
				if(stmt!=null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt=null;
			sql="";
			sql=null;
		}
	}
	
	
	
	/**
	 * 更新一部分微博的转发微博
	 * @param repostMids 转发自parent_mid
	 * @param parent_mid
	 * @param weiboTable 微博表名
	 * @throws SQLException 
	 */
	
	public void updateWeibos(ArrayList<String> repostMids,String parent_mid,String weiboTable) {
		PreparedStatement stmt=null;
		String sql="update "+weiboTable +" set parent_mid = ? where mid = ?";
		try {
			stmt=conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(String mid:repostMids){	
			try {
				stmt.setString(1, parent_mid);
				stmt.setString(2, mid);
				stmt.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}	
		}
		
		{//垃圾回收
			try {
				if(stmt!=null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt=null;
			sql="";
			sql=null;
		}
		
		
	}
	
	/**
	 * 获取某个微博表中的所有微博的创建时间并且以创建时间从早到晚的进行排序
	 * @param weiboTale 微博名
	 * @return rSet 结果集
	 * @throws SQLException
	 */
	public ResultSet getWeiboTime(String weiboTable, ArrayList<Long>l, long end_time){
		PreparedStatement stmt=null;
		ResultSet rSet=null;
		String sql="select create_time from "+weiboTable +"  where create_time < ? order by create_time ";
		String sql2="select max(create_time), min(create_time)  from "+ weiboTable +" where create_time < ?";
		String sql3="select count(mid) as count from "+weiboTable +" where create_time < ? ";
		try {
			stmt=conn.prepareStatement(sql3);
			stmt.setLong(1, end_time);
			rSet=stmt.executeQuery();
			while(rSet.next()){
				l.add(rSet.getLong(1));
			}
			
			stmt=conn.prepareStatement(sql2);
			stmt.setLong(1, end_time);
			rSet=stmt.executeQuery();
			while(rSet.next()){
				l.add(rSet.getLong(2));
				l.add(rSet.getLong(1));
			}
			stmt=conn.prepareStatement(sql);
			stmt.setLong(1, end_time);
			rSet=stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			//垃圾回收
			rSet=null;
			stmt=null;
			sql="";
			sql=null;
			sql2="";
			sql2=null;
			sql3="";
			sql3=null;
		}
		return rSet;		
	}
	
	/**
	 * 返回截止时间之前微博转发数量  默认只取出前50条
	 * @param viuewName 视图名字
	 * @param string_date 截止时间
	 * @return
	 */
	public ResultSet getTopTrasferWeibo(String viewName,long string_date) {
		ResultSet rSet=null;
		PreparedStatement pStatement=null;
		String sql="select * from "+viewName +" where create_time < ? order by repost_count desc limit 0 , 50 ";
		try {
			pStatement=conn.prepareStatement(sql);
			pStatement.setLong(1, string_date);
			rSet=pStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			rSet=null;
			pStatement=null;
			sql="";
			sql=null;
		}
		return rSet;	
		
	}
	
	/**
	 * 获取某条微博的直接转发微博的数量
	 * @param mid 微博id
	 * @param viewName	 
	 * @param string_date 截止时间
	 * @return
	 */
	public int getDirectRepostNum(String mid,String viewName,long string_date){
		ResultSet rSet=null;
		PreparedStatement pStatement=null;
		String sql="select count(*) as count from "+viewName +" where create_time < ? and parent_mid = ? ";
		try {
			pStatement=conn.prepareStatement(sql);
			pStatement.setLong(1,string_date);
			pStatement.setString(2, mid);
			rSet=pStatement.executeQuery();
		} catch (SQLException e) {
			try {
				if(pStatement!=null)
					pStatement.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		int count =0;
		try {
			while(rSet.next()){
				count=rSet.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(pStatement!=null)
					pStatement.close();
				if(rSet!=null)
					rSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}				
			pStatement=null;
			rSet=null;
			sql="";
			sql=null;
		}
		return count;
		
	}
	
	/**
	 * 更新父亲节点为根节点的父节点mid
	 * @param weibo
	 * @param weiboName
	 */
	public void updateRootSubWeibo(Weibo weibo,String weiboName){
		PreparedStatement pStatement=null;
		String sql="update "+weiboName+" set parent_mid= ? where ( parent_uname= ? or " +
				"parent_uname= ?  )";
		try {
			pStatement=conn.prepareStatement(sql);
			pStatement.setString(1, weibo.getMid());
			pStatement.setString(2, weibo.getUser().getName());
			pStatement.setString(3, "root");
			pStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(pStatement!=null)
					pStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pStatement=null;
			sql="";
			sql=null;		
		}
	}
	
	/**
	 * 插入热门微博种子
	 * @param tableName 数据库表名
	 * @param wurls 种子集合
	 * @throws SQLException
	 */
	public void Insert(String tableName,ArrayList<String> wurls){
		PreparedStatement pStatement=null;
		String sql="insert into "+tableName+" (wurl ,crawle ) values ( ? , ? )";
		for(String url:wurls){
			try {
				pStatement=conn.prepareStatement(sql);
				pStatement.setString(1, url);
				pStatement.setInt(2, 0);
				pStatement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		{//回收资源
			try {
				if(pStatement!=null)
					pStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			wurls.clear();
			wurls=null;
			pStatement=null;
			sql="";
			sql=null;
		}
	}
	
	/**
	 * 获取种子微博url
	 * @param tableName
	 * @return weibo url arrarylist
	 * @throws SQLException
	 */
	public ArrayList<String> getWeiboUrls(String tableName){
		ArrayList<String>wurls=new ArrayList<String>();
		PreparedStatement pStatement=null;
		String sql="select wurl from "+tableName +" where crawle = ? order by id desc ";
		ResultSet rSet=null;
		try {
			pStatement=conn.prepareStatement(sql);
			pStatement.setInt(1, 0);
			rSet=pStatement.executeQuery();
			while(rSet.next()){
				wurls.add(rSet.getString("wurl"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			//资源回收
			try {
				if(rSet!=null)
					rSet.close();
				if(pStatement!=null)
					pStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}		
			sql="";
			sql=null;
			rSet=null;
			pStatement=null;
		}
	
		return wurls;
	}
	
	

	/**
	 * 跟新种子种的标志信息
	 * @param mid
	 * @param weiboName
	 */
	public void updateSeeds(String url ,String weiboName){
		PreparedStatement pStatement=null;
		String sql="update "+weiboName+" set crawle = 1 where wurl = ? ";
		try {
			pStatement=conn.prepareStatement(sql);
			pStatement.setString(1, url);
			Log.addLog(url+"  "+sql);
			pStatement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//资源回收
			try {
				if(pStatement!=null)
					pStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pStatement=null;
			sql="";
			sql=null;
		}
	}

	/**
	 * 从数据库中获取图
	 * @param weiboName 表名
	 * @param endTime	微博创建截止时间
	 * @param rootMid	根用户的id
	 * @return
	 */
	public Graph getGraph(String weiboName,long endTime,String rootMid){
		ArrayList<GraphNode>nodes=new ArrayList<>();
		Graph graph=null;
		ResultSet rSet=null;
		PreparedStatement psmt=null;
		String sql="select * from "+ weiboName +" where create_time <= ?  order by create_time ";
		try {
			psmt=conn.prepareStatement(sql);
			psmt.setLong(1, endTime);
			rSet=psmt.executeQuery();
			GraphNode node=null;
			while(rSet.next()){
				node=new GraphNode();
				node.setMid(rSet.getString("mid"));
				node.setParent_mid(rSet.getString("parent_mid"));
				node.setCreateTime(rSet.getLong("create_time"));
				node.setFollowersCount(rSet.getInt("followers_count"));
				node.setVerified(rSet.getInt("verified"));
				nodes.add(node);	
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(psmt!=null){
					psmt.close();
				}
				if(rSet!=null){
					rSet.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		graph=new Graph(nodes, rootMid);
		return graph;
	}
	
	/**
	 * 获取某条微博的直接转发微博
	 * @param rootMid 根微博mid
	 * @param tableName 表名
	 * @param endTime  截止时间
	 * @param mids  用于存储的数据结构
	 */
	public int getWeiboRepostCountByTime(String rootMid,String tableName,long endTime, ArrayList<String>mids){
		int count=0;
		PreparedStatement pStatement=null;
		ResultSet rSet=null;
		String sql="select count(mid) as count from "+tableName +" where create_time <= ? and parent_mid = ? ";
		try {
			pStatement=conn.prepareStatement(sql);
			pStatement.setLong(1, endTime);
			pStatement.setString(2, rootMid);
			rSet=pStatement.executeQuery();
			while(rSet.next()){
				count=rSet.getInt("count");
			}
			sql="select mid from "+tableName +" where create_time <= ? and parent_mid = ? and repost_count>0 ";
			
			pStatement=conn.prepareStatement(sql);
			pStatement.setLong(1, endTime);
			pStatement.setString(2, rootMid);
			rSet=pStatement.executeQuery();
			while(rSet.next()){
				mids.add(rSet.getString("mid"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(pStatement!=null){
					pStatement.close();
				}
				if(rSet!=null){
					rSet.close();
				}
				sql="";
				sql=null;
				rSet=null;
				pStatement=null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	/**
	 * 层次遍历数据库生成的树结构，计算出该数的高度以及其最大长度
	 * @param mid
	 * @param endTime
	 * @param cData
	 * @throws IOException 
	 */
	public void getWidthAndDepthByTime(String mid,long endTime,CharacteristicData cData) throws IOException{
		int maxDepth=0;
		int maxWidth=0;
		String tableName=mid+"view";
		
		
		int level=0;
		int width=0;	
		ArrayList<String>mids=new ArrayList<>();
		width=getWeiboRepostCountByTime(mid,tableName, endTime, mids);	
		ArrayList<String>tempmid=new ArrayList<>();
		ArrayList<String>mids_t=new ArrayList<>();
		while(!mids.isEmpty()){
			level++;
			if(width>maxWidth)maxWidth=width;			
			Log.addLog("第"+level+"层 ："+width+" 个转发");
			width=0;
			for(String mid_t:mids){
				width+=getWeiboRepostCountByTime(mid_t,tableName, endTime, mids_t);
				if(mids_t.size()>0){
					tempmid.addAll(mids_t);
					mids_t.clear();
				}
			}
			mids.clear();
			mids.addAll(tempmid);
			tempmid.clear();	
		}
		maxDepth=level;
		cData.setDepth(maxDepth);
		cData.setWidth(maxWidth);
	}
	
	/**
	 * 获取当前加v用户个数
	 * @param tableName
	 * @param endTime
	 * @return
	 */
	public int getVusersCountByTime(String tableName,long endTime){
		int count =0;
		PreparedStatement pStatement=null;
		ResultSet rSet=null;
		String sql="select count(*) as vuCount  from "+tableName+" where create_time<=? and verified = 1";
		try {
			pStatement=conn.prepareStatement(sql);
			pStatement.setLong(1, endTime);
			rSet=pStatement.executeQuery();
			while(rSet.next()){
				count=rSet.getInt("vuCount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(pStatement!=null){
					pStatement.close();
				}
				if(rSet!=null){
					rSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rSet=null;
			tableName="";
			tableName=null;
			pStatement=null;
			sql="";
			sql=null;
		}	
		
		return count;
	}
	/**
	 * 获取当前粉丝总数
	 * @param tableName
	 * @param endTime
	 * @return
	 */
	public long getFollowersCountByTime(String tableName,long endTime){
		int count =0;
		PreparedStatement pStatement=null;
		ResultSet rSet=null;
		String sql="select sum(followers_count) as linkCount  from "+tableName+" where create_time<=?";
		try {
			pStatement=conn.prepareStatement(sql);
			pStatement.setLong(1, endTime);
			rSet=pStatement.executeQuery();
			while(rSet.next()){
				count=rSet.getInt("linkCount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(pStatement!=null){
					pStatement.close();
				}
				if(rSet!=null){
					rSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rSet=null;
			tableName="";
			tableName=null;
			pStatement=null;
			sql="";
			sql=null;
		}	
		
		return count;
	}
	
	/**
	 * 获取截止时间内的转发数量
	 * @param mid
	 * @param endDate
	 * @return
	 */
	public int getRepostCountByTime(String mid,long endTime){
		int count =0;
		String tableName=mid+"view";
		PreparedStatement pStatement=null;
		ResultSet rSet=null;
		String sql="select count( "+mid+" )-1 as count_of_reposts_by_time from "+tableName+" where create_time<=?";

		try {
			pStatement=conn.prepareStatement(sql);
			pStatement.setLong(1, endTime);
			rSet=pStatement.executeQuery();
			while(rSet.next()){
				count=rSet.getInt("count_of_reposts_by_time");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(pStatement!=null){
					pStatement.close();
				}
				if(rSet!=null){
					rSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rSet=null;
			tableName="";
			tableName=null;
			pStatement=null;
			sql="";
			sql=null;
		}	
		
		return count;
	}
	
	/**
	 * 某个表的一行信息
	 * @param tableName
	 * @param mid
	 * @return
	 */
	public ResultSet getOneRow(String tableName,String mid){
		PreparedStatement pStatement=null;
		ResultSet rSet=null;
		String sql="select * from "+tableName+ " where mid = ? ";
		
		try {
			pStatement=conn.prepareStatement(sql);
			pStatement.setString(1, mid);
			rSet=pStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			pStatement=null;
			sql="";
			sql=null;
		}	
		return rSet;
	}
	
	/**
	 * 创建视图
	 * @param mid
	 */
	public void createView(String mid){
		PreparedStatement pStatement=null;
		String viewName=mid+"view";
		String weiboName=mid+"repost_weibo";
		String userName=mid+"users";
				
		String sql="create view  "+viewName+"  as " 
				+"select weibo.mid, user.name,user.uid,weibo.src,weibo.content ,weibo.repost_count,"
				+"weibo.comment_count, weibo.like_count,weibo.original_pic,weibo.parent_mid, weibo.create_time,"
				+"weibo.weibo_from,user.home_page,user.verified,user.img_src,user.gender,user.user_type,"
				+"user.followers_count,user.friends_count,user.weibos_count,user.description,user.tags,user.constellation,"
				+"user.edu,user.location,user.job,user.level "
				+"from "+weiboName+" weibo, "+userName+" user "
				+"where weibo.uid =user.uid order by weibo.repost_count desc";	
		try {
			pStatement=conn.prepareStatement(sql);
			Log.addLog(mid);
			Log.addLog(sql);
			pStatement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//资源回收
			try {
				if(pStatement!=null)
					pStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			sql="";
			sql=null;
			pStatement=null;
			viewName="";
			viewName=null;
			weiboName="";
			weiboName=null;
			userName="";
			userName=null;
		}
	}
	
	/**
	 * 获取特征数据库表中内容
	 * @param tableName
	 * @param 返回数目
	 * @return 返回特征数据库中的每行的信息用CharacteristicData进行封装
	 */
	public ArrayList<CharacteristicData>getCDataList(String tableName,int num){
		ArrayList<CharacteristicData>cDatas=new ArrayList<>();
		String sql="select * from "+tableName +" order by repostCount desc limit 0 , "+num;
		PreparedStatement pstmt=null;
		ResultSet rSet=null;
		
		try {
			pstmt=conn.prepareStatement(sql);
			rSet=pstmt.executeQuery();
			CharacteristicData cData;
			while(rSet.next()){
				cData=new CharacteristicData();
				cData.setMid(rSet.getString("mid"));
				cData.setHasPic(rSet.getInt("hasPic"));
				cData.setContentLength(rSet.getInt("contentLength"));
				cData.setCreateTime(rSet.getLong("createTime"));
				cData.setRepostCount(rSet.getInt("repostCount"));
				cData.setVerified(rSet.getInt("verified"));
				cData.setLevel(rSet.getInt("level"));
				cData.setFollowerCount(rSet.getInt("followerCount"));
				cData.setFriendsCount(rSet.getInt("friendsCount"));
				cData.setStatusesCount(rSet.getInt("statusesCount"));
				cData.setDepth(rSet.getInt("depth"));
				cData.setWidth(rSet.getInt("width"));
				cData.setFollowersCount(rSet.getLong("followersCount"));
				cData.setLinkDensity((double)cData.getRepostCount()/(double)cData.getFollowersCount());
				cData.setFinalRepostCount(rSet.getInt("finalRepostCount"));
				cData.setvUserCount(rSet.getInt("vUserCount"));
				cData.setPopular(rSet.getInt(" popular"));//前面有空格	
				cDatas.add(cData);
				cData=null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(pstmt!=null){
					pstmt.close();
				}
				if(rSet!=null){
					rSet.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return cDatas;
	}
	
	/**
	 *获取微博的动态数据
	 * @param mid 微博id
	 * @param tableName 动态数据存在的表名
	 * @return
	 */
	public ArrayList<CharacteristicData>getDynamicFeaturesByMid(String mid,String tableName){
		ArrayList<CharacteristicData>cDatas=new ArrayList<>();
		String sql="select * from "+tableName+" where mid = ? ";
		try {
			PreparedStatement pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, mid);
			ResultSet rSet=pstmt.executeQuery();
			CharacteristicData cData;
			while(rSet.next()){
				cData=new CharacteristicData();
				cData.setMid(mid);	
				cData.setRepostCount((int)rSet.getDouble(2));
				cData.setRepostCountV(rSet.getDouble(3));
				cData.setRepostCountA(rSet.getDouble(4));			
				cData.setvUserCount((int)rSet.getDouble(5));
				cData.setvUserCountV(rSet.getDouble(6));
				cData.setvUserCountA(rSet.getDouble(7));			
				cData.setFollowersCount((long)rSet.getDouble(8));
				cData.setFollowersCountA(rSet.getDouble(9));
				cData.setFollowersCountV(rSet.getDouble(10));			
				cData.setWidth((int)rSet.getDouble(11));
				cData.setWidthA(rSet.getDouble(12));
				cData.setWidthV(rSet.getDouble(13));		
				cData.setDepth((int)rSet.getDouble(14));
				cData.setDepthA(rSet.getDouble(15));
				cData.setDepthV(rSet.getDouble(16));			
				cData.setPopular(rSet.getInt(19));
				cData.setTimeStamp(rSet.getInt(20));			
				cDatas.add(cData);			
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cDatas;
	}
	/**
	 * 向动态数据表中插入动态数据
	 * @param tableName
	 * @param cDatas
	 */
	public  void insertDynamicData(String tableName,ArrayList<CharacteristicData> cDatas){
		PreparedStatement pstmt=null;
		CharacteristicData cData=null;
		String sql="";
		for(int i=0;i<cDatas.size();++i){
			cData=cDatas.get(i);
			sql="insert into "+tableName+" ( mid ,repostCount ,repostCountV ,repostCountA ,	vUserCount ,vUserCountV ," +
					"vUserCountA ,followersCount ,followersCountA ,followersCountV ,width ,widthA ,widthV,depth, depthA ," +
					"depthV ,popularNow ,predictPopularNow ,finalPopular ,timeSpan ) " +
			"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				pstmt=conn.prepareStatement(sql);
				pstmt.setString(1, cData.getMid());
				pstmt.setDouble(2, cData.getRepostCount()*1.0);
				pstmt.setDouble(3, cData.getRepostCountV());
				pstmt.setDouble(4, cData.getRepostCountA());
				pstmt.setDouble(5, cData.getvUserCount()*1.0);
				pstmt.setDouble(6, cData.getvUserCountV());
				pstmt.setDouble(7, cData.getvUserCountA());
				pstmt.setDouble(8, cData.getFollowersCount()*1.0);
				pstmt.setDouble(9, cData.getFollowersCountA());
				pstmt.setDouble(10, cData.getFollowersCountV());
				pstmt.setDouble(11, cData.getWidth()*1.0);
				pstmt.setDouble(12, cData.getWidthA());
				pstmt.setDouble(13, cData.getWidthV());
				pstmt.setDouble(14,cData.getDepth()*1.0 );
				pstmt.setDouble(15, cData.getDepthA());
				pstmt.setDouble(16, cData.getDepthV());
				pstmt.setInt(17, -1);
				pstmt.setInt(18, -1);
				pstmt.setInt(19, cData.getPopular());
				pstmt.setInt(20, cData.getTimeStamp());
				pstmt.execute();	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(pstmt!=null){
					try {
						pstmt.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		}
		
	}
	
	/**
	 * 删除表
	 * @param tableName
	 */
	public boolean dropTable(String tableName){
		boolean rs=false;
		PreparedStatement pStatement=null;
		String sql="drop table "+tableName;
		try {
			pStatement=conn.prepareStatement(sql);
			rs=pStatement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(pStatement!=null){
				try {
					pStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return rs;
	}
	/**
	 * 删除视图
	 * @param viewName
	 */
	public boolean dropView(String viewName){
		boolean rs=false;
		PreparedStatement pStatement=null;
		String sql="drop view "+viewName;
		try {
			pStatement=conn.prepareStatement(sql);
			rs=pStatement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(pStatement!=null){
				try {
					pStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return rs;
	}
}











