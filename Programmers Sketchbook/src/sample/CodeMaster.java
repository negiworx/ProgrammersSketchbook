package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*Just a simple class that is used to pass all the
 * needed data between the database controller and
 * the rest of the program it only has some simple getters and setters
 * and a copy of a function that converts a string to a list.
 */



public class CodeMaster {
    private String CodeID;
    private String CodeName;
    private String Code;
    private String CodeNotes;
    private List   CodeTags;

    CodeMaster(){

    }

    CodeMaster(String CodeID , String CodeName , String Code , String CodeNotes , List CodeTags ){
        this.CodeID = CodeID;
        this.CodeName = CodeName;
        this.CodeNotes = CodeNotes;
        this.CodeTags = CodeTags;
        this.Code = Code;

    }

    public String getCodeID() {
        return CodeID;
    }

    public void setCodeID(String codeID) {
        CodeID = codeID;
    }

    public String getCodeName() {
        return CodeName;
    }

    public void setCodeName(String codeName) {
        CodeName = codeName;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getCodeNotes() {
        return CodeNotes;
    }

    public void setCodeNotes(String codeNotes) {
        CodeNotes = codeNotes;
    }

    public List getCodeTags() {
        return CodeTags;
    }

    public void setCodeTags(List codeTags) {
        CodeTags = codeTags;
    }

    public void addToTags(String TagTOAdd){
        CodeTags.add(TagTOAdd);
    }
/*
    public List convertStingToList(String stringToConvert){
        List list = new ArrayList();
        String pattern = "[0-9a-zA-Z-]{1,999}";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(stringToConvert);
        while(m.find()){
            list.add(m.group());
        }

        return list;
    }*/

}
