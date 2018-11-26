package networking;

import factories.FrameFactory;
import managers.DataManager;
import models.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Sender extends SocketController {

    private int posToSend = 0;
    private int highestPosSent = 0;

    private InformationFrameModel[] allFramesToSend;

    /**
     * Constructs a sender
     * @param hostname the hostname of the receiver
     * @param port the port of the receiver
     */
    public Sender(String hostname, int port) {
        setState(State.Waiting);
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
     * Envoie les frames suivantes sil en reste a envoyer ou une demande de déconnection sinon
     */
    private void sendNextFrames() {
        System.out.println("Sending next frames : " + posToSend);

        //Verifie que lon na pas deja envoyé toutes les frames
        if(posToSend + 1 >= allFramesToSend.length) {
            sendFrame(FrameFactory.createDeconnexionFrame());
        } else {
            //On peut envoyer des frames
            int framePosition = posToSend;
            do  {
                System.out.println("Sending frame at position : " + framePosition);
                InformationFrameModel frame = allFramesToSend[framePosition];
                highestPosSent = framePosition;
                sendFrame(frame);
            } while (++framePosition % 8 != posToSend % 8 && framePosition < allFramesToSend.length);
        }
    }

    private void confirmFrames(int to) {
        while (posToSend++ % 8 != to % 8);
    }

    @Override
    public void frameReceived(FrameModel frame) {
        super.frameReceived(frame);
//
//            switch (getState()) {
//                case Waiting: {
//                    if(frameModel.hasErrors()) {
//                        openConnection();
//                    } else {
//                        switch (frameModel.getType()) {
//                            case CONNECTION_REQUEST: {
//                                setState(State.Open);
//                                sendNextFrames();
//                                return;
//                            }
//                            default: {
//                                logWrongRequestType("Connection request", frameModel.getType().toString());
//                            }
//                        }
//                    }
//                    break;
//                }
//                case Open: {
//                    if(!frameModel.hasErrors()) {
//                        switch (frameModel.getType()) {
//                            case FRAME_RECEPTION: {
//                                ReceptionFrameModel receptionFrameModel = (ReceptionFrameModel) frameModel;
//                                confirmFrames(receptionFrameModel.getRecievedFrameId());
//
//                                //Only send next frames if all previous received frames were analysed
//                                if (i == framesReceived.size() - 1) {
//                                    sendNextFrames();
//                                    return;
//                                }
//                                break;
//                            }
//                            case REJECTED_FRAME: {
//                                RejectionFrameModel rejectionFrameModel = (RejectionFrameModel) frameModel;
//                                int confirmedPosition = prevPosN(rejectionFrameModel.getRejectedFrameId());
//                                confirmFrames(confirmedPosition);
//                                sendNextFrames();
//                                return;
//                            }
//                            default: {
//                                logWrongRequestType("frame reception or frame rejection", frameModel.getType().toString());
//                                FrameModel pBitFrameModel = FrameFactory.pBitFrame(posToSend % 8);
//                                sendFrame(pBitFrameModel);
//                                break;
//                            }
//                        }
//                    } else {
//                        //frame has errors
//                        FrameModel pBitFrameModel = FrameFactory.pBitFrame(posToSend % 8);
//                        sendFrame(pBitFrameModel);
//                        break;
//                    }
//                    break;
//                }
//            }
//        }
    }

    @Override
    public void timeOutReached(int position) {
        switch (getState()) {
            case Open: {
                FrameModel frameModel = FrameFactory.pBitFrame(posToSend % 8);
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
