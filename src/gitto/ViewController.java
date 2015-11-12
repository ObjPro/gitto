package gitto;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class ViewController implements Initializable {

    @FXML private TextField textField;
    @FXML private TableView<Commit> tableView;
    @FXML private TableColumn<Commit, String> commitIDColumn;
    @FXML private TableColumn<Commit, String> messageColumn;

    private Git git;

    private ObservableList<Commit> commits;
    private void setCommits(List<Commit> commits) {
        this.commits = FXCollections.observableList(commits);
        this.tableView.setItems(this.commits);
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        this.commitIDColumn.setCellValueFactory(new PropertyValueFactory<>("commitID"));
        this.messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        try {
            this.initializeGit("./");
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void initializeGit(String location) throws Exception {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(location + Constants.DOT_GIT)).readEnvironment().findGitDir().build();
        this.git = new Git(repository);

        Integer count = 10;
        ObjectId head = repository.resolve(Constants.HEAD);
        Iterable<RevCommit> revCommits = git.log().add(head).setMaxCount(count).call();
        List<Commit> commits = new ArrayList<>();
        for (RevCommit revCommit: revCommits) {
            Commit commit = new Commit();
            commit.setCommitID(revCommit.getId().toString());
            commit.setMessage(revCommit.getFullMessage());
            commits.add(commit);
        }
        this.setCommits(commits);
    }

    @FXML private void loadGitFromPath(ActionEvent event) {
        String path = this.textField.getText();
        System.out.println(path);
        try {
            this.initializeGit(path);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

}
