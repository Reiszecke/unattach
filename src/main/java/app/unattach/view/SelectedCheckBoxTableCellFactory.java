package app.unattach.view;

import app.unattach.model.Email;
import app.unattach.model.EmailStatus;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class SelectedCheckBoxTableCellFactory
    implements Callback<TableColumn.CellDataFeatures<Email, CheckBox>, ObservableValue<CheckBox>> {
  @Override
  public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Email, CheckBox> cellDataFeatures) {
    Email email = cellDataFeatures.getValue();
    CheckBox checkBox = new CheckBox();
    TableView<Email> tableView = cellDataFeatures.getTableView();
    TableColumn<Email, ?> statusTableColumn = tableView.getColumns().get(0);
    CheckBox toggleAllEmailsCheckBox = (CheckBox) statusTableColumn.getGraphic();
    checkBox.selectedProperty().setValue(email.isSelected());
    checkBox.getStyleClass().removeAll();
    if (email.getStatus() == EmailStatus.PROCESSED) {
      checkBox.getStyleClass().add("checkbox-processed");
    } else if (email.getStatus() == EmailStatus.FAILED) {
      checkBox.getStyleClass().add("checkbox-failed");
    }
    checkBox.tooltipProperty().setValue(new Tooltip(email.getStatus().toString()));
    checkBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
      EmailStatus targetStatus = newValue ? EmailStatus.TO_PROCESS : EmailStatus.NOT_SELECTED;
      ObservableList<Email> selectedEmails = tableView.getSelectionModel().getSelectedItems();
      if (selectedEmails.contains(email)) {
        // If multiple emails selected and toggling one of them, toggle all selected emails.
        for (Email selectedEmail : selectedEmails) {
          selectedEmail.setStatus(targetStatus);
        }
      } else {
        // Otherwise, toggle just the clicked email.
        email.setStatus(targetStatus);
      }
      // Update the "Toggle All" checkbox based on the currently selected emails.
      if (tableView.getItems().stream().allMatch(Email::isSelected)) {
        toggleAllEmailsCheckBox.setIndeterminate(false);
        toggleAllEmailsCheckBox.setSelected(true);
      } else if (tableView.getItems().stream().noneMatch(Email::isSelected)) {
        toggleAllEmailsCheckBox.setIndeterminate(false);
        toggleAllEmailsCheckBox.setSelected(false);
      } else{
        toggleAllEmailsCheckBox.setIndeterminate(true);
      }
      // Redraw the table based on the new state.
      tableView.refresh();
    });
    return new SimpleObjectProperty<>(checkBox);
  }
}