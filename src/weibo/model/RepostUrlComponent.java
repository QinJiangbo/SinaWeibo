package weibo.model;

public class RepostUrlComponent {
	
	@Override
	public String toString() {
		return "RepostUrlComponent [maxPage=" + maxPage + ", id=" + id
				+ ", max_id=" + max_id + "]";
	}
	private int maxPage;
	private String id;
	private String max_id;
	
	public int getMaxPage() {
		return maxPage;
	}
	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMax_id() {
		return max_id;
	}
	public void setMax_id(String max_id) {
		this.max_id = max_id;
	}
	

}
