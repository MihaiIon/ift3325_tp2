package networking;

import com.sun.xml.internal.ws.api.message.Packet;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import models.PacketModel;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public abstract class SocketController {

    SocketMonitorThread socketMonitor;
    private DataOutputStream out;

    private Disposable packetsSuscription;
    private Disposable timeOutsSuscription;
    public abstract boolean isBusy();

    public abstract void packetsReceived(ArrayList<PacketModel> packetsReceived);

    public abstract void timeOutReached(int position);

    public void configureSocket(Socket socket) throws IOException {
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        socketMonitor = new SocketMonitorThread(socket, this);
        packetsSuscription = socketMonitor.getReceivedPacketsObservable()
                .observeOn(Schedulers.trampoline())
                .subscribe(this::packetsReceived);

        timeOutsSuscription = socketMonitor.getTimeOutObservable()
                .observeOn(Schedulers.trampoline())
                .subscribe(this::timeOutReached);

        socketMonitor.run();
    }



    public void close() {
        if(out != null) {
            try {
                out.close();
            } catch (Exception e) {

            }
        }

        if(socketMonitor != null) {
            socketMonitor.interrupt();
        }
        if(packetsSuscription != null) {
            packetsSuscription.dispose();
        }
        if(timeOutsSuscription != null) {
            timeOutsSuscription.dispose();
        }
    }

    public void sendPacket(PacketModel p) {
        //   out.write(p.toBinary().getBytes());
    }

    public void sendData(String data) throws IOException {
        //PacketModel packet = new PacketModel((byte)'a', PacketModel.Type.INFORMATION, new PayloadModel(data)); //TODO byte[1] remplacer par num de trame
        //unconfirmedPackets.add(packet);
        //out.write(packet.toBinary().getBytes());
        out.writeUTF(data);
    }
}
