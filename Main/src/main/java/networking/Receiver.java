package networking;

import factories.FrameFactory;
import managers.DataManager;
import models.FrameModel;
import models.FrameWindowModel;
import models.InformationFrameModel;
import models.TypeModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
        packetModels.forEach(frame -> {
            //TODO if checksum

            switch (getState()) {
                case Waiting: {
                    if(frame.getType() == TypeModel.Type.CONNECTION_REQUEST) {
                            setState(State.Open);
                            frameWindowModels.add(new FrameWindowModel(getGoBackN()));
                    } else {
                        logWrongRequestType("Connection request", frame.getType().toString());
                    }
                    break;
                }
                case Open: {
                    switch (frame.getType()) {
                            case TERMINATE_CONNECTION_REQUEST: {
                                    setState(State.Closed);
                                    printReceivedMessage();
                                    close();
                                break;
                            }
                            case INFORMATION: {
                                InformationFrameModel informationFrameModel = (InformationFrameModel) frame;
                                if(getLatestFrameWindow().addFrame(informationFrameModel)) {
                                    if(getLatestFrameWindow().isFull()) {
                                        frameWindowModels.add(new FrameWindowModel(getGoBackN()));
                                    }
                                    FrameModel frameModel = FrameFactory.createReceptionFrame(informationFrameModel.getId());
                                    sendFrame(frameModel);
                                    printReceivedMessage();
                                } else {
                                    FrameModel frameModel = FrameFactory.createRejectionFrame(getLatestFrameWindow().getPosition());
                                    sendFrame(frameModel);
                                    printReceivedMessage();
                                }
                                break;
                            }
                            case REJECTED_FRAME: {
                                //TODO possible?
                                break;
                            }
                            case FRAME_RECEPTION: {
                                //TODO possible?
                                break;
                            }
                            case P_BITS: {
                                //TODO possible?
                            }
                        }

                    break;
                }
                case Closed: {
                    logWrongRequestType("None, request status is closed", frame.getType().toString());
                    break;
                }
            }
        });
    }

    private void logWrongRequestType(String expected, String found) {
        System.out.println("-----------------------------");
        System.out.println("Wrong frame type received :");
        System.out.println("expected : " + expected);
        System.out.println("received : " + found);
        System.out.println("-----------------------------");
    }

    @Override
    public void timeOutReached(int position) {
        switch (getState()) {
            case Open: {
                FrameModel frame = FrameFactory.createReceptionFrame(getCurrentPositionN());
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
        DataManager.extractMessageFromFrames(frameWindowModels);
        System.out.println("--------Received message end");
    }
}


