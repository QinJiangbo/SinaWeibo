package weibo.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import weibo.io.WeiboReader;
import weibo.io.WeiboWriter;
import weibo.model.User;
import weibo.model.Weibo;
import weibo.prase.WeiboSearchPrase;

/**
 * @author coderwang
 * 测试WeiboSearchParse解析类
 * */
public class WeiboSearchPraseExample {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws IOException {
		// TODO Auto-generated method stub
		String searchItem="打的软件";
		WeiboReader reader=new WeiboReader();
		File files[]=reader.getFiles("./data/"+searchItem+"搜索结果");
		WeiboSearchPrase weiboSearchPrase=new WeiboSearchPrase();
		
		WeiboWriter writer=new WeiboWriter();
		File fileDir=new File("./data/"+searchItem+"解析结果");
		fileDir.mkdir();
		ArrayList<User>users=new ArrayList<User>();
		for(File file:files){
			reader.setPath(file.getPath());
			ArrayList<Weibo> weibos=weiboSearchPrase.getWeibos(reader.readWholeFile(),users);	
			writer.writeWeibos(weibos, fileDir.getPath()+"\\weibos.txt");
		}
		writer.writeUids(users, fileDir.getPath()+"\\uids.txt");
	}

}
