package weibo.model;


/**
 * @author coderwang 
 * 
 * 2014/2/25
 *�û���ҳ��Ӧjson�����л���Ӧ����
 * ��ҪĿ�Ļ�����ȡ�Ƚϴ�����htmlԴ��
 * */
public class UserHomePage {
	
	private String ns="";
	private String domid="";
	private String css="";
	private String html="";
	private String js="";
	
	
	@Override
	public String toString() {
		return "UserHomePage [ns=" + ns + ", domid=" + domid + ", css=" + css
				+ ", html=" + html + ", js=" + js + "]";
	}
	public String getJs() {
		return js;
	}
	public void setJs(String js) {
		this.js = js;
	}
	public String getNs() {
		return ns;
	}
	public void setNs(String ns) {
		this.ns = ns;
	}
	public String getDomid() {
		return domid;
	}
	public void setDomid(String domid) {
		this.domid = domid;
	}
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}

}
