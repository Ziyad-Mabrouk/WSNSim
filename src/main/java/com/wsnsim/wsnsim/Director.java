package com.wsnsim.wsnsim;

import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import java.util.ArrayList;

public class Director {
    private static Director director;
    private GraphicsContext gc;
    private double canvasWidth, canvasHeight;
    private int circleSize = 20;
    private ArrayList<Node> listNodes = new ArrayList<>();
    private final RandomGenerator randomGenerator = new RandomGenerator();
    private int numNodes, roundDuration, formule, commRadius;
    private double chDensity, E_max, Elec, epsfs, epsmp, l, sensing_energy;
    private int round_number = 0;
    private ArrayList<Integer> deadNodesPerRound = new ArrayList<>();

    private Director(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.gc = gc;
    }

    public static Director getInstance(int numNodes, int roundDuration, double chDensity, int formule, int commRadius, double E_max, double Eelc, double l, double epsfs, double epsmp, double sensing_energy, GraphicsContext gc, double canvasWidth, double canvasHeight) {
        if (director == null) {
            director = new Director(gc, canvasWidth, canvasHeight);
        }
        director.numNodes = numNodes;
        director.roundDuration = roundDuration;
        director.chDensity = chDensity;
        director.formule = formule;
        director.commRadius = commRadius;
        director.E_max = E_max;
        director.Elec = Eelc;
        director.l = l;
        director.epsfs = epsfs;
        director.epsmp = epsmp;
        director.sensing_energy = sensing_energy;

        director.generateNodesList();

        return director;
    }

    public static Director getInstance(){
        return director;
    }

    public double getElec() {
        return Elec;
    }

    public double getEpsfs() {
        return epsfs;
    }

    public double getEpsmp() {
        return epsmp;
    }

    public double getL() {
        return l;
    }

    public double getSensing_energy() {
        return sensing_energy;
    }

    public void drawNodes() {
        for (Node node : listNodes) {
            node.draw(gc, circleSize, round_number);
        }
    }

    public void generateNodesList() {
        listNodes.clear();
        for (int i = 1; i <= numNodes; i++) {
            ArrayList<Integer> coordinates = randomGenerator.generateNodeCoordinates(canvasWidth, canvasHeight, circleSize, listNodes);

            ArrayList<Pair<Integer, Double>> energy = new ArrayList<>();
            Pair<Integer, Double> initialEnergy = new Pair<>(0, E_max);
            energy.add(initialEnergy);

            Node node = new Node(i, coordinates.get(0), coordinates.get(1), energy, commRadius);

            ArrayList<Boolean> isCH = new ArrayList<>();
            isCH.add(randomGenerator.chSelection(node, round_number, chDensity*0.01, formule, E_max));

            node.setIsCH(isCH);
            if (isCH.get(0)) {
                node.setColor(randomGenerator.assignColor());
            }

            node.setLog("Noeud " + node.getId() + "\n\n");
            node.setLog(node.getLog() + "Round: " + (round_number + 1) + ".\n");
            listNodes.add(node);
        }
    }

    public ArrayList<Node> getListNodes() {return listNodes;}

    public ArrayList<Node> getCurrentCHList() {
        ArrayList<Node> CHList = new ArrayList<>();
        for (Node node : listNodes) {
            if (node.isOn()) {
                if (node.getIsCH().get(round_number)) {
                    CHList.add(node);
                }
            }
        }
        return CHList;
    }

    public ArrayList<Node> getCurrentNonCHList() {
        ArrayList<Node> NonCHList = new ArrayList<>();
        for (Node node : listNodes) {
            if (node.isOn()) {
                if (!node.getIsCH().get(round_number)) {
                    NonCHList.add(node);
                }
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
        DirectorHistory directorHistory = DirectorHistory.getInstance();
        if (directorHistory.numSaves() >= round_number+1) {
            return;
        }
        ArrayList<Node> CHList = getCurrentCHList();
        for (Node CH : CHList) {
            if (!CH.isOn()) {continue;}
            ArrayList<Node> potential_members = CH.getNeighbors();
            CH.setLog(CH.getLog() + "Round: " + (round_number + 1) + " -> phase de setup.\n");
            for (Node pmember : potential_members) {
                CH.transmit(pmember, "request msg", round_number);
                if (!pmember.isOn()) {continue;}
                pmember.receive(CH, "request msg", round_number);
            }

            ArrayList<Node> members = CH.getCM();
            for (Node member : members) {
                member.transmit(CH, "membership msg", round_number);
                CH.receive(member, "membership msg", round_number);
            }
        }
    }

    public void stableTransmission() {
        DirectorHistory directorHistory = DirectorHistory.getInstance();
        if (directorHistory.numSaves() >= round_number+1) {
            return;
        }
        ArrayList<Node> CHList = getCurrentCHList();
        for (Node CH : CHList) {
            CH.setLog(CH.getLog() + "Round: " + (round_number + 1) + " -> phase de transmission.\n");
            ArrayList<Node> members = CH.getCM();

            for (Node member : members) {
                CH.transmit(member, "timeslot structure msg", round_number);
                member.receive(CH, "timeslot structure msg", round_number);
            }

            int i = 1;
            for (Node member : members) {
                if (member.isOn()) {
                    member.sense(round_number);
                    member.transmit(CH, "[timeslot " + i + "] msg", round_number);
                    CH.receive(member, "[timeslot " + i + "] msg", round_number);
                }
                i++;
            }

            if (members.size() != 0) {CH.transmitToSink(round_number);}
        }
        deadNodesPerRound.add(numOfDeadNodes());

        for (Node node : listNodes) {
            ArrayList<Pair<Integer, Double>> energy = node.getEnergy();
            Double E = energy.get(energy.size() - 1).getValue();
            Pair<Integer, Double> latestEnergy = new Pair<>(round_number, E);
            energy.add(latestEnergy);
            if (node.isOn()) {
                node.setLog(node.getLog() + "Fin de Round: " + (round_number + 1) + ".\n\n");
            }
        }

        save();
    }

    public void nextRound() {
        round_number++;
        resetNodes();
        for (Node node : listNodes) {
            if(node.isOn()) {
                node.setLog(node.getLog() + "Round: " + (round_number + 1) + ".\n");
                ArrayList<Boolean> isCH = node.getIsCH();
                isCH.add(randomGenerator.chSelection(node, round_number, chDensity * 0.01, formule, E_max));
                node.setIsCH(isCH);
                if (isCH.get(round_number)) {
                    node.setColor(randomGenerator.assignColor());
                }
            } else {
                //
            }

        }
    }


    public void clear() {
        round_number = 0;
        listNodes.clear();
        deadNodesPerRound.clear();
    }

    public int getRound_number() {
        return round_number;
    }

    public void drawChLinks() {
        for (Node node : listNodes) {
            node.drawChLink(gc, circleSize);
        }
    }

    public ArrayList<Double> getTotalEnergyPerRound() {
        ArrayList<Double> totalEnergy = new ArrayList<>();
        int numRounds = round_number + 1;

        for (int i = 0; i < numRounds; i++) {
            totalEnergy.add(0.0);
        }

        for (Node node : listNodes) {
            ArrayList<Double> nodeEnergyPerRound = node.getLastEnergyPerRound();

            for (int i = 0; i < numRounds && i < nodeEnergyPerRound.size(); i++) {
                totalEnergy.set(i, totalEnergy.get(i) + nodeEnergyPerRound.get(i));
            }
        }

        return totalEnergy;
    }


    public int numOfDeadNodes() {
        int numOfDeadNodes = 0;
        for (Node node : listNodes) {
            if (!node.isOn()) {
                numOfDeadNodes++;
            }
        }
        return numOfDeadNodes;
    }

    public ArrayList<Integer> getDeadNodesPerRound() {
        return deadNodesPerRound;
    }
    public void save() {
        DirectorHistory directorHistory = DirectorHistory.getInstance();
        ArrayList<Node> clonedListNodes = new ArrayList<>();
        for (Node node : listNodes) {
            clonedListNodes.add(node.clone());
        }
        ArrayList<Integer> clonedDeadNodesPerRound = new ArrayList<>(deadNodesPerRound);
        DirectorMemento directorMemento = new DirectorMemento(clonedListNodes, round_number, clonedDeadNodesPerRound);
        directorHistory.save(directorMemento);
    }

    public void restore(int index) {
        DirectorHistory directorHistory = DirectorHistory.getInstance();
        DirectorMemento directorMemento = directorHistory.pop(index);

        // Deep copy the list of nodes
        listNodes.clear();
        for (Node node : directorMemento.getListNodes()) {
            listNodes.add(node.clone());
        }

        // Copy other primitive types
        deadNodesPerRound = new ArrayList<>(directorMemento.getDeadNodesPerRound());
        round_number = directorMemento.getRound_number();
    }

}
