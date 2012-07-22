package pl.rafalmag.subtitledownloader.subtitles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import javafx.beans.property.DoubleProperty;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class Downloader {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Downloader.class);

	private final Subtitles subtitles;
	private final File movieFile;

	private DoubleProperty progressProperty;

	public Downloader(Subtitles subtitles, File movieFile) {
		this.subtitles = subtitles;
		this.movieFile = movieFile;
	}

	static String getSubtitlesDestinationPath(String movieFilePath,
			String extension) {
		String baseName = FilenameUtils.getBaseName(movieFilePath);
		String parentPath = FilenameUtils.getFullPath(movieFilePath);
		String destinationWithOutExtension = FilenameUtils.concat(parentPath,
				baseName);
		return destinationWithOutExtension + "." + extension;
	}

	public void download() throws SubtitlesDownloaderException {
		String extension = FilenameUtils.getExtension(subtitles.getFileName());
		String destinationPath = getSubtitlesDestinationPath(
				movieFile.getAbsolutePath(), extension);
		try {
			backupExistingSubtitles(destinationPath);
			URL url = new URL(subtitles.getDownloadLink());
			HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();
			long contentLength = httpConn.getContentLengthLong();
			try (InputStream is = new CountingSettingProgressBarInputStream(
					httpConn.getInputStream(), contentLength);
					FileOutputStream fos = new FileOutputStream(destinationPath)) {
				InputStream sourceInputStream;
				try {
					GZIPInputStream gzipInputStream = new GZIPInputStream(is);
					sourceInputStream = gzipInputStream;
				} catch (ZipException e) {
					LOGGER.info("This is not gzip", e);
					sourceInputStream = is;
				}
				try {
					ByteStreams.copy(sourceInputStream, fos);
				} finally {
					Closeables.closeQuietly(sourceInputStream);
				}
			} finally {
				httpConn.disconnect();
			}
		} catch (IOException e) {
			throw new SubtitlesDownloaderException(
					"Could not download substitles " + subtitles, e);
		}

	}

	private void backupExistingSubtitles(String destinationPath)
			throws IOException {
		File destinationFile = new File(destinationPath);
		if (destinationFile.exists()) {
			Files.move(destinationFile.toPath(), new File(destinationPath
					+ ".bak").toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public void setProgressProperty(DoubleProperty progressProperty) {
		this.progressProperty = progressProperty;

	}

	private class CountingSettingProgressBarInputStream extends
			org.apache.commons.io.input.CountingInputStream {
		private final long contentLength;

		public CountingSettingProgressBarInputStream(InputStream in,
				long contentLength) {
			super(in);
			this.contentLength = contentLength;
		}

		@Override
		public int read(byte[] bts) throws IOException {
			int read = super.read(bts);
			progressProperty.set(((double) getByteCount())
					/ (double) contentLength);
			return read;
		}

	}
}
