package pl.rafalmag.subtitledownloader.subtitles;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Hyperlink;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderProperties;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.gui.JavaFxUtils;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovie;
import pl.rafalmag.subtitledownloader.opensubtitles.CheckMovieSubtitles;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SearchSubtitlesResult;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SubtitleLanguage;
import pl.rafalmag.subtitledownloader.title.Movie;
import pl.rafalmag.subtitledownloader.utils.NamedCallable;
import pl.rafalmag.subtitledownloader.utils.ProgressCallback;
import pl.rafalmag.subtitledownloader.utils.Utils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.DeflaterOutputStream;

@Singleton
public class SubtitlesService {

    @InjectLogger
    private Logger LOG;

    @Inject
    private Session session;

    @Inject
    private CheckMovieSubtitles checkMovieSubtitles;

    @Inject
    private CheckMovie checkMovie;

    @Inject
    private SubtitlesDownloaderProperties subtitlesDownloaderProperties;

    private final static ExecutorService EXECUTOR = Executors
            .newCachedThreadPool(new BasicThreadFactory.Builder().daemon(true)
                    .namingPattern("Subtitle-%d").build());

    public SortedSet<Subtitles> getSubtitles(Movie movie, File movieFile, long timeoutMs,
                                             ProgressCallback progressCallback)
            throws InterruptedException {
        LOG.debug("search subtitles for {} with timeout {}ms", movie, timeoutMs);
        Callable<List<Subtitles>> callable = new NamedCallable<>(
                "-FromOpenSub", () -> getSubtitlesFromOpenSubtitles(movie, movieFile, timeoutMs));
        Collection<List<Subtitles>> solve = Utils.solve(EXECUTOR,
                ImmutableList.of(callable), timeoutMs, progressCallback);

        Supplier<TreeSet<Subtitles>> supplier = () -> new TreeSet<>(Collections.reverseOrder());
        return StreamSupport.stream(solve.spliterator(), false)
                .flatMap(i -> StreamSupport.stream(i.spliterator(), false))
                .collect(Collectors.toCollection(supplier));
    }

    protected List<Subtitles> getSubtitlesFromOpenSubtitles(Movie movie, File movieFile, long timeoutMs)
            throws SubtitlesDownloaderException, InterruptedException {
        List<SearchSubtitlesResult> subtitlesFromOpenSubtitles = checkMovieSubtitles
                .getSubtitles(movie, movieFile, timeoutMs);
        return subtitlesFromOpenSubtitles.stream().map(Subtitles::new).collect(Collectors.toList());
    }

    public Optional<Alert> uploadSubtitles(UploadSubtitlesTask task, File subtitles) throws SubtitlesDownloaderException, ExecutionException, InterruptedException {
        String movieFileName = task.getMovieFile().getName();
        String subtitleMd5Hash = md5(subtitles);
        String movieHash = checkMovie.getHashCode(task.getMovieFile());
        long movieSizeByte = checkMovie.getByteSize(task.getMovieFile());
        task.afterMovieHash();
        String subtitleFileName = subtitles.getName();
        boolean alreadyInDb = session.tryUploadSubtitles(
                subtitleMd5Hash,
                subtitleFileName,
                movieHash,
                movieSizeByte,
                movieFileName);
        task.afterTryUploadSubtitles();
        if (alreadyInDb) {
            LOG.info("Subtitles {} already in OpenSubtitles db", subtitles);
            return Optional.of(JavaFxUtils.invokeInJavaFxThread(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Subtitles already present in OpenSubtitles database");
                alert.setContentText("Subtitles already present in OpenSubtitles database");
                return alert;
            }));
        } else {
            String subtitleContent = gzip(subtitles);
            Optional<String> subtitleLanguageId = getSubtitleLanguageId(subtitles, subtitleMd5Hash, subtitleContent);
            task.afterGettingSubtitlesLanguageId();
            LOG.debug("SubtitleLanguageId = {} for subtitles {}", subtitleLanguageId, subtitles);
            if (subtitleLanguageId.isPresent()) {
                String idMovieImdb = Integer.toString(task.getMovie().getImdbId());
                String movieReleaseName = FilenameUtils.getBaseName(movieFileName);
                Optional<String> url = session.uploadSubtitles(
                        idMovieImdb,
                        movieReleaseName,
                        subtitleLanguageId.get(),
                        subtitleMd5Hash,
                        subtitleFileName,
                        movieHash,
                        movieSizeByte,
                        movieFileName,
                        subtitleContent);
                if (url.isPresent()) {
                    return Optional.of(JavaFxUtils.invokeInJavaFxThread(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("New subtitles uploaded");
                        alert.setContentText("New subtitles uploaded");
                        Hyperlink hyperlink = new Hyperlink(url.get());
                        hyperlink.setOnAction(event -> {
                            try {
                                URI uri = new URI(hyperlink.getText());
                                Desktop.getDesktop().browse(uri);
                            } catch (IOException | URISyntaxException e) {
                                LOG.error("Could not open url, because of " + e.getMessage(), e);
                            }
                        });
                        alert.getDialogPane().setExpanded(true);
                        alert.getDialogPane().setExpandableContent(hyperlink);
                        return alert;
                    }));
                } else {
                    return Optional.of(JavaFxUtils.invokeInJavaFxThread(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Subtitles already present in OpenSubtitles database");
                        alert.setContentText("Subtitles already present in OpenSubtitles database");
                        return alert;
                    }));
                }
            } else {
                LOG.info("Selecting language for subtitles {} aborted", subtitles);
                return Optional.empty();
            }
        }
    }

    private Optional<String> getSubtitleLanguageId(File subtitles, String subtitleMd5Hash, String subtitleContent) throws SubtitlesDownloaderException {
        @Nullable
        String subtitlesDetectedLanguage = session.detectLanguage(subtitleContent, subtitleMd5Hash);
        SubtitleLanguage subtitlesLanguageSetInUI = subtitlesDownloaderProperties.getSubtitlesLanguage();
        String subtitleLanguageIdSetInUI = subtitlesLanguageSetInUI.getId();
        if (subtitleLanguageIdSetInUI.equals(subtitlesDetectedLanguage)) {
            return Optional.of(subtitleLanguageIdSetInUI);
        } else {
            LOG.debug("subtitleLanguageIdSetInUI {} and subtitlesDetectedLanguage {} does not match", subtitlesDetectedLanguage, subtitleLanguageIdSetInUI);
            Optional<SubtitleLanguage> subtitleLanguage = JavaFxUtils.invokeInJavaFxThread(() -> {
                List<SubtitleLanguage> subLanguages = session.getSubLanguages();
                SubtitleLanguage defaultValue = subLanguages
                        .stream()
                        .filter(l -> l.getId().equals(subtitlesDetectedLanguage))
                        .findFirst()
                        .orElse(subtitlesLanguageSetInUI);
                ChoiceDialog<SubtitleLanguage> dialog = new ChoiceDialog<>(defaultValue, subLanguages);
                dialog.setTitle("Choose subtitles language");
                dialog.setHeaderText("Choose subtitles language for " + subtitles);
                dialog.setContentText("Language:");
                return dialog.showAndWait();
            });
            LOG.debug("Chosen subtitles language {}", subtitleLanguage);
            return subtitleLanguage.map(SubtitleLanguage::getId);
        }
    }

    @VisibleForTesting
    String md5(File file) throws SubtitlesDownloaderException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return DigestUtils.md5Hex(fis);
        } catch (IOException e) {
            throw new SubtitlesDownloaderException("Could not calculate md5 for " + file + ", because of " + e.getMessage(), e);
        }
    }

    @VisibleForTesting
    String gzip(File file) throws SubtitlesDownloaderException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream is = new FileInputStream(file);
             Base64OutputStream base64OutputStream = new Base64OutputStream(byteArrayOutputStream, true, -1, null);
             DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(base64OutputStream)
        ) {
            ByteStreams.copy(is, deflaterOutputStream);
            byteArrayOutputStream.close();
        } catch (IOException e) {
            throw new SubtitlesDownloaderException("Could not gzip " + file + ", because of " + e.getMessage(), e);
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return new String(bytes, Charsets.UTF_8);
    }

}
