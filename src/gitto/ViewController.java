package gitto;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class ViewController implements Initializable {

    @FXML private TextField textField;

    // commit log
    @FXML private TableView<Commit> tableView;
    @FXML private TableColumn<Commit, String> commitIDColumn;
    @FXML private TableColumn<Commit, String> messageColumn;

    // file tree
    @FXML private TreeTableView<File> fileTreeTableView;
    @FXML private TreeTableColumn<File, String> filenameColumn;
    private Git git;

    private ObservableList<Commit> commits;
    private void setCommits(List<Commit> commits) {
        this.commits = FXCollections.observableList(commits);
        this.tableView.setItems(this.commits);
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        this.commitIDColumn.setCellValueFactory(new PropertyValueFactory<>("commitID"));
        this.messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        this.filenameColumn.setCellValueFactory((file) -> {
            return new ReadOnlyObjectWrapper<>(file.getValue().getValue().getName());
        });
        try {
            this.loadFromPath("./");
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void loadFromPath(String location) throws Exception {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        File gitDir = new File(location + Constants.DOT_GIT);
        Repository repository = builder.setGitDir(gitDir).readEnvironment().findGitDir().build();
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

        this.fileTreeTableView.setRoot(createNode(gitDir));
    }

    @FXML private void loadButtonDidPress(ActionEvent event) {
        String path = this.textField.getText();
        System.out.println(path);
        try {
            this.loadFromPath(path);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    // utility function to build file tree
    // https://gist.github.com/james-d/11177668

    private TreeItem<File> createNode(final File f) {
        return new TreeItem<File>(f) {
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<File>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    File f = getValue();
                    isLeaf = f.isFile();
                }
                return isLeaf;
            }
        };
    }

    private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
        File f = TreeItem.getValue();
        if (f == null || !f.isDirectory()) {
            return FXCollections.emptyObservableList();
        }
        File[] files = f.listFiles();
        if (files == null) {
            return FXCollections.emptyObservableList();
        }
        ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();
        for (File childFile: files) {
            children.add(createNode(childFile));
        }
        return children;
    }

}
