package networking;

import factories.FrameFactory;
import models.FrameModel;
import models.ReceptionFrameModel;
import models.RejectionFrameModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Sender extends SocketController {

    private AtomicInteger position = new AtomicInteger();

    private ArrayList<FrameModel> unconfirmedPackets = new ArrayList<FrameModel>();

    private boolean busy;

    private int latestConfirmedPosition;

    private BufferedReader reader;

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
    public void openReader(String filepath) {
        try {
            reader = new BufferedReader(new FileReader(filepath));
        } catch (IOException e) {
            System.out.println("Reader could not open : " + filepath);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void sendFile(String filepath) {
        openReader(filepath);
        sendLines();
    }

    public void sendLines() {
        //TODO check si frame non confirmées doivent etre envoyé
        try {
            String line = null;
            while ((line = reader.readLine()) != null && canSend(getCurrentPosition())) {
                FrameModel frame = FrameFactory.createInformationFrame(getCurrentPosition(), line);
                incrementCurrentPosition();
                unconfirmedPackets.add(frame);
                super.sendData(frame);
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
            //unconfirmedPackets = (ArrayList<FrameModel>) unconfirmedPackets.stream().filter(x -> x.getData() != position.get()).collect(Collectors.toList());
            confirmPosition = nextPos(position.get());
        }
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
                    latestConfirmedPosition = receptionFrameModel.getRecievedFrameId();
                    sendLines();
                    break;
                }
                case REJECTED_FRAME: {
                    RejectionFrameModel rejectionFrameModel = (RejectionFrameModel) frameModel;
                    int confirmedPosition = prevPos(rejectionFrameModel.getRejectedFrameId());
                    confirmPackets(latestConfirmedPosition, confirmedPosition);
                    latestConfirmedPosition = confirmedPosition;
                    sendLines();
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
        sendData(frameModel);
        busy = false;
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            System.out.print("Error closing reader");
            e.printStackTrace();
        }
        super.close();
    }
}
