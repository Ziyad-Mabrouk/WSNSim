package com.wsnsim.wsnsim;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Director {
    private static Director director;
    private ArrayList<Node> listNodes = new ArrayList<>();
    private final RandomGenerator randomGenerator = new RandomGenerator();
    private int numNodes;
    private GraphicsContext gc;
    private double canvasWidth;
    private double canvasHeight;
    private int circleSize;
    private int round_number = 0;

    private Director(GraphicsContext gc, double canvasWidth, double canvasHeight, int circleSize) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.circleSize = circleSize;
        this.gc = gc;
    }

    public static Director getInstance(int numNodes, double CHdensity, GraphicsContext gc, double canvasWidth, double canvasHeight, int circleSize) {
        if (director == null) {
            director = new Director(gc, canvasWidth, canvasHeight, circleSize);
        }
        director.numNodes = numNodes;
        director.generateNodesList(CHdensity);
        return director;
    }

    public static Director getInstance(){
        return director;
    }


    public void drawNodes() {
        for (Node node : listNodes) {
            node.draw(gc, circleSize, round_number);
        }
    }

    public void generateNodesList(double CHdensity) {
        listNodes.clear();
        for (int i = 1; i <= numNodes; i++) {
            ArrayList<Integer> coordinates = randomGenerator.generateNodeCoordinates(canvasWidth, canvasHeight, circleSize, listNodes);
            ArrayList<Boolean> isCH = new ArrayList<>();
            ArrayList<Double> energy = new ArrayList<>();
            isCH.add(randomGenerator.chSelection(round_number, CHdensity*0.01));
            energy.add(0.5);
            Node node = new Node(i, coordinates.get(0), coordinates.get(1));
            node.setIsCH(isCH);
            node.setEnergy(energy);
            if (isCH.get(0)) {
                node.setColor(randomGenerator.assignColor());
            }
            listNodes.add(node);
        }
    }

    public ArrayList<Node> getListNodes() {
        return listNodes;
    }

    public ArrayList<Node> getCurrentCHList(int roundId) {
        ArrayList<Node> CHList = new ArrayList<>();
        for (Node node : listNodes) {
            if (node.getIsCH().get(roundId)) {
                CHList.add(node);
            }
        }
        return CHList;
    }

    public ArrayList<Node> getCurrentNonCHList(int round_number) {
        ArrayList<Node> NonCHList = new ArrayList<>();
        for (Node node : listNodes) {
            if (!node.getIsCH().get(round_number)) {
                NonCHList.add(node);
            }
        }
        return NonCHList;
    }

    public ArrayList<Node> getNonCHList() {
        ArrayList<Node> NonCHList = new ArrayList<>();
        for (Node node : listNodes) {
            if (!node.wasCH()) {
                NonCHList.add(node);
            }
        }
        return NonCHList;
    }

    public void resetNodes() {
        for (Node node : listNodes) {
            node.resetCH();
        }
    }

    public void setUp() {
        ArrayList<Node> CHList = director.getCurrentCHList(round_number);
        for (Node CH : CHList) {
            ArrayList<Node> potential_members = CH.getNeighbors(round_number);
            for (Node pmember : potential_members) {
                CH.transmit(pmember, gc, circleSize);
                pmember.receiveCHMsg(CH);
            }
        }

    }

    public void nextRound(double CHdensity) {
        round_number++;
        resetNodes();
        for (Node node : listNodes) {
            ArrayList<Boolean> isCH = node.getIsCH();
            isCH.add(randomGenerator.chSelection(node, round_number, CHdensity*0.01));
            node.setIsCH(isCH);
            if (isCH.get(round_number)) {
                node.setColor(randomGenerator.assignColor());
            }
            node.draw(gc,circleSize,round_number);
        }
        //director.setUp(round_number);
    }

    public void clear() {
        round_number = 0;
        listNodes.clear();
    }

    public int getRound_number() {
        return round_number;
    }

    public void drawChLinks() {
        for (Node node : listNodes) {
            node.drawChLink(gc, circleSize);
        }
    }

}
