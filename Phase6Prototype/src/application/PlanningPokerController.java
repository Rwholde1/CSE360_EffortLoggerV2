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
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//Controller for the Planning Poker screen
public class PlanningPokerController implements Initializable {

	//Setup SQL credentials and data structures for later useful data
	private static String JDBC_URL;
    private static String USERNAME;
    private static String PASSWORD;
    
    ArrayList<UserStory> HistoricData = new ArrayList<>();
    ArrayList<UserStory> RelevantUserStories = new ArrayList<>();
    ArrayList<String> RelevantUserStoryNames = new ArrayList<>();
	
    @FXML
    private Button AEAvg;

    @FXML
    private Label AvgIndicesIncorrectFormat;

    @FXML
    private Button ClearScreen;

    @FXML
    private Label ComputedAvg;

    @FXML
    private Button DefectLogInfo;

    @FXML
    private Button EEAvg;

    @FXML
    private TextField EEField;

    @FXML
    private Button EffortLogInfo;

    @FXML
    private TextArea InfoBox;

    @FXML
    private Label KeywordIncorrectFormat;

    @FXML
    private TextField KeywordsField;

    @FXML
    private TextArea ProjectDescArea;

    @FXML
    private TextField ProjectNameField;

    @FXML
    private ListView<String> RelevantUserStoryList;

    @FXML
    private Button SaveProject;

    @FXML
    private Button SearchDatabase;

    @FXML
    private TextField SelectedUserStoriesAVG;

    @FXML
    private Button ToDefinitions;

    @FXML
    private Button ToEffortConsole;

    @FXML
    private Button UserStoryInfo;
    
    @FXML
    private Label ProjectFormatError;
    
    private ArrayList<Defect> Defects = new ArrayList<Defect>();

    //Initialize method; overridden since the Controller implements Initializable
    //Used to dynamically set Event handling functions and pull database data on load
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		//Get database credentials
		getCreds();
		//Pull User stories from database
		populateHistoricData();
		KeywordsField.setOnAction(this::queryPastData);
		SearchDatabase.setOnAction(this::queryPastData);
//		RelevantUserStoryList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//
//			@Override
//			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
//							
//			}
//			
//		});
		UserStoryInfo.setOnAction(this::seeUserStoryInfo);
		
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
			String query = "SELECT * FROM Defect";
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
			}catch (SQLException e) {
	            e.printStackTrace();
	            // Handle the exception as needed
	        }
	}
    
	//On load, pulls historical Data from the UserStory table in the database
	private void populateHistoricData() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
        	String query = "SELECT * FROM UserStory";
        	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        		try (ResultSet resultSet = preparedStatement.executeQuery()) {
        			while (resultSet.next()) {
        				int storyID = resultSet.getInt("storyID");
        				int oldProjID = resultSet.getInt("oldProjID");
        				String storyName = resultSet.getString("storyName");
        				String storyDesc = resultSet.getString("storyDesc");
        				int EE = resultSet.getInt("estimated_effort");
        				int AE = resultSet.getInt("actual_effort");
        				String storyKeywords = resultSet.getString("Keywords");
                        
        				ArrayList<String> keywordList = parseKeywords(storyKeywords);
        					
        				//Create a UserStory object and add it to the ArrayList of UserStory objects
        				UserStory  newUserStory = new UserStory(storyID, oldProjID, storyName, storyDesc, EE, AE, keywordList);
        					
        				HistoricData.add(newUserStory);
        			}
        		}
        	}
        } catch (SQLException e) {
        	e.printStackTrace();
        }
	}
	
	//From the string of Keywords on the database, make an ArrayList based on their format
	private static ArrayList<String> parseKeywords(String keywords){
		String[] keywordsArray = keywords.split("\\|");
		String currentKeyword;
		
		if(keywordsArray.length != 0) {
			ArrayList<String> keywordList = new ArrayList<>();
			for(int i = 0; i < keywordsArray.length; i++)
			{
				keywordList.add(keywordsArray[i].trim());
			}
			return keywordList;
		} else {
			ArrayList<String> keywordList = new ArrayList<>();
			return keywordList;
		}
	}
	
	//Return similar user stories based on the number of similar keywords
	private void queryPastData(ActionEvent event) {
		String input = KeywordsField.getText();
		
		//Create a regular expression, compile it, and check if the input format is valid
		String pattern = "^\\s*\\w+(\\s\\w+)*(\\s*\\|\\s*\\w+(\\s\\w+)*)*\\s*$";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(input);
		
		if(matcher.matches()) {
			//If the input format is valid, hide the format error Label
			KeywordIncorrectFormat.setVisible(false);
			
			//Reset the relevance of all UserStories and the relevant user story lists
            RelevantUserStoryList.getItems().clear();
            RelevantUserStoryList.getSelectionModel().clearSelection();
            RelevantUserStoryNames.clear();
            RelevantUserStories.clear();
            resetRelevance();
			
			//Create an ArrayList based on the input
			ArrayList<String> inputKeywords = parseKeywords(input);
			int totalKeywords = inputKeywords.size();
			ArrayList<String> pastStoryKeywords = null;
			double relevance;

			//Check if any words from any past UserStory are the same as the current one
			//more similar words increases the story's relevance
			for(UserStory PastStory : HistoricData) {
				pastStoryKeywords = PastStory.getKeywords();
				relevance = 0.0;
				for(String inputKeyword : inputKeywords) {
					for(String pastKeyword : pastStoryKeywords) {
						if(pastKeyword.equals(inputKeyword)) {
							relevance++;
							break;
						}
					}
				}
				//If the story is relevant enough, add it to the list of relevant user stories
				relevance = relevance / (double) totalKeywords;
				if(relevance >= 0.75) {
					PastStory.setRelevance(4);
					RelevantUserStories.add(PastStory);
					RelevantUserStoryNames.add(PastStory.getName());
				} else if(relevance < 0.75 && relevance >= 0.5) {
					PastStory.setRelevance(3);
					RelevantUserStoryNames.add(PastStory.getName());
					RelevantUserStories.add(PastStory);
				} else if(relevance < 0.5 && relevance >= 0.25) {
					PastStory.setRelevance(2);
				} else {
					PastStory.setRelevance(1);
				}
			}
			
			if(RelevantUserStoryNames.size() > 0) {
				//Show relevant user stories
				RelevantUserStoryList.getItems().addAll(RelevantUserStoryNames);
				SelectedUserStoriesAVG.setEditable(true);
			} else {
				KeywordIncorrectFormat.setText("No Relevant User Stories found");
				KeywordIncorrectFormat.setVisible(true);
			}
		} else {
			//If the input format is invalid, show a Label to notify the user
			KeywordIncorrectFormat.setText("Incorrect Format");
			KeywordIncorrectFormat.setVisible(true);
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
	
	//See the UserStory object info for a relevant User story
	public void seeUserStoryInfo(ActionEvent event) {
		String selectedStoryName = RelevantUserStoryList.getSelectionModel().getSelectedItem();
		
		if(selectedStoryName != null && !selectedStoryName.isEmpty()) {
			int chosenStoryIndex = RelevantUserStoryList.getSelectionModel().getSelectedIndex();
			UserStory selectedUserStory = RelevantUserStories.get(chosenStoryIndex);
			String storyInfo = selectedUserStory.toString();
			
			InfoBox.clear();
			InfoBox.setText(storyInfo);
		}
	}
	
	@FXML
	public void seeEffortLogs(ActionEvent event)
	{
		InfoBox.clear();
		String selectedStoryName = RelevantUserStoryList.getSelectionModel().getSelectedItem();
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
	        String query = "SELECT * FROM EffortLogs";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                while (resultSet.next()) {
	                	String databaseString = resultSet.getString("ProjName");
	                	if (databaseString.equals(selectedStoryName))
	                	{
	                		String ProjName = resultSet.getString("ProjName");
	                		String LifeCycleStep = resultSet.getString("LifeCycleStep");
	                		String EffortCategory = resultSet.getString("EffortCategory");
	                		String SubEffortCategory = resultSet.getString("SubEffortCategory");
	                		String StartTime = resultSet.getString("StartTime");
	                		String EndTime = resultSet.getString("EndTime");
	                        
	                        InfoBox.appendText("==New Effort Log==\n" + "Start Time: " + StartTime + "\nEnd Time: " + EndTime + "\nEffort Category: " + EffortCategory + "\nSub Effort Category: " + SubEffortCategory + "\n\n");
	                        
	                        
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

    @FXML
    private void ComputeAEAVG(ActionEvent event) {
    	String input = SelectedUserStoriesAVG.getText();
    	
		//Create a regular expression, compile it, and check if the input format is valid
		String pattern = "^\\s*\\d+(\\s*\\|\\s*\\d+)*\\s*$";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(input);
		
		if(matcher.matches()) {
			//If the format is correct, hide the Label
			AvgIndicesIncorrectFormat.setVisible(false);
			
			String[] SelectedStories = input.split("\\|");
			ArrayList<Integer> SSIndices = new ArrayList<>();
			for(int i = 0; i < SelectedStories.length; i++) {
				SSIndices.add(Integer.parseInt(SelectedStories[i].trim()) - 1);
			}
	    	boolean badIndexing = false;
	    	int totalAE = 0;
			for(int SSIndex : SSIndices) {
				if(SSIndex < 0) {
					badIndexing = true;
					break;
				} else {
					UserStory currentUS = RelevantUserStories.get(SSIndex);
					totalAE += currentUS.getAE();
				}
			}
			
			if(!badIndexing) {
				//Compute an Average and show the User
				int AEAVG = (int)((double) totalAE / (double) SSIndices.size());
				ComputedAvg.setText("Average : " + AEAVG);
			} else {
				//If the user didn't index their list correctly, display an error label
				AvgIndicesIncorrectFormat.setText("Index from 1");
				AvgIndicesIncorrectFormat.setVisible(true);
			}
		} else {
			//If the format is incorrect, notify the user by displaying a Label
			AvgIndicesIncorrectFormat.setText("Incorrect Format");
			AvgIndicesIncorrectFormat.setVisible(true);
		}
    }

    @FXML
    private void ComputeEEAVG(ActionEvent event) {
    	String input = SelectedUserStoriesAVG.getText();
    	
    	//Create a regular expression, compile it, and check if the input format is valid
		String pattern = "^\\s*\\d+(\\s*\\|\\s*\\d+)*\\s*$";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(input);
		
		if(matcher.matches()) {
			//If the format is correct, hide the Label
			AvgIndicesIncorrectFormat.setVisible(false);
			
			String[] SelectedStories = input.split("\\|");
			ArrayList<Integer> SSIndices = new ArrayList<>();
			for(int i = 0; i < SelectedStories.length; i++) {
				SSIndices.add(Integer.parseInt(SelectedStories[i].trim()) - 1);
			}
	    	boolean badIndexing = false;
	    	int totalEE = 0;
			for(int SSIndex : SSIndices) {
				if(SSIndex < 0) {
					badIndexing = true;
					break;
				} else {
					UserStory currentUS = RelevantUserStories.get(SSIndex);
					totalEE += currentUS.getEE();
				}
			}
			
			if(!badIndexing) {
				//Compute an Average and show the User
				int EEAVG = (int)((double) totalEE / (double) SSIndices.size());
				ComputedAvg.setText("Average : " + EEAVG);
			} else {
				//If the user didn't index their list correctly, display an error label
				AvgIndicesIncorrectFormat.setText("Index from 1");
				AvgIndicesIncorrectFormat.setVisible(true);
			}
		} else {
			//If the format is incorrect, notify the user by displaying a Label
			AvgIndicesIncorrectFormat.setText("Incorrect Format");
			AvgIndicesIncorrectFormat.setVisible(true);
		}
    }
    
    @FXML
    private void createNewProject(ActionEvent event) {
    	//Get the inputs in all TextFields
    	String projectNameInput = ProjectNameField.getText();
    	String projectDescInput = ProjectDescArea.getText();
    	String projectEEInput = EEField.getText();
    	String keywordsInput = KeywordsField.getText();
    	
    	//Check that all of the TextFields have input
    	//If any don't display an error that they don't
    	if(projectNameInput.equals("")) {
    		ProjectFormatError.setText("Please input a Project Name");
    		ProjectFormatError.setVisible(true);
    	} else if(projectDescInput.equals("")) {
    		ProjectFormatError.setText("Please input a Project Description");
    		ProjectFormatError.setVisible(true);
    	} else if(projectEEInput.equals("")) {
    		ProjectFormatError.setText("Please input a Project Effort Estimation");
    		ProjectFormatError.setVisible(true);
    	} else if(keywordsInput.equals("")) {
    		ProjectFormatError.setText("Please input Project Keywords");
    		ProjectFormatError.setVisible(true);
    	} else {
    		//All of the TextFields have input
    		//Create a regular expression, compile it, and check if the project name input format is valid
    		String pattern = "^\\s*[\\w,]+(\\s[\\w,]+)*\\s*$";
    		Pattern regex = Pattern.compile(pattern);
    		Matcher matcher = regex.matcher(projectNameInput);
    		
    		if(matcher.matches()) {
    			//The project name format is valid
    			//Create a regular expression, compile it, and check if the description input format is valid
    			pattern = "^\\s*[\\w,.?!'-]+(\\s*[\\w,.?!'-]+)*\\s*$";
    			regex = Pattern.compile(pattern);
    			matcher = regex.matcher(projectDescInput);
    			
    			if(matcher.matches()) {
    				//The project description format is valid
    				//Attempt to parse an int from the Effort Estimation input
    				try {
    					//Parse it and check that the integer is valid in the planning poker context
    					int EEInput = Integer.parseInt(projectEEInput);
    					if(EEInput == 1 || EEInput == 2 || EEInput == 3 || EEInput == 5 || 
    					   EEInput == 8 || EEInput == 13 || EEInput == 20 || EEInput == 40 || EEInput == 100) {
    						//The effortEstimation is a valid planning poker integer
    						//Create a regular expression, compile it, and check if the keyword input format is valid
    						pattern = "^\\s*\\w+(\\s\\w+)*(\\s*\\|\\s*\\w+(\\s\\w+)*)*\\s*$";
    						regex = Pattern.compile(pattern);
    						matcher = regex.matcher(keywordsInput);
    						
    						if(matcher.matches()) {
    		            		//All inputs are valid, hide the format error label
    							ProjectFormatError.setVisible(false);
    							//Create a new project with all the inputs
    							Project newProject = new Project(projectNameInput, projectDescInput, keywordsInput, EEInput);
    							
    							//Add Project object to the Project table
    							try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
    								String insertQuery = "INSERT INTO Project (ProjName, ProjectDesc, ProjectLCS, Keywords, estimated_effort, actual_effort) " +
    					                     "VALUES (?, ?, ?, ?, ?, ?)";
    				                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
    				                    //Set the new values
    				                    preparedStatement.setString(1, newProject.getName());
    				                    preparedStatement.setString(2, newProject.getDesc());
    				                    preparedStatement.setString(3, newProject.getProjectLCS());
    				                    preparedStatement.setString(4, newProject.getKeywords());
    				                    preparedStatement.setString(5, "" + newProject.getEE());
    				                    preparedStatement.setString(6, "" + newProject.getAE());

    				                    preparedStatement.executeUpdate();
    				                }
    				            } catch (SQLException e) {
    				                e.printStackTrace();
    				            }
    							
    							//clear all areas to prepare for the next round
    							resetForNextRound(event);
    						} else {
    		            		ProjectFormatError.setText("Incorrect Keyword Format");
    		            		ProjectFormatError.setVisible(true);
    						}
    					} else {
        		    		ProjectFormatError.setText("Effort Estimation must be a Planning Poker Integer");
        		    		ProjectFormatError.setVisible(true);
    					}
    				} catch(NumberFormatException e) {
		                e.printStackTrace();
    		    		ProjectFormatError.setText("Effort Estimation must be a Planning Poker Integer");
    		    		ProjectFormatError.setVisible(true);
    				}
    			} else {
            		ProjectFormatError.setText("Incorrect Description Format");
            		ProjectFormatError.setVisible(true);
    			}
    		} else {
        		ProjectFormatError.setText("Incorrect Project Name Format");
        		ProjectFormatError.setVisible(true);
    		}
    	}
    	
    }
    
    @FXML
    private void resetForNextRound(ActionEvent event) {
    	//Get the inputs in all TextFields
    	ProjectNameField.clear();
    	ProjectDescArea.clear();
    	EEField.clear();
    	KeywordsField.clear();
    	ComputedAvg.setText("Average: ");
    	SelectedUserStoriesAVG.clear();
    	SelectedUserStoriesAVG.setEditable(false);
    	InfoBox.clear();
    	RelevantUserStoryList.getItems().clear();
    	RelevantUserStoryList.getSelectionModel().clearSelection();
    	RelevantUserStoryNames.clear();
    	RelevantUserStories.clear();
    	resetRelevance();
    }
    
    private void resetRelevance() {
		for(UserStory PastStory : HistoricData) {
			PastStory.setRelevance(0);
		}
    }
    
	//Change scenes to the Planning Poker screen
	@FXML
	private void goToDefinitions(ActionEvent event) {
		try {
			Main m = new Main();
			m.changeScene("ProjectScreen.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void GoToEffortConsole(ActionEvent event) {
		try {
			Main m = new Main();
			m.changeScene("EffortLoggerMenu.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void seeDefectLogs(ActionEvent event)
	{
		String CurrentUserStory = RelevantUserStoryList.getSelectionModel().getSelectedItem();
		InfoBox.clear();
		String DefectInfo = "";
		for(int i = 0; i < Defects.size(); i++) {
			if(Defects.get(i).GetProjName().equals(CurrentUserStory)) {
				DefectInfo += "" + Defects.get(i).GetDefectName() + " - " + Defects.get(i).GetDefectStatus() + "\n";
				DefectInfo += Defects.get(i).GetDefectDesc() + "\n";
			}
		}
		InfoBox.setText(DefectInfo);
		
	}
	
	
}