package com.wsnsim.wsnsim;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class SimController implements Initializable {
    @FXML
    private Label density_percentage;
    @FXML
    private Label warning;
    @FXML
    private Slider ch_density;
    @FXML
    private TextField nodes_number;
    @FXML
    private TextField round_duration;
    @FXML
    private Canvas canvas;
    @FXML
    private Button simuler;
    @FXML
    private ProgressBar round_progress;
    @FXML
    private Button next;
    @FXML
    private Button back;

    private Director director;
    @FXML
    public void quit() {
        Platform.exit();
    }

    @FXML
    public void reset() {
        ch_density.setValue(0);
        ch_density.setDisable(false);

        nodes_number.setText("");
        nodes_number.setDisable(false);

        round_duration.setText("");
        round_duration.setDisable(false);

        next.setDisable(true);
        back.setDisable(true);
        round_progress.setProgress(0);

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        simuler.setDisable(false);

        warning.setVisible(false);

    }

    public void draw(GraphicsContext gc, int numNodes, double canvasWidth, double canvasHeight, int circleSize) {
        director = Director.getInstance(numNodes, canvasWidth, canvasHeight, circleSize);
        director.drawNodes(gc);
    }

    @FXML
    public void start() {
        try {
            int numNodes = Integer.parseInt(nodes_number.getText());
            if (numNodes > 100) {
                numNodes = 100;
                warning.setVisible(true);
                nodes_number.setText("100");
            }
            draw(canvas.getGraphicsContext2D(), numNodes, canvas.getWidth(), canvas.getHeight(), 20);
            ch_density.setDisable(true);
            nodes_number.setDisable(true);
            round_duration.setDisable(true);
            simuler.setDisable(true);

        } catch (NumberFormatException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Valeur Invalide");
            errorAlert.setContentText("Le nombre de noeuds doit être un nombre.");
            errorAlert.showAndWait();
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur");
            errorAlert.setContentText("Erreur Inattandu.");
            errorAlert.showAndWait();
        }
    }

    @FXML
    public void test() {
        //test:
        Node node1 = director.getListNodes().get(0);
        Node node2 = director.getListNodes().get(1);
        node1.transmit(node2, canvas.getGraphicsContext2D(), 20);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ch_density.valueProperty().addListener((observableValue, number, t1) -> {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);
            density_percentage.setText(df.format(ch_density.getValue()) + "%");
        });
    }
}