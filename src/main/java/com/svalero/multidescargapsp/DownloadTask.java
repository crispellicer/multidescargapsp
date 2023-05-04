package com.svalero.multidescargapsp;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadTask extends Task<Integer> {

    private URL url;
    private File file;
    private boolean paused;
    private static final Logger logger = LogManager.getLogger(DownloadController.class);

    public DownloadTask(String urlText, File file) throws MalformedURLException {
        this.url = new URL(urlText);
        this.file = file;
    }

    @Override
    protected synchronized Integer call() throws Exception {

        logger.trace("Download " + url.toString() + " started");
        updateMessage("Conecting with server...");

        URLConnection urlConnection = url.openConnection();
        double fileSize = urlConnection.getContentLength();
        double megaSize = fileSize / 1048576;

        BufferedInputStream in = new BufferedInputStream(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        int totalRead = 0;
        double downloadProgress = 0;

        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {

            while (paused){
                try {
                    wait(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            notifyAll();


            downloadProgress = ((double) totalRead / fileSize);
            updateProgress(downloadProgress, 1);
            updateMessage(Math.round(downloadProgress * 100) + " %\t\t\t\t" + Math.round(downloadProgress*megaSize) + " of " + Math.round(megaSize) + "MB");

            fileOutputStream.write(dataBuffer, 0, bytesRead);
            totalRead += bytesRead;

            if (isCancelled()) {
                logger.trace("Download " + url.toString() + " cancelled");
                return null;
            }
        }

        updateProgress(1, 1);
        updateMessage("100 %");

        logger.trace("Download " + url.toString() + " has finished");
        return null;
    }

    public void pauseButton (boolean paused) {
        this.paused = paused;
    }
}
