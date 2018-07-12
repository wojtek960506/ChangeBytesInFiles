package sample;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;

public class SecondViewController implements Initializable {
    @FXML ListView listView;
    @FXML Button startButton;
    @FXML TextField textField2;
    @FXML Button onceAgainButton;
    @FXML TableView<FileData> tableView;
    @FXML TableColumn<FileData, String> fullPathColumn;
    @FXML TableColumn<FileData, IsFileChanged> isChangedColumn;
    @FXML TableColumn<FileData, Integer> sizeColumn;
    @FXML Label labelRunning;

    private String directory;
    private String extension;
    private byte[] bytesToFind;
    private byte[] bytesToPut;

    private int flag = 0;

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startButton.setVisible(true);
        textField2.setVisible(true);
        textField2.setBackground(null);
        textField2.setBorder(null);
        onceAgainButton.setVisible(true);
        labelRunning.setVisible(false);

        listView.setVisible(false);

        tableView.setVisible(true);
        tableView.setEditable(true);

        fullPathColumn.setCellValueFactory(new PropertyValueFactory<>("fullPath"));
        isChangedColumn.setCellValueFactory(new PropertyValueFactory<>("isChanged"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
    }

    public void showList() {
        tableView.setVisible(false);
        listView.setVisible(true);
    }

    public void hideLabel() {
        labelRunning.setVisible(false);
    }

    public void addToTableView(String fullPath, IsFileChanged isChanged, Integer size) {
        FileData fD = new FileData(fullPath,isChanged,size);
        tableView.getItems().add(fD);
    }

    public void addToListView(String s) {
        listView.getItems().add(s);
    }

    public void setTextField2(String s) {
        textField2.setText(s);
    }

    public void startButton() {
        startButton.setVisible(false);
        labelRunning.setVisible(true);
        visitAllFiles();
    }

    private void visitAllFiles() {
            Path startingDirectory = Paths.get(directory);

            //class to define what to do while walking file tree
            VisitorWhenWalking visitor = new VisitorWhenWalking(extension,this,bytesToFind,bytesToPut);

            //thread for showing changes in gui while algorithm is proceeding
            Service<Void> backgroundThread;
            backgroundThread = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() {
                            try {
                                Files.walkFileTree(startingDirectory, visitor);
                                labelRunning.setVisible(false);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };
                }
            };
            backgroundThread.setOnSucceeded(event -> {
                if(flag == 0)
                    textField2.setText("Done!");
                labelRunning.setVisible(false);
                listView.setVisible(true);
                onceAgainButton.setVisible(true);
            });
            backgroundThread.restart();
    }

    //it is used in main controller to connect with this controller
    public void setData(String directory, String extension, byte[] bytesToFind, byte[] bytesToPut) {
        this.directory = directory;
        this.extension = extension;
        this.bytesToFind = bytesToFind;
        this.bytesToPut = bytesToPut;
    }

    public void onceAgain(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("mainView.fxml"));
        Parent sampleParent = loader.load();
        Scene sampleScene = new Scene(sampleParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(sampleScene);
        window.show();
    }
}
