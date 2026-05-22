package Vue;
import java.awt.event.*;

public class Chrono implements ActionListener{
    int timeMs;
    int delay;
    
    Chrono(int ms,int delay){
        timeMs = ms;
        this.delay = delay;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        timeMs -= delay;
    }
}