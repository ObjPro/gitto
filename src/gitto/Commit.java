package gitto;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Commit {

    private StringProperty commitID;
    public void setCommitID(String value) { commitIDProperty().set(value); }
    public String getCommitID() { return commitIDProperty().get(); }
    public StringProperty commitIDProperty() {
        if (commitID == null) commitID = new SimpleStringProperty(this, "firstName");
        return commitID;
    }

    private StringProperty message;
    public void setMessage(String value) { messageProperty().set(value); }
    public String getMessage() { return messageProperty().get(); }
    public StringProperty messageProperty() {
        if (message == null) message = new SimpleStringProperty(this, "lastName");
        return message;
    }

}

