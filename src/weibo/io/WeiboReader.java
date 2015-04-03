package weibo.io;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author coderwang 
 * 2014/1/11
 * 用于读取存储在本地的文件
 * */


public class WeiboReader {
	
	private String path;//所读取文件所在的位置
	private BufferedReader reader = null;//读取文件对象
	
	
	public  WeiboReader(String path){
		this.path=path;
	}
	
	public WeiboReader(){
		
	}
	
	/**
	 * 设置读取文件所在的位置
	 * @param path 文件所在路径
	 * */
	public void  setPath(String path){
		this.path=path;
	}
	
	/**
	 * 读取某个文件夹下面的所有文件
	 * @param path 文件夹所在的位置
	 * @return filenames 返回文件列表
	 * */
	public File[] getFiles(String path){
		//ArrayList<String>filenames=new ArrayList<String>();
		File file=new File(path);
		if(!file.exists()){
			System.out.println("文件不存在");
		}else{
			return file.listFiles();
		}
		return null;	
	}
	
	/**
	 * 读取整个文件并且返回
	 * @return readCotent 返回整个文件的内容
	 * @exception IOException FileNotFoundException
	 * */
	public String readWholeFile() throws IOException{
		String readContent="";
		File file=new File(this.path);
		try {
			reader=new BufferedReader(new FileReader(file));
			String temp="";
			while((temp=reader.readLine())!=null){
				readContent+=temp;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			reader.close();
		}
		return readContent;
	}
	
	/**
	 * 返回某个文件所有行组成的list
	 * @param path 文件所在的路径
	 * @param lines 所有的行
	 * */
	public ArrayList<String> readWholeList(String path) throws IOException{
		ArrayList<String>lines=new ArrayList<>();
		File file=new File(path);
		try {
			reader=new BufferedReader(new FileReader(file));
			String temp="";
			while((temp=reader.readLine())!=null){
				lines.add(temp);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			reader.close();
		}
		return lines;
		
	}
	
	public void close(){
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
 

}
