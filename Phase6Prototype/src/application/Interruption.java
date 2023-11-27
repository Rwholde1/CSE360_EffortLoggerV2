package application;

public class Interruption {

	private int ID;
	private String name;
	
	public Interruption(int ID, String name) {
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
