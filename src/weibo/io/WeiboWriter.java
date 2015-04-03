package weibo.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import weibo.model.User;
import weibo.model.Weibo;

public class WeiboWriter {

	OutputStreamWriter writer;
	PrintWriter printWriter;
	private String path;// "./data/weibopage.txt"

	public String getPath() {
		return path;
	}

	public WeiboWriter() {
	}

	/**
	 * 设置存储路径
	 * 
	 * @param path
	 *            存储路径 包括文件名字
	 * */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 将文本写到新建的文件中去
	 * 
	 * @param content
	 *            需要写入文本的内容
	 * @throws IOException
	 * */
	public void write(String content) throws IOException {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(
					this.path));
			writer = new OutputStreamWriter(fileOutputStream, "utf-8");
			writer.write(content);
		} finally {
			// fileWriter.close();
			writer.close();
		}
	}
	
	/**
	 * 像某个文件添加信息
	 * @param content
	 * @throws IOException 
	 */
	public void append(String content) throws IOException{
		File file = new File(path);
		printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file,
				true)), true);
	    printWriter.append(content+ "\n");
	
		printWriter.flush();
		printWriter.close();
	}

	/**
	 * 写入一系列mids
	 * 
	 * @throws IOException
	 * */
	public void write(ArrayList<String> mids, String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			if (!file.createNewFile()) {
				System.out.println("创建文件" + path + "失败");
			}
		}
		printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file,
				true)), true);
		for (int i = 0; i < mids.size(); ++i) {
			printWriter.append(mids.get(i) + "\n");
		}
		printWriter.flush();
		printWriter.close();

	}

	/**
	 * 写入一系列weibos
	 * 
	 * @throws IOException
	 * */
	public void writeWeibos(ArrayList<Weibo> weibos, String path)
			throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			if (!file.createNewFile()) {
				System.out.println("创建文件" + path + "失败");
			}
		}
		printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file,
				true)), true);
		for (int i = 0; i < weibos.size(); ++i) {
			Weibo weibo = weibos.get(i);
			printWriter.append(weibo.toString() + "\n");
		}
		printWriter.close();

	}

	/**
	 * 写入一系列uids
	 * 
	 * @throws IOException
	 * */
	public void writeUids(ArrayList<User> users, String path)
			throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			if (!file.createNewFile()) {
				System.out.println("创建文件" + path + "失败");
			}
		}
		printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file,
				true)), true);
		for (int i = 0; i < users.size(); ++i) {
			User user = users.get(i);
			printWriter.append(user.getUid() + "\n");
		}
		printWriter.close();

	}

	/**
	 * 写入一系列users
	 * 
	 * @param users
	 *            用户信息列表
	 * @param 需要写入的路径
	 * @throws IOException
	 * */
	public void writeUsers(ArrayList<User> users, String path)
			throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			if (!file.createNewFile()) {
				System.out.println("创建文件" + path + "失败");
			}
		}
		printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file,
				true)), true);
		for (int i = 0; i < users.size(); ++i) {
			User user = users.get(i);
			printWriter.append(user.toString() + "\n");
		}
		printWriter.close();

	}

}
