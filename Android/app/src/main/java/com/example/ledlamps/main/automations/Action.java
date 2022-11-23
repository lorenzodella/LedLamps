// Della Matera Lorenzo 5E

package com.example.ledlamps.main.automations;

public class Action {
    private int idAutomation;
    private int position;
    private int movePosition;
    private int modeId;
    private String modeName;
    private String custom;
    private String fade1;
    private String fade2;
    private int time;

    public Action(int idAutomation, int position, int modeId, String modeName, String custom, String fade1, String fade2, int time) {
        this.idAutomation = idAutomation;
        this.position = position;
        this.movePosition = position;
        this.modeId = modeId;
        this.modeName = modeName;
        this.custom = custom;
        this.fade1 = fade1;
        this.fade2 = fade2;
        this.time = time;
    }

    public int getIdAutomation() {
        return idAutomation;
    }

    public void setIdAutomation(int idAutomation) {
        this.idAutomation = idAutomation;
    }

    public int getPosition() {
        return position;
    }

    public int getMovePosition() {
        return movePosition;
    }

    public void setMovePosition(int movePosition) {
        this.movePosition = movePosition;
    }

    public int getModeId() {
        return modeId;
    }

    public String getModeName() {
        return modeName;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getFade1() {
        return fade1;
    }

    public void setFade1(String fade1) {
        this.fade1 = fade1;
    }

    public String getFade2() {
        return fade2;
    }

    public void setFade2(String fade2) {
        this.fade2 = fade2;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Action{" +
                "idAutomation=" + idAutomation +
                ", position=" + position +
                ", modeId=" + modeId +
                ", modeName='" + modeName + '\'' +
                ", custom='" + custom + '\'' +
                ", fade1='" + fade1 + '\'' +
                ", fade2='" + fade2 + '\'' +
                ", time=" + time +
                '}';
    }
}
