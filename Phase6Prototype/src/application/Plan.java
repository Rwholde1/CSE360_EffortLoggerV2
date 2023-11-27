package application;

public class Plan {

	private int ID;
	private String name;
	
	public Plan(int ID, String name) {
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
