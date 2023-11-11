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
        Color color = Color.DARKCYAN;

        if (wasCH()) {
            color = Color.SKYBLUE;
        }

        if (isCH.get(round_number).equals(true)) {
            color = Color.RED;
            //gc.setFill(color);
            //gc.strokeOval(x-radius/2, y-radius/2, radius,radius);
        }
        /*
        if (!isOn) {
            color = Color.GRAY;
        }
        */
        gc.setFill(color);
        gc.fillOval(x, y, circleSize, circleSize);

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

    public boolean transmit(Node targetNode, GraphicsContext gc, int circleSize){
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

        //handle energy dissipation
        return false;
    }

    public void receive(Node node){

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
}
