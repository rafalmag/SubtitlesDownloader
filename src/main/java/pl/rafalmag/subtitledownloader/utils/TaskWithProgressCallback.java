package pl.rafalmag.subtitledownloader.utils;

import javafx.concurrent.Task;

public abstract class TaskWithProgressCallback<T> extends Task<T> implements ProgressCallback {

    @Override
    public void updateProgress(long progress, long max) {
        super.updateProgress(progress, max);
    }

    @Override
    public void updateProgress(double procDone) {
        long multiplier = 1000;
        updateProgress((long) (procDone * multiplier), 1000);
    }

}
