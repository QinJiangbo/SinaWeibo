package weibo.model;

/**
 * @author coderwang 
 * 2014/1/11
 * 搜索结果页面对应json反序列化对应的类
 * 主要目的还是提取比较纯净的html源码
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
