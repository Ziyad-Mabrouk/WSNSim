package com.wsnsim.wsnsim;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Random;

public class RandomGenerator {

    public ArrayList<Integer> generateNodeCoordinates(double canvasWidth, double canvasHeight, int circleSize, ArrayList<Node> listNodes) {
        boolean isOverlap = true;
        int x = 0;
        int y = 0;

        while (isOverlap) {
            isOverlap = false;
            x = (int) (Math.random() * (canvasWidth - circleSize));
            y = (int) (Math.random() * (canvasHeight - circleSize));

            // Check for overlap with existing circles
            for (Node existing_node : listNodes) {
                if (existing_node.intersects(x, y, circleSize)) {
                    isOverlap = true;
                    break;
                }
            }
        }

        ArrayList<Integer> coordinates = new ArrayList<>();
        coordinates.add(x);
        coordinates.add(y);
        return coordinates;
    }

    public boolean chSelection(Node node, int roundId, double P, int formule, double E_max) {
        if (!node.isOn() || node.wasCH()) {
            return false;
        }

        Random random = new Random();
        double rnd = random.nextDouble();
        double T = P /(1-P*(roundId%(1/P)));
        switch (formule) {
            case 2:
                T *= (node.getEnergy().get(node.getEnergy().size() - 1).getValue() / E_max);
                break;
            case 3:
                T *= ( node.getEnergy().get(node.getEnergy().size() - 1).getValue() / E_max) + ( (node.calculateRs() / (1/P)) * (1 - (node.getEnergy().get(node.getEnergy().size() - 1).getValue() / E_max) ) );
                break;
        }
        return (rnd < T);
    }

    public Color assignColor() {
        Color standardColor = Color.DARKCYAN;
        Color color;
        do {
            color = Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
        } while (color.equals(standardColor));
        return color;
    }


}
