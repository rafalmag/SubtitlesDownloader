package pl.rafalmag.subtitledownloader.gui;

import com.google.common.collect.ImmutableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.RunMeMain;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderException;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderProperties;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.entities.InterfaceLanguage;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.SubtitleLanguage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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

    @Inject
    private SubtitlesDownloaderProperties subtitlesDownloaderProperties;

    @Inject
    private Session session;

    @Inject
    private RunMeMain runMeMain;

    private SubtitleLanguage initialSubtitlesLanguage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initInterfaceLanguageCombo();
        initSubtitlesLanguageCombo();
    }

    private void initInterfaceLanguageCombo() {
        interfaceLanguageCombo.getItems().addAll(InterfaceLanguage.values());
        initialInterfaceLanguage = InterfaceLanguage.fromLocale(Locale.getDefault());
        interfaceLanguageCombo.getSelectionModel().select(initialInterfaceLanguage);
        new SelectKeyComboBoxListener<>(interfaceLanguageCombo);
    }

    private void initSubtitlesLanguageCombo() {
        List<SubtitleLanguage> subLanguages;
        try {
            subLanguages = session.getSubLanguages();
        } catch (SubtitlesDownloaderException e) {
            LOG.error("Could not init subtitles languages, because of " + e.getMessage(), e);
            subLanguages = ImmutableList.of(SubtitlesDownloaderProperties.DEFAULT_SUBTITLES_LANGUAGE,
                    SubtitlesDownloaderProperties.EXTRA_SUBTITLES_LANGUAGE);
        }
        subtitlesLanguageCombo.getItems().addAll(subLanguages);
        initialSubtitlesLanguage = subtitlesDownloaderProperties.getSubtitlesLanguage();
        subtitlesLanguageCombo.getSelectionModel().select(initialSubtitlesLanguage);
        new SelectKeyComboBoxListener<>(subtitlesLanguageCombo);
    }

    @FXML
    public void okAction(ActionEvent actionEvent) {
        saveInterfaceLanguage();
        saveSubtitlesLanguage();

        ((Stage) anchorPane.getScene().getWindow()).close();
        actionEvent.consume();
    }

    private void saveInterfaceLanguage() {
        InterfaceLanguage selectedInterfaceLanguage = interfaceLanguageCombo.getSelectionModel().getSelectedItem();
        if (initialInterfaceLanguage != selectedInterfaceLanguage) {
            LOG.debug("New UI language: " + selectedInterfaceLanguage.toString());
            subtitlesDownloaderProperties.setInterfaceLanguage(selectedInterfaceLanguage);
            try {
                runMeMain.reloadView();
            } catch (IOException e) {
                throw new IllegalStateException("Could not change language, as reloading of main window failed, because of " + e.getMessage(), e);
            }
        }
    }

    private void saveSubtitlesLanguage() {
        SubtitleLanguage selectedSubtitleLanguage = subtitlesLanguageCombo.getSelectionModel().getSelectedItem();
        if (!initialSubtitlesLanguage.equals(selectedSubtitleLanguage)) {
            LOG.debug("New subtitles language: " + selectedSubtitleLanguage);
            subtitlesDownloaderProperties.setSubtitlesLanguage(selectedSubtitleLanguage);
        }
    }

}
