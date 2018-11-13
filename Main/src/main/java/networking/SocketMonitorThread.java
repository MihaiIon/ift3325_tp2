package networking;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import models.PacketModel;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Un socket qui gere envoie et recoie des packets au lieu de bytes et qui ecoute la reception de packets
 */

public class SocketMonitorThread extends Thread {

    private DataInputStream in;

    private PublishSubject<ArrayList<PacketModel>> packetsPublisher;
    private PublishSubject<Integer> timeOutPublisher;

    private SocketController socketController;

    private AtomicInteger packetsReceived = new AtomicInteger(0);

    public SocketMonitorThread(Socket socket, SocketController socketController) throws IOException {
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        packetsPublisher = PublishSubject.create();
        timeOutPublisher = PublishSubject.create();
        this.socketController = socketController;
    }

    /*
     * Écoute le socket et envoie des evenement lors que chaque reception
     */
    public void run() {
        try {
            String input;
            ArrayList<PacketModel> cumulatedPackets = new ArrayList<>();
            while ((input = in.readUTF()) != null) {
                System.out.println(input);
                if(socketController.isBusy()) {
                    //cumulatedPackets.add(new PacketModel())
                    // TODO
                    //
                } else {
                    packetsPublisher.onNext(cumulatedPackets);
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

        public void run() {
            try {
                Thread.sleep(3000);
                if(packetsReceived.get() <= packetNumber) {
                    timeOutPublisher.onNext(packetNumber);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Observable<ArrayList<PacketModel>> getReceivedPacketsObservable() {
        return packetsPublisher;
    }

    public Observable<Integer> getTimeOutObservable() {
        return timeOutPublisher;
    }
}
