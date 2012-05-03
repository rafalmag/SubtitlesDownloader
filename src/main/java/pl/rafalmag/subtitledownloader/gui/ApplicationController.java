package pl.rafalmag.subtitledownloader.gui;

import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ApplicationController.class);

	private final ApplicationView applicationView;

	// private final ApplicationModel applicationModel; //TODO

	public ApplicationController() {
		// applicationModel = new ApplicationModel();
		applicationView = new ApplicationView(this);
	}

	/**
	 * Initializes viewer and displays GUI.
	 */
	public void initAndStart() {
		applicationView.open();
	}

	/**
	 * Exit action which closes application.
	 */
	public final void exitAction() {
		LOGGER.info("Bye");
		System.exit(0);
	}

	public void aboutAction() {
		AboutDialog aboutDialog = new AboutDialog(applicationView.getShell(),
				SWT.DIALOG_TRIM);
		aboutDialog.open();
	}
}
