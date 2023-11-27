package application;

public class EffortCategory {

	private int ID;
	private String name;
	
	public EffortCategory(int ID, String name) {
		this.ID = ID;
		this.name = name;
	}
	
	public int getID() {
		return this.ID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
