package gitto;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class ViewController implements Initializable {

    @FXML private TextField textField;
    @FXML private Pane      dropPane;

    private GitController controller;

    @FXML
    private void commitButtonDidPush() {
        controller.commit();
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        controller = GitController.sharedController();

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
                File dir = file.isDirectory() ? file : file.getParentFile();
                File repository = controller.findRepository(dir);
                if (repository == null) {
                    controller.init(dir);
                }
                controller.add(file);

                this.textField.setText(controller.getCurrentRepository().getParent() + "/");
            });
            event.setDropCompleted(true);
        });
    }

}
