package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainViewController implements Initializable {

    @FXML TextField pathTextField;
    @FXML TextField extensionTextField;
    @FXML TextField sequenceToFindTextField;
    @FXML TextField sequenceToPutTextField;

    private final int num = 22; //number Of Characters In Default Directory Name

    public void startAlgorithm(ActionEvent event) throws IOException {
        try{

            Vector<String> extensions = new Vector<>();
            String[] extensionsTable = {"doc", "docx", "pdf", "txt", "png", "xlsx", "wmv", "java", "sql", "sln", "cpp", "h", "obj",
                    "lib"};
            Collections.addAll(extensions, extensionsTable);

            String directory = pathTextField.getText();
            String temp = "D:\\MyDocumentsToChange";
            if (!(directory.substring(0, Math.min(directory.length(), num)).equals(temp))) throw new Error();

            String extension = extensionTextField.getText();
            if (!(extensions.contains(extension))) throw new Error();

            String sequenceToFind = sequenceToFindTextField.getText() + ",";
            if (!checkRegex(sequenceToFind)) throw new Error();

            List<Byte> bytes1 = new ArrayList<>();
            saveBytes(sequenceToFind, bytes1);


            String sequenceToPut = sequenceToPutTextField.getText() + ",";
            if (!checkRegex(sequenceToPut)) throw new Error();

            List<Byte> bytes2 = new ArrayList<>();
            if (!saveBytes(sequenceToPut, bytes2)) throw new Error();

            if (bytes1.size() != bytes2.size()) throw new Error();

            int size = bytes1.size();

            byte[] bytesToFind = new byte[size];
            byte[] bytesToPut = new byte[size];

            for (int i = 0; i < size; i++) {
                bytesToFind[i] = bytes1.get(i);
                bytesToPut[i] = bytes2.get(i);
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("secondView.fxml"));
            Parent secondViewParent = loader.load();
            Scene secondViewScene = new Scene(secondViewParent);
            SecondViewController controller = loader.getController(); //passing informations to another scene

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(secondViewScene);
            window.show();

            controller.setData(directory, extension, bytesToFind, bytesToPut);

        }catch (Error e) {
            setDefaultTextFields();
            AlertBox.display("WARNING!!!", "You have to type correct values!");
        }
    }

    private void setDefaultTextFields() {
        pathTextField.setText("D:\\MyDocumentsToChange");
        extensionTextField.setText("");
        sequenceToFindTextField.setText("");
        sequenceToPutTextField.setText("");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private boolean saveBytes(String input, List<Byte> list) {
        Scanner s = new Scanner(input);
        s.useDelimiter("(\\s)*,(\\s)*");
        while(s.hasNext()) {
            if(s.hasNextInt()){
                int temp = s.nextInt();
                if(temp > 127 || temp < -128)
                    return false;
                list.add((byte)temp);

            }
            else {
                s.next();
            }
        }
        return true;
    }

    private boolean checkRegex(String input) {
        //akceptowane jest podanie liczby trzycyfrowej ujemnej lub dodatniej, spacje moga byc przed liczba albo
        //za liczba. Jak podajemy wiecej niz jedna liczbe to musza byc one oddzielone przecinkami;
        //przed pierwsza i za ostatnia nie stawiamy przecinka
        final String REGEX = "(((\\s)*-([0-9]{1,3})(\\s)*,)|((\\s)*([0-9]{1,3})(\\s)*,))+";
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(input);

        if(matcher.matches())
            return true;
        else
            return false;
    }
}
