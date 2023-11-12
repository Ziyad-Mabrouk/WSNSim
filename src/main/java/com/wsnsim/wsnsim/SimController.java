package com.wsnsim.wsnsim;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;


public class SimController implements Initializable {
    @FXML
    private Label density_percentage;
    @FXML
    private Label warning;
    @FXML
    private Label round_number;
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
    @FXML
    private Label nbre_CH;
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

        director.clear();
        round_number.setText("Round " + director.getRound_number());
    }

    public void draw(GraphicsContext gc, int numNodes, double CHdensity, double canvasWidth, double canvasHeight, int circleSize) {
        director = Director.getInstance(numNodes, CHdensity, canvas.getGraphicsContext2D(), canvasWidth, canvasHeight, circleSize);
        director.drawNodes();
    }

    @FXML
    public void nextRound() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        director = Director.getInstance();

        director.nextRound(ch_density.getValue());
        director.setUp();

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        director.drawNodes();
        director.drawChLinks();

        round_number.setText("Round: " + director.getRound_number());
        nbre_CH.setText("Nbre de CH: " + director.getCurrentCHList(director.getRound_number()).size());

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
            draw(canvas.getGraphicsContext2D(), numNodes, ch_density.getValue(), canvas.getWidth(), canvas.getHeight(), 20);
            ch_density.setDisable(true);
            nodes_number.setDisable(true);
            round_duration.setDisable(true);
            simuler.setDisable(true);
            next.setDisable(false);

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
        //Node node1 = director.getListNodes().get(0);
        //Node node2 = director.getListNodes().get(1);
        //node1.transmit(node2, canvas.getGraphicsContext2D(), 20);
        director.setUp();

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        director.drawNodes();
        director.drawChLinks();

        nbre_CH.setText("Nbre de CH: " + director.getCurrentCHList(director.getRound_number()).size());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ch_density.valueProperty().addListener((observableValue, number, t1) -> {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);
            density_percentage.setText(df.format(ch_density.getValue()) + "%");
        });

        canvas.setOnMouseMoved(mouseEvent -> {
            canvas.setCursor(Cursor.CROSSHAIR);
            for (Node node : director.getListNodes()) {
                if(node.clicked(mouseEvent.getX(), mouseEvent.getY(), 20)) {
                    canvas.setCursor(Cursor.HAND);
                    break;
                }
            }
        });
        canvas.setOnMouseClicked(mouseEvent -> {
            //System.out.println(mouseEvent.getX());
            //System.out.println(mouseEvent.getY());
            for (Node node : director.getListNodes()) {
                if(node.clicked(mouseEvent.getX(), mouseEvent.getY(), 20)) {
                    /*
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("node " + node.getId() + " clicked.");
                    errorAlert.setContentText("show smth");
                    errorAlert.showAndWait();
                    */

                    GraphController graphDirector = new GraphController();
                    graphDirector.load(node);

                    break;
                }
            }
        });

    }


}