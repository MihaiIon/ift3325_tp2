package networking;

import models.PacketModel;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Un socket qui gere envoie et recoie des packets au lieu de bytes et qui ecoute la reception de packets
 */

public class SocketMonitorThread extends Thread {

    private PacketReceptionListener receptionListener;
    private DataInputStream in;

    public SocketMonitorThread(Socket socket, PacketReceptionListener receptionListener) throws IOException {
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.receptionListener = receptionListener;
    }

    /*
     * Écoute le socket et envoie des evenement lors que chaque reception
     */
    public void run() {
        try {
            String input;
            while ((input = in.readUTF()) != null) {
                System.out.println(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface PacketReceptionListener{
        void onPacketReceived(PacketModel packet);
    }
}
