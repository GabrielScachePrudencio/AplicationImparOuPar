package ImparOuPar.view;

import ImparOuPar.model.*;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sons {
    
    private final File comecar, ganhar, perder;

    public Sons(String comecar, String ganhar, String perder) {
        this.comecar = new File( comecar );
        this.ganhar = new File( ganhar );
        this.perder = new File( perder );
    }
    
    private void play(File arquivo) throws Exception {
        Clip clip = AudioSystem.getClip();
        clip.open( AudioSystem.getAudioInputStream(arquivo) );
        clip.start();
    }
    
    public void comecar() throws Exception {
        play(comecar);
    }
    
    public void ganhar() throws Exception {
        play(ganhar);
    }
    
    public void perder() throws Exception {
        play(perder);
    }

}