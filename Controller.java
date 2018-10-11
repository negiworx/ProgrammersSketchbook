package sample;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    public String database = "";

    DatabaseController dbc = new DatabaseController();

    public TextField searchTextField;
    public TextField codeNameTextField;
    public TextArea consoleTextArea;
    public TextArea codeNotesTextArea;
    public TextArea codeWindowTextArea;
    public ListView allTagsListView;
    public ListView resultsListView;
    public ListView allTagsCodeTagEditorListView;
    public ListView codeTagsCodeTagEditorListView;

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
        if(allTagsListView.getItems().isEmpty() == false) {
            allTagsListView.getItems().clear();
            allTagsCodeTagEditorListView.getItems().clear();
        }
        while(iterator.hasNext()){
            element = iterator.next();
            allTagsListView.getItems().add(element);
            allTagsCodeTagEditorListView.getItems().add(element);
        }
        consoleTextArea.appendText("Successfully retrieved all tags from DB \n");

    }

    public void addTagToCode(){

    }

    public void removeTagFromCode(){

    }

    public void getCodeTags(){

    }

    public void getCodeByTag(){

    }

    public void getCodeByName(){

    }

    public void updateCode(){

    }

    public void addNewTagToDatabase(){

    }

    public void removeTagFromDatabase(){

    }

    public void getAllTagsListSelection(){
        searchTextField.setText((String) allTagsListView.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getTagListFromDatabase();
    }
}
