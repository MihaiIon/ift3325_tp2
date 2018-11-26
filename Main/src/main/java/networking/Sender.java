package networking;

import factories.FrameFactory;
import managers.DataManager;
import models.*;

import java.io.IOException;
import java.net.Socket;

public class Sender extends SocketController {

    // Attributes
    private int savedFrameIndex =0;
    private int currFrameIndex = 0;
    private int lastFrameIdReceived = -1;
    private int lastFrameIdSent = -1;
    private String filepath;
    private InformationFrameModel[] framesToBeSent;

    /**
     * Constructs a sender
     * @param hostname the hostname of the receiver
     * @param port the port of the receiver
     */
    public Sender(String hostname, int port, String filepath) {
        setState(State.STANDBY);
        this.filepath = filepath;
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("Connected");
            // sends output to the socket
            configureSocket(socket);
            // Starts the operation of sending frames through the socket.
            sendConnectionFrame();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------------------
    // Methods

    /**
     *
     */
    private void sendConnectionFrame() {
        sendFrame(FrameFactory.createConnectionFrame());
    }

    /**
     *
     */
    private void sendPBitFrame() {
        sendFrame(FrameFactory.pBitFrame(lastFrameIdSent));
    }

    /**
     * Envoie les frames suivantes sil en reste a envoyer ou une demande de déconnection sinon
     */
    private void sendNextFrame() {
        // Saved current position.
        savedFrameIndex = currFrameIndex;
        do {
            // Retrieve next frame.
            FrameModel frame = getNextFrame();
            currFrameIndex++;

            // Close connection if all frames were sent.
            if(frame == null) {
                sendFrame(FrameFactory.createDisconnectionFrame());
                setState(State.CONNECTION_CLOSED);
                return;
            }

            // Send next frame.
            lastFrameIdSent = ((InformationFrameModel) frame).getId();
            System.out.println(
                    "Sending next frame " + currFrameIndex + "/" + framesToBeSent.length
                            + " | id : " + lastFrameIdSent + "."
            );
            sendFrame(frame);
        } while ((lastFrameIdSent - lastFrameIdReceived) <  8);
    }

    // ----------------------------------------------------------------------------------
    // Handlers

    @Override
    public boolean onFrameReceived(FrameModel frame) {
        super.onFrameReceived(frame);
        switch (getState()) {
            case STANDBY:
                return handleOnStandbyState(frame);
            case CONNECTION_OPENED:
                return handleOnConnectionOpenedState(frame);
            default:
                logWrongRequestType("None, receiver status is closed", frame.getType().toString());
                return false;
        }
    }
//
//            switch (getState()) {

//                case CONNECTION_OPENED: {
//                    if(!frameModel.hasErrors()) {
//                        switch (frameModel.getType()) {
//                            case FRAME_RECEPTION: {
//                                ReceptionFrameModel receptionFrameModel = (ReceptionFrameModel) frameModel;
//                                confirmFrames(receptionFrameModel.getRecievedFrameId());
//
//                                //Only send next frames if all previous received frames were analysed
//                                if (i == framesReceived.size() - 1) {
//                                    sendNextFrame();
//                                    return;
//                                }
//                                break;
//                            }
//                            case REJECTED_FRAME: {
//                                RejectionFrameModel rejectionFrameModel = (RejectionFrameModel) frameModel;
//                                int confirmedPosition = prevPosN(rejectionFrameModel.getRejectedFrameId());
//                                confirmFrames(confirmedPosition);
//                                sendNextFrame();
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


    /**
     * @param frame Received frame.
     * @return True if all went good.
     */
    @Override
    boolean handleOnStandbyState(FrameModel frame) {
        switch (frame.getType()) {
            case CONNECTION_REQUEST:
                setState(State.CONNECTION_OPENED);
                framesToBeSent = DataManager.readFile(filepath);
                sendNextFrame();
                break;
            default:
                sendConnectionFrame();
                return false;
        }
        return true;
    }

    /**
     * @param frame Received frame.
     * @return True if all went good.
     */
    @Override
    boolean handleOnConnectionOpenedState(FrameModel frame) {
        switch (frame.getType()) {
            case REJECTED_FRAME:
                break;
            case FRAME_RECEPTION:
                savedFrameIndex = currFrameIndex;
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onTimeOutReached(int position) {
        switch (getState()) {
            case CONNECTION_OPENED: {
                sendPBitFrame();
                break;
            }
            case STANDBY: {
                sendConnectionFrame();
                break;
            }
        }
    }

    // ----------------------------------------------------------------------------------
    // Getters

    private FrameModel getNextFrame() {
        if(currFrameIndex < framesToBeSent.length) {
            return framesToBeSent[currFrameIndex];
        }
        return null;
    }
}
