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
                String filePath = file.toString();
                if (this.git == null) {
                    loadGitFromPath(filePath);
                }
                addFile(file);
            });
            event.setDropCompleted(true);
        });
    }

    private void loadGitFromPath(String location) {
        File file = new File(location);
        File dir = file.isDirectory() ? file : file.getParentFile();

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        File gitPath = Paths.get(dir.getPath(), Constants.DOT_GIT).toFile();
        builder.findGitDir(gitPath);

        System.out.println(gitPath.getAbsolutePath());

        Repository repository;
        try {
            if (builder.getGitDir() == null) { // .gitがみつからない
                repository = FileRepositoryBuilder.create(gitPath);
            } else { // .gitがみつかった
                repository = builder.build();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        this.textField.setText(repository.getDirectory().getParent());
        this.git = new Git(repository);
    }

    private void addFile(File file) {
        // TODO: implementation
    }

}
