package weibo.io;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * ����Gephi �ܹ���ȡ��xml�ļ�
 * @author coderwang
 *
 */
public class XMLIO {
	
	static int count=0;
	private DAO dao=null;//�������ݿ������
	static int nodeId=0;
	static int endgeId=0;
	
	public XMLIO(DAO dao){
		this.dao=dao;
	}
	
	/**
	 * ����gexf ��ʽ�ļ�
	 * @param path �ļ��洢·��
	 * @throws IOException 
	 */
	public void createGexfFile(String path) throws IOException{
		
		Document document=createDocument();		
		XMLWriter writer = new XMLWriter(new FileWriter(path));
		writer.write(document);
		writer.close();	
	}
	
	/**
	 * ��� node �ڵ���Ϣ
	 * @param viewName
	 * @param path
	 * @throws SQLException 
	 * @throws DocumentException 
	 */
	public void updateGexf(String viewName,String path) throws DocumentException, SQLException{
		int totalCount=0;//���ݿ���ͼ�е�������
		ResultSet rSet=null;
		totalCount=dao.getCount(viewName);
		int scope=500;//ÿ��ȡ��500��
		if(totalCount<scope){
			rSet=dao.getViewResultSet(viewName, 0, totalCount);
			insertNodes(rSet, path);
		}else{
			int i=0;
			int t=totalCount/scope;
			int lastNum=totalCount%scope;
			for(i=0;i<t;){
				rSet=dao.getViewResultSet(viewName, i*scope, scope);
				insertNodes(rSet, path);
				i++;
			}
			if(lastNum>0){
				rSet=dao.getViewResultSet(viewName, t*scope, lastNum);
				insertNodes(rSet, path);
			}
			
		}
		
	}
	/**
	 * ��ӽڵ���Ϣ
	 * @param rSet
	 * @param path
	 * @throws DocumentException 
	 * @throws SQLException 
	 */
	public void insertNodes(ResultSet rSet,String path) throws DocumentException, SQLException{
		 SAXReader reader = new SAXReader();
	     Document document = reader.read(path);    
	     Element root=document.getRootElement();
	     Element nodes =root.element("graph").element("nodes");
	     Element edges=root.element("graph").element("edges");
	     while(rSet.next()){
	    	 count++;//���Ʊ�ǩ����ʾ
	    	 if(count<50){
	    		 nodes.addElement("node").addAttribute("id", rSet.getString("mid"))
	    		 .addAttribute("Label", rSet.getString("name"));	
	    	 }else{
	    		 nodes.addElement("node").addAttribute("id", rSet.getString("mid"));
		    	// .addAttribute("Label", rSet.getString("name"));
	    		 
	    	 }
	    	 
	    	 nodeId++;
	    	 String parent_mid=rSet.getString("parent_mid");
	    	 if(parent_mid!=null && !parent_mid.equals("")){
	    		 endgeId++;
	    		 edges.addElement("edge").addAttribute("id",Integer.toString(endgeId))
	    		 .addAttribute("source",parent_mid ).addAttribute("target", rSet.getString("mid"));
	    	 }
	    	 
	     }
	     nodes.attribute("count").setValue(Integer.toString(nodeId));
	     edges.attribute("count").setValue(Integer.toString(endgeId));
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(path));
			writer.write(document);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
/**
 * ������ʼ�ĵ�
 * @return
 */
	public Document createDocument() {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("gexf");
		root.addAttribute("xmlns:viz", "http:///www.gexf.net/1.1draft/viz");
		root.addAttribute("xmlns", "http://www.gexf.net/1.1draft");
		root.addAttribute("version", "1.1");

		root.addElement("meta").addAttribute("lastmodifieddate", "2010-03-03+23:44")
				.addElement("creater").addText("Gephi 0.7");
		
		Element graph=root.addElement("graph").addAttribute("defaultedgetype", "directed")
				.addAttribute("idtype", "string").addAttribute("type", "static");
		
		graph.addElement("nodes").addAttribute("count", "0");
		graph.addElement("edges").addAttribute("count", "0");

		return document;
	}

}
