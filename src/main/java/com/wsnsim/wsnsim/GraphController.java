package com.wsnsim.wsnsim;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphController {
    @FXML
    private Label label;
    @FXML
    private Button button;
    static Stage stage;
    private Node node;

    public void load(Node node) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("graph.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 350, 350);
            Stage stage = new Stage();
            stage.setTitle("Node " + node.getId());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            this.stage = stage;
            this.node = node;
            //prblm
            label.setText("Graphe d'evolution d'énergie du noeud " + node.getId());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exit() {
        stage.close();
    }

}
