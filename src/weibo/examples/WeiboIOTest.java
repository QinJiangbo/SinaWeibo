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
	 * ��ȡ���е�΢��
	 * @throws IOException 
	 * 
	 */
	public static void getWeibos() throws IOException{
		WeiboReader reader=new WeiboReader();
		WeiboWriter writer =new WeiboWriter();
		String content="";
		WeiboSearchPrase weiboSearchPrase=new WeiboSearchPrase();
		ArrayList<Weibo>weibos=null;
		String []dirNames={"�����","��ʿͨ","�����ǳ�","�������"};
		String path="";
		for(int i=0;i<dirNames.length;++i){
			File [] files=reader.getFiles("./data/"+dirNames[i]+"�������");
			path="./data/"+dirNames[i]+"weibos.txt";
			for(int j=0;j<files.length;++j){
				reader.setPath("./data/"+dirNames[i]+"�������/resultPage"+(j+1)+".txt");
				content=reader.readWholeFile();
				//System.out.println(content);
				weibos=weiboSearchPrase.getWeibos(content, null);
				writer.writeWeibos(weibos, path);
				System.out.println(j+"��ȡ�洢���");
			}
		}	
	}
	
	/**
	 * ��ȡ�ļ��е�����΢��id���ұ���
	 * @throws IOException 
	 * */
	public static void getAllMids() throws IOException{
		WeiboReader reader=new WeiboReader();
		WeiboWriter writer =new WeiboWriter();
		String content="";
		WeiboSearchPrase weiboSearchPrase=new WeiboSearchPrase();
		ArrayList<String>mids=null;
		String []dirNames={"�����","��ʿͨ","�������","�����ǳ�"};
		String path="";
		for(int i=0;i<dirNames.length;++i){
			File [] files=reader.getFiles("./data/searchResult"+dirNames[i]+"�������");
			path="./data/"+dirNames[i]+"MIDS.txt";
			for(int j=0;j<files.length;++j){
				reader.setPath(files[j].toString());
				content=reader.readWholeFile();
				mids=weiboSearchPrase.getMids(content);
				writer.write(mids, path);
				System.out.println(j+"��ȡ�洢���");
			}
		}
	}

}
