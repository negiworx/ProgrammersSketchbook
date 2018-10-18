package sample;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseController {

    //Holds the connection
    public Connection con = null;

    //Holds the for name
    private String ForName = "org.sqlite.JDBC";

    //holds the connection for the default database
    private String Connection = "jdbc:sqlite:code.db";

    //Used to hold any errors that might have occurred or other relevant information.
    private String Output = "";


    //Creates the Database using a default name
    public String createDB() {
        try {
            Class.forName(ForName);
            con = DriverManager.getConnection(Connection);
        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return "Failed to open Code Database";
            //  System.exit(0);
        }
        return "code.db";
    }

    //Creates a database with a custom database name
    public String createDB(String DatabaseName) {

        try {
            Class.forName(ForName);
            Connection = "jdbc:sqlite:" + DatabaseName;
            con = DriverManager.getConnection(Connection);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return "Failed to open " + DatabaseName + " Database";
            //  System.exit(0);
        }
        return "";
    }

    public String getCurrentDB(){
        return Connection;
    }

    public void setCurrentDB(String Name){
        Connection = "jdbc:sqlite:" + Name;
    }

    //Adds all the default tables to the database
    public String initDatabase() {
        con = null;
        Statement statement = null;

        try {
            Class.forName(ForName);
            con = DriverManager.getConnection(Connection);

            statement = con.createStatement();
            String Argument = "CREATE TABLE CODE" +
                    "(ID INT PRIMARY KEY NOT NULL," +
                    "CODENAME TEXT NOT NULL," +
                    "Tags TEXT NOT NULL," +
                    "CODE TEXT NOT NULL," +
                    "NOTES TEXT NOT NULL)";
            statement.executeUpdate(Argument);
            statement.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            Connection += "Failed to create code Table " + e.getMessage() + "\n ";
        }

        con = null;
        statement = null;

        try {
            Class.forName(ForName);
            con = DriverManager.getConnection(Connection);

            statement = con.createStatement();
            String Argument = "CREATE TABLE TAGS(ID INT PRIMARY KEY NOT NULL,TAGNAME TEXT NOT NULL,TAGEDCODE TEXT)";

            statement.executeUpdate(Argument);
            statement.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            Connection += "Failed to create Tags Table " + e.getMessage() + "\n ";
        }

        con = null;
        statement = null;

        try {
            Class.forName(ForName);
            con = DriverManager.getConnection(Connection);

            statement = con.createStatement();
            String Argument = "INSERT into TAGS (ID, TAGNAME) values ('1', 'UN TAGGED')";

            statement.executeUpdate(Argument);
            statement.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            Connection += "Failed to create UN TAGGED tag in TAGS Table " + e.getMessage() + "\n ";
        }
        if (Connection.length() > 1) {
            return Connection;
        } else {
            return "Successfully Created All Tables and ";
        }
    }

    //Adds the specified tag to the database
    public String addTagToDatabase(String Tag) {
        con = null;
        Random r = new Random();
        int randomID = r.nextInt() + 1;
        try {
            con = DriverManager.getConnection(Connection);
            PreparedStatement statement = con.prepareStatement
                    ("insert into TAGS values(?,?,?)");
            statement.setInt(1, randomID);
            statement.setString(2, Tag);
            statement.setString(3, "");
            statement.executeUpdate();
            statement.close();
            return "Successfully added the new \"" + Tag + "\" tag to the tag table in the database \n";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add Tag to Database " + e.getMessage() + "\n";
        }
    }

    //Removes the specified tag from the database by using its name
    public String removeTagFromDatabase(String Tag) {
        con = null;
        Random r = new Random();
        try {
            con = DriverManager.getConnection(Connection);
            PreparedStatement statement = con.prepareStatement
                    ("delete from TAGS where TAGNAME = ?");
            statement.setString(1, Tag);
            statement.executeUpdate();
            statement.close();
            return "Successfully Removed the \"" + Tag + "\" from the tag table in the database \n";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to remove tag from database " + e.getMessage() + "\n";
        }
    }

    //Returns the current list of all the tags in the database
    public List getTags() {
        con = null;
        List tagList = new ArrayList();
        try {
            con = DriverManager.getConnection(Connection);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select * from TAGS");
            while (rs.next()) {
                tagList.add(rs.getString("TAGNAME"));
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagList;
    }

    //Creates a new code entry into the database and gives it the default tag of UN TAGGED
    public String addNewCodeToDatabase(String name, String notes, String code) {
        con = null;
        Random r = new Random();
        int randomID = r.nextInt() + 1;
        List codeTags = new ArrayList();
        codeTags.add("1");
        try {
            con = DriverManager.getConnection(Connection);
            PreparedStatement statement = con.prepareStatement
                    ("insert into CODE values(?,?,?,?,?)");
            statement.setInt(1, randomID);
            statement.setString(2, name);
            statement.setString(3, String.valueOf(codeTags));
            statement.setString(4, code);
            statement.setString(5, notes);
            statement.executeUpdate();
            statement.close();
            return "Successfully added the new code snippet to the database. \n";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add code to Database " + e.getMessage() + "\n";
        }
    }

    //Gets a whole code listing from the database using the code ID and returns the result as a Code Master Object
    public CodeMaster getCodeFromDatabaseUsingCodeID(String ID) {
        CodeMaster cm = new CodeMaster();
        con = null;
        try {
            con = DriverManager.getConnection(Connection);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select * from CODE where ID = " + ID);
            cm = new CodeMaster(rs.getString(1), rs.getString(2), rs.getString(4), rs.getString(5), SeparateAll(rs.getString(3)));
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cm;
    }

    //Adds a tag to the specified code entry using the codes ID
    public List AddTagToCode(String ID, String TagID, List TagList) {
        updateCodeTags(ID, TagList);
        return TagList;
    }

    //Removes a tag from the specified code entry using the code ID
    public List RemoveTagFromCode(String ID, List TagList) {
        updateCodeTags(ID, TagList);
        return TagList;
    }

    //Gets called by the add and remove tag functions to change the tags that are assigned to the code.
    public String updateCodeTags(String ID, List TagList) {
        con = null;
        String listtoString = String.valueOf(TagList);
        try {
            con = DriverManager.getConnection(Connection);
            PreparedStatement statement = con.prepareStatement("UPDATE CODE Set Tags = ? where ID = ?");
            statement.setString(1, listtoString);
            statement.setString(2, ID);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Successfully updated the codes tags \n";
    }

    //Reruns a list of code entries that have the specified tag ID
    public List getCodeListUsingTagID(String TagID) {
        con = null;
        List codeList = new ArrayList();
        CodeMaster cm = new CodeMaster();
        try {
            con = DriverManager.getConnection(Connection);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select * from CODE");
            String pattern = "(?<![\\w\\d])" + TagID + "(?![\\w\\d])";
            Pattern p = Pattern.compile(pattern);
            while (rs.next()) {
                List temp = SeparateAll(rs.getString("TAGS"));
                String tags = rs.getString("TAGS");
                Iterator iterator = temp.iterator();
                Object element;
                while (iterator.hasNext()) {
                    element = iterator.next();
                    Matcher m = p.matcher(element.toString());
                    if (m.find()) {
                        codeList.add(rs.getString(2));
                    }
                }
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeList;
    }

    //returns the ID of a tag by passing its name
    public String getTagIDByName(String name) {
        con = null;
        String ID = "";
        String Built = "'" + name + "'";
        try {
            con = DriverManager.getConnection(Connection);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select * from TAGS where TAGNAME =" + Built);
            if (rs.isClosed() == false) {
                ID = rs.getString(1);
            } else {
                return null;
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ID;
    }

    //returns the name of a tag by passing its ID
    public String getTagNameByID(String ID) {
        con = null;
        String name = "";
        String Built = "'" + ID + "'";
        try {
            con = DriverManager.getConnection(Connection);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select * from TAGS where ID =" + Built);
            if (rs.isClosed() == false) {
                name = rs.getString(2);
            } else {
                return null;
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;

    }

    //Returns the ID of a code entry by passing its name
    public String getCodeIDByName(String Name) {
        String Built = "'" + Name + "'";
        String results = "";
        con = null;
        try {
            con = DriverManager.getConnection(Connection);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select * from CODE where CODENAME = " + Built);
            while (rs.next()) {
                results = rs.getString(1);
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    //Adds or updates the notes to a specified code entry
    public String addNotesToCode(String ID, String Notes) {
        con = null;
        String Built = "'" + Notes + "'";
        try {
            con = DriverManager.getConnection(Connection);
            Statement statement = con.createStatement();
            statement.executeUpdate("UPDATE CODE set NOTES =" + Built + " WHERE ID =" + ID);
            statement.close();
            return "Successfully updated notes on code \n";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed to add notes to code" + e.getMessage() + "\n";
        }
    }

    //Adds or updates the code that that is in a code entry by using the codes ID
    public String AddCodeToCode(String ID, String Code) {
        con = null;
        String Built = "'" + Code + "'";
        try {
            con = DriverManager.getConnection(Connection);
            Statement statement = con.createStatement();
            statement.executeUpdate("UPDATE CODE set CODE =" + Built + " WHERE ID =" + ID);
            statement.close();
            return "Successfully updated Code in Database \n";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed to Update code" + e.getMessage() + "\n";

        }
    }

    //Deletes the code entry by passing a code ID
    public String deleteCodeEntryByID(String ID){
        con = null;
        try{
            con = DriverManager.getConnection(Connection);
            Statement statement = con.createStatement();
            statement.executeUpdate("DELETE FROM CODE WHERE ID =" + ID );
            statement.close();
            return "Successfully deleted Code in Database \n";
        }
        catch (Exception e){
            e.printStackTrace();
            return "failed to Update code" + e.getMessage() + "\n";
        }
    }

    //A helper function that returns a true or false if two strings match by using a Regular Expression
    public boolean matches(String toMatch, String MatchIn){
        String pattern = "(?<![\\w\\d])" + toMatch + "(?![\\w\\d])";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(MatchIn);
        if(m.find()){
            return true;
        }
        else{
            return false;
        }
    }

    //Returns a list that contains all the elements of a string separated by using a Regular Expression
    public List SeparateAll(String stringToConvert){
        List list = new ArrayList();
        String pattern = "[0-9a-zA-Z-]{1,999}";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(stringToConvert);
        while(m.find()){
            list.add(m.group());
        }

        return list;
    }

}
