package weibo.model;

/**
 * @author coderwang 
 * 2014/1/11
 * �������ҳ���Ӧjson�����л���Ӧ����
 * ��ҪĿ�Ļ�����ȡ�Ƚϴ�����htmlԴ��
 * */
public class SearchResult {

	private String pid;
	private String css;
	private String js;
	private String html;
	public SearchResult(){
		
	}
	
	@Override
	public String toString() {
		return "SearchResult [pid=" + pid + ", css=" + css + ", js=" + js
				+ ", html=" + html + "]";
	}


	public String getPid() {
		return pid;
	}


	public void setPid(String pid) {
		this.pid = pid;
	}


	public String getCss() {
		return css;
	}


	public void setCss(String css) {
		this.css = css;
	}


	public String getJs() {
		return js;
	}


	public void setJs(String js) {
		this.js = js;
	}


	public String getHtml() {
		return html;
	}


	public void setHtml(String html) {
		this.html = html;
	}


	public SearchResult(String pid, String css, String js, String html) {
		super();
		this.pid = pid;
		this.css = css;
		this.js = js;
		this.html = html;
	}

}
