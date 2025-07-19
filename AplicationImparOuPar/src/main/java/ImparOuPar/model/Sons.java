package ImparOuPar.model;

import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sons {
    
    private final File comecar, mover, errar, ganhar, perder;

    public Sons(String comecar, String mover, String errar, String ganhar, String perder) {
        this.comecar = new File( comecar );
        this.mover = new File( mover );
        this.errar = new File( errar );
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
    
    public void mover() throws Exception {
        play(mover);
    }
    
    public void errar() throws Exception {
        play(errar);
    }
    
    public void ganhar() throws Exception {
        play(ganhar);
    }
    
    public void perder() throws Exception {
        play(perder);
    }

}