package application;

public class LifeCycleStep {
	//Basic Life Cycle Step info
	private String name;
	private int ID;
	private int effortCategory;
	private int defaultDeliverable;
	
	//Basic Life Cycle Step Constructor
	public LifeCycleStep(String name, int ID, int EC, int D)
	{
		this.name = name;
		this.ID = ID;
		this.effortCategory = EC;
		this.defaultDeliverable = D;
	}
	
	//Getters
	public String getName() {
		return this.name;
	}
	
	public int getID() {
		return this.ID;
	}
	
	public int getEC() {
		return this.effortCategory;
	}
	
	public int getD() {
		return this.defaultDeliverable;
	}

	//Setters
	public void setName(String name) {
		this.name = name;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public void setEC(int EC) {
		this.effortCategory = EC;
	}
	
	public void setD(int D) {
		this.defaultDeliverable = D;
	}
	
	//toString override for format checking / initialization
	@Override
	public String toString() {
		String LCSInfo = this.name + " | " + this.effortCategory + " | " + this.defaultDeliverable;
		return LCSInfo;
	}
}