package sample;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    public String database = "";

    DatabaseController dbc = new DatabaseController();

    String currentNameOfCode;
    String currentIdOfCode;
    List   currentCodeTags = new ArrayList();

    public TextField searchTextField;
    public TextField codeNameTextField;
    public TextField addTagToCodeTextField;
    public TextField removeTagFromCodeTextField;
    public TextField addTagTextField;
    public TextField removeTagTextField;

    public TextArea consoleTextArea;
    public TextArea codeNotesTextArea;
    public TextArea codeWindowTextArea;

    public ListView allTagsListView;
    public ListView resultsListView;
    public ListView allTagsCodeTagEditorListView;
    public ListView codeTagsCodeTagEditorListView;
    public ListView allTagsEditorListView;

    public Button saveButton;
    public Button clearButton;
    public Button newButton;
    public Button deleteButton;

    public void createDatabase(){

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("databaseCreationWindow.fxml"));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load(), 600, 200);
            Stage stage = new Stage();
            stage.setTitle("Add Tags");
            stage.setScene(scene);

            DatabaseCreationWindow con = fxmlLoader.<DatabaseCreationWindow>getController();
            con.initData(dbc);

            stage.show();
        } catch (IOException e) {
            consoleTextArea.appendText(e.getMessage());
        }
    }

    public void createDefaultDatabase(){
        dbc.createDB();
        dbc.initDatabase();
        database = "code.db";
    }

    public void openFileExplorer(){
        File f = new File(System.getProperty("user.dir"));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Database File");
        fileChooser.setInitialDirectory(f);
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("All Files", "*.db*"));
        File selectedFile = fileChooser.showOpenDialog(consoleTextArea.getScene().getWindow());
        if (selectedFile != null) {
            consoleTextArea.appendText("opening" + selectedFile.getName() + "\n");
            database = selectedFile.getName();
            writeToSettingsFile();
            getTagListFromDatabase();
        }
    }

    public void rebuildTheDatabase(){
        dbc.initDatabase();
    }

    public void searchDatabaseForTag(){
        String searchTerm = searchTextField.getText();
        String ID = dbc.getTagIDByName(searchTerm);
        List Tags = dbc.getCodeListUsingTagID(ID);
        Iterator iterator = Tags.iterator();
        Object element;

        if(resultsListView.getItems().isEmpty() == false) {
            resultsListView.getItems().clear();
        }
        while(iterator.hasNext()){
            element = iterator.next();
            resultsListView.getItems().add(element);
        }
    }

    public void getTagListFromDatabase(){
        List tags = dbc.getTags();
        Iterator iterator = tags.iterator();
        Object element;
        dbc.setCurrentDB(database);
        if(allTagsListView.getItems().isEmpty() == false) {
            allTagsListView.getItems().clear();
            allTagsCodeTagEditorListView.getItems().clear();
            allTagsEditorListView.getItems().clear();
        }
        while(iterator.hasNext()){
            element = iterator.next();
            allTagsListView.getItems().add(element);
            allTagsCodeTagEditorListView.getItems().add(element);
            allTagsEditorListView.getItems().add(element);
        }
        consoleTextArea.appendText("Successfully retrieved all tags from " + database + " DB \n");

    }

    public void addTagToCode(){

        String Tag = addTagToCodeTextField.getText();
        String TagID;
        if(dbc.getTagIDByName(Tag) == null){
            dbc.addTagToDatabase(Tag);
            TagID = dbc.getTagIDByName(Tag);
            currentCodeTags.add(TagID);
            dbc.updateCodeTags(currentIdOfCode, currentCodeTags);
        }
        else{
            TagID = dbc.getTagIDByName(Tag);
            currentCodeTags.add(TagID);
            dbc.updateCodeTags(currentIdOfCode, currentCodeTags);
        }
        updateListViews();

    }

    public void removeTagFromCode(){

        List oldList = new ArrayList();
        oldList.addAll(currentCodeTags);
        List newList = new ArrayList();
        Iterator iterator = oldList.iterator();
        String TagToRemove = dbc.getTagIDByName(removeTagFromCodeTextField.getText());
        Object element;
        while(iterator.hasNext()){
            element = iterator.next();
            if(dbc.matches(TagToRemove ,element.toString())){
                consoleTextArea.appendText("Removed the " + removeTagFromCodeTextField.getText() + " tag from the code \n");
            }
            else{
                newList.add(element);
            }
            getTagListFromDatabase();
        }
        currentCodeTags.clear();
        currentCodeTags.addAll(newList);
        dbc.updateCodeTags(currentIdOfCode, currentCodeTags);
        updateListViews();
    }

    public void addNewTagToDatabase(){

        String tag = addTagTextField.getText();
        if(dbc.getTagIDByName(tag) == null){
            dbc.addTagToDatabase(tag);
        }else{
            consoleTextArea.appendText("That tag already exists, please try a different tag name \n");
        }
        getTagListFromDatabase();

    }

    public void removeTagFromDatabase(){

        if(removeTagTextField.getText().contains("UN Tagged")){
            consoleTextArea.appendText("That tag is protected and cannot be removed");
        }else{
            if(removeTagTextField.getLength() > 0){
                consoleTextArea.appendText(dbc.removeTagFromDatabase(removeTagTextField.getText()));
                getTagListFromDatabase();
            }else{
                consoleTextArea.appendText("Please enter in a tag to remove");
            }
        }

    }

    public void updateListViews(){

        List cTags = currentCodeTags;
        Iterator cIterator = cTags.iterator();
        Object cElement;

        if(codeTagsCodeTagEditorListView.getItems().isEmpty() == false){
            codeTagsCodeTagEditorListView.getItems().clear();
        }

        while (cIterator.hasNext()){
            cElement = cIterator.next();
            codeTagsCodeTagEditorListView.getItems().add(dbc.getTagNameByID(cElement.toString()));
        }

        getTagListFromDatabase();
    }

    public void updateCodeEntry(){
        consoleTextArea.appendText(dbc.AddCodeToCode(currentIdOfCode, codeWindowTextArea.getText()));
        consoleTextArea.appendText(dbc.addNotesToCode(currentIdOfCode, codeNotesTextArea.getText()));
    }

    public void addNewCode(){
        String nameOfCode = codeNameTextField.getText();
        String notesOnCode = codeNotesTextArea.getText();
        String code = codeWindowTextArea.getText();

        if(dbc.getCodeIDByName(nameOfCode).length() == 0){
            consoleTextArea.appendText(dbc.addNewCodeToDatabase(nameOfCode,notesOnCode,code));
            enableControls();
            if(searchTextField.getLength() > 0){
                searchDatabaseForTag();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("All Code Must Have Unique Name");
            alert.setHeaderText("Name is invalid");
            alert.setContentText("Please choose a different name for the code snippet.");
            alert.showAndWait();
            consoleTextArea.appendText("Invalid name for code it must be unique! \n");
        }


    }

    public void deleteCode(){
        consoleTextArea.appendText(dbc.deleteCodeEntryByID(currentIdOfCode));

    }

    public void retrieveCodeFromDatabase(){
        try{
            currentNameOfCode = (String)resultsListView.getSelectionModel().getSelectedItem();
            currentIdOfCode = dbc.getCodeIDByName(currentNameOfCode);
            CodeMaster cm = dbc.getCodeFromDatabaseUsingCodeID(currentIdOfCode);
            codeNameTextField.setText(cm.getCodeName());
            codeWindowTextArea.setText(cm.getCode());
            codeNotesTextArea.setText(cm.getCodeNotes());
            currentCodeTags = cm.getCodeTags();
            enableControls();
        }
        catch(Exception e){
            consoleTextArea.appendText(e.getStackTrace().toString() + " " + e.getMessage() + "\n");
        }
        updateListViews();
    }

    public void getAllTagsListSelection(){
        searchTextField.setText((String) allTagsListView.getSelectionModel().getSelectedItem());
        searchDatabaseForTag();
    }

    public void getCodeTagsCodeTagEditorListViewSelection(){
        removeTagFromCodeTextField.setText((String) codeTagsCodeTagEditorListView.getSelectionModel().getSelectedItem());
        searchDatabaseForTag();
    }

    public void getAllTagsCodeTagEditorListViewSelection(){
        addTagToCodeTextField.setText((String) allTagsCodeTagEditorListView.getSelectionModel().getSelectedItem());
        searchDatabaseForTag();
    }

    public void getAllTagsEditorListViewSelection(){
        removeTagTextField.setText((String) allTagsEditorListView.getSelectionModel().getSelectedItem());
        searchDatabaseForTag();
    }

    public void enableControls(){

        saveButton.setDisable(false);
        clearButton.setDisable(false);
        newButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    public void disableControls(){
        saveButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    public void clearFields(){
        searchTextField.clear();
        codeNameTextField.clear();
        addTagToCodeTextField.clear();
        removeTagFromCodeTextField.clear();
        addTagTextField.clear();
        removeTagTextField.clear();
        codeNotesTextArea.clear();
        codeWindowTextArea.clear();
        disableControls();

    }

    public void openSettingsFile(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("settings.txt"));
            database = reader.readLine();
            reader.close();
            dbc.setCurrentDB(database);
        }
        catch (Exception e){
            consoleTextArea.appendText(e.getMessage() + "\n");
        }
        consoleTextArea.appendText(dbc.getCurrentDB() + "\n");
    }

    public void writeToSettingsFile(){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("settings.txt"));
            writer.write(database);
            writer.close();
        }
        catch(Exception e){
            consoleTextArea.appendText(e.getMessage() + "\n");
        }
        getTagListFromDatabase();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openSettingsFile();
        getTagListFromDatabase();
        disableControls();
    }

}
