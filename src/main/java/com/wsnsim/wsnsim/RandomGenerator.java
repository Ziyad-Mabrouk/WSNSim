package com.wsnsim.wsnsim;

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

    //test function
    public boolean chSelection() {
        Random random = new Random();
        return random.nextBoolean();
    }

    public boolean chSelection(Node node, int roundId, double P) {
        if (node.wasCH()) {
            return false;
        }
        Random random = new Random();
        double rnd = random.nextDouble();
        double T = P /(1-P*(roundId%(1/P)));

        return (rnd < T);
    }

    public boolean chSelection(int roundId, double P) {
        Random random = new Random();
        double rnd = random.nextDouble();
        double T = P /(1-P*(roundId%(1/P)));

        return (rnd < T);
    }
}
