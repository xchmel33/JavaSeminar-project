package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class settings {

    static String filename;
    static TextArea settingsText;
    static Stage window;

    public static void display(){

        String cwd = System.getProperty("user.dir");
        cwd = cwd.replace("\\","/");

        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Nastavenia aplikácie");
        window.setWidth(500);
        window.setHeight(400);

        Text title = new Text("Nastavenia vstupu: obsadenost skladu a poziadavky");
        title.getStyleClass().add("title");
        Button filePicker = new Button("Nahrat vstup zo suboru");
        Button saveSettings = new Button("Ulozit vstup");
        Button saveFile = new Button("Ulozit vstupny subor s nastaveniami");

        settingsText = new TextArea();
        File defaultSetting = new File("data/input.json");
        List<String> lines = read(defaultSetting);
        for (String line:lines) {
            settingsText.appendText(line+"\n");
        }

        File in = new File("data/in.json");

        filePicker.setOnAction(e -> settings.filePicker());
        saveSettings.setOnAction(e -> settings.fileWriter(in,settingsText));

        GridPane layout = new GridPane();
        layout.add(title,0,0,3,1);
        layout.add(settingsText,0,1,3,1);
        layout.add(filePicker,0,2);
        layout.add(saveSettings,1,2);
        layout.add(saveFile, 2,2);
        layout.setHgap(15);
        layout.setVgap(10);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(10,0,10,0));

        StackPane root = new StackPane();
        root.getChildren().add(layout);
        Scene scene =  new Scene(root,500,200);
        scene.getStylesheets().add("file:data/SettingTheme.css");
        window.setScene(scene);
        window.show();
    }

    public static void filePicker(){
        FileChooser chooser = new FileChooser();
        File f = chooser.showOpenDialog(null);
        filename = f.getAbsolutePath();
        settingsText.appendText(filename);
        List<String> lines;
        lines = read(f);
        for (String line:lines) {
            settingsText.appendText(line+"\n");
        }


    }
    public static List<String> read(File file) {
        List<String> lines = new ArrayList<String>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return lines;
    }

    public static void fileWriter(File savePath, TextArea textArea) {
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(savePath));
            bf.write(textArea.getText());
            bf.flush();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
