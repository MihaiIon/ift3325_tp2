package networking;

import models.PacketModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Un socket qui gere envoie et recoie des packets au lieu de bytes et qui ecoute la reception de packets
 */

public class SocketMonitorThread extends Thread {

    private PacketReceptionListener receptionListener;
    private BufferedReader in;

    public SocketMonitorThread(Socket socket, PacketReceptionListener receptionListener) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.receptionListener = receptionListener;
    }

    /*
     * Écoute le socket et envoie des evenement lors que chaque reception
     */
    public void run() {
        try {
            String input;
            while ((input = in.readLine()) != null) {
                receptionListener.onPacketReceived(PacketModel.convertToPacket(input));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface PacketReceptionListener{
        void onPacketReceived(PacketModel packet);
    }
}
