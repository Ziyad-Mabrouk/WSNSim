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
    private ArrayList<Node> listNodes= new ArrayList<>();
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

        listNodes.clear();
    }

    public void draw(GraphicsContext gc, int numNodes, double canvasWidth, double canvasHeight, int circleSize) {
        for (int i = 1; i <= numNodes; i++) {
            boolean isOverlap = true;
            int x = 0;
            int y = 0;

            while (isOverlap) {
                isOverlap = false;
                x = (int) (Math.random() * (canvasWidth - circleSize));
                y = (int) (Math.random() * (canvasHeight - circleSize));

                // Check for overlap with existing circles
                for (Node existing_node : listNodes) {
                    if (existing_node.intersects(x, y, circleSize)) {
                        isOverlap = true;
                        break;
                    }
                }
            }

            Node node = new Node(i, x, y);
            listNodes.add(node);
            node.draw(gc, canvasWidth, canvasHeight, circleSize);
        }
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

        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Valeur Invalide");
            errorAlert.setContentText("Le nombre de noeuds doit être un nombre.");
            errorAlert.showAndWait();
        }
    }

    @FXML
    public void test() {
        //test:
        Node node1 = listNodes.get(0);
        Node node2 = listNodes.get(1);
        node1.transmit(node2, canvas.getGraphicsContext2D(), 10);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ch_density.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(1);
                density_percentage.setText(df.format(ch_density.getValue()) + "%");
            }
        });
    }
}