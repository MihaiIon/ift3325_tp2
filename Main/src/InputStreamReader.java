import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//Basé sur https://stackoverflow.com/questions/28137972/is-there-an-event-in-java-socket-when-socket-receive-data
public class InputStreamReader {

    private InputStream in;
 //  private
/*
    public InputStreamReader( InputStream in ) {
        this.in = in;
    }

    public void run() {

        byte[] bytes = new byte[256];
        ArrayList<byte[]> bytesList = new ArrayList<>();

        while( in.read( bytes ) > 0) {
            bytesList.add(bytes);
        }


        /*
        //need some more checking here to make sure 256 bytes was read, etc.
        //Maybe write a subclass of ChangeEvent
        {
            ChangeEvent evt = new ChangeEvent( bytes );
        }
        for( ChangeListener l : listeners ) {
            l.stateChanged( evt );
        }*/
  //  }
}
