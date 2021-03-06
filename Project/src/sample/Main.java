package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent mainView = FXMLLoader.load(getClass().getResource("mainView.fxml"));
        primaryStage.setTitle("Program created by Wojceich Zielinski for changing bytes in files with specific extension.");
        primaryStage.setScene(new Scene(mainView, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
