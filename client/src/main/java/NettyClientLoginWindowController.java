import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NettyClientLoginWindowController implements Controllers, Initializable {

    @FXML
    GridPane rootNode;

    @FXML
    TextField loginArea;

    @FXML
    PasswordField passArea;

    @FXML
    ListView<FileInfo> clientFilesList;
    @FXML
    ListView<FileInfo> serverFilesList;


    public void showErrorWindow(String errorMessage){
        Alert errorAlert=new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(errorMessage);
        errorAlert.showAndWait();
    }

    public void showInformationWindow(String infoMessage){
        Alert infoAlert=new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Information");
        infoAlert.setHeaderText(null);
        infoAlert.setContentText(infoMessage);
        infoAlert.showAndWait();
    }

    public void showConfirmationWindow(String confirmationMessage){
        Alert confirmAlert=new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Are you sure?");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText(confirmationMessage);
        confirmAlert.showAndWait();
    }


    public void showErrorWindowInFxThread(String errorMessage){
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    showErrorWindow(errorMessage);
                }
            });
        } else {
            showErrorWindow(errorMessage);
        }
    }

    public void showInformationWindowInFxThread(String infoMessage){
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    showInformationWindow (infoMessage);
                }
            });
        } else {
            showInformationWindow(infoMessage);
        }
    }

    public void showConfirmationWindowInFxThread(String confirmationMessage){
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    showConfirmationWindow (confirmationMessage);
                }
            });
        } else {
            showConfirmationWindow(confirmationMessage);
        }
    }

    private Network network=Network.getInstance();

    public void authAction() {
        network.setController(this);
        network.sendAuthInfo(loginArea.getText(), passArea.getText());
        loginArea.clear();
        passArea.clear();
    }

    public void regAction() {
        network.setController(this);
        network.sendRegInfo(loginArea.getText(),passArea.getText());
    }

    @Override
    public void changeScene() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    change();
                }
            });
        } else {
            change();
        }
    }

    @Override
    public void updateServerListSide(List<FileInfo> msg) {

    }

    private void change() {
        try {
            Parent mainScene = FXMLLoader.load(getClass().getResource("/main.fxml"));
            ((Stage) rootNode.getScene().getWindow()).setScene(new Scene(mainScene));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateServerListSideInFxThread(List<FileInfo> msg) {

    }

    @Override
    public void refreshClientSideInFxThread(String destinationPath) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginArea.setText("login");
        passArea.setText("pass");
    }
}
