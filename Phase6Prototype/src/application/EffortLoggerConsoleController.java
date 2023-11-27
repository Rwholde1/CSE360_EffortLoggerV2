package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class EffortLoggerConsoleController implements Initializable{
 
	ArrayList<Project> projectList;
	
	@FXML
    private ComboBox<String> effortCategoryButton;

    @FXML
    private ComboBox<String> lifeCycleStepButton;

    @FXML
    private ComboBox<String> projectButton;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;
    
    @FXML
    private Button logScreenButton;
    
    @FXML
    private Button planningPokerButton;

    @FXML
    private ComboBox<String> subEffortCategoryButton;

    @FXML
    private Label subEffortCategoryLabel;

    @FXML
    private Label timeStarted;

    @FXML
    private Label timeStopped;

    @FXML
    private Label timerNotRunningLabel;

    @FXML
    private Label timerRunningLabel;
    
    @FXML
    private Button effortLogEditorButton;
    
    @FXML
    private Button DefectConsoleBtn;
    
    @FXML
    private Button DefinitionsBtn;
    
    private static String JDBC_URL = "jdbc:mysql://localhost:3306/Library";
    private static String USERNAME = "hunterfunk";
    private static String PASSWORD = "Nugget10f!";
    
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private String formattedTimeStart;
    private String formattedTimeEnd;
    
    public String[] InterruptionNames = {"Break", "Phone", "Teammate", "Visitor", "Other"};
    public String[] DeliverableNames = {"Conceptual Design", "Detailed Design", "Test Cases", "Solution",
				"Reflection", "Outline", "Draft", "Report", "User Defined", "Other"};
    public String[] EffortCategoryNames = {"Plans", "Deliverables", "Interruptions", "Defects", "Others"};
    public String[] PlanNames = {"Project Plan", "Risk Management Plan", "Conceptual Design Plan", "Detailed Design Plan", "Implementation Plan"};
    public ArrayList<LifeCycleStep> LCSList;
    
//    setonAction(pull name of the effort category)
//    if index = 0, 1, 2, 3, 4
//    	clear combo box for placeholder
//    	add all of the words for deliverableNames
//    	change Placeholder to Deliverables/
//    	
//    CREATE EFFORTLOG TABLE
    
    //When the Definitions Page is opened, the Life Cycle Steps are pulled from the database
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
    
    void addToDatabase() // updates the MySQL database once the user ends the activity
    {
    	try (
                // Establish the connection
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            ) {
                // SQL insert statement
                String insertSQL = "INSERT INTO EffortLogs (ProjName, LifeCycleStep, EffortCategory, SubEffortCategory, StartTime, EndTime) VALUES (?, ?, ?, ?, ?, ?)";

                // Prepare the statement
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                    // Set the parameters
                    preparedStatement.setString(1, projectButton.getValue());
//                    preparedStatement.setInt(2, Integer.parseInt(ageField.getText()));
                    preparedStatement.setString(2,  lifeCycleStepButton.getValue());
                    preparedStatement.setString(3, effortCategoryButton.getValue());
                    preparedStatement.setString(4, subEffortCategoryButton.getValue());
                    preparedStatement.setString(5,  timeStarted.getText());
                    preparedStatement.setString(6,  timeStopped.getText());
                    

                    // Execute the insert
                    preparedStatement.executeUpdate();

//                    // Display the result
//                    if (rowsAffected > 0) {
//                        statusLabel.setText("Insert successful");
//                    } else {
//                        statusLabel.setText("Insert failed");
//                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
//                statusLabel.setText("Database error: " + e.getMessage());
            }
    }
    
    @FXML
    void startActivity(ActionEvent event) // tracks the time the user started the activity
    {
    	timerNotRunningLabel.setText("");
    	timerRunningLabel.setText("CLOCK IS RUNNING");
    	timeStart = LocalDateTime.now();
    	formattedTimeStart = timeStart.format(timeFormatter);
    	timeStarted.setText(formattedTimeStart);
    	
    }
    
    @FXML
    void stopActivity(ActionEvent event) throws SQLException { // tracks the time the user ended the activity, and adds the effort log to the database
    	timerRunningLabel.setText("");
    	timerNotRunningLabel.setText("CLOCK IS STOPPED");
    	timeEnd = LocalDateTime.now();
    	formattedTimeEnd = timeEnd.format(timeFormatter);
    	timeStopped.setText(formattedTimeEnd);
    	
    	addToDatabase();
    	incrementActualEffort();
    }
    
    private void populateLifeCycleSteps() {
        
        //clear the project list
        lifeCycleStepButton.getItems().clear();
        
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
                        lifeCycleStepButton.getItems().add(newLCS.getName());
                        
                        
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
    void updateAttributes(ActionEvent event) {
    	populateLifeCycleSteps();
    }
    
    @FXML
    void changeSubEffortCategory(ActionEvent event) { // populates the sub effort category menu based on the effort category selected by the user
//      setonAction(pull name of the effort category)
//      if index = 0, 1, 2, 3, 4
//      	clear combo box for placeholder
//      	add all of the words for deliverableNames
//      	change Placeholder to Deliverables/
//      	
//      CREATE EFFORTLOG TABLE
    	String currentSubEffortCategory = effortCategoryButton.getValue();
    	System.out.println(currentSubEffortCategory);
    	if (currentSubEffortCategory.equals("Interruptions"))
    	{
    		subEffortCategoryButton.getItems().clear();
    		subEffortCategoryLabel.setText("Interruption:");
    		subEffortCategoryButton.getItems().addAll(InterruptionNames);
    	}
    	else if (currentSubEffortCategory.equals("Deliverables"))
    	{
    		subEffortCategoryButton.getItems().clear();
    		subEffortCategoryButton.getItems().addAll(DeliverableNames);
    		subEffortCategoryLabel.setText("Deliverable:");
    	}
    	else if (currentSubEffortCategory.equals("Plans"))
    	{
    		subEffortCategoryButton.getItems().clear();
    		subEffortCategoryButton.getItems().addAll(PlanNames);
    		subEffortCategoryLabel.setText("Plan:");
    		
    	}
    	
    	
    }
    
    @FXML
    void goEffortLogEditor(ActionEvent event) throws IOException // used to switch scenes to the effort log editor
    {
    	Main m = new Main();
    	m.changeScene("EffortLogEditor.fxml");
    }
    @FXML
    void goPlanningPoker(ActionEvent event) throws IOException { // used to switch scenes to the planning poker screen
    	Main m = new Main();
    	m.changeScene("PlanningPokerScreen.fxml");
    }
    
    @FXML
    void goLogScreen(ActionEvent event) throws IOException {
    	Main m = new Main();
    	m.changeScene("LogScreen.fxml");
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { // occurs as soon as the scene is entered
		
		projectList = new ArrayList<>();
		getCreds();
		populateLifeCycleSteps();
		
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
                        projectButton.getItems().add(projectName);

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
		
		effortCategoryButton.getItems().addAll(EffortCategoryNames);
		
	}
	
	@FXML
    void ChangeToDefectConsole(ActionEvent event) throws IOException {
		Main m = new Main();
    	m.changeScene("DefectConsole.fxml");
    }
	
	private void incrementActualEffort() throws SQLException { 
        // TODO Auto-generated method stub
        try (
                // Establish the connection
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            ) {

                String query = "UPDATE Project SET actual_effort = actual_effort + 1 WHERE ProjName = ?";

                // Prepare the statement
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    // Set the parameter
                    String currentProjectName = projectButton.getValue();
                    preparedStatement.setString(1, currentProjectName);

                    // Execute the delete
                    preparedStatement.executeUpdate();
                }
        }
    }
	
	@FXML
	private void GoToDefinitions(ActionEvent event) {
		try {
			Main m = new Main();
			m.changeScene("ProjectScreen.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}
