package com.svalero.multidescargapsp;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class DownloadController implements Initializable {

    public TextField tfUrl;

    public TextField tfRoute;

    public TextField tfSetTime;
    public Label lbStatus;
    public ProgressBar pbProgress;
    private String urlText;
    private String routeText;
    private DownloadTask downloadTask;

    private File file;

    private static final Logger logger = LogManager.getLogger(DownloadController.class);

    public DownloadController(String urlText, String routeText) {
        logger.info("Download " + urlText + " created");
        this.urlText = urlText;
        this.routeText = routeText;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfUrl.setText(urlText);
    }

    @FXML
    public void start(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(urlText.substring(urlText.lastIndexOf("/") + 1));
            fileChooser.setInitialDirectory(new File(routeText));
            File file = fileChooser.showSaveDialog(tfUrl.getScene().getWindow());

            if (file == null)
                return;

            try {
                Thread.sleep(setTimeDownload() * 1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            downloadTask = new DownloadTask(urlText, file);

            pbProgress.progressProperty().unbind();
            pbProgress.progressProperty().bind(downloadTask.progressProperty());

            downloadTask.stateProperty().addListener((observableValue, oldState, newState) -> {
                System.out.println(observableValue.toString());
                if (newState == Worker.State.SUCCEEDED) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Download has finished");
                    alert.show();
                }
                if (newState == Worker.State.CANCELLED) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Download has canceled");
                    alert.show();
                }
            });

            downloadTask.messageProperty().addListener((observableValue, oldValue, newValue) -> lbStatus.setText(newValue));

            new Thread(downloadTask).start();
        } catch (MalformedURLException murle) {
            murle.printStackTrace();
            logger.error("URL not valid", murle.fillInStackTrace());
        }
    }

    @FXML
    public void stop(ActionEvent event) {
        stop();
    }

    public void stop() {
        if (downloadTask != null)
            downloadTask.cancel();

    }

    @FXML
    public void pause (ActionEvent actionEvent) {
        downloadTask.pauseButton(true);
    }

    @FXML
    public void reload (ActionEvent actionEvent) {
        downloadTask.pauseButton(false);
    }

    public int setTimeDownload() {
        if (tfSetTime.getText().equals("")) {
            return 0;
        } else {
            int setTime = Integer.parseInt(tfSetTime.getText());
            return setTime;
        }
    }

}

