// Della Matera Lorenzo 5E

import java.util.LinkedList;

public class Automator extends Thread{
    private LinkedList<Action> actions;
    private LampClient lampClient;

    public Automator(LinkedList<Action> actions, LampClient lampClient){
        this.lampClient = lampClient;
        this.actions = actions;
    }

    public void update(LinkedList<Action> actions){
        this.actions = actions;
    }
    
    @Override
    public void run() {
        try{
            while(true){
                for (int i=0; i<actions.size(); i++) {
                    Action action = actions.get(i);
                    if(action.getModeId() == 44 || action.getModeId() == 45){
                        lampClient.send("/set_color_hash?color="+action.getCustom()+"&colorMode="+action.getModeId());
                    }
                    else if(action.getModeId() >= 46 && action.getModeId() <= 49){
                        lampClient.send("/set_color_hash?first="+action.getFade1()+"&second="+action.getFade2()+"&colorMode="+action.getModeId());
                    }
                    else {
                        lampClient.send("/mode?opMode="+action.getModeLinkParam());
                    }
                    Thread.sleep((long)action.getTime());
                }
            }
        } catch(InterruptedException e){
            
        }
    }
}
