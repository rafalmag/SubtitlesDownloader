package pl.rafalmag.subtitledownloader.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AboutDialog extends Dialog {

	private static final Logger LOGGER = Logger.getLogger(AboutDialog.class);

	protected static final String url = "http://www.opensubtitles.org/";

	protected Shell shell;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public AboutDialog(Shell parent, int style) {
		super(parent, style);
		setText("About SubtitlesDownloader");
	}

	public void open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true,
				1, 1));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		Canvas canvas = new Canvas(composite, SWT.NONE);
		// canvas.setBounds(0, 0, 150 + 20, 50 + 20);

		Label lblNewLabel = new Label(composite, SWT.WRAP);
		lblNewLabel
				.setText("Subtitles service powered by www.OpenSubtitles.org");
		// lblNewLabel.setBounds(0, 0, 300 - 150 + 20, 50 + 20);

		Button exitButton = new Button(shell, SWT.NONE);
		exitButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		exitButton.setText("Exit");

		// listeners

		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent event) {
				Image image = null;
				try {
					image = new Image(
							shell.getDisplay(),
							AboutDialog.class
									.getResourceAsStream("/opensubtitles-logo-transparent.png"));

				} catch (Exception e) {
					LOGGER.error(
							"Could not load opensubtitles image because of "
									+ e.getMessage(), e);
				}
				event.gc.drawImage(image, 10, 10);
				image.dispose();
			}
		});

		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				try {
					final URI uri = new URI(url);
					Desktop.getDesktop().browse(uri);
				} catch (IOException | URISyntaxException e) {
					LOGGER.error("Could not open URL " + url + " because of "
							+ e.getMessage(), e);
				}
			}
		});

		exitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

	}
}
