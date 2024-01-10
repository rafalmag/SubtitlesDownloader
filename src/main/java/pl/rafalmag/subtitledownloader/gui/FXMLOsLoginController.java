package pl.rafalmag.subtitledownloader.gui;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import pl.rafalmag.subtitledownloader.SubtitlesDownloaderProperties;
import pl.rafalmag.subtitledownloader.annotations.InjectLogger;
import pl.rafalmag.subtitledownloader.opensubtitles.Session;
import pl.rafalmag.subtitledownloader.opensubtitles.SessionException;
import pl.rafalmag.subtitledownloader.opensubtitles.entities.LoginAndPassword;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

@Singleton
public class FXMLOsLoginController implements Initializable {

    private final static String REGISTER_URL = "http://www.opensubtitles.org/newuser";

    @InjectLogger
    private Logger LOG;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    protected TextField loginField;

    @FXML
    protected PasswordField passwordField;

    @Inject
    private SubtitlesDownloaderProperties subtitlesDownloaderProperties;

    @Inject
    private Session session;

    @Inject
    private HostServices hostServices;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void okAction(ActionEvent actionEvent) {
        String passwordMd5 = getMd5(passwordField.getText());
        LoginAndPassword loginAndPassword = new LoginAndPassword(loginField.getText(), passwordMd5);
        try {
            session.login(loginAndPassword);
            subtitlesDownloaderProperties.setLoginAndPassword(loginAndPassword);
            ((Stage) anchorPane.getScene().getWindow()).close();
        } catch (SessionException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        actionEvent.consume();
    }

    private String getMd5(String password) {
        if (Strings.isNullOrEmpty(password)) {
            return "";
        }
        try {
            return new String(Hex.encodeHex(MessageDigest.getInstance("MD5").digest(password.getBytes("UTF-8"))));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not calculate md5, because of " + e.getMessage(), e);
        }
    }

    public void register(ActionEvent actionEvent) {
        LOG.debug("register");
        try {
            final URI uri = new URI(REGISTER_URL);
            hostServices.showDocument(uri.toString());
        } catch (URISyntaxException e) {
            LOG.error("Could not open URL " + REGISTER_URL + " because of " + e.getMessage(), e);
        }
        actionEvent.consume();
    }
}
