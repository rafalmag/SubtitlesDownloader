package pl.rafalmag.subtitledownloader.gui;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Based on http://tech.chitgoks.com/2013/07/19/how-to-go-to-item-in-combobox-on-keypress-in-java-fx-2/
 *
 * @param <T>
 */
public class SelectKeyComboBoxListener<T> implements EventHandler<KeyEvent> {

    private ComboBox<T> comboBox;
    private StringBuilder sb = new StringBuilder();

    public SelectKeyComboBoxListener(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        this.comboBox.setOnKeyReleased(SelectKeyComboBoxListener.this);

        this.comboBox.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ESCAPE && sb.length() > 0) {
                sb.delete(0, sb.length());
            }
        });

        // add a focus listener such that if not in focus, reset the filtered typed keys
        this.comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ListView lv = ((ComboBoxListViewSkin) SelectKeyComboBoxListener.this.comboBox.getSkin()).getListView();
                lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
            } else {
                sb.delete(0, sb.length());
            }
        });

        this.comboBox.setOnMouseClicked(event -> {
            ListView lv = ((ComboBoxListViewSkin) SelectKeyComboBoxListener.this.comboBox.getSkin()).getListView();
            lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
        });
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP || event.getCode() == KeyCode.TAB) {
            return;
        } else if (event.getCode() == KeyCode.BACK_SPACE && sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        } else {
            sb.append(event.getText());
        }

        if (sb.length() == 0)
            return;

        boolean found = false;
        ObservableList<T> items = comboBox.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (event.getCode() != KeyCode.BACK_SPACE && items.get(i).toString().toLowerCase().startsWith(sb.toString().toLowerCase())) {
                ListView lv = ((ComboBoxListViewSkin) comboBox.getSkin()).getListView();
                lv.getSelectionModel().clearAndSelect(i);
                lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
                found = true;
                break;
            }
        }

        if (!found && sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}