package com.wsnsim.wsnsim;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Node {
    private int id;
    private int x;
    private int y;
    private ArrayList<Double> energy = new ArrayList<>();
    private boolean isOn = true;
    private Node CH = this;
    private Color color = Color.DARKCYAN;
    private double distanceToCH = 0;

    private ArrayList<Boolean> isCH = new ArrayList<>();
    private int radius = 50;
    //private NodeState state;
    //public NodeState m_NodeState;
    //public ClusterHead m_ClusterHead;

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<Double> getEnergy() {
        return this.energy;
    }

    public void setEnergy(ArrayList<Double> energy) {
        this.energy = energy;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public ArrayList<Boolean> getIsCH() {
        return isCH;
    }

    public void setIsCH(ArrayList<Boolean> isCH) {
        this.isCH = isCH;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void draw(GraphicsContext gc, int circleSize, int round_number) {
        if (!isOn) {
            color = Color.GRAY;
        }
        gc.setFill(color);
        if (isCH.get(round_number).equals(true)) {
            gc.fillRect(x, y, circleSize, circleSize);

        } else {
            gc.fillOval(x, y, circleSize, circleSize);
        }

        // Set font size based on circle size
        int fontSize = circleSize / 2;
        Color textColor = Color.WHITE;

        gc.setFill(textColor);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));

        // Get the bounds of the text to calculate its width and height
        Text text = new Text(String.valueOf(id));
        text.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();

        // Calculate text position to center it inside the circle
        double textX = x + (circleSize - textWidth) / 2;
        double textY = y + (circleSize + textHeight) / 2;

        // Draw the number inside the circle
        gc.fillText(String.valueOf(id), textX, textY);
    }

    public void sense(){

    }

    public void updateTxEnergy(double distance) {
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

        double E = energy.get(energy.size() - 1);
        //E -= Etx;
        //test
        E -= 0.01;
        if (E < 0.0) {
            energy.add(0.0);
            isOn = false;
        } else {
            energy.add(E);
        }
    }

    public void transmit(Node targetNode, GraphicsContext gc, int circleSize){
        gc.setStroke(Color.BLACK); // Set the color of the arrow to red (you can change it to any color you want)
        gc.setLineWidth(1.0); // Set the width of the arrow line

        // Calculate the arrow starting point (center of the current node)
        double startX = x + (circleSize / 2);
        double startY = y + (circleSize / 2);

        // Calculate the arrow ending point (center of the target node)
        double targetX = targetNode.getX() + (circleSize / 2);
        double targetY = targetNode.getY() + (circleSize / 2);

        // Draw the arrow line
        gc.strokeLine(startX, startY, targetX, targetY);

        // Draw the arrowhead (you can customize the arrowhead appearance if needed)
        double arrowLength = 10;
        double arrowAngle = Math.toRadians(30);
        double angle = Math.atan2(targetY - startY, targetX - startX);
        double arrowX1 = targetX - arrowLength * Math.cos(angle + arrowAngle);
        double arrowY1 = targetY - arrowLength * Math.sin(angle + arrowAngle);
        double arrowX2 = targetX - arrowLength * Math.cos(angle - arrowAngle);
        double arrowY2 = targetY - arrowLength * Math.sin(angle - arrowAngle);

        gc.strokeLine(targetX, targetY, arrowX1, arrowY1);
        gc.strokeLine(targetX, targetY, arrowX2, arrowY2);

        double distance = Math.sqrt(Math.pow(targetNode.x - this.x, 2) + Math.pow(targetNode.y - this.y, 2));
        updateTxEnergy(distance);

    }

    public void updateRxEnergy(double distance) {
        double Elec = 50 * Math.pow(10, -9);
        double l = 1.0;
        double Erx = l * Elec;

        double E = energy.get(energy.size() - 1);
        //E -= Erx;
        //Test
        E -= 0.01;
        if (E < 0.0) {
            energy.add(0.0);
            isOn = false;
        } else {
            energy.add(E);
        }
    }

    public void receive(Node sourceNode, GraphicsContext gc, int circleSize){
        gc.setStroke(Color.GREEN);
        if (!isOn) {
            gc.setStroke(Color.RED);
        }
        gc.setLineWidth(1.0); // Set the width of the arrow line

        // Calculate the arrow starting point (center of the current node)
        double startX = sourceNode.getX() + (circleSize / 2);
        double startY = sourceNode.getY() + (circleSize / 2);

        // Calculate the arrow ending point (center of the target node)
        double targetX = x + (circleSize / 2);
        double targetY = y + (circleSize / 2);

        // Draw the arrow line
        gc.strokeLine(startX, startY, targetX, targetY);

        // Draw the arrowhead (you can customize the arrowhead appearance if needed)
        double arrowLength = 10;
        double arrowAngle = Math.toRadians(30);
        double angle = Math.atan2(targetY - startY, targetX - startX);
        double arrowX1 = targetX - arrowLength * Math.cos(angle + arrowAngle);
        double arrowY1 = targetY - arrowLength * Math.sin(angle + arrowAngle);
        double arrowX2 = targetX - arrowLength * Math.cos(angle - arrowAngle);
        double arrowY2 = targetY - arrowLength * Math.sin(angle - arrowAngle);

        gc.strokeLine(targetX, targetY, arrowX1, arrowY1);
        gc.strokeLine(targetX, targetY, arrowX2, arrowY2);

        double distance = Math.sqrt(Math.pow(sourceNode.x - this.x, 2) + Math.pow(sourceNode.y - this.y, 2));
        updateRxEnergy(distance);

    }

    public void receiveCHMsg(Node CH) {
        double distance = Math.sqrt(Math.pow(CH.getX() - this.x, 2) + Math.pow(CH.getY() - this.y, 2));
        if (distanceToCH == 0) {
            distanceToCH = distance;
            this.CH = CH;
            color = CH.color;
        } else {
            if (distanceToCH > distance) {
                distanceToCH = distance;
                this.CH = CH;
                color = CH.color;
            }
        }
        updateRxEnergy(distance);
    }

    public void listen(){

    }

    public void sleep(){

    }

    public boolean intersects(int x, int y, int circleSize) {
        int minDistance = circleSize; // Minimum distance between circle centers for non-overlapping nodes

        // Calculate distance between centers of circles using the distance formula
        double distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));

        // Check if distance is greater than or equal to minimum allowed distance
        return distance < minDistance;
    }

    public boolean clicked(double x, double y, int circleSize) {
        return Math.sqrt(Math.pow(this.x + (circleSize / 2) - x, 2)) < (circleSize / 2) && Math.sqrt(Math.pow(this.y + (circleSize / 2) -y, 2)) < (circleSize / 2);
    }

    public boolean isWithinCommunicationRadius(Node node) {
        double distance = Math.sqrt(Math.pow(this.x - node.getX(), 2) + Math.pow(this.y - node.getY(), 2));
        return distance < this.radius + node.radius;
    }

    public ArrayList<Node> getNeighbors(int roundId) {
        Director director = Director.getInstance();
        ArrayList<Node> neighbors = new ArrayList<>();
        ArrayList<Node> listNodes = director.getCurrentNonCHList(roundId);

        for (Node node : listNodes) {
            if (isWithinCommunicationRadius(node)) {
                neighbors.add(node);
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
        gc.setStroke(color); // Set the color of the arrow to red (you can change it to any color you want)
        gc.setLineWidth(0.5); // Set the width of the arrow line

        // Calculate the arrow starting point (center of the current node)
        double startX = x + (circleSize / 2);
        double startY = y + (circleSize / 2);

        // Calculate the arrow ending point (center of the target node)
        double targetX = CH.getX() + (circleSize / 2);
        double targetY = CH.getY() + (circleSize / 2);

        // Draw the arrow line
        gc.strokeLine(startX, startY, targetX, targetY);
    }
}
