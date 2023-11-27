package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Controller class for the DefectConsole
public class DefectConsoleController implements Initializable{

//	All the FXML objects on the screen
	@FXML
    private AnchorPane CategoryList;

    @FXML
    private Button ClearDefectLogBtn;

    @FXML
    private Button CreateDefectBtn;

    @FXML
    private ComboBox<String> CurrentDefectBox;

    @FXML
    private TextField DefectNameTxt;

    @FXML
    private TextArea DefectSymptomsTxt;

    @FXML
    private Button DeleteBtn;

    @FXML
    private Button EffortLogBtn;

    @FXML
    private ComboBox<String> FixBox;

    @FXML
    private ComboBox<String> InjectedList;

    @FXML
    private ComboBox<String> ProjectBox;

    @FXML
    private ComboBox<String> RemovedList;

    @FXML
    private Button StatusCloseBtn;

    @FXML
    private Button StatusReopenBtn;

    @FXML
    private Button UpdateBtn;
    
    @FXML
    private Label StatusLabel;
    
    @FXML
    private Label DuplicateTxt;
    
    @FXML
    private ComboBox<String> DefectList;
    
    
       
//  Credentials for accessing the SQL database
    private static String JDBC_URL;
    private static String USERNAME;
    private static String PASSWORD;
    
//	Lists for holding all Projects and their defects locally when running defect console
    private ArrayList<Project> Projects = new ArrayList<Project>();
    private ArrayList<Defect> Defects = new ArrayList<Defect>();
    private ArrayList<LifeCycleStep> LCSList = new ArrayList<LifeCycleStep>();
    private ArrayList<DefectName> DefectNames = new ArrayList<DefectName>();
  
//	Used to track the current project being looked at in the Projects\Defects list
    private int currentProject = 0;
    private int currentDefect = 0;
    
//  Runs on start
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	getCreds();
//    	Adds event to CurrentDefectBox to update the displayed Defects Name
		CurrentDefectBox.setOnAction(arg01 -> {
			try {
				UpdateDefectName(arg01);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
//		Makes a connection with the database to fetch Projects
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM Project";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
//                    	Fetches data from database
                    	int projectID = resultSet.getInt("ProjID");
                        String projectName = resultSet.getString("ProjName");
                        String projectDesc = resultSet.getString("ProjectDesc");
                        String keywords = resultSet.getString("Keywords");
                        int EE = resultSet.getInt("estimated_effort");
                        
//                      builds a new local project object from the database
                        Project project = new Project(projectID, projectName, projectDesc, keywords, EE);
                        
                        // Add the project to the UI
                        ProjectBox.getItems().add(projectName);

                        // Adds the project to our local list
                        Projects.add(project);
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
//          Makes a connection with the database to fetch Defects
            query = "SELECT * FROM Defect";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
//                    	Fetches data from database
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
                        
//                      builds a new local defect object from the database
                        Defect defect = new Defect(defectID, projectID, projectName, defectName, defectDesc, defectStatus, 
                        		injectedStep, removedStep, defectCateg, defectOrigin);
                        // Adds the defect to our local list
                        Defects.add(defect);
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
            query = "SELECT * FROM LifeCycleStep";
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
                        InjectedList.getItems().add(LCSName);
                        RemovedList.getItems().add(LCSName);
                        
                        //Add the LifeCycleStep object to the LifeCycleStep ArrayList
                        LCSList.add(newLCS);
                    }
                }
            }
            query = "SELECT * FROM DefectName";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        
                        //Get all data of a specific entry
                        String DefectCategory = resultSet.getString("DefectName");
                        int DefectValue = resultSet.getInt("DefectValue");

                        // Create a new LifeCycleStep object
                        DefectName newDefectName = new DefectName(DefectCategory, DefectValue);
                        
                        //Add the LifeCycleStep object to the ComboBox
                        DefectList.getItems().add(DefectCategory);
                        
                        //Add the LifeCycleStep object to the LifeCycleStep ArrayList
                        DefectNames.add(newDefectName);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
		ProjectBox.setOnAction(arg01 -> {
			CurrentDefectBox.getItems().clear();
			FixBox.getItems().clear();
			DefectSymptomsTxt.setText("");
			StatusLabel.setText("");
			currentProject = ProjectBox.getSelectionModel().getSelectedIndex();
			FixBox.getItems().add("");
			for(int i = 0; i < Defects.size(); i++) {
				if(Defects.get(i).GetProjName().equals(ProjectBox.getValue())) {
					CurrentDefectBox.getItems().add(Defects.get(i).GetDefectName());
					FixBox.getItems().add(Defects.get(i).GetDefectName());
				}
			}
		});
		
	}
    
//  Used to change the status of the project to close
    public void ChangeStatusClosed(ActionEvent event) throws IOException {
		StatusLabel.setText("Closed");
	}
//  Used to change the status of the project to open
    public void ChangeStatusReopen(ActionEvent event) throws IOException {
		StatusLabel.setText("Open");
	}
    
//	Used to create a new defect
    public void CreateDefect(ActionEvent event) throws IOException {
    	if(!duplicateNameNew("New Defect")) {
//    		Gathers information to build defect object
        	int nextDefID = Defects.get(Defects.size()-1).GetDefectID() + 1;
        	int currentProjectID = Projects.get(currentProject).getID();
        	String currentProjectName = Projects.get(currentProject).getName();
        	
//        	Creates new defect obj and adds it to our list
        	Defect defect = new Defect(nextDefID, currentProjectID, currentProjectName, "New Defect", "Start of Symptoms", "Open", "", "", "", "");
        	Defects.add(defect);
        	
//        	Populates screen with new defects information
        	CurrentDefectBox.getItems().add(defect.GetDefectName());
        	CurrentDefectBox.setValue(defect.GetDefectName());
//        	console messages for debugging
//        	System.out.println(defect.GetDefectID());
//        	System.out.println(defect.GetID());
//        	System.out.println(defect.GetProjName());
//        	System.out.println(defect.GetDefectName());
    	}
        
    }
//  Used to update a defects name
    public void UpdateDefectName(ActionEvent event) throws IOException{
    	String defectName = CurrentDefectBox.getValue();
    	DefectNameTxt.setText(defectName);
    	for(int i = 0; i < Defects.size(); i++) {
    		if(Defects.get(i).GetDefectName().equals(defectName)) {
    			StatusLabel.setText(Defects.get(i).GetDefectStatus());
    			DefectSymptomsTxt.setText(Defects.get(i).GetDefectDesc());
    			InjectedList.setValue(Defects.get(i).GetInjectedStep());
    			RemovedList.setValue(Defects.get(i).GetRemovedStep());
    			DefectList.setValue(Defects.get(i).GetDefectCategory());
    			FixBox.setValue(Defects.get(i).GetDefectOrigin());
    			currentDefect = i;
    		}
    	}
    }
    
    // EXPERIMENTAL, RETURN TO LATER
    public void DeleteProjectDefects(ActionEvent event) throws IOException{
    	String projectDefectsToDelete = ProjectBox.getValue();
    	for(int i = 0; i < Defects.size(); i++) {
    		if(Defects.get(i).GetProjName().equals(projectDefectsToDelete)) {
    			CurrentDefectBox.getItems().removeAll(Defects.get(i).GetDefectName());
    			Defects.remove(i);
    	    	CurrentDefectBox.setValue("");
    	    	i--;
    		}
    	}
    	UpdateAfterClear();
    	
    }
    
//  Used to delete the current Defect
    public void DeleteCurrentDefect(ActionEvent event) throws IOException{
//    	Finds the defect to remove and remove it from our list
    	String defectNameToDelete = CurrentDefectBox.getValue();
    	Defect defectToDelete = null;
    	for(int i = 0; i < Defects.size(); i++) {
    		if(Defects.get(i).GetDefectName().equals(defectNameToDelete)) {
    			defectToDelete = Defects.get(i);
    		}
    	}
    	Defects.remove(defectToDelete);
//    	Updates fields on screen to not have the removed defect
    	CurrentDefectBox.getItems().remove(CurrentDefectBox.getSelectionModel().getSelectedIndex());
    	CurrentDefectBox.setValue("");
    	
    }
    
//  Used to Update the current defect when user selects update
    public void UpdateCurrentDefect(ActionEvent event) throws IOException{
    	String OGName = CurrentDefectBox.getValue();
//    	Ensures the name is unique
    	if(!duplicateName(DefectNameTxt.getText())) {
    		String defectNameToChange = CurrentDefectBox.getValue();
        	Defect defectToChange = null;
        	for(int i = 0; i < Defects.size(); i++) {
        		if(Defects.get(i).GetDefectName().equals(defectNameToChange)) {
        			defectToChange = Defects.get(i);
        		}
        	}
//        	Sets defects properties to the fields on screen
        	defectToChange.SetDefectName(DefectNameTxt.getText());
        	defectToChange.SetDefectDesc(DefectSymptomsTxt.getText());
        	defectToChange.SetDefectStatus(StatusLabel.getText());
        	defectToChange.SetInjectedStep(InjectedList.getValue());
        	defectToChange.SetRemovedStep(RemovedList.getValue());
        	defectToChange.SetDefectCategory(DefectList.getValue());
        	defectToChange.SetDefectOrigin(FixBox.getValue());
//        	changes screen to reflect the update
        	CurrentDefectBox.getItems().set(CurrentDefectBox.getSelectionModel().getSelectedIndex(), defectToChange.GetDefectName());
        	CurrentDefectBox.setValue(defectToChange.GetDefectName());
        	
        	for(int i = 0; i < FixBox.getItems().size(); i++) {
        		if(FixBox.getItems().get(i).equals(OGName)) {
        			FixBox.getItems().remove(i);
        			FixBox.getItems().add(DefectNameTxt.getText());
        		}
        	}
        	
        	UpdateOrigins(OGName);
    	}
    	
    }
    
//  Used to call if a name is unique
    public void checkDuplicateName(KeyEvent event) throws IOException{
    	duplicateName(DefectNameTxt.getText());
    }
    
    
//  Used to check if a name is unique
    public boolean duplicateName(String nameToCheck) {
    	for(int i = 0; i < Defects.size(); i++) {
    		if(Defects.get(i).GetDefectName().equals(nameToCheck) && !CurrentDefectBox.getValue().equals(nameToCheck)) {
    			DuplicateTxt.setText("" + nameToCheck + " already exists!" );
    			return true;
    		}
    	}
    	DuplicateTxt.setText("");
    	return false;
    }
    
//  Used for if a user tries creating a new duplicate when they have not changed the name of the previous new defect
    public boolean duplicateNameNew(String nameToCheck) {
    	for(int i = 0; i < Defects.size(); i++) {
    		if(Defects.get(i).GetDefectName().equals(nameToCheck)) {
    			DuplicateTxt.setText("" + nameToCheck + " already exists!" );
    			return true;
    		}
    	}
    	DuplicateTxt.setText("");
    	return false;
    }
    
//  Used to switch to EffortLoggerConsole
    public void ChangeScene() throws IOException{
    	Main m = new Main();
    	m.changeScene("EffortLoggerMenu.fxml");
    }
    
//	Used to update the database to reflect the changes in our local lists
    public void UpdateDataBase(ActionEvent event) throws IOException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
        	String clearQuery = "TRUNCATE TABLE Defect";
            try (PreparedStatement clearStatement = connection.prepareStatement(clearQuery)) {
                clearStatement.executeUpdate();
            }
            String insertQuery = "INSERT INTO Defect (ProjID, ProjName, DefectName, DefectDesc, DefectStatus, InjectedStep, RemovedStep, DefectCategory, DefectOrigin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                for(int i = 0; i < Defects.size(); i++) {
                	insertProject(insertStatement, Defects.get(i).GetID(), Defects.get(i).GetProjName(),
                			Defects.get(i).GetDefectName(), Defects.get(i).GetDefectDesc(), Defects.get(i).GetDefectStatus(),
                			Defects.get(i).GetInjectedStep(), Defects.get(i).GetRemovedStep(),
                			Defects.get(i).GetDefectCategory(), Defects.get(i).GetDefectOrigin());
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ChangeScene();
    }
    
    public void UpdateAfterClear() {
    	try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
        	String clearQuery = "TRUNCATE TABLE Defect";
            try (PreparedStatement clearStatement = connection.prepareStatement(clearQuery)) {
                clearStatement.executeUpdate();
            }
            String insertQuery = "INSERT INTO Defect (ProjID, ProjName, DefectName, DefectDesc, DefectStatus, InjectedStep, RemovedStep, DefectCategory, DefectOrigin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                for(int i = 0; i < Defects.size(); i++) {
                	insertProject(insertStatement, Defects.get(i).GetID(), Defects.get(i).GetProjName(),
                			Defects.get(i).GetDefectName(), Defects.get(i).GetDefectDesc(), Defects.get(i).GetDefectStatus(),
                			Defects.get(i).GetInjectedStep(), Defects.get(i).GetRemovedStep(),
                			Defects.get(i).GetDefectCategory(), Defects.get(i).GetDefectOrigin());
                }
                insertStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
//  Used to insert a projects defects into a table on the database
    private static void insertProject(PreparedStatement statement, int ProjID, String ProjName, String DefectName,
    		String DefectDesc, String DefectStatus, String InjectedStep, String RemovedStep, String DefectCategory, String DefectOrigin) throws SQLException {
//        statement.setInt(1, DefectID);
        statement.setInt(1, ProjID);
        statement.setString(2, ProjName);
        statement.setString(3, DefectName);
        statement.setString(4, DefectDesc);
        statement.setString(5, DefectStatus);
        statement.setString(6, InjectedStep);
        statement.setString(7, RemovedStep);
        statement.setString(8, DefectCategory);
        statement.setString(9, DefectOrigin);
        statement.addBatch(); // Add to batch for batch execution
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
    
    private void UpdateOrigins(String OGName) {
    	for(int i = 0; i < Defects.size(); i++) {
    		if(Defects.get(i).GetDefectOrigin().equals(OGName)) {
    			Defects.get(i).SetDefectOrigin(DefectNameTxt.getText());
    		}
    	}
    }
    
}

	
