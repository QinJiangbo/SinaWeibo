package weibo.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import weibo.io.WeiboReader;
import weibo.io.WeiboWriter;
import weibo.model.Weibo;
import weibo.prase.WeiboSearchPrase;

public class WeiboIOTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		getWeibos();
		
	}
	
	/**
	 * 抽取所有的微博
	 * @throws IOException 
	 * 
	 */
	public static void getWeibos() throws IOException{
		WeiboReader reader=new WeiboReader();
		WeiboWriter writer =new WeiboWriter();
		String content="";
		WeiboSearchPrase weiboSearchPrase=new WeiboSearchPrase();
		ArrayList<Weibo>weibos=null;
		String []dirNames={"北大荒","卫士通","启明星辰","浙大网新"};
		String path="";
		for(int i=0;i<dirNames.length;++i){
			File [] files=reader.getFiles("./data/"+dirNames[i]+"搜索结果");
			path="./data/"+dirNames[i]+"weibos.txt";
			for(int j=0;j<files.length;++j){
				reader.setPath("./data/"+dirNames[i]+"搜索结果/resultPage"+(j+1)+".txt");
				content=reader.readWholeFile();
				//System.out.println(content);
				weibos=weiboSearchPrase.getWeibos(content, null);
				writer.writeWeibos(weibos, path);
				System.out.println(j+"读取存储完毕");
			}
		}	
	}
	
	/**
	 * 抽取文件中的所有微博id并且保存
	 * @throws IOException 
	 * */
	public static void getAllMids() throws IOException{
		WeiboReader reader=new WeiboReader();
		WeiboWriter writer =new WeiboWriter();
		String content="";
		WeiboSearchPrase weiboSearchPrase=new WeiboSearchPrase();
		ArrayList<String>mids=null;
		String []dirNames={"北大荒","卫士通","浙大网新","启明星辰"};
		String path="";
		for(int i=0;i<dirNames.length;++i){
			File [] files=reader.getFiles("./data/searchResult"+dirNames[i]+"搜索结果");
			path="./data/"+dirNames[i]+"MIDS.txt";
			for(int j=0;j<files.length;++j){
				reader.setPath(files[j].toString());
				content=reader.readWholeFile();
				mids=weiboSearchPrase.getMids(content);
				writer.write(mids, path);
				System.out.println(j+"读取存储完毕");
			}
		}
	}

}
