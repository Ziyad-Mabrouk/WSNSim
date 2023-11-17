package com.wsnsim.wsnsim;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Random;

public class Node implements Cloneable {
    private int id, x, y, radius;
    private ArrayList<Pair<Integer, Double>> energy;
    private boolean isOn;
    private Node CH;
    private Color color;
    private double distanceToCH;
    private ArrayList<Boolean> isCH = new ArrayList<>();
    private String log;

    public Node(int id, int x, int y, ArrayList<Pair<Integer, Double>> energy, int radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.radius = radius;
        isOn = true;
        CH = this;
        color = Color.DARKCYAN;
        distanceToCH = 0;
    }

    @Override
    public Node clone() {
        try {
            Node cloned = (Node) super.clone();
            // Perform deep copy for energy ArrayList
            cloned.energy = new ArrayList<>();
            for (Pair<Integer, Double> pair : this.energy) {
                cloned.energy.add(new Pair<>(pair.getKey(), pair.getValue()));
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Node() {
        
    }


    private void setDistanceToCH(double distanceToCH) {
    }

    private void setCH(Node ch) {
    }

    public void setY(int y) {
    }

    public void setX(int x) {
    }

    private void setId(int id) {
    }

    public double getDistanceToCH() {
        return distanceToCH;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ArrayList<Pair<Integer, Double>> getEnergy() {
        return this.energy;
    }

    public void setEnergy(ArrayList<Pair<Integer, Double>> energy) {
        this.energy = energy;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
        if (on == false) {
            setLog(getLog() + "Énergie épuisée, arrêt...\n");
        }
    }

    public ArrayList<Boolean> getIsCH() {
        return isCH;
    }

    public void setIsCH(ArrayList<Boolean> isCH) {
        this.isCH = isCH;
    }

    public void draw(GraphicsContext gc, int circleSize, int round_number) {
        if (!isOn) {
            color = Color.GRAY;
            gc.setFill(color);
            gc.fillOval(x, y, circleSize, circleSize);

        } else {
            gc.setFill(color);
            if (isCH.get(round_number).equals(true)) {
                gc.fillRect(x, y, circleSize, circleSize);
            } else {
                gc.fillOval(x, y, circleSize, circleSize);
            }
        }

        int fontSize = circleSize / 2;
        Color textColor = Color.WHITE;

        gc.setFill(textColor);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));

        Text text = new Text(String.valueOf(id));
        text.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();

        double textX = x + (circleSize - textWidth) / 2;
        double textY = y + (circleSize + textHeight) / 2;

        gc.fillText(String.valueOf(id), textX, textY);
    }

    public void draw(GraphicsContext gc, int circleSize, int round_number, int x, int y) {
        if (!isOn) {
            color = Color.GRAY;
            gc.setFill(color);
            gc.fillOval(x, y, circleSize, circleSize);

        } else {
            gc.setFill(color);
            int validRound = Math.min(round_number, isCH.size() - 1);
            if (isCH.get(validRound)) {
                gc.fillRect(x, y, circleSize, circleSize);
            } else {
                gc.fillOval(x, y, circleSize, circleSize);
            }
        }

        int fontSize = circleSize / 2;
        Color textColor = Color.WHITE;

        gc.setFill(textColor);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));

        Text text = new Text(String.valueOf(id));
        text.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();

        double textX = x + (circleSize - textWidth) / 2;
        double textY = y + (circleSize + textHeight) / 2;

        gc.fillText(String.valueOf(id), textX, textY);
    }

    public void sense(int round_number){
        if(!isOn) {
            return;
        }

        Random random = new Random();
        int eventCount = 0;
        double poissonLambda = 0.5;
        while (random.nextDouble() < poissonLambda) {
            eventCount++;
        }

        double sensingEnergy = 50 * Math.pow(10, -6);
        double energyConsumed = sensingEnergy * eventCount;

        double updatedEnergy = energy.get(energy.size() - 1).getValue();
        updatedEnergy -= energyConsumed;

        if (updatedEnergy <= 0.0) {
            updatedEnergy = 0.0;
            setOn(false);
        }

        energy.add(new Pair<>(round_number, updatedEnergy));
    }

    public void updateTxEnergy(double distance, int roundNumber) {
        double Elec = 50 * Math.pow(10, -9);
        double l = 1.0;
        double epsFs = 10 * Math.pow(10, -12);
        double epsMp = 0.0013 * Math.pow(10, -12);
        double d0 = Math.sqrt(epsFs / epsMp);
        double Etx;
        if (distance < d0) {
            Etx = l*Elec + l * epsFs * Math.pow(distance,2);
        } else {
            Etx = l*Elec + l * epsMp * Math.pow(distance,4);
        }

        double E = energy.get(energy.size() - 1).getValue();
        E -= Etx;

        if (E <= 0.0) {
            E = 0.0;
            setOn(false);
        }

        energy.add(new Pair<>(roundNumber, E));
    }

    public void transmit(Node targetNode, String msg, int roundNumber){
        if(!isOn) {
            return;
        }
        log += msg + " transmis vers le noeud " + targetNode.getId() + ".\n";
        double distance = Math.sqrt(Math.pow(targetNode.x - this.x, 2) + Math.pow(targetNode.y - this.y, 2));
        updateTxEnergy(distance, roundNumber);
    }

    public void transmitToSink(int roundNumber) {
        if(!isOn) {
            return;
        }
        updateTxEnergy(radius, roundNumber);
        log += "aggregated data transmis vers le sink.\n";
    }

    public void updateRxEnergy(int roundNumber) {
        double Elec = 50 * Math.pow(10, -9);
        double l = 1.0;
        double Erx = l * Elec;

        double E = energy.get(energy.size() - 1).getValue();
        E -= Erx;

        if (E <= 0.0) {
            E = 0.0;
            setOn(false);
        }

        energy.add(new Pair<>(roundNumber, E));
    }

    public void receive(Node sourceNode, String msg, int roundNumber){
        if(!isOn) {
            return;
        }

        log += msg + " reçu du noeud " + sourceNode.getId() + ".\n";

        if (msg.equals("request msg")) {
            double distance = Math.sqrt(Math.pow(sourceNode.x - this.x, 2) + Math.pow(sourceNode.y - this.y, 2));
            // distanceToCH == 0 => It's the first req msg ; distance < distanceToCH => This CH is closer to me
            if (distanceToCH == 0 || distance < distanceToCH) {
                distanceToCH = distance;
                CH = sourceNode;
                color = CH.color;
            }
        }

        updateRxEnergy(roundNumber);
    }

    public boolean intersects(int x, int y, int circleSize) {
        int minDistance = circleSize;

        double distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));

        return distance < minDistance;
    }

    public boolean clicked(double x, double y, int circleSize) {
        return Math.sqrt(Math.pow(this.x + (circleSize / 2) - x, 2)) < (circleSize / 2) && Math.sqrt(Math.pow(this.y + (circleSize / 2) -y, 2)) < (circleSize / 2);
    }

    public boolean isWithinCommunicationRadius(Node node) {
        double distance = Math.sqrt(Math.pow(this.x - node.getX(), 2) + Math.pow(this.y - node.getY(), 2));
        return distance < this.radius + node.radius;
    }

    public ArrayList<Node> getNeighbors() {
        Director director = Director.getInstance();
        ArrayList<Node> neighbors = new ArrayList<>();
        ArrayList<Node> listNodes = director.getCurrentNonCHList();

        for (Node node : listNodes) {
            if (isWithinCommunicationRadius(node)) {
                if (!node.equals(this)) {
                    neighbors.add(node);
                }
            }
        }

        return neighbors;
    }

    public boolean wasCH() {
        for (Boolean wasCH : isCH) {
            if(wasCH){
                return true;
            }
        }
        return false;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void resetCH() {
        CH = this;
        distanceToCH = 0;
        color = Color.DARKCYAN;
    }

    public void drawChLink(GraphicsContext gc, int circleSize) {
        gc.setStroke(color);
        gc.setLineWidth(0.5);

        double startX = x + (circleSize / 2);
        double startY = y + (circleSize / 2);

        double targetX = CH.getX() + (circleSize / 2);
        double targetY = CH.getY() + (circleSize / 2);

        gc.strokeLine(startX, startY, targetX, targetY);
    }

    public int calculateRs() {
        int rs = 0;
        for (Boolean wasCH : isCH) {
            rs++;
            if (wasCH) {
                rs = 0;
            }
        }
        return rs;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Node getCH() {
        return CH;
    }

    public ArrayList<Node> getCM() {
        Director director = Director.getInstance();
        ArrayList<Node> CM = new ArrayList<>();
        ArrayList<Node> listNodes = director.getCurrentNonCHList();

        for (Node node : listNodes) {
            if (node.getCH().getId() == id) {
                CM.add(node);
            }
        }

        return CM;
    }

    public ArrayList<Double> getLastEnergyPerRound() {
        ArrayList<Double> E = new ArrayList<>();

        // Add the initial energy for round 0
        E.add(energy.get(0).getValue());

        for (int i = 1; i < energy.size(); i++) {
            // If the round number changes, add the energy value for the new round
            if (energy.get(i - 1).getKey() != energy.get(i).getKey()) {
                E.add(energy.get(i - 1).getValue());
            }
        }

        // Add the last recorded energy value for the last round
        E.add(energy.get(energy.size() - 1).getValue());

        return E;
    }

    public Double getEnergyPerRound(int index) {
        return getLastEnergyPerRound().get(index);
    }
}
