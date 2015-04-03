package weibo.login;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



import weibo.crawler.Log;




public class WeiboRobotManager {
	
	public static List<WeiboRobot> robots1=new ArrayList<>();
	public static List<WeiboRobot> robots2=new ArrayList<>();
	public static List<WeiboRobot> robots3=new ArrayList<>();
	public static int index=0;
	public static int getCounts=0;//get������ܴ���
	

	
	/**
	 * ��ʼ��΢���˺Ŷ���
	 * @throws IOException
	 */
	public static void init() throws IOException{
		init2();
		init3();
		String userNames[]={//�û���
				"isswang1@126.com","isswang2@126.com",
				"isswang3@126.com","isswang4@126.com",
				"isswang5@126.com","isswang23@126.com"
				};
		String pwds[]={//����
				"wangxiaoyi","wangxiaoyi",
				"wangxiaoyi","wangxiaoyi",
				"wangxiaoyi","wangxiaoyi"
		};
		
		for(int i=0;i<userNames.length;++i){
			WeiboRobot robot=new WeiboRobot(userNames[i], pwds[i]);
			if(robot.login()){
				Log.addLog(userNames[i]+"��¼�ɹ�");
				robots1.add(robot);
			}else{
				Log.addLog(userNames[i]+"��¼ʧ��");
			}
		}
		
	}
	
		
	
	
	

	public static void init2() throws IOException{
		String userNames[]={//�û���
				"isswang8@126.com","isswang9@126.com",
				"isswang10@126.com","isswang15@126.com",
				"lwsen@foxmail.com"
				};
		String pwds[]={//����
				"wangxiaoyi","wangxiaoyi",
				"wangxiaoyi","wangxiaoyi",
				"sen912521"
		};
		
		for(int i=0;i<userNames.length;++i){
			WeiboRobot robot=new WeiboRobot(userNames[i], pwds[i]);
			if(robot.login()){
				Log.addLog(userNames[i]+"��¼�ɹ�");
				robots2.add(robot);
			}else{
				Log.addLog(userNames[i]+"��¼ʧ��");
			}
		}
		
	}
	
	
	public static void init3() throws IOException{
		String userNames[]={//�û���
				"isswang@126.com",
				"isswang21@126.com","isswang6@126.com",
				"isswang7@126.com","isswang@live.cn"
				};
		String pwds[]={//����
				"wangxiaoyi","wangxiaoyi",
				"wangxiaoyi","wangxiaoyi",
				"wangxiaoyi@1244"
		};
		
		for(int i=0;i<userNames.length;++i){
			WeiboRobot robot=new WeiboRobot(userNames[i], pwds[i]);
			if(robot.login()){
				Log.addLog(userNames[i]+"��¼�ɹ�");
				robots3.add(robot);
			}else{
				Log.addLog(userNames[i]+"��¼ʧ��");
			}
		}
		
	}

	
	/**
	 * ��ȡһ�������˶���
	 * @return
	 * @throws IOException
	 */
	
	public static List<WeiboRobot> getRobots() throws IOException{
		System.out.println("���������"+getCounts);
		int num=getCounts/3000 + 1;
		int index=num%3;
		if(index==1){
			if(robots1.size()>0){
				return robots1;
			}else {
				init();
				return robots1;
			}
		} else if(index==2){
			if(robots2.size()>0){
				return robots2;
			}else {
				init2();
				return robots2;
			}
			
		}else{
			if(robots3.size()>0){
				return robots3;
			}else {
				init3();
				return robots3;
			}
		}
	}
	
	
	/**
	 * ȡ��һ���Ѿ���¼�Ļ�����
	 * @return
	 * @throws IOException
	 */
	public static synchronized WeiboRobot getRobot() throws IOException{
		getCounts++;
		List<WeiboRobot> robots=getRobots();//���Ȼ�ȡ���������˶���
		WeiboRobot robot=null;
		if(robots.isEmpty()){
			Log.addLog("ȡ����¼�����˳��������˶���Ϊ��");
		}else{
		
				int len=robots.size();
				System.out.println("���û���������"+robots.size());
				if(len>0)
				robot=robots.get(index%len);			
				while(robot.good==false){
					System.out.println(robot.getUsername()+" is bad now ");
					robots.remove(index%len);
					len=robots.size();	
					if(len>0){
						index=(index+1)%len;
						robot=robots.get(index);
					}else{
						break;
					}
				}
				index++;
				if(index>10000){
					index=0;
				}
			}
			
		
		robots=null;//��ֹ�ڴ�й¶
		return robot;	
	}

}
