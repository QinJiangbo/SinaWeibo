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
 * ����WeiboSearchParse������
 * */
public class WeiboSearchPraseExample {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws IOException {
		// TODO Auto-generated method stub
		String searchItem="������";
		WeiboReader reader=new WeiboReader();
		File files[]=reader.getFiles("./data/"+searchItem+"�������");
		WeiboSearchPrase weiboSearchPrase=new WeiboSearchPrase();
		
		WeiboWriter writer=new WeiboWriter();
		File fileDir=new File("./data/"+searchItem+"�������");
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
