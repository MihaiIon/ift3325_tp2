package networking;

import models.FrameModel;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Un socket qui gere envoie et recoie des packets au lieu de bytes et qui ecoute la reception de packets
 */

public class SocketMonitorThread extends Thread {

    private PacketReceptionListener receptionListener;
    private DataInputStream in;

    private AtomicInteger packetsReceived = new AtomicInteger(0);

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
                if(receptionListener != null) {
                    // TODO receptionListener.onPacketReceived();
                }
                new TimeOutMonitor(packetsReceived.incrementAndGet()).run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Attends 3 secondes et averti que le timeout de 3 secondes a été atteint
     */
    private class TimeOutMonitor implements Runnable {

        final int packetNumber;

        TimeOutMonitor(final int packetNumber) {
            this.packetNumber = packetNumber;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                if(packetsReceived.get() <= packetNumber) {
                    receptionListener.onPacketReceptionTimeOut();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public interface PacketReceptionListener {
        void onPacketReceived(FrameModel packet);

        void onPacketReceptionTimeOut();
    }
}
