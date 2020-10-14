import javafx.scene.control.Alert;

import java.util.List;

public interface Controllers {

    void changeScene();

    void updateServerListSide(List<FileInfo> msg);


    void showInformationWindowInFxThread(String errorMessage);

    void showConfirmationWindow(String confirmationMessage);


    void showErrorWindowInFxThread(String errorMessage);

    void updateServerListSideInFxThread(List<FileInfo> msg);

    void refreshClientSideInFxThread(String destinationPath);
}