package com.wsnsim.wsnsim;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    public static Director getInstance(int numNodes, double CHdensity, double canvasWidth, double canvasHeight, int circleSize) {
        if (director == null) {
            director = new Director(canvasWidth, canvasHeight, circleSize);
        }
        director.numNodes = numNodes;
        director.generateNodesList(CHdensity);
        return director;
    }

    public static Director getInstance(){
        return director;
    }


    public void drawNodes(GraphicsContext gc) {
        for (Node node : listNodes) {
            node.draw(gc, circleSize, round_number);
        }
    }

    public void generateNodesList(double CHdensity) {
        listNodes.clear();
        for (int i = 1; i <= numNodes; i++) {
            ArrayList<Integer> coordinates = randomGenerator.generateNodeCoordinates(canvasWidth, canvasHeight, circleSize, listNodes);
            ArrayList<Boolean> isCH = new ArrayList<>();
            isCH.add(randomGenerator.chSelection(round_number, CHdensity*0.01));
            Node node = new Node(i, coordinates.get(0), coordinates.get(1));
            node.setIsCH(isCH);
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

    public void setUp(GraphicsContext gc,int round_number, int circleSize) {
        ArrayList<Node> CHList = director.getCurrentCHList(round_number);

        for (Node CH : CHList) {
            ArrayList<Node> potential_members = CH.getNeighbors(round_number);
            for (Node pmember : potential_members) {
                CH.transmit(pmember, gc, circleSize);
            }
        }
    }

    public void nextRound(GraphicsContext gc, double CHdensity) {
        round_number++;
        for (Node node : listNodes) {
            ArrayList<Boolean> isCH = node.getIsCH();
            isCH.add(randomGenerator.chSelection(node, round_number, CHdensity*0.01));
            node.setIsCH(isCH);
            node.draw(gc,circleSize,round_number);
        }
        director.setUp(gc,round_number, circleSize);

    }

    public void clear() {
        round_number = 0;
        listNodes.clear();
    }

    public int getRound_number() {
        return round_number;
    }

}
