package networking;

import factories.FrameFactory;
import managers.CheckSumManager;
import managers.DataManager;
import models.FrameModel;
import models.InformationFrameModel;
import models.ReceptionFrameModel;
import models.RejectionFrameModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Sender extends SocketController {

    private int framePosition;
    private int latestConfirmedPosition;

    private InformationFrameModel[] allFramesToSend;
    private ArrayList<InformationFrameModel> unconfirmedFrames = new ArrayList<>();

    /**
     * Constructs a sender
     * @param hostname the hostname of the receiver
     * @param port the port of the receiver
     * @param goBackN the size of a frame window
     */
    public Sender(String hostname, int port, int goBackN) {
        setGoBackN(goBackN);
        latestConfirmedPosition = prevPosN(goBackN);
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
     *   @param filepath le path du fichier
     */
    private void readFile(String filepath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            allFramesToSend = DataManager.splitMessageIntoFrames(sb.toString());
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
        openConnection();
    }

    private void openConnection() {
        sendFrame(FrameFactory.createConnexionFrame());
    }


    /**
     *
     */
    private void sendNextFrames() {
        System.out.println("Sending next frames");
        //TODO check si frame non confirmées doivent etre envoyé?

        ArrayList<FrameModel> frameModelsToSend = new ArrayList<>(unconfirmedFrames);
        while (canSend(framePosition)) {
            InformationFrameModel frame = allFramesToSend[framePosition];
            unconfirmedFrames.add(frame);
            frameModelsToSend.add(frame);
            framePosition ++;
        }

        try {
            sendFrames(frameModelsToSend);
        } catch (Exception e) {
            System.out.println("Error sending lines");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private boolean canSend(int position) {
        return position != latestConfirmedPosition;
    }

    private void confirmPackets(int to) {
        while (getCurrentPositionN() != to) {
            int finalConfirmPosition = getCurrentPositionN();
            unconfirmedFrames = new ArrayList(Arrays.asList(unconfirmedFrames.stream().filter(x-> (x.getId() != finalConfirmPosition)).toArray()));
            incrementCurrentPositionN();
        }
        latestConfirmedPosition = to;
    }

    @Override
    public void packetsReceived(ArrayList<FrameModel> framesReceived) {
        super.packetsReceived(framesReceived);
        framesAnalysis : for(int i = 0; i < framesReceived.size(); i++) {
            FrameModel frameModel = framesReceived.get(i);

            //make sure frame is valid
            if(CheckSumManager.isFrameContentValid(frameModel)) {
                switch (frameModel.getType()) {
                    case FRAME_RECEPTION: {
                        ReceptionFrameModel receptionFrameModel = (ReceptionFrameModel) frameModel;
                        confirmPackets(receptionFrameModel.getRecievedFrameId());

                        //Only send next frames if all previous received frames were analysed
                        if(i == framesReceived.size() - 1) {
                            sendNextFrames();
                            break framesAnalysis;
                        }
                        break;
                    }
                    case REJECTED_FRAME: {
                        RejectionFrameModel rejectionFrameModel = (RejectionFrameModel) frameModel;
                        int confirmedPosition = prevPosN(rejectionFrameModel.getRejectedFrameId());
                        confirmPackets(confirmedPosition);
                        sendNextFrames();
                        break framesAnalysis;
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
                        sendNextFrames();
                        break framesAnalysis;
                    }
                }
            } else {
                sendFrame(FrameFactory.createRejectionFrame(nextPos(latestConfirmedPosition)));
                break;
            }
        }
    }

    @Override
    public void timeOutReached(int position) {
        switch (getState()) {
            case Open: {
                FrameModel frameModel = FrameFactory.createReceptionFrame(nextPos(latestConfirmedPosition));
                sendFrame(frameModel);
                break;
            }
            case Waiting: {
                openConnection();
                break;
            }
        }
    }
}