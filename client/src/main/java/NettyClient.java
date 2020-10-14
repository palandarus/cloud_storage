import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class NettyClient extends Application {


        private String clientPath=Directories.CLIENT_FILES_DIRECTORY.getPath();

        @Override
        public void start(Stage stage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            stage.setTitle("Authrisation Cloud Storage");
            Scene scene = new Scene(root, 1024, 600);
            stage.setScene(scene);
            stage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }



}
