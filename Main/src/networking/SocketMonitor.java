package networking;

import models.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Un socket qui gere envoie et recoie des packets au lieu de bytes et qui ecoute la reception de packets
 */

public class SocketMonitor {

    private PacketReceptionListener receptionListener;
    private InputStream in;

    public SocketMonitor(Socket socket) throws IOException {
        in = socket.getInputStream();
        listenSocket();
    }

    /*
     * Écoute le socket et envoie des evenement lors que chaque reception
     */
    private void listenSocket() {
        ((Runnable) () -> {
            try {
                while (true) {
                    byte[] bytes = new byte[in.available()];
                    in.read(bytes);
                    receptionListener.onPacketReceived(Packet.convertToPacket(bytes));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).run();
    }

    public void setReceptionListener(PacketReceptionListener receptionListener) {
        this.receptionListener = receptionListener;
    }


    public interface PacketReceptionListener{
        void onPacketReceived(Packet packet);
    }
}
