// Della Matera Lorenzo 5E

public class Action {
    private int idAutomation;
    private int modeId;
    private String modeLinkParam;
    private String custom;
    private String fade1;
    private String fade2;
    private int time;
    
    public Action(int idAutomation, int modeId, String modeLinkParam, String custom, String fade1, String fade2, int time) {
        this.idAutomation = idAutomation;
        this.modeId = modeId;
        this.modeLinkParam = modeLinkParam;
        this.custom = custom;
        this.fade1 = fade1;
        this.fade2 = fade2;
        this.time = time*1000;
    }

    public int getIdAutomation() {
        return idAutomation;
    }

    public int getModeId() {
        return modeId;
    }

    public String getModeLinkParam() {
        return modeLinkParam;
    }

    public String getCustom() {
        return custom;
    }

    public String getFade1() {
        return fade1;
    }

    public String getFade2() {
        return fade2;
    }

    public int getTime() {
        return time;
    }

}
