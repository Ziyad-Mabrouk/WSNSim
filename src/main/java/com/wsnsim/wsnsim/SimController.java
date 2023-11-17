package com.wsnsim.wsnsim;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ScrollBar;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;
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
    private TextField initial_energy;
    @FXML
    private TextField communication_radius;
    @FXML
    private RadioButton formule1;
    @FXML
    private RadioButton formule2;
    @FXML
    private RadioButton formule3;
    @FXML
    private Canvas canvas;
    @FXML
    private Button simuler;
    @FXML
    private Button next;
    @FXML
    private Button back;
    @FXML
    private Button infos;
    @FXML
    private Label nbre_CH;
    @FXML
    private LineChart<?, ?> chart1;
    @FXML
    private BarChart<?, ?> chart2;
    @FXML
    private SubScene subScene;
    @FXML
    private Button general;
    @FXML
    private Button formules;
    @FXML
    private Label nodeTitle;
    @FXML
    private Canvas nodeCanvas;
    @FXML
    private Label role;
    @FXML
    private Label CH;
    @FXML
    private Label timeslot;
    @FXML
    private BarChart<?, ?> chart3;
    @FXML
    private TextArea logs;
    @FXML
    private Label etat;
    @FXML
    private Label logsLabel;
    @FXML
    private ImageView hoverImage;
    @FXML
    private Label aboutUs;
    @FXML
    private Label au1;
    @FXML
    private Label au2;
    @FXML
    private Label au3;
    @FXML
    private Label au4;
    @FXML
    private Label au5;
    @FXML
    private Label au6;
    @FXML
    private Label au7;
    @FXML
    private Label au8;
    @FXML
    private Label au9;
    @FXML
    private Label au10;
    @FXML
    private Rectangle auSubScene;
    private Director director;
    private int numNodes, roundDuration, commRadius, formule = 1;
    private double E_max;
    private Node selectedNode;
    private int maxNbreNodes = 200;

    @FXML
    public void quit() {Platform.exit();}

    @FXML
    public void reset() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //ch_density.setValue(0);
        ch_density.setDisable(false);

        //nodes_number.setText("");
        nodes_number.setDisable(false);

        //round_duration.setText("");
        round_duration.setDisable(false);

        //initial_energy.setText("0.5");
        initial_energy.setDisable(false);

        //communication_radius.setText("50");
        communication_radius.setDisable(false);

        formule1.setDisable(false);
        formule2.setDisable(false);
        formule3.setDisable(false);

        next.setDisable(true);
        back.setDisable(true);
        infos.setDisable(true);
        simuler.setDisable(false);
        warning.setVisible(false);

        round_number.setText("Round: 0");
        nbre_CH.setText("Nbre de CH: 0");
        director.clear();
        DirectorHistory directorHistory = DirectorHistory.getInstance();
        directorHistory.clearHistory();

        hideDetails();
        chart1.getData().removeAll(Collections.singleton(chart1.getData().setAll()));
        chart2.getData().removeAll(Collections.singleton(chart2.getData().setAll()));
    }

    public void draw() {
        checkSelectedFormula();
        director = Director.getInstance(numNodes, roundDuration, ch_density.getValue(), formule, commRadius, E_max , canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight());
        director.drawNodes();
    }

    @FXML
    public void nextRound() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        director = Director.getInstance();

        try {
            director.restore(director.getRound_number() + 1);
        } catch (Exception e) {
            // don't have that yet:
            director.nextRound();
            director.setUp();
            director.stableTransmission();

        }
        round_number.setText("Round: " + (director.getRound_number() + 1));
        nbre_CH.setText("Nbre de CH: " + director.getCurrentCHList().size());

        director.drawNodes();
        director.drawChLinks();

        back.setDisable(false);

        if(selectedNode == null) {
            showInfos();
        } else {
            setDetailsVisible();
            getNodeInfos(selectedNode);
        }
    }

    @FXML
    public void previousRound() {
        director = Director.getInstance();
        director.restore(director.getRound_number() - 1);

        round_number.setText("Round: " + (director.getRound_number() + 1));
        nbre_CH.setText("Nbre de CH: " + director.getCurrentCHList().size());

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        director.drawNodes();
        director.drawChLinks();

        if (director.getRound_number() == 0) {
            back.setDisable(true);
        }

        if(selectedNode == null) {
            showInfos();
        } else {
            setDetailsVisible();
            getNodeInfos(selectedNode);
        }
    }

    @FXML
    public void start() {
        int validFields = getValues();
        if (validFields == 0) {return;}

        initial_energy.setDisable(true);
        communication_radius.setDisable(true);
        formule1.setDisable(true);
        formule2.setDisable(true);
        formule3.setDisable(true);
        ch_density.setDisable(true);
        nodes_number.setDisable(true);
        round_duration.setDisable(true);
        simuler.setDisable(true);
        next.setDisable(false);
        infos.setDisable(false);

        draw();

        director.setUp();
        round_number.setText("Round: 1");
        nbre_CH.setText("Nbre de CH: " + director.getCurrentCHList().size());

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        director.drawNodes();

        director.drawChLinks();

        director.stableTransmission();

        showInfos();
    }

    public int getValues() {
        int field1Valid = 0, field2Valid = 0, field3Valid = 0, field4Valid = 0;

        try {
            numNodes = Integer.parseInt(nodes_number.getText());
            field1Valid = 1;
            if (numNodes > maxNbreNodes) {
                numNodes = maxNbreNodes;
                warning.setVisible(true);
                warning.setText("*max=" + maxNbreNodes);
                nodes_number.setText(String.valueOf(maxNbreNodes));
            }
        } catch (NumberFormatException e) {
            showWarning("Valeur Invalide", "Le nombre de noeuds doit être un nombre naturel.");
        }
        try {
            roundDuration = Integer.parseInt(round_duration.getText());
            field2Valid = 1;
        } catch (NumberFormatException e) {
            showWarning("Valeur Invalide", "La durée des rounds doit être un nombre naturel.");
        }
        try {
            E_max = Double.parseDouble(initial_energy.getText());
            if (E_max == 0.0) {
                NumberFormatException e = new NumberFormatException();
                throw e;
            }
            field3Valid = 1;
        } catch (NumberFormatException e) {
            showWarning("Valeur Invalide", "L'énergie initiale doit être un nombre supérieure à 0.");
        }
        try {
            commRadius = Integer.parseInt(communication_radius.getText());
            field4Valid = 1;
        } catch (NumberFormatException e) {
            showWarning("Valeur Invalide", "Le rayon de communication doit être un nombre naturel.");
        }
        return field1Valid * field2Valid * field3Valid * field4Valid ;
    }

    public void showWarning(String title, String Description) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(title);
        errorAlert.setContentText(Description);
        errorAlert.showAndWait();
    }

    public void checkSelectedFormula() {
        if (formule1.isSelected()) {
            formule = 1;
        }
        if (formule2.isSelected()) {
            formule = 2;
        }
        if (formule3.isSelected()) {
            formule = 3;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hideDetails();
        ch_density.valueProperty().addListener((observableValue, number, t1) -> {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);
            density_percentage.setText(df.format(ch_density.getValue()) + "%");
        });

        ToggleGroup toggleGroup = new ToggleGroup();
        formule1.setToggleGroup(toggleGroup);
        formule2.setToggleGroup(toggleGroup);
        formule3.setToggleGroup(toggleGroup);

        canvas.setOnMouseMoved(mouseEvent -> {
            changeCursor(mouseEvent);
        });
        canvas.setOnMouseClicked(mouseEvent -> {
            showNodeDetails(mouseEvent);
        });

        // Set up the mouse hover events for "formules"
        formules.setOnMouseEntered(event -> showHoverImage());
        formules.setOnMouseExited(event -> hideHoverImage());


        // About Us section

        aboutUs.setStyle("-fx-text-fill: blue;");
        aboutUs.setCursor(Cursor.DEFAULT);
        aboutUs.setOnMouseEntered(event -> {
            // Change the text color to purple
            aboutUs.setStyle("-fx-text-fill: purple;");

            // Change cursor to HAND
            aboutUs.setCursor(Cursor.HAND);

            // Show the elements
            auSubScene.setVisible(true);
            au1.setVisible(true);
            au2.setVisible(true);
            au3.setVisible(true);
            au4.setVisible(true);
            au5.setVisible(true);
            au6.setVisible(true);
            au7.setVisible(true);
            au8.setVisible(true);
            au9.setVisible(true);
            au10.setVisible(true);
        });

        aboutUs.setOnMouseExited(event -> {
            aboutUs.setStyle("-fx-text-fill: blue;");
            aboutUs.setCursor(Cursor.DEFAULT);

            // Hide the elements
            auSubScene.setVisible(false);
            au1.setVisible(false);
            au2.setVisible(false);
            au3.setVisible(false);
            au4.setVisible(false);
            au5.setVisible(false);
            au6.setVisible(false);
            au7.setVisible(false);
            au8.setVisible(false);
            au9.setVisible(false);
            au10.setVisible(false);
        });

    }


    private void showHoverImage() {
        hoverImage.setVisible(true);
    }

    private void hideHoverImage() {
        hoverImage.setVisible(false);
    }

    public void plotChart1() {
        chart1.getData().removeAll(Collections.singleton(chart1.getData().setAll()));
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Variation d'énergie totale");
        chart1.getXAxis().setLabel("Rounds");
        chart1.getYAxis().setLabel("Énergie (en joules)");
        Director director = Director.getInstance();
        int i = 0;
        for (Double energy : director.getTotalEnergyPerRound()) {
            series1.getData().add(new XYChart.Data<>(String.valueOf(i), energy));
            i++;
        }
        chart1.getData().add(series1);
    }

    public void plotChart2() {
        chart2.getData().removeAll(Collections.singleton(chart2.getData().setAll()));
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Durée de vie des noeuds.");
        chart2.getXAxis().setLabel("Rounds");
        chart2.getYAxis().setLabel("Nbre de noeuds actifs");
        Director director = Director.getInstance();
        int i = 0;
        for (Integer deadNodes : director.getDeadNodesPerRound()) {
            series2.getData().add(new XYChart.Data<>(String.valueOf(i), (numNodes - deadNodes)));
            i++;
        }
        chart2.getData().add(series2);
    }

    public void plotChart3(Node node) {
        chart3.getData().removeAll(Collections.singleton(chart3.getData().setAll()));
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Énergie par Round.");
        chart3.getXAxis().setLabel("Rounds");
        chart3.getYAxis().setLabel("Énergie");
        int i = 0;
        for (Double energy : node.getLastEnergyPerRound()) {
            series3.getData().add(new XYChart.Data<>(String.valueOf(i), energy));
            i++;
        }
        chart3.getData().add(series3);
    }

    @FXML
    public void showInfos() {
        hideDetails();
        plotChart1();
        plotChart2();
    }

    public void changeCursor(MouseEvent mouseEvent) {
        if (!simuler.isDisabled()) {return;}
        canvas.setCursor(Cursor.CROSSHAIR);
        for (Node node : director.getListNodes()) {
            if(node.clicked(mouseEvent.getX(), mouseEvent.getY(), 20)) {
                canvas.setCursor(Cursor.HAND);
                break;
            }
        }
    }

    public void showNodeDetails(MouseEvent mouseEvent) {
        if (!simuler.isDisabled()) {return;}
        for (Node node : director.getListNodes()) {
            if(node.clicked(mouseEvent.getX(), mouseEvent.getY(), 20)) {
                setDetailsVisible();
                getNodeInfos(node);
                selectedNode = node;
                break;
            }
        }
    }

    public void hideDetails() {
        subScene.setVisible(false);
        general.setVisible(false);
        formules.setVisible(false);
        nodeTitle.setVisible(false);
        nodeCanvas.setVisible(false);
        role.setVisible(false);
        CH.setVisible(false);
        timeslot.setVisible(false);
        chart3.setVisible(false);
        logs.setVisible(false);
        etat.setVisible(false);
        logsLabel.setVisible(false);
        formules.setDisable(true);

        selectedNode = null;
    }

    public void setDetailsVisible() {
        subScene.setVisible(true);
        general.setVisible(true);
        formules.setVisible(true);
        nodeTitle.setVisible(true);
        nodeCanvas.setVisible(true);
        role.setVisible(true);
        CH.setVisible(true);
        timeslot.setVisible(true);
        chart3.setVisible(true);
        logs.setVisible(true);
        etat.setVisible(true);
        logsLabel.setVisible(true);
        formules.setDisable(false);
    }
    
    public void getNodeInfos(Node node) {
        director = Director.getInstance();
        nodeTitle.setText("Noeud " + node.getId());

        nodeCanvas.getGraphicsContext2D().clearRect(0, 0, nodeCanvas.getWidth(), nodeCanvas.getHeight());
        node.draw(nodeCanvas.getGraphicsContext2D(), 29, director.getRound_number(), 0, 0);

        if(node.isOn()) {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(6);
            etat.setText("✓ État: Actif (E = " + df.format(node.getEnergy().get(node.getEnergy().size() - 1).getValue()) + " Joules )");

            if(node.getIsCH().get(Math.min(director.getRound_number(), node.getIsCH().size() - 1))) {
                role.setText("✓ Rôle: Cluster Head");
                String text = "✓ Membres: ";
                if (node.getCM().size() == 0) {
                    text += "aucun membre";
                    timeslot.setText("✓ Durée timeslot: 0 secondes");
                } else {
                    for (Node member : node.getCM()) {
                        text += "noeud " + member.getId() + "; ";
                    }
                    df.setMaximumFractionDigits(3);
                    double timeslotDuration = (double) roundDuration / node.getCM().size();
                    timeslot.setText("✓ Durée timeslot: " + df.format(timeslotDuration) + " secondes");
                }
                CH.setText(text);
            } else if (node.getCH().equals(node)) {
                role.setText("✓ Rôle: sans rôle");
                CH.setText("✓ Cluster Head: aucun");
                timeslot.setText("✓ Timeslot: aucun");
            } else {
                role.setText("✓ Rôle: Membre de Cluster");
                CH.setText("✓ Cluster Head: Noeud " + node.getCH().getId());
                timeslot.setText("✓ Timeslot: " + (node.getCH().getCM().indexOf(node) + 1));
            }

        } else {
            etat.setText("✓ État: Etteint");
            role.setText("✓ Rôle: sans rôle");
            CH.setText("✓ Cluster Head: aucun");
            timeslot.setText("✓ Timeslot: aucun");
        }


        plotChart3(node);
        logs.setText(node.getLog());
    }

    @FXML
    public void general() {
        hideDetails();
        showInfos();
    }

}