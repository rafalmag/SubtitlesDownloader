package pl.rafalmag.subtitledownloader.gui;

import javafx.fxml.Initializable;

public abstract class FXMLMainTab implements Initializable {
	protected FXMLMainController fxmlMainController;

	protected void setMainController(FXMLMainController fxmlMainController) {
		this.fxmlMainController = fxmlMainController;
	}
}
