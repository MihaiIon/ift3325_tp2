package networking;

import factories.FrameFactory;
import managers.DataManager;
import models.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Receiver extends SocketController {

    private ServerSocket server;

    private ArrayList<FrameWindowModel> frameWindowModels = new ArrayList<>();

    public Receiver(int port) throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server started");
    }

    public void listen() throws IOException {
        System.out.println("Waiting for a client ...");
        setState(State.Waiting);
        Socket client = server.accept();
        System.out.println("Client accepted");
        configureSocket(client);
    }

    public void packetsReceived(ArrayList<FrameModel> packetModels) {
        super.packetsReceived(packetModels);
        for (int i = 0, packetModelsSize = packetModels.size(); i < packetModelsSize; i++) {
            FrameModel frame = packetModels.get(i);
            switch (getState()) {
                case Waiting: {
                    if(!frame.hasErrors()) {
                        if (frame.getType() == TypeModel.Type.CONNECTION_REQUEST) {
                            setState(State.Open);
                            sendFrame(FrameFactory.createReceptionFrame(0));
                            frameWindowModels.add(new FrameWindowModel());
                        } else {
                            logWrongRequestType("Connection request", frame.getType().toString());
                        }
                    }
                    break;
                }
                case Open: {
                    if(!frame.hasErrors()) {
                        switch (frame.getType()) {
                            case TERMINATE_CONNECTION_REQUEST: {
                                setState(State.Closed);
                                printReceivedMessage();
                                close();
                                break;
                            }
                            case INFORMATION: {
                                InformationFrameModel informationFrameModel = (InformationFrameModel) frame;

                                if (getLatestFrameWindow().isFull()) {
                                    frameWindowModels.add(new FrameWindowModel());
                                }

                                //Verifie si lindex de linformation recue est bon
                                if (getLatestFrameWindow().addFrame(informationFrameModel)) {
                                    //Pour repondre uniquement si cest la derniere frame dinformation recue de la batch
                                    if (i == packetModelsSize - 1) {
                                        FrameModel frameModel = FrameFactory.createReceptionFrame(informationFrameModel.getId());
                                        sendFrame(frameModel);
                                    }
                                } else {
                                    //Lindex nest pas bon alors on rejette la frame
                                    FrameModel frameModel = FrameFactory.createRejectionFrame((getLatestFrameWindow().getPosition() + 1) % 8);
                                    sendFrame(frameModel);
                                    return;
                                }
                                printReceivedMessage();
                                break;
                            }
                            case P_BITS: {
                                PBitFrameModel frameModel = (PBitFrameModel) frame;
                                int pos = getLatestFrameWindow().getPosition();
                                sendFrame(FrameFactory.createReceptionFrame(pos));
                                break;
                            }
                            default: {
                                logWrongRequestType("terminate, information or pbit", frame.getType().toString());
                            }
                        }
                    } else {
                        //La frame a une erreur alors on la rejette
                        FrameModel frameModel = FrameFactory.createRejectionFrame((getLatestFrameWindow().getPosition() + 1) % 8);
                        sendFrame(frameModel);
                        return;
                    }

                    break;
                }
                case Closed: {
                    logWrongRequestType("None, request status is closed", frame.getType().toString());
                    break;
                }
            }
        }
    }


    @Override
    public void timeOutReached(int position) {
        switch (getState()) {
            case Open: {
                int positionN = getLatestFrameWindow().getPosition();
                FrameModel frame = FrameFactory.createReceptionFrame(positionN);
                sendFrame(frame);
                break;
            }
        }
    }

    public FrameWindowModel getLatestFrameWindow() {
        return frameWindowModels.get(frameWindowModels.size()-1);
    }


    @Override
    public void close() {
        if(server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.close();
    }

    private void printReceivedMessage() {
        System.out.println("--------Received message :");
        System.out.println(DataManager.extractMessageFromFrames(frameWindowModels));
        System.out.println("--------Received message end");
    }
}


