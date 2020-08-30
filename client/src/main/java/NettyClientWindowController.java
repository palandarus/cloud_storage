import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class NettyClientWindowController implements Initializable, Controllers {
    @FXML
    ListView<FileInfo> clientFilesList;
    @FXML
    ListView<FileInfo> serverFilesList;

    private String stringRootPath = Directories.CLIENT_FILES_DIRECTORY.getPath();
    private String serverRootPath = Directories.SERVER_FILES_DIRECTORY.getPath();
    private static String destinationPath;


    private boolean isClientSideFocus = true;
    //    private TelnetFileClient telnetFileClient;
    private Network network = Network.getInstance();

    Path root = Paths.get(stringRootPath);
    Path serverRoot = Paths.get("./", serverRootPath);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);



    public void showErrorWindow(String errorMessage) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(errorMessage);
        errorAlert.showAndWait();
    }

    public void showInformationWindow(String infoMessage) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Information");
        infoAlert.setHeaderText(null);
        infoAlert.setContentText(infoMessage);
        infoAlert.showAndWait();
    }

    public void showConfirmationWindow(String confirmationMessage) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Are you sure?");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText(confirmationMessage);
        confirmAlert.showAndWait();
    }


    public void showErrorWindowInFxThread(String errorMessage) {
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

    @Override
    public void updateServerListSideInFxThread(List<FileInfo> msg) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    updateServerListSide(msg);
                }
            });
        } else {
            updateServerListSide(msg);
        }
    }

    @Override
    public void refreshClientSideInFxThread(String destinationPath) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    clientGoToPath(Paths.get(destinationPath));
                }
            });
        } else {
            clientGoToPath(Paths.get(destinationPath));
        }
    }

    public void showInformationWindowInFxThread(String infoMessage) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    showInformationWindow(infoMessage);
                }
            });
        } else {
            showInformationWindow(infoMessage);
        }
    }

    public void showConfirmationWindowInFxThread(String confirmationMessage) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    showConfirmationWindow(confirmationMessage);
                }
            });
        } else {
            showConfirmationWindow(confirmationMessage);
        }
    }

    public void menuItemFileExitAction(ActionEvent actionEvent) {
        network.close();
        Platform.exit();
    }




    public void buttonFileSendAction(ActionEvent actionEvent) {
        FileInfo selectedFile;

        if (isClientSideFocus) {
            destinationPath = serverFilesList.getItems().get(0).getFilePath();
            selectedFile = clientFilesList.getSelectionModel().getSelectedItem();
            clientSendFile(selectedFile, destinationPath);
        } else {
            destinationPath = clientFilesList.getItems().get(0).getFilePath();
            selectedFile = serverFilesList.getSelectionModel().getSelectedItem();
            network.sendMessage(commandLibrary.requestFileMessage(selectedFile.getFileName(), selectedFile.getFilePath(), destinationPath));
        }


    }


    public void buttonDirectoryCreateAction(ActionEvent actionEvent) {

    }

    public void buttonRenameAction(ActionEvent actionEvent) {

    }

    public void buttonFileDeleteAction(ActionEvent actionEvent) {
        FileInfo selectedFile;
        if (!isClientSideFocus)
            network.sendMessage(commandLibrary.deleteFileMessage(
                serverFilesList.getSelectionModel().getSelectedItem().getFileName(),
                serverFilesList.getSelectionModel().getSelectedItem().getFullFilePath()));
        else {
            try {
                Files.delete(Paths.get(clientFilesList.getSelectionModel().getSelectedItem().getFullFilePath()));
                clientGoToPath(root);
            }catch (IOException e){
                showErrorWindowInFxThread("Возникла ошибка при удалении "+clientFilesList.getSelectionModel().getSelectedItem().getFullFilePath());
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network.setController(this);
        clientFilesList.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
            @Override
            public ListCell<FileInfo> call(ListView<FileInfo> param) {
                return new ListCell<FileInfo>() {
                    @Override
                    protected void updateItem(FileInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {

                            String formattedFilename = String.format("%-30s", item.getFileName());
                            String formattedFileLength;
                            if (item.getFileLength() == -1L)
                                formattedFileLength = String.format("%s", "[ DIR ]");
                            else if (item.getFileLength() == -2L)
                                formattedFileLength = String.format("%s", "[ UP DIR ]");
                            else {

                                if (item.getFileLength() > 1048576)
                                    formattedFileLength = String.format("%,d  MBytes", item.getFileLength() / 1048576);
                                else if (item.getFileLength() > 1024)
                                    formattedFileLength = String.format("%,d  KBytes", item.getFileLength() / 1024);
                                else
                                    formattedFileLength = String.format("%,d  Bytes", item.getFileLength());
                            }
                            String text = String.format("%s %-20s", formattedFilename, formattedFileLength);


                            setText(text);
                        }
                    }
                };
            }
        });
        serverFilesList.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
            @Override
            public ListCell<FileInfo> call(ListView<FileInfo> param) {
                return new ListCell<FileInfo>() {
                    @Override
                    protected void updateItem(FileInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {

                            String formattedFilename = String.format("%-100s", item.getFileName());
                            String formattedFileLength;
                            if (item.getFileLength() == -1L)
                                formattedFileLength = String.format("%s", "[ DIR ]");
                            else if (item.getFileLength() == -2L)
                                formattedFileLength = String.format("%s", "[ UP DIR ]");
                            else {

                                if (item.getFileLength() > 1048576)
                                    formattedFileLength = String.format("%,d  MBytes", item.getFileLength() / 1048576);
                                else if (item.getFileLength() > 1024)
                                    formattedFileLength = String.format("%,d  KBytes", item.getFileLength() / 1024);
                                else
                                    formattedFileLength = String.format("%,d  Bytes", item.getFileLength());
                            }
                            String text = String.format("%s %-30s", formattedFilename, formattedFileLength);


                            setText(text);
                        }
                    }
                };
            }
        });
        clientGoToPath(root);
        serverGoToPath();
        network.sendMessage(commandLibrary.getRootFilesListMessage(network.getUserId()));

    }

    public void clientGoToPath(Path path) {
        clientFilesList.getItems().clear();
        root = path;
        clientFilesList.getItems().add(new FileInfo("...", -2L, root.toString()));
        clientFilesList.getItems().addAll(scanClientFiles(root));
        clientFilesList.getItems().sort(new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                return new Long(o1.getFileLength() - o2.getFileLength()).intValue();
            }
        });
    }

    private void serverGoToPath() {

    }

    public List<FileInfo> scanClientFiles(Path root) {
        try {
            return Files.list(root).map(FileInfo::new).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Files scan exception on " + root);
        }

    }

    public void clientFilesListClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 1) {
            isClientSideFocus = true;
        }
        String destinationPath;
        if (mouseEvent.getClickCount() == 2) {
            isClientSideFocus = true;
            FileInfo fileInfo = clientFilesList.getSelectionModel().getSelectedItem();
            if (fileInfo != null) {
                if (fileInfo.isDirectory()) clientGoToPath(root.resolve(fileInfo.getFileName()));
                else if (fileInfo.isUpDirectory()) {
                    String stringPath = root.toAbsolutePath().toString()+"\\";
                    System.out.println(stringPath);
                    if (!stringRootPath.equals(stringPath.substring(stringPath.indexOf("\\.\\") + 3)))
                        clientGoToPath(root.getParent());
                } else {
                    destinationPath = serverFilesList.getItems().get(0).getFilePath();
                    clientSendFile(fileInfo, destinationPath);
                }
            }
        }
    }

    private void clientSendFile(FileInfo fileInfo, String destinationPath) {
        if (fileInfo != null) {
            if (fileInfo.isDirectory()) clientGoToPath(root.resolve(fileInfo.getFileName()));
            else if (fileInfo.isUpDirectory()) {
                String stringPath = root.toAbsolutePath().toString();
                System.out.println(stringPath);
                if (!stringRootPath.equals(stringPath.substring(stringPath.indexOf("\\.\\") + 3)))
                    clientGoToPath(root.getParent());
            } else {
                try {
                    FileUtility.splitAndSend(new File(fileInfo.getFullFilePath()), 1024 * 780, network.getChannel());
                    network.sendMessage(commandLibrary.sendingFileCompleteMessage(fileInfo.getFileName(), destinationPath, fileInfo.getFileLength()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void serverFilesListClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 1) {
            isClientSideFocus = false;
        } else if (mouseEvent.getClickCount() == 2) {
            isClientSideFocus = false;
            FileInfo fileInfo = serverFilesList.getSelectionModel().getSelectedItem();
            if (fileInfo != null) {
                if (fileInfo.isDirectory())
                    network.sendMessage(serverRoot.resolve(fileInfo.getFileName()).toString());
                else if (fileInfo.isUpDirectory()) {

                    String stringPath = root.toAbsolutePath().toString();
                    System.out.println(stringPath);
                    if (!stringRootPath.equals(stringPath.substring(stringPath.indexOf("\\.\\") + 3)))
                        clientGoToPath(root.getParent());
                } else {

                }
            }
        }
    }


    @Override
    public void changeScene() {

    }

    public void updateServerListSide(List<FileInfo> msg) {
        serverFilesList.getItems().clear();
        serverFilesList.getItems().addAll(msg);
        serverRoot = Paths.get(serverFilesList.getItems().get(0).getFilePath());
        serverFilesList.getItems().sort(new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                return new Long(o1.getFileLength() - o2.getFileLength()).intValue();
            }
        });
    }
}
