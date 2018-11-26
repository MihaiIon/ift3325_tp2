package networking;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import managers.DataManager;
import models.FrameModel;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Un socket qui gere envoie et recoie des packets au lieu de bytes et qui ecoute la reception de packets
 */

public class SocketMonitorThread extends Thread {

    private DataInputStream in;

    private PublishSubject<FrameModel> packetsPublisher;

    /**
     * Construit un moniteur de socket qui surveille les frames recues
     *
     * @param socket le socket a surveiller
     * @throws IOException if a exception is happening with the socket
     */
    SocketMonitorThread(Socket socket) throws IOException {
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        packetsPublisher = PublishSubject.create();
    }

    /*
     * Écoute le socket et envoie des evenements au publish subject lors que chaque reception
     */
    public void run() {
        try {
            while (true) { //TODO ajouter condition?
                String stream = in.readUTF();
                System.out.println("Received " + stream);
                if (FrameModel.isFrameValid(stream)) {
                    FrameModel receivedFrame = FrameModel.convertStreamToFrame(stream);
                    System.out.println("---Received frame : ");
                    System.out.println(receivedFrame);
                    System.out.println("---Received frame end ");
                    packetsPublisher.onNext(receivedFrame);
                }
            }
        } catch (IOException e) {
            System.out.println("Socket closed");
        }
    }

    /*
     * Retourne le observable de packets recues
     */
    Observable<FrameModel> getReceivedPacketsObservable() {
        return packetsPublisher;
    }
}
