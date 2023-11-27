package application;

import java.util.ArrayList;

public class UserStory {
	//UserStory Information
	private int ID;
	private int oldProjID;
	private String name;
	private String description;
	private int estimatedEffort;
	private int actualEffort;
	private ArrayList<String> keywords;
	private int relevance;
	
	//UserStory constructor
	public UserStory(int id, int oldProjID, String name, String desc, int EE, int AE, ArrayList<String> keywords) {
		this.ID = id;
		this.oldProjID = oldProjID;
		this.name = name;
		this.description = desc;
		this.estimatedEffort = EE;
		this.actualEffort = AE;
		this.keywords = keywords;
		this.relevance = 0;
	}
	
	//Getters
	public int getID() {
		return this.ID;
	}
	
	public int getOldProjID() {
		return this.oldProjID;
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
	
	public ArrayList<String> getKeywords() {
		return this.keywords;
	}
	
	public double getRelevance() {
		return this.relevance;
	}
	
	//Setters
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
	
	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}
	
	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}
	
	//toString override for format checking / initialization
	@Override
	public String toString() {
		String storyInfo = "Story Name: " + this.name + "\nStory Description: " + this.description
							+ "\nEstimated Effort: " + this.estimatedEffort + "\nActual Effort: " + this.actualEffort
							+ "\nStory Keywords: " + this.keywords.toString() + "\nStory Relevance: " + this.relevance;
		
		return storyInfo;
	}
}
