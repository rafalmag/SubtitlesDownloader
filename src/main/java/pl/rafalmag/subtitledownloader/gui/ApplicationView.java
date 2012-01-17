package pl.rafalmag.subtitledownloader.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationView {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ApplicationView.class);

	protected Shell shlSubtitlesdownloader;
	private final ApplicationController controller;

	public ApplicationView(ApplicationController controller) {
		this.controller = controller;
		createContents();
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		shlSubtitlesdownloader.open();
		shlSubtitlesdownloader.layout();
		while (!shlSubtitlesdownloader.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlSubtitlesdownloader = new Shell();
		shlSubtitlesdownloader.setSize(450, 300);
		shlSubtitlesdownloader.setText("SubtitlesDownloader");

		Menu menu = new Menu(shlSubtitlesdownloader, SWT.BAR);
		shlSubtitlesdownloader.setMenuBar(menu);

		MenuItem fileMenu = new MenuItem(menu, SWT.CASCADE);
		fileMenu.setText("File");

		Menu fileMenuGroup = new Menu(fileMenu);
		fileMenu.setMenu(fileMenuGroup);

		MenuItem openMenuItem = new MenuItem(fileMenuGroup, SWT.NONE);
		openMenuItem.setText("Open");

		MenuItem exitMenuItem = new MenuItem(fileMenuGroup, SWT.NONE);
		exitMenuItem.setText("Exit");

		MenuItem aboutMenuItem = new MenuItem(menu, SWT.NONE);
		aboutMenuItem.setText("About");

		// listeners
		exitMenuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				LOGGER.debug("Exit menu item pressed");
				controller.exitAction();
			}

		});

		aboutMenuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				LOGGER.debug("About menu item pressed");
				controller.aboutAction();
			}

		});

	}

	public Shell getShell() {
		return shlSubtitlesdownloader;
	}
}
