package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EffortLoggerEditorController implements Initializable{

    ArrayList<String> projectList;
    private static String JDBC_URL;
    private static String USERNAME;
    private static String PASSWORD;
    
    public String[] InterruptionNames = {"Break", "Phone", "Teammate", "Visitor", "Other"};
    public String[] DeliverableNames = {"Conceptual Design", "Detailed Design", "Test Cases", "Solution",
				"Reflection", "Outline", "Draft", "Report", "User Defined", "Other"};
    public String[] EffortCategoryNames = {"Plans", "Deliverables", "Interruptions", "Defects", "Others"};
    public String[] PlanNames = {"Project Plan", "Risk Management Plan", "Conceptual Design Plan", "Detailed Design Plan", "Implementation Plan"};
	
	@FXML
    private Button clearButton;

    @FXML
    private Button consoleButton;

    @FXML
    private ComboBox<String> currentEffortCategory;

    @FXML
    private ComboBox<String> currentEffortLogEntry;

    @FXML
    private ComboBox<String> currentLifeCycleSteps;

    @FXML
    private ComboBox<String> currentPlan;

    @FXML
    private ComboBox<String> currentProject;

    @FXML
    private Button deleteButton;

//    @FXML
//    private TextField entryDate;

    @FXML
    private Label attributesSaved;
    
    @FXML
    private TextField entryStartTime;

    @FXML
    private TextField entryStopTime;

    @FXML
    private Button updateButton;
    
    

    @FXML
    void updateEffortLogEntries(ActionEvent event) { // updates the effort log entries able to be chosen when the user selects a project
    	currentEffortLogEntry.getItems().clear();
    	attributesSaved.setText("");
    	try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM EffortLogs";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                    	System.out.println(currentProject.getValue());
                    	System.out.println(resultSet.getString("ProjName"));
                    	if (resultSet.getString("ProjName").equals(currentProject.getValue()))
                    	{
                    		String ProjName = resultSet.getString("ProjName");
                    		String LifeCycleStep = resultSet.getString("LifeCycleStep");
                    		String EffortCategory = resultSet.getString("EffortCategory");
                    		String SubEffortCategory = resultSet.getString("SubEffortCategory");
                    		String StartTime = resultSet.getString("StartTime");
                    		String EndTime = resultSet.getString("EndTime");
                    		EffortLog effortLog = new EffortLog(ProjName, LifeCycleStep, EffortCategory, SubEffortCategory, StartTime, EndTime);
//                    		effortLogList.add(effortLog);
                    		currentEffortLogEntry.getItems().add(StartTime + " " + EffortCategory);
                    		
                    	}
//                    	int projectID = resultSet.getInt("ProjID");
//                        String projectName = resultSet.getString("ProjName");
//                        String projectDesc = resultSet.getString("ProjectDesc");
////                        String projectLCS = resultSet.getString("ProjectLCS");
//                        String keywords = resultSet.getString("Keywords");
//                        int EE = resultSet.getInt("estimated_effort");
//                        
//                        // Parse other fields as needed
//
//                        // Create a new Project object
//                        Project project = new Project(projectID, projectName, projectDesc, keywords, EE /* other fields */);
//                        projectList.add(project);
//                        
//                        // Add the project to the UI or a collection
//                        currentProject.getItems().add(projectName);

                        // You might also add the project to an array or list for later use
                        // e.g., projectList.add(project);
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                    // Handle the exception as needed
                }
            }catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    	
    }
    
    // clears all of the effort logs associated with a project
    @FXML
    void clearProjectEffortLogs(ActionEvent event) throws SQLException, IOException {
    	try (
                // Establish the connection
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            ) {
                // SQL delete statement
                String deleteSQL = "DELETE FROM EffortLogs WHERE ProjName = ?";

                // Prepare the statement
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
                    // Set the parameter
                    preparedStatement.setString(1, currentProject.getValue());

                    // Execute the delete
                    preparedStatement.executeUpdate();
                }
    	}
    	Main m = new Main();
		m.changeScene("EffortLogEditor.fxml");
    }

    @FXML
    void deleteEntry(ActionEvent event) throws SQLException, IOException { // used when the user wants to delete an entry
        try (
                // Establish the connection
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            ) {
                // SQL delete statement
                String deleteSQL = "DELETE FROM EffortLogs WHERE StartTime = ?";

                // Prepare the statement
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
                    // Set the parameter
                    preparedStatement.setString(1, entryStartTime.getText());

                    // Execute the delete
                    preparedStatement.executeUpdate();
                }

                String query = "UPDATE Project SET actual_effort = actual_effort - 1 WHERE ProjName = ?";

                // Prepare the statement
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    // Set the parameter
                    String currentProjectName = currentProject.getValue();
                    preparedStatement.setString(1, currentProjectName);

                    // Execute the delete
                    preparedStatement.executeUpdate();
                }
        }
        Main m = new Main();
        m.changeScene("EffortLogEditor.fxml");
    }
    
private void populateLifeCycleSteps() {
        
        //clear the project list
        currentLifeCycleSteps.getItems().clear();
        
        //connect to the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            
            //Pull every entry from the Project Database
            String query = "SELECT * FROM LifeCycleStep";
            
            //For each entry in the table
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        
                        //Get all data of a specific entry
                        int LCSID = resultSet.getInt("LCSID");
                        String LCSName = resultSet.getString("LCSName");
                        int LCSEC = resultSet.getInt("LCSEC");
                        int LCSD = resultSet.getInt("LCSD");

                        // Create a new LifeCycleStep object
                        LifeCycleStep newLCS = new LifeCycleStep(LCSName, LCSID, LCSEC, LCSD);
                        
                        //Add the LifeCycleStep object to the ComboBox
                        currentLifeCycleSteps.getItems().add(newLCS.getName());
                        
                        
                        //Add the LifeCycleStep object to the LifeCycleStep ArrayList
//                        LCSList.add(newLCS);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }

    @FXML
    void updateEffortLogAttributeFields(ActionEvent event) { // updates all of the entry log attributes once the user selects a specific effort log under a project
    	
    	try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM EffortLogs";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                    	System.out.println(currentProject.getValue());
                    	System.out.println(resultSet.getString("ProjName"));
                    	String databaseString = resultSet.getString("StartTime") + " " + resultSet.getString("EffortCategory");
                    	if (databaseString.equals(currentEffortLogEntry.getValue()))
                    	{
                    		String ProjName = resultSet.getString("ProjName");
                    		String LifeCycleStep = resultSet.getString("LifeCycleStep");
                    		String EffortCategory = resultSet.getString("EffortCategory");
                    		String SubEffortCategory = resultSet.getString("SubEffortCategory");
                    		String StartTime = resultSet.getString("StartTime");
                    		String EndTime = resultSet.getString("EndTime");
                    		EffortLog effortLog = new EffortLog(ProjName, LifeCycleStep, EffortCategory, SubEffortCategory, StartTime, EndTime);

                    		entryStartTime.setText(StartTime);
                    		entryStopTime.setText(EndTime);
                    		currentEffortCategory.getItems().addAll(EffortCategoryNames);
                    		populateLifeCycleSteps();
                    		currentLifeCycleSteps.setValue(LifeCycleStep);
                    		currentEffortCategory.setValue(EffortCategory);
                    		currentPlan.setValue(SubEffortCategory);
                    		
                    	}
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                    // Handle the exception as needed
                }
            }catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    @FXML
    void goEffortLogConsole(ActionEvent event) throws IOException { // used to switch scenes to the effort log console
    	Main m = new Main();
		m.changeScene("EffortLoggerMenu.fxml");
    }

    @FXML
    void updateEntry(ActionEvent event) { // used when the user wants to save changes made to the effort log they are currently editing
    	try (
                // Establish the connection
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            ) {
                // SQL update statement
                String updateSQL = "UPDATE EffortLogs SET EffortCategory = ?, SubEffortCategory = ?, EndTime = ?, LifeCycleStep = ? WHERE startTime = ?";

                // Prepare the statement
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                    // Set the parameters
                    preparedStatement.setString(1, currentEffortCategory.getValue());
                    preparedStatement.setString(2, currentPlan.getValue());
                    preparedStatement.setString(3, entryStopTime.getText());
                    preparedStatement.setString(4, currentLifeCycleSteps.getValue());
                    preparedStatement.setString(5, entryStartTime.getText());
                    

                    // Execute the update
                    preparedStatement.executeUpdate();
//
//                    // Display the result
//                    if (rowsAffected > 0) {
//                        statusLabel.setText("Update successful");
//                    } else {
//                        statusLabel.setText("Update failed. ID not found.");
//                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
//                statusLabel.setText("Database error: " + e.getMessage());
            }	
    	attributesSaved.setText("These attributes have been saved.");
    }
    
    @FXML
    void updateSubEffortCategory(ActionEvent event) { // populates the sub effort category drop-down box based on what effort category the user selects
    	currentPlan.getItems().clear();
    	String currentEffortCategoryName = currentEffortCategory.getValue();
    	if (currentEffortCategoryName.equals("Deliverables"))
    	{
    		currentPlan.getItems().addAll(DeliverableNames);
    	}
    	else if (currentEffortCategoryName.equals("Interruptions"))
    	{
    		currentPlan.getItems().addAll(InterruptionNames);
    	}
    	else if (currentEffortCategoryName.equals("Plans"))
    	{
    		currentPlan.getItems().addAll(PlanNames);
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
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { // is performed immediately as soon as the scene starts
		// TODO Auto-generated method stub
		ArrayList<EffortLog> effortLogList = new ArrayList<>();
		ArrayList<Project> projectList = new ArrayList<>();
		getCreds();
		
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM Project";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                    	int projectID = resultSet.getInt("ProjID");
                        String projectName = resultSet.getString("ProjName");
                        String projectDesc = resultSet.getString("ProjectDesc");
//                        String projectLCS = resultSet.getString("ProjectLCS");
                        String keywords = resultSet.getString("Keywords");
                        int EE = resultSet.getInt("estimated_effort");
                        
                        // Parse other fields as needed

                        // Create a new Project object
                        Project project = new Project(projectID, projectName, projectDesc, keywords, EE /* other fields */);
                        projectList.add(project);
                        
                        // Add the project to the UI or a collection
                        currentProject.getItems().add(projectName);

                        // You might also add the project to an array or list for later use
                        // e.g., projectList.add(project);
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                    // Handle the exception as needed
                }
            }catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
		
//		for (int i = 0; i < projectList.size(); i++)
//		{
//			System.out.println(projectList.get(i).getID());
//		}
		
		
	}

}
