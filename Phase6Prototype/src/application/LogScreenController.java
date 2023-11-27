package application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class LogScreenController implements Initializable{

	private static String JDBC_URL = "jdbc:mysql://localhost:3306/Library";
    private static String USERNAME = "hunterfunk";
    private static String PASSWORD = "Nugget10f!";
    
    String currentProject;
    
    @FXML
    private TextArea defectLogView;

    @FXML
    private Button displayLogsButton;

    @FXML
    private TextArea effortLogView;

    @FXML
    private ListView<String> projectListView;
    
    @FXML
    private Button goEffortLogConsoleButton;
    
    private ArrayList<Defect> Defects = new ArrayList<Defect>();
    
    @FXML
    void goEffortLogConsole(ActionEvent event) throws IOException {
    	Main m = new Main();
    	m.changeScene("EffortLoggerMenu.fxml");
    }

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
    
    @FXML
    void displayLogs(ActionEvent event) {
    	
    }
    
    void displayEffortLogs() { // displays the existing effort logs for the currently selected project
//    	ArrayList<Project> projectList = new ArrayList<>();
		effortLogView.setText("");
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM EffortLogs";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                    	String databaseString = resultSet.getString("ProjName");
                    	if (databaseString.equals(currentProject))
                    	{
                    		String ProjName = resultSet.getString("ProjName");
                    		String LifeCycleStep = resultSet.getString("LifeCycleStep");
                    		String EffortCategory = resultSet.getString("EffortCategory");
                    		String SubEffortCategory = resultSet.getString("SubEffortCategory");
                    		String StartTime = resultSet.getString("StartTime");
                    		String EndTime = resultSet.getString("EndTime");
                            
                            effortLogView.appendText("==New Effort Log==\n" + "Start Time: " + StartTime + "     End Time: " + EndTime + "\nLife Cycle Step: " + LifeCycleStep + "\nEffort Category: " + EffortCategory + "\nSub Effort Category: " + SubEffortCategory + "\n\n");
                            
                            
                    	}


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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
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
//                        projectList.add(project);
                        
                        // Add the project to the UI or a collection
                        projectListView.getItems().add(projectName);

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
            
            query = "SELECT * FROM Defect";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                    	int defectID = resultSet.getInt("DefectID");
                    	int projectID = resultSet.getInt("ProjID");
                    	String projectName = resultSet.getString("ProjName");
                        String defectName = resultSet.getString("DefectName");
                        String defectDesc = resultSet.getString("DefectDesc");
                        String defectStatus = resultSet.getString("DefectStatus");
                        String injectedStep = resultSet.getString("InjectedStep");
                        String removedStep = resultSet.getString("RemovedStep");
                        String defectCateg = resultSet.getString("DefectCategory");
                        String defectOrigin = resultSet.getString("DefectOrigin");
                        // Add the project to the UI or a collection
                        Defect defect = new Defect(defectID, projectID, projectName, defectName, defectDesc, defectStatus,
                        		injectedStep, removedStep, defectCateg, defectOrigin);
                        // You might also add the project to an array or list for later use
                        Defects.add(defect);
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
		
		projectListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				// TODO Auto-generated method stub
				currentProject = projectListView.getSelectionModel().getSelectedItem();
				displayEffortLogs();
				GetDefectLogs();
				
			}
			
			
		});
		
		
	}
	
	public void GetDefectLogs() {
		ArrayList<Defect> currentDefects = new ArrayList<Defect>();
		String DefectInfo = "";
		for(int i = 0; i < Defects.size(); i++) {
			if(Defects.get(i).GetProjName().equals(currentProject)) {
				DefectInfo += "" + Defects.get(i).GetDefectName() + " - " + Defects.get(i).GetDefectStatus() + "\n";
				DefectInfo += Defects.get(i).GetDefectDesc() + "\n";
			}
		}
		defectLogView.setText(DefectInfo);
	}
}


