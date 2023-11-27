package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import javafx.fxml.Initializable;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Login implements Initializable{
	
	public Login() {
	}
		
	@FXML
	private Button addBtn;
	@FXML
	private Button loginBtn;
	@FXML
	private Label failureLbl;
	@FXML
	private Label successLbl;
	@FXML
	private TextField newUser;
	@FXML
	private TextField newPass;
	@FXML
	private TextField user;
	@FXML
	private TextField pass;
//  database credentials
    private static String JDBC_URL;
    private static String USERNAME;
    private static String PASSWORD;
    
    private ArrayList<User> Users = new ArrayList<User>();
	
//  Used to call checking a user logging in
	public void userLogin(ActionEvent event) throws IOException {
		checkLogin();
	}
// runs on startup to build a local list of user credentials from database
	public void initialize(URL arg0, ResourceBundle arg1) {
		getCreds();
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM Login";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                    	String uname = resultSet.getString("Username");
                    	String pword = resultSet.getString("User_Pass");
                        
                    	User user = new User(uname, pword);
                    	Users.add(user);
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	private void checkLogin() throws IOException{  // checks if the log in matches the coded user credentials
		Main m = new Main();
		for(int i = 0; i < Users.size(); i++) {
			if(user.getText().toString().equals(Users.get(i).Get_Username()) && pass.getText().equals(Users.get(i).Get_User_Pass())) { 
				failureLbl.setText("success!");
				m.changeScene("EffortLoggerMenu.fxml");
			}else if(user.getText().isEmpty() && pass.getText().isEmpty()){ // if the given log in is incorrect
				failureLbl.setText("Please enter your data.");
			}else {
				failureLbl.setText("Wrong username or password");
			}
		}
	}
	//Hard-coded Credentials risk mitigation: Credentials pulled from config file
    private void getCreds() {
        Properties prop = new Properties();
        InputStream input = null;
        
        //Read database credentials from config.properties
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            
            JDBC_URL = prop.getProperty("mysql.url");
            USERNAME = prop.getProperty("mysql.user");
            PASSWORD = prop.getProperty("mysql.password");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(input != null) {
                try {
                    //Close the file read
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
}
