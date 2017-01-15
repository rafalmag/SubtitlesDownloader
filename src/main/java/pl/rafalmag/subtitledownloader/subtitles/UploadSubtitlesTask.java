package pl.rafalmag.subtitledownloader.subtitles;


import com.google.common.collect.ImmutableSet;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.title.Movie;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class UploadSubtitlesTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadSubtitlesTask.class);

    private static final long STEP_TRY = 20;
    private static final long STEP_UPLOAD = 50;
    private static final long DONE = 100;
    // based on https://en.wikipedia.org/wiki/Subtitle_(captioning)#For_software_video_players
    private static final Set<String> SUBTITLES_EXTENSIONS = ImmutableSet.of("txt", "gsub", "jss", "ttxt",
            "srt", "sub", "pjs", "psb",
            "rt", "smi", "stl", "ssf",
            "ssa", "ass", "usf", "sbv",
            "aqt");

    private final SubtitlesService subtitlesService;
    private final Movie movie;
    private final File movieFile;
    private final BooleanProperty disableProgressBarProperty;

    public UploadSubtitlesTask(SubtitlesService subtitlesService, Movie movie, File movieFile, BooleanProperty disableProgressBarProperty) {
        this.subtitlesService = subtitlesService;
        this.movie = movie;
        this.movieFile = movieFile;
        this.disableProgressBarProperty = disableProgressBarProperty;
    }

    @Override
    protected Void call() {
        updateProgress(0, DONE);

        Optional<File> subtitles = getSubtitlesFile();
        try {
            if (subtitles.isPresent()) {
                subtitlesService.uploadSubtitles(movie, movieFile, subtitles.get());
            }
        } catch (SubtitlesDownloaderException e) {
            String message = "Could not upload subtitles " + subtitles + ", because of " + e.getMessage();
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

    private Optional<File> getSubtitlesFile() {
        String movieBaseName = FilenameUtils.getBaseName(movieFile.getAbsolutePath());
        File folder = movieFile.getParentFile();
        File[] subtitles = folder.listFiles(file -> {
            if (file.isFile()
                    && !file.equals(movieFile)
                    && SUBTITLES_EXTENSIONS.contains(FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase())) {
                String fileBaseName = FilenameUtils.getBaseName(file.getAbsolutePath());
                if (fileBaseName.startsWith(movieBaseName)) {
                    return true;
                }
            }
            return false;
        });
        if (subtitles == null) {
            throw new IllegalStateException(folder + " must be a directory");
        }
        if (subtitles.length == 0) {
            LOGGER.debug("No local subtitles found for " + movieFile);
            return Optional.empty();
        } else if (subtitles.length == 1) {
            LOGGER.debug("Found single subtitle {}", subtitles[0]);
            return Optional.of(subtitles[0]);
        } else {
            LOGGER.debug("Found multiple potential subtitles");
            return choseSubtitlesFile(subtitles);
        }
    }

    private Optional<File> choseSubtitlesFile(File[] subtitles) {
        Map<String, File> map = Arrays.stream(subtitles).collect(Collectors.toMap(File::getName, f -> f));
        String defaultValue = map.keySet().stream().min(Comparator.comparing(String::length)).get();
        final FutureTask<Optional<String>> query = new FutureTask<>(() -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(defaultValue, map.keySet());
            dialog.setTitle("Choose subtitles file");
            dialog.setHeaderText("Choose subtitles for " + movie.getTitle() + " (" + movieFile.getName() + ")");
            dialog.setContentText("Available subtitles:");
            return dialog.showAndWait();
        });
        Platform.runLater(query);
        try {
            Optional<File> file = query.get().map(map::get);
            LOGGER.debug("Chosen subtitles file {}", file);
            return file;
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Could not get subtitle from choice dialog, because of " + e.getMessage(), e);
        }
    }
}
