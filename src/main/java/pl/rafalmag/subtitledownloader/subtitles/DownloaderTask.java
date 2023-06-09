package pl.rafalmag.subtitledownloader.subtitles;

import com.google.common.io.ByteStreams;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.concurrent.CancellationException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public class DownloaderTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloaderTask.class);

    private static final long STEP_PREPARE = 30;
    private static final long STEP_AFTER_BACKUP = 40;
    private static final long STEP_DOWNLOAD = 50;
    private static final long DONE = 100;

    private final Subtitles subtitles;
    private final File movieFile;

    private final BooleanProperty disableProgressBarProperty;

    public DownloaderTask(Subtitles subtitles, File movieFile, BooleanProperty disableProgressBarProperty) {
        this.subtitles = subtitles;
        this.movieFile = movieFile;
        this.disableProgressBarProperty = disableProgressBarProperty;
    }

    static Path getSubtitlesDestinationPath(String movieFilePath, String extension) {
        String baseName = FilenameUtils.getBaseName(movieFilePath);
        String parentPath = FilenameUtils.getFullPath(movieFilePath);
        return Paths.get(parentPath, baseName + "." + extension);
    }

    @Override
    protected Void call() {
        updateProgress(0, DONE);
        String extension = FilenameUtils.getExtension(subtitles.getFileName());
        Path destinationPath = getSubtitlesDestinationPath(movieFile.getAbsolutePath(), extension);
        try {
            updateProgress(STEP_PREPARE, DONE);
            backupExistingSubtitles(destinationPath);
            updateProgress(STEP_AFTER_BACKUP, DONE);
            URL url = new URL(subtitles.getDownloadLink());
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            long contentLength = httpConn.getContentLengthLong();
            try (InputStream is = getInputStream(httpConn, contentLength);
                 FileOutputStream fos = new FileOutputStream(destinationPath.toFile())) {
                updateProgress(STEP_DOWNLOAD, DONE);
                ByteStreams.copy(is, fos);
                LOGGER.info("Subtitles " + destinationPath.getFileName() + " downloaded");
            } finally {
                httpConn.disconnect();
            }
        } catch (Exception e) {
            String message = "Could not download subtitles " + subtitles + ", because of " + e.getMessage();
            LOGGER.error(message, e);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(message);
                alert.showAndWait();
            });
        } finally {
            updateProgress(DONE, DONE);
            Platform.runLater(() -> disableProgressBarProperty.set(true));
        }
        return null;
    }

    private InputStream getInputStream(HttpURLConnection httpConn, long contentLength) throws IOException {
        InputStream inputStream = new CountingSettingProgressBarInputStream(httpConn.getInputStream(), contentLength);
        try {
            return new GZIPInputStream(inputStream);
        } catch (ZipException e) {
            LOGGER.info("Url (" + subtitles.getDownloadLink() + ") does not lead to gzip archive", e);
            return inputStream;
        }
    }

    private void backupExistingSubtitles(Path destinationPath) throws IOException {
        try {
            Files.move(destinationPath,
                    Paths.get(destinationPath.toString() + ".bak"),
                    StandardCopyOption.REPLACE_EXISTING,
                    LinkOption.NOFOLLOW_LINKS);
        } catch (NoSuchFileException e) {
            // its oki
            LOGGER.debug("Nothing to backup, because no such file: " + destinationPath);
        }
    }

    private class CountingSettingProgressBarInputStream extends org.apache.commons.io.input.CountingInputStream {

        private final long contentLength;

        public CountingSettingProgressBarInputStream(InputStream in, long contentLength) {
            super(in);
            this.contentLength = contentLength;
        }

        @Override
        public int read(byte[] bts) throws IOException {
            if (isCancelled()) {
                throw new CancellationException("Due to timeout - download subtitles thread cancelled");
            }
            int read = super.read(bts);
            double downloadProgress = ((double) getByteCount()) / (double) contentLength;
            updateProgress(STEP_DOWNLOAD + (long) (STEP_DOWNLOAD * downloadProgress), DONE);
            return read;
        }
    }
}
