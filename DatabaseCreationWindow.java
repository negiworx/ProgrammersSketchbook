package sample;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DatabaseCreationWindow {
DatabaseController dbc2;
    public TextField newDatabaseCreationTextField;

    public void initData(DatabaseController dbc){
        dbc2 = dbc;
    }

    public void createDatabase(){
        dbc2.createDB(newDatabaseCreationTextField.getText());
        dbc2.initDatabase();

    }


}
