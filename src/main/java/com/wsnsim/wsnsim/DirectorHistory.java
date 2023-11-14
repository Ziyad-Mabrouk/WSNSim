package com.wsnsim.wsnsim;

import java.util.ArrayList;

public class DirectorHistory {

    static DirectorHistory directorHistory = new DirectorHistory();
    private ArrayList<DirectorMemento> listDirectors = new ArrayList<>();
    private DirectorHistory() {}

    public static DirectorHistory getInstance() {
        return directorHistory;
    }
    public void save(DirectorMemento directorMemento) {
        listDirectors.add(directorMemento);
    }

    public DirectorMemento pop(int index) {
        DirectorMemento lastSave = listDirectors.get(index);
        return lastSave;
    }

    public void clearHistory() {
        listDirectors.clear();
    }

    public int numSaves() {return listDirectors.size();}
}
