package pl.rafalmag.subtitledownloader;

public interface ProgressCallback {

	void updateProgress(long progress, long max);

	void updateProgress(double procDone);

	double getProgress();
}
