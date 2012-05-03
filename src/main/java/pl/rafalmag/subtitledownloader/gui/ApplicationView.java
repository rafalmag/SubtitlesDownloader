package pl.rafalmag.subtitledownloader.gui;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.collections.ObservableListWrapper;

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
		display.dispose();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlSubtitlesdownloader = new Shell();
		shlSubtitlesdownloader.setSize(450, 300);
		shlSubtitlesdownloader.setText("SubtitlesDownloader");
		shlSubtitlesdownloader.setLayout(new FillLayout(SWT.HORIZONTAL));

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

		// Composite composite = new Composite(shlSubtitlesdownloader,
		// SWT.NONE);
		// composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		FXCanvas fxCanvas = new FXCanvas(shlSubtitlesdownloader, SWT.NONE);
		Scene scene = createJavaFxScene();
		fxCanvas.setScene(scene);

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

	protected Scene createJavaFxScene() {
		/* Create a JavaFX Group node */
		Group group = new Group();

		/* Create a JavaFX button */
		final Button jfxButton = new Button("JFX Button");
		/* Assign the CSS ID ipad-dark-grey */
		jfxButton.setId("ipad-dark-grey");
		/* Add the button as a child of the Group node */
		group.getChildren().add(jfxButton);

		ObservableList<SubtitleObservable> items = new ObservableListWrapper<>(
				new ArrayList<SubtitleObservable>());
		final TableView<SubtitleObservable> table = new TableView<>(items);

		TableColumn<SubtitleObservable, String> firstNameCol = new TableColumn<SubtitleObservable, String>(
				"First Name");
		firstNameCol
				.setCellValueFactory(new PropertyValueFactory<SubtitleObservable, String>(
						"firstName"));

		table.getColumns().add(firstNameCol);

		group.getChildren().add(table);

		/* Create the Scene instance and set the group node as root */
		Scene scene = new Scene(group, Color.rgb(shlSubtitlesdownloader
				.getBackground().getRed(), shlSubtitlesdownloader
				.getBackground().getGreen(), shlSubtitlesdownloader
				.getBackground().getBlue()));
		// /* Attach an external stylesheet */
		// scene.getStylesheets().add("twobuttons/Buttons.css");
		return scene;
	}

	public Shell getShell() {
		return shlSubtitlesdownloader;
	}
}
