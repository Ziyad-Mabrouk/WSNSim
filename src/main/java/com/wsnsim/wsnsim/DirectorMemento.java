package com.wsnsim.wsnsim;

import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;

import java.util.ArrayList;

public class DirectorMemento {
    private ArrayList<Node> listNodes = new ArrayList<>();
    private int round_number = 0;
    private ArrayList<Integer> deadNodesPerRound = new ArrayList<>();


    public DirectorMemento(ArrayList<Node> listNodes, int round_number, ArrayList<Integer> deadNodesPerRound) {
        this.listNodes = listNodes;
        this.round_number = round_number;
        this.deadNodesPerRound = deadNodesPerRound;
    }

    public ArrayList<Node> getListNodes() {
        return listNodes;
    }

    public int getRound_number() {
        return round_number;
    }

    public ArrayList<Integer> getDeadNodesPerRound() {
        return deadNodesPerRound;
    }
}
