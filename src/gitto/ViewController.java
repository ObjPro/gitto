package gitto;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.util.Date;

public class ViewController implements Initializable {

    @FXML private TextField textField;
    @FXML private Pane      dropPane;

    private Git git;

    @Override public void initialize(URL location, ResourceBundle resources) {
        dropPane.addEventHandler(DragEvent.DRAG_OVER, (DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });

        dropPane.addEventHandler(DragEvent.DRAG_DROPPED, (DragEvent event) -> {
            Dragboard db = event.getDragboard();
            db.getFiles().stream().findFirst().ifPresent((File file) -> {
                loadGit(file);
                addFile(file);
            });
            event.setDropCompleted(true);
        });
    }

    private void loadGit(File file) {
        // .gitのあるディレクトリ
        File dir = file.isDirectory() ? file : file.getParentFile();

        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        // .gitのパス
        File gitPath = Paths.get(dir.getPath(), Constants.DOT_GIT).toFile();
        builder.findGitDir(gitPath);

        System.out.println(gitPath.getAbsolutePath());

        try {
            if (builder.getGitDir() == null) { // .gitがない
                this.git = new Git(FileRepositoryBuilder.create(gitPath));
                gitInit(dir);
            } else { // .gitがある
                this.git = new Git(builder.build());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        this.textField.setText(dir.getPath());
    }

    private void initTargetGit(){
        this.git = null;
    }

    private void gitInit(File dir){
        try {
            this.git.init().setDirectory(dir).setBare(false).call();
            System.out.println("git init");
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    private void addFile(File dir) {
        try {
            this.git.add().addFilepattern(dir.getAbsolutePath()).call();
            System.out.println("git add "+ dir);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    private void commitGit(){
        try{
            String commitMessage = new Date().toString();
            this.git.commit().setMessage(commitMessage).call();
            System.out.println("git commit -m "+ commitMessage);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }
}
