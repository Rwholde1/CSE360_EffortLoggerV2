package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//This control the Definitions screen of EffortLogger V2
public class DefinitionsViewController implements Initializable {
	
	//Credentials in order to access the MySQL database
	private static String JDBC_URL;
    private static String USERNAME;
    private static String PASSWORD;
    
    //Setup of data structure for projects pulled from database
    ArrayList<Project> projectList = new ArrayList<>();
    ArrayList<LifeCycleStep> LCSList = new ArrayList<>();
    ArrayList<DefectType> DefectTypeList = new ArrayList<>();
    ArrayList<EffortCategory> ECList = new ArrayList<>();
    ArrayList<Deliverable> DeliverableList = new ArrayList<>();
    ArrayList<Interruption> InterruptionList = new ArrayList<>();
    ArrayList<Plan> PlanList = new ArrayList<>();
	
    @FXML
    private Label ActualEffort;

    @FXML
    private ComboBox<String> DefectTypes;

    @FXML
    private TextField DefectInfo;
    
    @FXML
    private TextField ECInfo;
    
    @FXML
    private TextField PlanInfo;
    
    @FXML
    private TextField DeliverableInfo;
    
    @FXML
    private TextField InterruptionInfo;

    @FXML
    private ComboBox<String> Deliverables;

    @FXML
    private ComboBox<String> EffortCategories;

    @FXML
    private Button EndProject;

    @FXML
    private Label EstimatedEffort;
    
    @FXML
    private Label ProjectsSaved;

    @FXML
    private ComboBox<String> Interruptions;

    @FXML
    private TextField Keywords;

    @FXML
    private TextField LCSInfo;

    @FXML
    private ComboBox<String> LifeCycleSteps;

    @FXML
    private Button LoadProject;

    @FXML
    private ComboBox<String> PlanTypes;

    @FXML
    private ComboBox<String> Projects;
    
    @FXML
    private TextField ProjectLCS;
    
    @FXML
    private Label KeywordsFormatError;

    @FXML
    private Button SaveProject;

    @FXML
    private Button ToDefectConsole;

    @FXML
    private Button ToEditor;

    @FXML
    private Button ToEffortConsole;

    @FXML
    private Button ToLogs;
    
    @FXML
    private Label FormatError;
    
    @FXML
    private Button toPoker;
    
    @FXML
    private Label LCSID;
    
    @FXML
    private Label LCSIncorrectFormat;
    
    @FXML
    private Label ProjectLCSFormatError;
    
    @FXML
    private Label ProjectEndBad;
    
    @FXML
    private Label ProjectEndGood;
    
    @FXML
    private Button AddButton;
    
    @FXML
    private ComboBox<String> Categories;
    
    
    //Items for the Categories ComboBox
    String[] CategoryNames = {"Plans", "Interruptions", "Defects", "Life Cycle Steps"};
    
    
    @FXML
    private TextArea DescriptionTextArea;
    
    @FXML
    private Label NewItemFormat;

    @FXML
    private TextField NewItemInfo;
    
    @FXML
    private Label CategoryError;
    
    @FXML
    private Label ProjDescError;
    
    @FXML
    private Button UpdateDescButton;
    
    //Initialize method; overridden since the Controller implements Initializable
    //Used to dynamically set Event handling functions and ComboBox contents
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		//Get Credentials for the database
		getCreds();
		
		//Pull database data and add items to ComboBoxes
		populateLifeCycleSteps();
		populateDefectTypes();
		populateEffortCategories();
		populateDeliverables();
		populateInterruptions();
		populatePlans();		
		Categories.getItems().addAll(CategoryNames);
		
		//Set actions based on ComboBoxes and their respective TextFields
		EffortCategories.setOnAction(this::getEC);
		ECInfo.setOnAction(this::updateECs);
		PlanTypes.setOnAction(this::getPlan);
		PlanInfo.setOnAction(this::updatePlans);
		Interruptions.setOnAction(this::getInterruption);
		InterruptionInfo.setOnAction(this::updateInterruptions);
		Deliverables.setOnAction(this::getDeliverable);
		DeliverableInfo.setOnAction(this::updateDeliverables);
		DefectTypes.setOnAction(this::getDefect);
		DefectInfo.setOnAction(this::UpdateDefect);
		LifeCycleSteps.setOnAction(this::getLCS);
		LCSInfo.setOnAction(this::UpdateLCS);
		Categories.setOnAction(this::createNewItem);
		NewItemInfo.setOnAction(this::AddItem);
		
		//Pull Projects from the database
		populateProjects();
		
		//Set actions for ComboBox and its respective TextFields
        Projects.setOnAction(this::getProject);
        Keywords.setOnAction(this::updateProjectKeywords);
        ProjectLCS.setOnAction(this::updateProjectLCS);
	}
	
	//Pulls selected value from EffortCategories and adds it to its respective TextField for editing
	public void getEC(ActionEvent event) {
		String EC = EffortCategories.getValue();
		
		//Set the TextField up for editing
		ECInfo.setText(EC);
		ECInfo.setEditable(true);
	}
    
	//Sets the text of the EffortCategories ComboBox item to whatever
	//the user has put in the TextField after pressing enter
	public void updateECs(ActionEvent event) {
		//get info needed to rename the Effort Category
		int selectedEC = EffortCategories.getSelectionModel().getSelectedIndex();
		String renamedEC = ECInfo.getText();
		
		//Update the ArrayList and the ComboBox
		EffortCategory updatedEC = ECList.get(selectedEC);
		updatedEC.setName(renamedEC);
		ECList.set(selectedEC, updatedEC);
		EffortCategories.getItems().set(selectedEC, renamedEC);
		
		//Reset the TextField
		ECInfo.clear();
		ECInfo.setEditable(false);
	}
	
	//Pulls selected value from PlanTypes and adds it to its respective TextField for editing
	public void getPlan(ActionEvent event) {
		String PlanName = PlanTypes.getValue();	
		
		//Set the TextField up for editing
		PlanInfo.setText(PlanName);
		PlanInfo.setEditable(true);
	}
    
	//Sets the text of the PlanTypes ComboBox item to whatever
	//the user has put in the TextField after pressing enter
	public void updatePlans(ActionEvent event) {
		//get info needed to rename the Plan
		int selectedPlan = PlanTypes.getSelectionModel().getSelectedIndex();
		String renamedPlan = PlanInfo.getText();
		
		//Update the ArrayList and the ComboBox
		Plan updatedPlan = PlanList.get(selectedPlan);
		updatedPlan.setName(renamedPlan);
		PlanList.set(selectedPlan, updatedPlan);
		PlanTypes.getItems().set(selectedPlan, renamedPlan);
		
		//Reset the TextField
		PlanInfo.clear();
		PlanInfo.setEditable(false);
	}
	
	//Pulls selected value from Interruptions and adds it to its respective TextField for editing
	public void getInterruption(ActionEvent event) {
		String Interruption = Interruptions.getValue();	
		
		//Set the TextField up for editing
		InterruptionInfo.setText(Interruption);
		InterruptionInfo.setEditable(true);
	}
    
	//Sets the text of the Interruptions ComboBox item to whatever
	//the user has put in the TextField after pressing enter
	public void updateInterruptions(ActionEvent event) {
		//get info needed to rename the Interruption
		int selectedInterruption = Interruptions.getSelectionModel().getSelectedIndex();
		String renamedInterruption = InterruptionInfo.getText();
		
		//Update the ArrayList and the ComboBox
		Interruption updatedInterruption = InterruptionList.get(selectedInterruption);
		updatedInterruption.setName(renamedInterruption);
		InterruptionList.set(selectedInterruption, updatedInterruption);
		Interruptions.getItems().set(selectedInterruption, renamedInterruption);
		
		//Reset the TextField
		InterruptionInfo.clear();
		InterruptionInfo.setEditable(false);
	}
	
	//Pulls selected value from Deliverables and adds it to its respective TextField for editing
	public void getDeliverable(ActionEvent event) {
		String DeliverableName = Deliverables.getValue();	
		
		//Set the TextField up for editing
		DeliverableInfo.setText(DeliverableName);
		DeliverableInfo.setEditable(true);
	}
    
	//Sets the text of the Deliverables ComboBox item to whatever
	//the user has put in the TextField after pressing enter
	public void updateDeliverables(ActionEvent event) {
		//get info needed to rename the Deliverable
		int selectedDeliverable = Deliverables.getSelectionModel().getSelectedIndex();
		String renamedDeliverable = DeliverableInfo.getText();
		
		//Update the ArrayList and the ComboBox
		Deliverable updatedDeliverable = DeliverableList.get(selectedDeliverable);
		updatedDeliverable.setName(renamedDeliverable);		
		DeliverableList.set(selectedDeliverable, updatedDeliverable);
		Deliverables.getItems().set(selectedDeliverable, renamedDeliverable);
		
		//Reset the TextField
		DeliverableInfo.clear();
		DeliverableInfo.setEditable(false);
	}
	
	//Pulls the DefectName object associated with selected value from DefectTypes 
	//and adds it to its respective TextField for editing in toString form
	public void getDefect(ActionEvent event) {
		//Get the index in the ComboBox of the selected Defect
		int chosenDefect = DefectTypes.getSelectionModel().getSelectedIndex();
		
		//Pull the associated defect from the DefectName objects array, get its toString
		DefectType selectedDefectType = DefectTypeList.get(chosenDefect);
		String toStringDefectType = selectedDefectType.toString();
		
		//Set the TextField up for editing
		DefectInfo.setText(toStringDefectType);
		DefectInfo.setEditable(true);	
	}
	
	//Update the DefectName object and the DefectTypes ComboBox
	//based on the validated input of the respective TextField
	public void UpdateDefect(ActionEvent event) {
		String input = DefectInfo.getText();
		
		//Create a regular expression, compile it, and check if the input is valid
		String pattern = "^\\s*[\\w,]+(\\s[\\w,]+)*\\s*\\|\\s*\\d+\\s*$";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(input);
		
		if(matcher.matches()) {
			//If the input is valid, disable error label in case
			FormatError.setVisible(false);
			
			//Split the string based on the validated format
			String[] parts = input.split("\\|");
			
			//Get the name and the defect's value
			String DN = parts[0].trim();
			int DV = Integer.parseInt(parts[1].trim());
			
			//Find the associated DefectName object and set the input data on that object and in the ComboBox
			int chosenDefectIndex = DefectTypes.getSelectionModel().getSelectedIndex();
			DefectType selectedDefectType = DefectTypeList.get(chosenDefectIndex);
			selectedDefectType.setName(DN);
			selectedDefectType.setValue(DV);
			DefectTypeList.set(chosenDefectIndex, selectedDefectType);
			DefectTypes.getItems().set(DefectTypes.getSelectionModel().getSelectedIndex(), DN);
			
			//Reset the TextField
			DefectInfo.clear();
			DefectInfo.setEditable(false);
		} else {
			//If the input is invalid, notify the user with a Label
			FormatError.setVisible(true);
		}	
	}
	
	//Pulls the DefectName object associated with selected value from LifeCycleSteps ComboBox
	//and adds it to its respective TextField for editing in toString form
	public void getLCS(ActionEvent event) {
		//Get the index in the ComboBox of the selected LCS
		int chosenStep = LifeCycleSteps.getSelectionModel().getSelectedIndex();
		
		//Pull the associated LCS from the LifeCycleStep objects array, get its toString
		//Also separately pull the ID of the LCS
		LifeCycleStep selectedLCS = LCSList.get(chosenStep);
		String toStringLCS = selectedLCS.toString();
		String selectedLCSID = "" + selectedLCS.getID();
		
		//Set the TextField up for editing
		LCSInfo.setText(toStringLCS);
		LCSID.setText(selectedLCSID);
		LCSInfo.setEditable(true);	
	}
	
	//Update the LifeCycleStep object and the LifeCycleSteps ComboBox
	//based on the validated input of the respective TextField
	public void UpdateLCS(ActionEvent event) {
		String input = LCSInfo.getText();
		
		//Create a regular expression, compile it, and check if the input format is valid
		String pattern = "^\\s*\\w+(\\s\\w+)*\\s*\\|\\s*\\d+\\s*\\|\\s*\\d+\\s*$";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(input);
		
		if(matcher.matches()) {
			//If the input format is valid, disable error label in case
			LCSIncorrectFormat.setVisible(false);
			
			//Split the string based on the validated format
			String[] parts = input.split("\\|");
			
			//Get the updated information
			String SN = parts[0].trim();
			int EC = Integer.parseInt(parts[1].trim());
			int D = Integer.parseInt(parts[2].trim());
			
			//Validate that the input works in context, if it doesn't, tell the user how they can fix it
			if(EC > 5 || EC < 1)
			{
				LCSIncorrectFormat.setText("Effort Category out of bounds (1 <= EC <= 5)");
				LCSIncorrectFormat.setVisible(true);
			} else if((EC == 1 || EC == 2 || EC == 3) && (D > 10 || D < 1)) {
				LCSIncorrectFormat.setText("Default Deliverable out of bounds (1 <= D <= 10)");
				LCSIncorrectFormat.setVisible(true);
			} else if(EC == 4 && (D > 15 || D < 1)) {
				LCSIncorrectFormat.setText("Default Deliverable out of bounds (1 <= D <= 15)");
				LCSIncorrectFormat.setVisible(true);
			} else if(EC == 5 && D != 0) {
				LCSIncorrectFormat.setText("Default Deliverable must be 0 with Effort Category 5");
				LCSIncorrectFormat.setVisible(true);
			} else {
				LCSIncorrectFormat.setVisible(false);
				
				//Update LifeCycleStep object with new information and the selected index of the LifeCycleSteps ComboBox
				int chosenLCSIndex = LifeCycleSteps.getSelectionModel().getSelectedIndex();
				LifeCycleStep selectedLCS = LCSList.get(chosenLCSIndex);
				selectedLCS.setName(SN);
				selectedLCS.setEC(EC);
				selectedLCS.setD(D);
				LCSList.set(chosenLCSIndex, selectedLCS);
				LifeCycleSteps.getItems().set(LifeCycleSteps.getSelectionModel().getSelectedIndex(), SN);
				
				//reset the TextField
				LCSID.setText("0");
				LCSInfo.clear();
				LCSInfo.setEditable(false);
			}
			
		} else {
			//If the input format is invalid, notify the user with a Label
			LCSIncorrectFormat.setVisible(true);
		}	
	}
	
	//Pulls the Project object associated with selected value from Projects ComboBox 
	//and adds it to its respective TextField for editing in toString form
	public void getProject(ActionEvent event) {
		Project selectedProject;
		
		//Get the index in the ComboBox of the selected project
		int chosenProjectIndex = Projects.getSelectionModel().getSelectedIndex();
		
		//Mitigate Invalid Read by checking that projects exist in the project list
		if(!projectList.isEmpty()) {
			
			//Pull the associated project from the ArrayList of projects
			selectedProject = projectList.get(chosenProjectIndex);
			
			//Set the TextFields up for editing
			ProjectLCS.setText(selectedProject.getProjectLCS());
			Keywords.setText(selectedProject.getKeywords());
			DescriptionTextArea.setText(selectedProject.getDesc());
			EstimatedEffort.setText("" + selectedProject.getEE());
			ActualEffort.setText("" + selectedProject.getAE());
			ProjectLCS.setEditable(true);
			Keywords.setEditable(true);
			DescriptionTextArea.setEditable(true);
			UpdateDescButton.setDisable(false);
			ProjectEndBad.setVisible(false);
			ProjectEndGood.setVisible(false);
		}
		
	}
	
	//Update the Project object and the Projects ComboBox
	//based on the validated input of the Keywords TextField
	public void updateProjectKeywords(ActionEvent event) {
		String input = Keywords.getText();
		
		//Create a regular expression, compile it, and check if the input format is valid
		String pattern = "^\\s*\\w+(\\s\\w+)*(\\s*\\|\\s*\\w+(\\s\\w+)*)*\\s*$";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(input);
		
		if(matcher.matches()) {
			//If the input format is valid, disable error label in case
			KeywordsFormatError.setVisible(false);
			
			//Pull the associated project and update its list of keywords
			int chosenProjectIndex = Projects.getSelectionModel().getSelectedIndex();
			Project selectedProject = projectList.get(chosenProjectIndex);
			selectedProject.setKeywords(input);
			projectList.set(chosenProjectIndex, selectedProject);
			
			//reset the TextField
			Keywords.clear();
			Keywords.setEditable(false);
		} else {
			//If the input format is invalid, notify the user with a Label
			KeywordsFormatError.setVisible(true);
		}
	}
	
	
	//Update the Project object and the Projects ComboBox
	//based on the validated input of the ProjectLCS TextField
	public void updateProjectLCS(ActionEvent event) {
		String input = ProjectLCS.getText();
		
		//Create a regular expression, compile it, and check if the input format is valid
		String pattern = "^\\s*\\d+(\\s*\\|\\s*\\d+)*\\s*$";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(input);
		
		if(matcher.matches()) {
			//If the input format is valid, disable error label in case
			ProjectLCSFormatError.setVisible(false);
			
			//Pull the associated project and update its list of life cycle steps
			int chosenProjectIndex = Projects.getSelectionModel().getSelectedIndex();
			Project selectedProject = projectList.get(chosenProjectIndex);
			selectedProject.setProjectLCS(input);
			projectList.set(chosenProjectIndex, selectedProject);
			
			//reset the TextField
			ProjectLCS.clear();
			ProjectLCS.setEditable(false);
		} else {
			//If the input format is invalid, notify the user with a Label
			ProjectLCSFormatError.setVisible(true);
		}
	}
	
	public void updateProjectDesc(ActionEvent event) {
		String input = DescriptionTextArea.getText();
		
		//Create a regular expression, compile it, and check if the input format is valid
		String pattern = "^\\s*[\\w,.?!'-]+(\\s*[\\w,.?!'-]+)*\\s*$";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(input);
		
		if(matcher.matches()) {
			//If the input format is valid, disable error label in case
			ProjDescError.setVisible(false);
			
			//Pull the associated project and update its description
			int chosenProjectIndex = Projects.getSelectionModel().getSelectedIndex();
			Project selectedProject = projectList.get(chosenProjectIndex);
			selectedProject.setDesc(input);
			projectList.set(chosenProjectIndex, selectedProject);
			
			//reset the TextArea
			DescriptionTextArea.setEditable(false);
			DescriptionTextArea.clear();
			UpdateDescButton.setDisable(true);
		} else {
			//If the input format is invalid, notify the user with a Label
			ProjDescError.setVisible(true);
		}
	}
	
	//Saves all Project objects back to the MySQL database
	@FXML
	private void saveAllProjects(ActionEvent event) {
	    //connects with the database
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
	        //Creates a query to update each entry in the Project table
			String updateQuery = "UPDATE Project SET ProjectLCS = ?, Keywords = ?, ProjectDesc = ? WHERE projName = ?";
	        
	        // Iterate through the list of projects
	        for (Project project : projectList) {
	            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
	                // Set the new values for each project
	                preparedStatement.setString(1, project.getProjectLCS());
	                preparedStatement.setString(2, project.getKeywords());
	                preparedStatement.setString(3, project.getDesc());
	                preparedStatement.setString(4, project.getName());

	                // Execute the update
	                preparedStatement.executeUpdate();
	                ProjectsSaved.setVisible(true);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	//When the Definitions Page is opened, the projects are pulled from the database
	private void populateProjects() {
		
		//clear the project list
		Projects.getItems().clear();
		
		//connect to the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            
        	//Pull every entry from the Project Database
        	String query = "SELECT * FROM Project";
            
        	//For each entry in the table
        	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                	while (resultSet.next()) {
                    	
                		//Get all data of a specific entry
                		int projectID = resultSet.getInt("ProjID");
                        String projectName = resultSet.getString("ProjName");
                        String projectDesc = resultSet.getString("ProjectDesc");
                        String projectLCS = resultSet.getString("ProjectLCS");
                        String keywords = resultSet.getString("Keywords");
                        int EE = resultSet.getInt("estimated_effort");
                        int AE = resultSet.getInt("actual_effort");

                        // Create a new Project object
                        Project project = new Project(projectID, projectName, projectDesc, projectLCS, keywords, EE, AE);
                        
                        //Add the Project object to the ComboBox
                        Projects.getItems().add(projectName);
                        
                        //Add the Project object to the project ArrayList
                        projectList.add(project);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	//When the Definitions Page is opened, the Life Cycle Steps are pulled from the database
	private void populateLifeCycleSteps() {
		
		//clear the project list
		LifeCycleSteps.getItems().clear();
		
		//connect to the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            
        	//Pull every entry from the LifeCycleStep Database
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
                        LifeCycleSteps.getItems().add(newLCS.getName());
                        
                        //Add the LifeCycleStep object to the LifeCycleStep ArrayList
                        LCSList.add(newLCS);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	//When the Definitions Page is opened, the Defect Types are pulled from the database
	private void populateDefectTypes() {
		
		//clear the project list
		DefectTypes.getItems().clear();
		
		//connect to the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            
        	//Pull every entry from the DefectName Database
        	String query = "SELECT * FROM DefectName";
            
        	//For each entry in the table
        	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                	while (resultSet.next()) {
                    	
                		//Get all data of a specific entry
                		int DefectID = resultSet.getInt("DefectNameID");
                        String DefectName = resultSet.getString("DefectName");
                        int DefectValue = resultSet.getInt("DefectValue");

                        // Create a new LifeCycleStep object
                        DefectType newDefect  = new DefectType(DefectID, DefectName, DefectValue);
                        
                        //Add the LifeCycleStep object to the ComboBox
                        DefectTypes.getItems().add(newDefect.getName());
                        
                        //Add the LifeCycleStep object to the LifeCycleStep ArrayList
                        DefectTypeList.add(newDefect);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	//When the Definitions Page is opened, the Effort Categories are pulled from the database
	private void populateEffortCategories() {
		
		//clear the project list
		EffortCategories.getItems().clear();
		
		//connect to the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            
        	//Pull every entry from the EffortCategory Database
        	String query = "SELECT * FROM EffortCategories";
            
        	//For each entry in the table
        	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                	while (resultSet.next()) {
                    	
                		//Get all data of a specific entry
                		int ECID = resultSet.getInt("ECID");
                        String ECName = resultSet.getString("ECName");
                        
                        //Create an EffortCategory object
                        EffortCategory newEC = new EffortCategory(ECID, ECName);
                        
                        //Add the Effort Category to the ComboBox
                        EffortCategories.getItems().add(ECName);
                        
                        //Add the LifeCycleStep object to the LifeCycleStep ArrayList
                        ECList.add(newEC);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	//When the Definitions Page is opened, the Deliverables are pulled from the database
	private void populateDeliverables() {
		
		//clear the project list
		Deliverables.getItems().clear();
		
		//connect to the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            
        	//Pull every entry from the EffortCategory Database
        	String query = "SELECT * FROM Deliverables";
            
        	//For each entry in the table
        	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                	while (resultSet.next()) {
                    	
                		//Get all data of a specific entry
                		int DeliverableID = resultSet.getInt("DeliverableID");
                        String DeliverableName = resultSet.getString("DeliverableName");
                        
                        //Create a Deliverable object
                        Deliverable newDeliverable = new Deliverable(DeliverableID, DeliverableName);
                        
                        //Add the Effort Category to the ComboBox
                        Deliverables.getItems().add(DeliverableName);
                        
                        //Add the LifeCycleStep object to the LifeCycleStep ArrayList
                        DeliverableList.add(newDeliverable);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	//When the Definitions Page is opened, the Interruptions are pulled from the database
	private void populateInterruptions() {
		
		//clear the project list
		Interruptions.getItems().clear();
		
		//connect to the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            
        	//Pull every entry from the Interruptions Database
        	String query = "SELECT * FROM Interruptions";
            
        	//For each entry in the table
        	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                	while (resultSet.next()) {
                    	
                		//Get all data of a specific entry
                		int InterruptionID = resultSet.getInt("InterruptionID");
                        String InterruptionName = resultSet.getString("InterruptionName");
                        
                        //Create an Interruption object
                        Interruption newInterruption = new Interruption(InterruptionID, InterruptionName);
                        
                        //Add the Interruptions to the ComboBox
                        Interruptions.getItems().add(InterruptionName);
                        
                        //Add the Interruption object to the LifeCycleStep ArrayList
                        InterruptionList.add(newInterruption);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	//When the Definitions Page is opened, the Plans are pulled from the database
	private void populatePlans() {
		
		//clear the project list
		PlanTypes.getItems().clear();
		
		//connect to the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            
        	//Pull every entry from the Plans Database
        	String query = "SELECT * FROM Plans";
            
        	//For each entry in the table
        	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                	while (resultSet.next()) {
                    	
                		//Get all data of a specific entry
                		int PlanID = resultSet.getInt("PlanID");
                        String PlanName = resultSet.getString("PlanName");
                        
                        //Create a Plan Object
                        Plan newPlan = new Plan(PlanID, PlanName);
                        
                        //Add the Plan to the ComboBox
                        PlanTypes.getItems().add(PlanName);
                        
                        //Add the Plan object to the LifeCycleStep ArrayList
                        PlanList.add(newPlan);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	//When the user selects an item from the Categories ComboBox
	//they are directed to add information about the item they
	//want to add
	private void createNewItem(ActionEvent event) {
		//Find the selected Category from the ComboBox
		int chosenCategory = Categories.getSelectionModel().getSelectedIndex();
		NewItemFormat.setText("");
		//Based on the selection, Show the desired format and allow the user to edit the TextField
		switch(chosenCategory) {
			case 0:
				if(PlanList.size() < 10) {
					NewItemFormat.setText("Enter a Plan Name");
					NewItemInfo.setEditable(true);
					NewItemFormat.setVisible(true);
					CategoryError.setVisible(false);
					AddButton.setDisable(false);
				} else {
					CategoryError.setText("Can't add a new Plan. Please edit an old one.");
					CategoryError.setVisible(true);
					NewItemInfo.setEditable(false);
					NewItemFormat.setVisible(false);
					AddButton.setDisable(true);
				}
				break;
			case 1:
				if(InterruptionList.size() < 10) {
					NewItemFormat.setText("Enter an Interruption Name");
					NewItemInfo.setEditable(true);
					NewItemFormat.setVisible(true);
					CategoryError.setVisible(false);
					AddButton.setDisable(false);
				} else {
					CategoryError.setText("Can't add a new Interruption. Please edit an old one.");
					CategoryError.setVisible(true);
					NewItemInfo.setEditable(false);
					NewItemFormat.setVisible(false);
					AddButton.setDisable(true);
				}
				break;
			case 2:
				if(DefectTypeList.size() < 15) {
					NewItemFormat.setText("(Name | Value)");
					NewItemInfo.setEditable(true);
					NewItemFormat.setVisible(true);
					CategoryError.setVisible(false);
					AddButton.setDisable(false);
				} else {
					CategoryError.setText("Can't add a new Defect Type. Please edit an old one.");
					CategoryError.setVisible(true);
					NewItemInfo.setEditable(false);
					NewItemFormat.setVisible(false);
					AddButton.setDisable(true);
				}
				break;
			case 3:
				if(LCSList.size() < 50) {
					NewItemFormat.setText("(Name | Effort Category | Default Deliverable)");
					NewItemInfo.setEditable(true);
					NewItemFormat.setVisible(true);
					CategoryError.setVisible(false);
					AddButton.setDisable(false);
				} else {
					CategoryError.setText("Can't add a new Life Cycle Step. Please edit an old one.");
					CategoryError.setVisible(true);
					NewItemInfo.setEditable(false);
					NewItemFormat.setVisible(false);
					AddButton.setDisable(true);
				}
				break;
		}
	}
	
    @FXML
    private void AddItem(ActionEvent event) {
		//Find the selected Category from the ComboBox
		int chosenCategory = Categories.getSelectionModel().getSelectedIndex();
		String input = NewItemInfo.getText();
		String pattern;
		Pattern regex;
		Matcher matcher;
		int newItemID;
		//Based on the selection, Create a new object for the selected category and add it
		switch(chosenCategory) {
			case 0:
				//Create a regular expression, compile it, and check if the input format is valid
				pattern = "^\\s*[\\w,]+(\\s[\\w,]+)*\\s*$";
				regex = Pattern.compile(pattern);
				matcher = regex.matcher(input);
				
				if(matcher.matches())
				{
					CategoryError.setVisible(false);
					newItemID = PlanList.size() + 1;
					Plan newPlan = new Plan(newItemID, input);
					PlanList.add(newPlan);
					PlanTypes.getItems().add(newPlan.getName());
					//Add Plan object to the UserStory table
					try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
						String insertQuery = "INSERT INTO Plans (PlanID, PlanName) " +
			                     "VALUES (?, ?)";
		                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
		                    //Set the new values
		                    preparedStatement.setString(1, "" + newPlan.getID());
		                    preparedStatement.setString(2, newPlan.getName());

		                    preparedStatement.executeUpdate();
		                }
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
					
					//Clear and disable the TextField
					NewItemInfo.clear();
					NewItemInfo.setEditable(false);
					NewItemFormat.setVisible(false);
					AddButton.setDisable(true);
				} else {
					CategoryError.setText("Format Error");
					CategoryError.setVisible(true);
				}
				break;
			case 1:
				//Create a regular expression, compile it, and check if the input format is valid
				pattern = "^\\s*[\\w,]+(\\s[\\w,]+)*\\s*$";
				regex = Pattern.compile(pattern);
				matcher = regex.matcher(input);
				
				if(matcher.matches())
				{
					CategoryError.setVisible(false);
					newItemID = InterruptionList.size() + 1;
					Interruption newInt = new Interruption(newItemID, input);
					InterruptionList.add(newInt);
					Interruptions.getItems().add(newInt.getName());
					//Add Interruption object to the UserStory table
					try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
						String insertQuery = "INSERT INTO Interruptions (InterruptionID, InterruptionName) " +
			                     "VALUES (?, ?)";
		                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
		                    //Set the new values
		                    preparedStatement.setString(1, "" + newInt.getID());
		                    preparedStatement.setString(2, newInt.getName());

		                    preparedStatement.executeUpdate();
		                }
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
					
					//Clear and disable the TextField
					NewItemInfo.clear();
					NewItemInfo.setEditable(false);
					NewItemFormat.setVisible(false);
					AddButton.setDisable(true);
				} else {
					CategoryError.setText("Format Error");
					CategoryError.setVisible(true);
				}
				break;
			case 2:
				//Create a regular expression, compile it, and check if the input is valid
				pattern = "^\\s*[\\w,]+(\\s[\\w,]+)*\\s*\\|\\s*\\d+\\s*$";
				regex = Pattern.compile(pattern);
				matcher = regex.matcher(input);
				
				if(matcher.matches())
				{
					CategoryError.setVisible(false);
					//Split the string based on the validated format
					String[] parts = input.split("\\|");
					
					//Get the name and the defect's value
					String DN = parts[0].trim();
					int DV = Integer.parseInt(parts[1].trim());
					
					newItemID = DefectTypeList.size() + 1;
					DefectType newDefectType = new DefectType(newItemID, DN, DV);
					DefectTypeList.add(newDefectType);
					DefectTypes.getItems().add(newDefectType.getName());
					//Add DefectType object to the UserStory table
					try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
						String insertQuery = "INSERT INTO DefectName (DefectNameID, DefectName, DefectValue) " +
			                     "VALUES (?, ?, ?)";
		                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
		                    //Set the new values
		                    preparedStatement.setString(1, "" + newDefectType.getID());
		                    preparedStatement.setString(2, newDefectType.getName());
		                    preparedStatement.setString(3, "" + newDefectType.getValue());

		                    preparedStatement.executeUpdate();
		                }
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
					
					//Clear and disable the TextField
					NewItemInfo.clear();
					NewItemInfo.setEditable(false);
					NewItemFormat.setVisible(false);
					AddButton.setDisable(true);
				} else {
					CategoryError.setText("Format Error");
					CategoryError.setVisible(true);
				}
				break;
			case 3:
				//Create a regular expression, compile it, and check if the input format is valid
				pattern = "^\\s*\\w+(\\s\\w+)*\\s*\\|\\s*\\d+\\s*\\|\\s*\\d+\\s*$";
				regex = Pattern.compile(pattern);
				matcher = regex.matcher(input);
				
				if(matcher.matches())
				{
					CategoryError.setVisible(false);
					//Split the string based on the validated format
					String[] parts = input.split("\\|");
					
					//Get the updated information
					String SN = parts[0].trim();
					int EC = Integer.parseInt(parts[1].trim());
					int D = Integer.parseInt(parts[2].trim());
					newItemID = LCSList.size() + 1;
					
					//Validate that the input works in context, if it doesn't, tell the user how they can fix it
					if(EC > 5 || EC < 1)
					{
						CategoryError.setText("Effort Category out of bounds (1 <= EC <= 5)");
						CategoryError.setVisible(true);
					} else if((EC == 1 || EC == 2 || EC == 3) && (D > 10 || D < 1)) {
						CategoryError.setText("Default Deliverable out of bounds (1 <= D <= 10)");
						CategoryError.setVisible(true);
					} else if(EC == 4 && (D > 15 || D < 1)) {
						CategoryError.setText("Default Deliverable out of bounds (1 <= D <= 15)");
						CategoryError.setVisible(true);
					} else if(EC == 5 && D != 0) {
						CategoryError.setText("Default Deliverable must be 0 with Effort Category 5");
						CategoryError.setVisible(true);
					} else {
						CategoryError.setVisible(false);
						
						//Create new LifeCycleStep object and add it to the ArrayList and ComboBox
						LifeCycleStep newLCS = new LifeCycleStep(SN, newItemID, EC, D);
						LCSList.add(newLCS);
						LifeCycleSteps.getItems().add(newLCS.getName());
						
						//Add LifeCycleStep object to the LifeCycleStep database
						try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
							String insertQuery = "INSERT INTO LifeCycleStep (LCSID, LCSName, LCSEC, LCSD) " +
				                     "VALUES (?, ?, ?, ?)";
			                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
			                    //Set the new values
			                    preparedStatement.setString(1, "" + newLCS.getID());
			                    preparedStatement.setString(2, newLCS.getName());
			                    preparedStatement.setString(3, "" + newLCS.getEC());
			                    preparedStatement.setString(4, "" + newLCS.getD());

			                    preparedStatement.executeUpdate();
			                }
			            } catch (SQLException e) {
			                e.printStackTrace();
			            }
						
						//Clear and disable the TextField
						NewItemInfo.clear();
						NewItemInfo.setEditable(false);
						NewItemFormat.setVisible(false);
						AddButton.setDisable(true);
					}
				} else {
					CategoryError.setText("Format Error");
					CategoryError.setVisible(true);
				}
				break;
		}
    }
	
	//Add a project to historical data in the database and delete it from the Project table
	@FXML
	private void endProject(ActionEvent event) {
		//Get project that will be turned into a user story
		String selectedProjectName = Projects.getValue();
		
		//Mitigate invalid read: make sure a project is selected
		if(selectedProjectName != null && !selectedProjectName.isEmpty()) {
			int chosenProjectIndex = Projects.getSelectionModel().getSelectedIndex();
			Project selectedProject = projectList.get(chosenProjectIndex);
			UserStoryDB newUserStoryDB = new UserStoryDB(selectedProject.getID(), selectedProject.getName(), selectedProject.getDesc(),
												   selectedProject.getEE(), selectedProject.getAE(), 
												   selectedProject.getKeywords());
			//Add UserStoryDB object to the UserStory table
			try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
				String insertQuery = "INSERT INTO UserStory (oldProjID, storyName, storyDesc, estimated_effort, actual_effort, Keywords) " +
	                     "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    //Set the new values
                    preparedStatement.setString(1, "" + newUserStoryDB.getProjID());
                    preparedStatement.setString(2, newUserStoryDB.getName());
                    preparedStatement.setString(3, newUserStoryDB.getDesc());
                    preparedStatement.setString(4, "" + newUserStoryDB.getEE());
                    preparedStatement.setString(5, "" + newUserStoryDB.getAE());
                    preparedStatement.setString(6, newUserStoryDB.getKeywords());

                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
			
			//Delete the project from the Project table
			try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
				String deleteQuery = "DELETE FROM Project WHERE projID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                    //Set the new values
                    preparedStatement.setString(1, "" + selectedProject.getID());

                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
			
			//Reset the Projects ComboBox without the ended project
			projectList.clear();
			Projects.getSelectionModel().clearSelection();
			populateProjects();
			
			//Reset TextFields and notify the user of a successful project end.
			ProjectLCS.clear();
			ProjectLCS.setEditable(false);
			Keywords.clear();
			Keywords.setEditable(false);
			ProjectEndBad.setVisible(false);
			ProjectEndGood.setVisible(true);
			
        } else {
        	//If not, show the user an error occurred with a label
        	ProjectEndGood.setVisible(false);
        	ProjectEndBad.setVisible(true);
        }
    }
	
	//Update all category tables in the database with locally saved data
	private void saveCategories(ActionEvent event) {
	    //connects with the database
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
	        //Creates a query to update each entry in the Plans table
			String updateQuery = "UPDATE Plans SET PlanName = ? WHERE PlanID = ?";
	        
	        // Iterate through the list of plans
	        for (Plan plan : PlanList) {
	            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
	                // Set the new values for each plan
	                preparedStatement.setString(1, plan.getName());
	                preparedStatement.setInt(2, plan.getID());

	                // Execute the update
	                preparedStatement.executeUpdate();
	                ProjectsSaved.setVisible(true);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	    //connects with the database
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
	        //Creates a query to update each entry in the Interruptions table
			String updateQuery = "UPDATE Interruptions SET InterruptionName = ? WHERE InterruptionID = ?";
	        
	        // Iterate through the list of interruptions
	        for (Interruption interruption : InterruptionList) {
	            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
	                // Set the new values for each interruption
	                preparedStatement.setString(1, interruption.getName());
	                preparedStatement.setInt(2, interruption.getID());

	                // Execute the update
	                preparedStatement.executeUpdate();
	                ProjectsSaved.setVisible(true);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	    //connects with the database
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
	        //Creates a query to update each entry in the Interruptions table
			String updateQuery = "UPDATE Deliverables SET DeliverableName = ? WHERE DeliverableID = ?";
	        
	        // Iterate through the list of deliverables
	        for (Deliverable deliverable : DeliverableList) {
	            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
	                // Set the new values for each deliverable
	                preparedStatement.setString(1, deliverable.getName());
	                preparedStatement.setInt(2, deliverable.getID());

	                // Execute the update
	                preparedStatement.executeUpdate();
	                ProjectsSaved.setVisible(true);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	    //connects with the database
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
	        //Creates a query to update each entry in the EffortCategories table
			String updateQuery = "UPDATE EffortCategories SET ECName = ? WHERE ECID = ?";
	        
	        // Iterate through the list of Effort Categories
	        for (EffortCategory EC : ECList) {
	            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
	                // Set the new values for each Effort Category
	                preparedStatement.setString(1, EC.getName());
	                preparedStatement.setInt(2, EC.getID());

	                // Execute the update
	                preparedStatement.executeUpdate();
	                ProjectsSaved.setVisible(true);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	    //connects with the database
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
	        //Creates a query to update each entry in the DefectName table
			String updateQuery = "UPDATE DefectName SET DefectName = ?, DefectValue = ? WHERE DefectNameID = ?";
	        
	        // Iterate through the list of Defect Types
	        for (DefectType defectType : DefectTypeList) {
	            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
	                // Set the new values for each Defect Type
	                preparedStatement.setString(1, defectType.getName());
	                preparedStatement.setInt(2, defectType.getValue());
	                preparedStatement.setInt(3, defectType.getID());

	                // Execute the update
	                preparedStatement.executeUpdate();
	                ProjectsSaved.setVisible(true);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	    //connects with the database
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
	        //Creates a query to update each entry in the LifeCycleStep table
			String updateQuery = "UPDATE LifeCycleStep SET LCSName = ?, LCSEC = ?, LCSD = ? WHERE LCSID = ?";
	        
	        // Iterate through the list of Life Cycle Steps
	        for (LifeCycleStep LCS : LCSList) {
	            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
	                // Set the new values for each Life Cycle Steps
	                preparedStatement.setString(1, LCS.getName());
	                preparedStatement.setInt(2, LCS.getEC());
	                preparedStatement.setInt(3, LCS.getD());
	                preparedStatement.setInt(4, LCS.getID());

	                // Execute the update
	                preparedStatement.executeUpdate();
	                ProjectsSaved.setVisible(true);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
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
	
	//Change scenes to the Planning Poker screen
	@FXML
	private void goToPlanningPoker(ActionEvent event) {
		saveAllProjects(event);
		saveCategories(event);
		try {
			Main m = new Main();
			m.changeScene("PlanningPokerScreen.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void GoToEffortConsole(ActionEvent event) {
		saveAllProjects(event);
		saveCategories(event);
		try {
			Main m = new Main();
			m.changeScene("EffortLoggerMenu.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void GoToEditor(ActionEvent event) {
		saveAllProjects(event);
		saveCategories(event);
		try {
			Main m = new Main();
			m.changeScene("EffortLogEditor.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void GoToDefect(ActionEvent event) {
		saveAllProjects(event);
		saveCategories(event);
		try {
			Main m = new Main();
			m.changeScene("DefectConsole.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void GoToLogs(ActionEvent event) {
		saveAllProjects(event);
		saveCategories(event);
		try {
			Main m = new Main();
			m.changeScene("LogScreen.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}