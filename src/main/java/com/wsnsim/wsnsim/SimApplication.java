package com.wsnsim.wsnsim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SimApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SimApplication.class.getResource("new-sim-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1020, 636);
        stage.setTitle("Simulateur WSN");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}