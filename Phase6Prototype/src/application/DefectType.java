package application;

public class DefectType {
	//Basic DefectName info
	private int ID;
	private String name;
	private int value;
	
	//Basic Constructor
	public DefectType(int ID, String name, int value) {
		this.ID = ID;
		this.name = name;
		this.value = value;
	}
	
	//Getters
	public int getID() {
		return this.ID;
	}
	public String getName() {
		return this.name;
	}
	
	public int getValue() {
		return this.value;
	}
	
	//Setters
	public void setName(String name) {
		this.name = name;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	//toString override for format checking / initialization
	@Override
	public String toString() {
		String DefectInfo = this.name + " | " + this.value;
		return DefectInfo;
	}
}