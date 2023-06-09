package com.svalero.multidescargapsp;

import com.svalero.multidescargapsp.util.R;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppController {

    public TextField tfUrl;
    public TextField tfRoute;
    public String routeText;

    public Button btDownload;
    public TabPane tpDownloads;
    public Label lbRegister;

    private Map<String, DownloadController> allDownloads;

    public AppController() {
        allDownloads = new HashMap<>();
    }

    @FXML
    public void download(ActionEvent event) {
        String urlText = tfUrl.getText();
        routeText = tfRoute.getText();
        tfUrl.clear();
        tfUrl.requestFocus();
        download(urlText, routeText);
    }

    private void download(String url, String route) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getUI("download.fxml"));

            DownloadController downloadController = new DownloadController(url, route);
            loader.setController(downloadController);
            BorderPane borderPane = loader.load();

            String filename = url.substring(url.lastIndexOf("/") + 1);
            tpDownloads.getTabs().add(new Tab(filename, borderPane));

            allDownloads.put(url, downloadController);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    public void stopAllDownloads() {
        for (DownloadController downloadController : allDownloads.values()) {
            downloadController.stop();
        }
        tpDownloads.getTabs().clear();
    }

    public void readDLC() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Do you want to read DLC?");
        alert.showAndWait();
        ButtonType result = alert.getResult();
        routeText = tfRoute.getText();
        if (result == ButtonType.OK) {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            try {
                List<String> urls = Files.readAllLines(file.toPath());
                urls.forEach(urlText -> AppController.this.download(urlText, routeText));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
       }
    }

    public String readDownloadLog() {
        String text = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\Cristina\\IdeaProjects\\2º DAM\\PSP\\multidescargapsp\\multidownloads.log"));
            String total = "";
            String bufRead;
            while ((bufRead = bufferedReader.readLine()) != null) {
                total = total + bufRead;
            }
            text = total;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
    public void register() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getUI("register.fxml"));
            lbRegister = loader.load();

            Scene scene = new Scene(lbRegister);
            stage.setScene(scene);
            stage.setTitle("Register");
            stage.show();
            lbRegister.setText(readDownloadLog());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
