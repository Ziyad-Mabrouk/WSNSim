package com.wsnsim.wsnsim;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class Director {
    private static Director director;
    private ArrayList<Node> listNodes = new ArrayList<>();
    private final RandomGenerator randomGenerator = new RandomGenerator();
    private int numNodes;
    private double canvasWidth;
    private double canvasHeight;
    private int circleSize;
    private int round_number = 0;

    private Director(double canvasWidth, double canvasHeight, int circleSize) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.circleSize = circleSize;
    }

    public static Director getInstance(int numNodes, double canvasWidth, double canvasHeight, int circleSize) {
        if (director == null) {
            director = new Director(canvasWidth, canvasHeight, circleSize);
        }
        director.numNodes = numNodes;
        director.generateNodesList();
        return director;
    }

    public void drawNodes(GraphicsContext gc) {
        for (Node node : listNodes) {
            node.draw(gc, circleSize, round_number);
        }
    }

    public void generateNodesList() {
        listNodes.clear();
        for (int i = 1; i <= numNodes; i++) {
            ArrayList<Integer> coordinates = randomGenerator.generateNodeCoordinates(canvasWidth, canvasHeight, circleSize, listNodes);
            ArrayList<Boolean> isCH = new ArrayList<>();
            isCH.add(randomGenerator.chSelection());
            Node node = new Node(i, coordinates.get(0), coordinates.get(1));
            node.setIsCH(isCH);
            listNodes.add(node);
        }
    }

    public ArrayList<Node> getListNodes() {
        return listNodes;
    }

}
