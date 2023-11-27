package application;
// class definition for a user
public class User {
//	properties
	private String Username;
	private String User_Pass;
//	getters and setters
	public User(String uname, String pword) {
		Username = uname;
		User_Pass = pword;
	}
	
	public void Set_Username(String uname) {
		Username = uname;
	}
	
	public String Get_Username() {
		return Username;
	}
	
	public void Set_User_Pass(String pword) {
		User_Pass = pword;
	}
	
	public String Get_User_Pass() {
		return User_Pass;
	}
}
