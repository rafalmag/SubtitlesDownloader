package pl.rafalmag.subtitledownloader.utils;

public class ProgressCallbackDummy implements ProgressCallback {

    private volatile double procDone = 0;

    @Override
    public void updateProgress(double procDone) {
        procDone = Math.min(1.0, procDone);
        procDone = Math.max(0.0, procDone);
        this.procDone = procDone;
    }

    @Override
    public void updateProgress(long progress, long max) {
        updateProgress((double) progress / (double) max);
    }

    @Override
    public double getProgress() {
        return procDone;
    }
}
