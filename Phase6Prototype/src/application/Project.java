package application;

public class Project {
	//Project information
	private int id;
	private String name;
	private String description;
	private String ProjectLCS;
	private String Keywords;
	private int estimated_effort;
	private int actualEffort;
	//EffortLog ArrayList
	//DefectLog ArrayList
	
	//Initial Project object creation
	public Project(String name, String desc, String keywords, int EE) {
		this.id = 0;
		this.name = name;
		this.description = desc;
		this.ProjectLCS = "";
		this.Keywords = keywords;
		this.estimated_effort = EE;
		this.actualEffort = 0;	
	}
	
	//Initial Project object creation with ID
	public Project(int ID, String name, String desc, String keywords, int EE) {
		this.id = ID;
		this.name = name;
		this.description = desc;
		this.ProjectLCS = "";
		this.Keywords = keywords;
		this.estimated_effort = EE;
		this.actualEffort = 0;	
	}
	
	//Project object creation for data pulled from database
	public Project(int ID, String name, String desc, String projectLCS, String keywords, int EE, int AE) {
		this.id = ID;
		this.name = name;
		this.description = desc;
		this.ProjectLCS = projectLCS;
		this.Keywords = keywords;
		this.estimated_effort = EE;
		this.actualEffort = AE;
	}
	
	//Getters
	public String getName() {
		return this.name;
	}
	
	public int getID() {
		return this.id;
	}
	
	public String getDesc() {
		return this.description;
	}
	
	public String getProjectLCS() {
		return this.ProjectLCS;
	}
	
	public String getKeywords() {
		return this.Keywords;
	}
	
	public int getEE() {
		return this.estimated_effort;
	}
	
	public int getAE() {
		return this.actualEffort;
	}
	
	//Setters
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDesc(String desc) {
		this.description = desc;
	}
	
	public void setProjectLCS(String PLCS) {
		this.ProjectLCS = PLCS;
	}
	
	public void setKeywords(String keywords) {
		this.Keywords = keywords;
	}
	
	public void setEE(int EE) {
		this.estimated_effort = EE;
	}
	
	public void setAE(int AE) {
		this.actualEffort = AE;
	}
}
