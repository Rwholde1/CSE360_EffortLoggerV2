package application;

public class UserStoryDB {
	private int projID;
	private String name;
	private String description;
	private int estimatedEffort;
	private int actualEffort;
	private String keywords;
	
	public UserStoryDB(int projID, String name, String desc, int EE, int AE, String keywords) {
		this.projID = projID;
		this.name = name;
		this.description = desc;
		this.estimatedEffort = EE;
		this.actualEffort = AE;
		this.keywords = keywords;
	}
	
	public int getProjID() {
		return this.projID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDesc() {
		return this.description;
	}
	
	public int getEE() {
		return this.estimatedEffort;
	}
	
	public int getAE() {
		return this.actualEffort;
	}
	
	public String getKeywords() {
		return this.keywords;
	}
	
	public void setProjID(int projID) {
		this.projID = projID;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDesc(String desc) {
		this.description = desc;
	}
	
	public void setEE(int EE) {
		this.estimatedEffort = EE;
	}
	
	public void setAE(int AE) {
		this.actualEffort = AE;
	}
	
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
}