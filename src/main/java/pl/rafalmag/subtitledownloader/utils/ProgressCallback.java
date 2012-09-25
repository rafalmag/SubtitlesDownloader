package pl.rafalmag.subtitledownloader.utils;

public interface ProgressCallback {

	void updateProgress(long progress, long max);

	void updateProgress(double procDone);

	double getProgress();
}
