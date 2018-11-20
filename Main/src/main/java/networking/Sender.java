package networking;

import factories.FrameFactory;
import managers.DataManager;
import models.FrameModel;
import models.ReceptionFrameModel;
import models.RejectionFrameModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Sender extends SocketController {

    private AtomicInteger position = new AtomicInteger();

    private ArrayList<FrameModel> unconfirmedFrames = new ArrayList<>();

    private boolean busy;

    private int latestConfirmedPosition;

    private FrameModel[] framesToSend;

    /**
     * Constructs a sender
     * @param hostname the hostname of the receiver
     * @param port the port of the receiver
     * @param goBackN the size of a frame window
     */
    public Sender(String hostname, int port, int goBackN) {
        position.set(0);
        setGoBackN(goBackN);
        latestConfirmedPosition = prevPos(goBackN);
        try
        {
            Socket socket = new Socket(hostname, port);
            System.out.println("Connected");
            // sends output to the socket
            configureSocket(socket);
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /*
     *   https://www.journaldev.com/709/java-read-file-line-by-line
     *   Lit un fichier ligne par ligne et les envoie
     */
    private void readFile(String filepath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            framesToSend = DataManager.splitMessageIntoFrames(sb.toString());
        } catch (IOException e) {
            System.out.println("Reader could not open : " + filepath);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Starts the operation of sending an entire file throught the socket
     * @param filepath the path of the file to send
     */
    public void sendFile(String filepath) {
        readFile(filepath);
        sendNextFrames();
    }


    /**
     *
     */
    private void sendNextFrames() {
        //TODO check si frame non confirmées doivent etre envoyé
        unconfirmedFrames.forEach(this::sendFrame);
        try {
            while (canSend(position.get())) {
                    FrameModel frame = framesToSend[position.getAndIncrement()];
                    unconfirmedFrames.add(frame);
                    super.sendFrame(frame);
                }
        } catch (Exception e) {
            System.out.println("Error sending lines");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private boolean canSend(int position) {
        return position != latestConfirmedPosition;
    }

    private void confirmPackets(int from, int to) {
        int confirmPosition = from;
        while (confirmPosition != to) {
            //unconfirmedFrames = (ArrayList<FrameModel>) unconfirmedFrames.stream().filter(x -> x.getData() != position.get()).collect(Collectors.toList());
            confirmPosition = nextPos(position.get());
        }
        latestConfirmedPosition = to;
    }

    public boolean isBusy() {
        return busy;
    }

    @Override
    public void packetsReceived(ArrayList<FrameModel> packetsReceived) {
        busy = true;
        packetsReceived.forEach(frameModel -> {
            switch (frameModel.getType()) {
                case FRAME_RECEPTION: {
                    ReceptionFrameModel receptionFrameModel = (ReceptionFrameModel) frameModel;
                    confirmPackets(latestConfirmedPosition, receptionFrameModel.getRecievedFrameId());
                    sendNextFrames();
                    break;
                }
                case REJECTED_FRAME: {
                    RejectionFrameModel rejectionFrameModel = (RejectionFrameModel) frameModel;
                    int confirmedPosition = prevPos(rejectionFrameModel.getRejectedFrameId());
                    confirmPackets(latestConfirmedPosition, confirmedPosition);
                    sendNextFrames();
                    break;
                }
                case INFORMATION: {
                    //TODO possible?
                    break;
                }
                case TERMINATE_CONNECTION_REQUEST: {
                    //TODO possible?
                    break;
                }
                case CONNECTION_REQUEST: {
                    //TODO possible?
                    break;
                }
                case P_BITS: {
                    //TODO ??

                    break;
                }



            }
        });
        busy = false;
    }

    @Override
    public void timeOutReached(int position) {
        busy = true;
        FrameModel frameModel = FrameFactory.createReceptionFrame(latestConfirmedPosition);
        sendFrame(frameModel);
        busy = false;
    }
}
