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
	 * ���ô洢·��
	 * 
	 * @param path
	 *            �洢·�� �����ļ�����
	 * */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * ���ı�д���½����ļ���ȥ
	 * 
	 * @param content
	 *            ��Ҫд���ı�������
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
	 * ��ĳ���ļ������Ϣ
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
	 * д��һϵ��mids
	 * 
	 * @throws IOException
	 * */
	public void write(ArrayList<String> mids, String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			if (!file.createNewFile()) {
				System.out.println("�����ļ�" + path + "ʧ��");
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
	 * д��һϵ��weibos
	 * 
	 * @throws IOException
	 * */
	public void writeWeibos(ArrayList<Weibo> weibos, String path)
			throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			if (!file.createNewFile()) {
				System.out.println("�����ļ�" + path + "ʧ��");
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
	 * д��һϵ��uids
	 * 
	 * @throws IOException
	 * */
	public void writeUids(ArrayList<User> users, String path)
			throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			if (!file.createNewFile()) {
				System.out.println("�����ļ�" + path + "ʧ��");
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
	 * д��һϵ��users
	 * 
	 * @param users
	 *            �û���Ϣ�б�
	 * @param ��Ҫд���·��
	 * @throws IOException
	 * */
	public void writeUsers(ArrayList<User> users, String path)
			throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			if (!file.createNewFile()) {
				System.out.println("�����ļ�" + path + "ʧ��");
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
