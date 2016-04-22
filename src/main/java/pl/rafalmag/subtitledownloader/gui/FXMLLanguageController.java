package pl.rafalmag.subtitledownloader.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.RunMeMain;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderProperties;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.entities.InterfaceLanguage;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SubtitleLanguage;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

@Singleton
public class FXMLLanguageController implements Initializable {
    @InjectLogger
    private Logger LOG;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    protected ComboBox<InterfaceLanguage> interfaceLanguageCombo;
    private InterfaceLanguage initialInterfaceLanguage;

    @FXML
    protected ComboBox<SubtitleLanguage> subtitlesLanguageCombo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initInterfaceLanguageCombo();
        initSubtitlesLanguageCombo();
    }

    private void initInterfaceLanguageCombo() {
        interfaceLanguageCombo.getItems().addAll(InterfaceLanguage.values());
        initialInterfaceLanguage = InterfaceLanguage.fromLocale(Locale.getDefault());
        interfaceLanguageCombo.getSelectionModel().select(initialInterfaceLanguage);
    }

    private void initSubtitlesLanguageCombo() {
        subtitlesLanguageCombo.getItems().addAll(SubtitleLanguage.getAllLanguages());
    }

    @FXML
    public void okAction(ActionEvent actionEvent) {
        InterfaceLanguage selectedInterfaceLanguage = interfaceLanguageCombo.getSelectionModel().getSelectedItem();
        if (initialInterfaceLanguage != selectedInterfaceLanguage) {
            LOG.debug("New UI language: " + selectedInterfaceLanguage.toString());
            SubtitlesDownloaderProperties.getInstance().setInterfaceLanguage(selectedInterfaceLanguage);
            try {
                RunMeMain.getInstance().reloadView();
            } catch (IOException e) {
                throw new IllegalStateException("Could not change language, as reloading of main window failed, because of " + e.getMessage(), e);
            }
        }
//        LOG.warn(subtitlesLanguageCombo.getSelectionModel().getSelectedItem());

        ((Stage) anchorPane.getScene().getWindow()).close();
        actionEvent.consume();
    }

}
