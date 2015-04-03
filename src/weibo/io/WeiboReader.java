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
 * ���ڶ�ȡ�洢�ڱ��ص��ļ�
 * */


public class WeiboReader {
	
	private String path;//����ȡ�ļ����ڵ�λ��
	private BufferedReader reader = null;//��ȡ�ļ�����
	
	
	public  WeiboReader(String path){
		this.path=path;
	}
	
	public WeiboReader(){
		
	}
	
	/**
	 * ���ö�ȡ�ļ����ڵ�λ��
	 * @param path �ļ�����·��
	 * */
	public void  setPath(String path){
		this.path=path;
	}
	
	/**
	 * ��ȡĳ���ļ�������������ļ�
	 * @param path �ļ������ڵ�λ��
	 * @return filenames �����ļ��б�
	 * */
	public File[] getFiles(String path){
		//ArrayList<String>filenames=new ArrayList<String>();
		File file=new File(path);
		if(!file.exists()){
			System.out.println("�ļ�������");
		}else{
			return file.listFiles();
		}
		return null;	
	}
	
	/**
	 * ��ȡ�����ļ����ҷ���
	 * @return readCotent ���������ļ�������
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
	 * ����ĳ���ļ���������ɵ�list
	 * @param path �ļ����ڵ�·��
	 * @param lines ���е���
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
