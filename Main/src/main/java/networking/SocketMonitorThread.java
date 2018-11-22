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

    private PublishSubject<ArrayList<FrameModel>> packetsPublisher;

    private AtomicInteger packetsReceived = new AtomicInteger(0);


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
     * Écoute le socket et envoie des evenement lors que chaque reception
     */
    public void run() {
        try {
            ArrayList<FrameModel> cumulatedFrames = new ArrayList<>();
            while (true) { //TODO ajouter condition?
                String input = in.readUTF();
                System.out.println("Received " + input);
                FrameModel[] receivedFrame = new FrameModel[]{FrameModel.convertStreamToFrame(input)};
                System.out.println("---Received frames : ");
                Arrays.stream(receivedFrame).forEach(frameModel ->
                        System.out.println(frameModel)
                        );
                System.out.println("---Received frames end ");
                cumulatedFrames.addAll(Arrays.asList(receivedFrame));
                packetsPublisher.onNext(cumulatedFrames);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Retourne le observable de packets recues
     */
    Observable<ArrayList<FrameModel>> getReceivedPacketsObservable() {
        return packetsPublisher;
    }
}
