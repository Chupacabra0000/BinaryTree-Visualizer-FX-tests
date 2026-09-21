package org.example.kitpo_l1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.Parent;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main-view.fxml"));
        Scene scene = new Scene(root, 800, 500);
        stage.setTitle("ЛР1 — Двоичное дерево");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
