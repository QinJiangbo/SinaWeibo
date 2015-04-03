package weibo.crawler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import weibo.io.WeiboWriter;

public class Log {
	private  static WeiboWriter writer=null;//–¥∂‘œÛ
	
	public static void init(){
		writer=new WeiboWriter();
		writer.setPath("./weibo.log");
	}
	
	public static void addLog(String content) throws IOException{
		init();
		Date date=new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
		writer.append(f.format(date).toString()+" : "+content);
	}
}
