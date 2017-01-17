package pl.rafalmag.subtitledownloader.gui;

import javafx.application.Platform;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class JavaFxUtils {
    public static <V> V invokeInJavaFxThread(Callable<V> callable) throws SubtitlesDownloaderException {
        FutureTask<V> futureTask = new FutureTask<>(callable);
        Platform.runLater(futureTask);
        try {
            return futureTask.get();
        } catch (InterruptedException e) {
            throw new SubtitlesDownloaderException("Could not invoke method, because of " + e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new SubtitlesDownloaderException("Could not invoke method, because of " + e.getCause().getMessage(), e.getCause());
        }
    }
}
