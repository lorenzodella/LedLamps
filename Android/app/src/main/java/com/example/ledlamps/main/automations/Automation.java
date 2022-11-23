// Della Matera Lorenzo 5E

package com.example.ledlamps.main.automations;

import java.util.LinkedList;

public class Automation {
    private int idAutomation;
    private int idUser;
    private String name;
    private String username;
    private boolean isActive;
    private LinkedList<Action> actions;
    private boolean expanded = false;

    public Automation(int idAutomation, int idUser, String name, String username, boolean isActive, LinkedList<Action> actions) {
        this.idAutomation = idAutomation;
        this.idUser = idUser;
        this.name = name;
        this.username = username;

        this.isActive = isActive;
        this.actions = actions;
    }

    public int getIdAutomation() {
        return idAutomation;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getName() {
        return name;
    }

    public LinkedList<Action> getActions() {
        return actions;
    }

    public void setActions(LinkedList<Action> actions) {
        this.actions = actions;
    }

    public String getUsername() {
        return username;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int getDuration(){
        int duration = 0;
        for (Action a : actions) {
            duration += a.getTime();
        }
        return duration;
    }

    @Override
    public String toString() {
        return "Automation{" +
                "idAutomation=" + idAutomation +
                ", idUser=" + idUser +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", isActive=" + isActive +
                ", actions=" + actions +
                ", expanded=" + expanded +
                '}';
    }
}
