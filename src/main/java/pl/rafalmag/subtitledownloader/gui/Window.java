package pl.rafalmag.subtitledownloader.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class Window {

	protected Shell shlSubtitlesdownloader;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Window window = new Window();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
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

	}
}
