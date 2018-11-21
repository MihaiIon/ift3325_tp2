package networking;

import factories.FrameFactory;
import models.FrameModel;
import models.FrameWindowModel;
import models.InformationFrameModel;
import models.TypeModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Receiver extends SocketController {

    private ServerSocket server;

    private AtomicBoolean isBusy = new AtomicBoolean(false);

    private ArrayList<FrameWindowModel> frameWindowModels = new ArrayList<>();

    private State state;

    private enum State {
        Waiting,
        Open,
        Closed
    }


    public Receiver(int port) throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server started");
    }

    public void listen() throws IOException {
        System.out.println("Waiting for a client ...");
        Socket client = server.accept();
        System.out.println("Client accepted");
        configureSocket(client);
    }

    public void packetsReceived(ArrayList<FrameModel> packetModels) {
        isBusy.set(true);
        packetModels.forEach(frame -> {
            //TODO if checksum

            switch (state) {
                case Waiting: {
                    if(frame.getType() == TypeModel.Type.CONNECTION_REQUEST) {
                            state = State.Open;
                            frameWindowModels.add(new FrameWindowModel(getGoBackN()));
                    } else {
                        logWrongRequestType("Connection request", frame.getType().toString());
                    }
                    break;
                }
                case Open: {
                    switch (frame.getType()) {
                            case TERMINATE_CONNECTION_REQUEST: {
                                    state = State.Closed;
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
                                } else {
                                    FrameModel frameModel = FrameFactory.createRejectionFrame(getLatestFrameWindow().getPosition());
                                    sendFrame(frameModel);
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
        isBusy.set(false);
    }

    private void logWrongRequestType(String expected, String found) {
        System.out.println("-----------------------------");
        System.out.println("Wrong frame type received : " + expected);
        System.out.println("expected : ");
        System.out.println("received : " + found);
        System.out.println("-----------------------------");
    }

    @Override
    public void timeOutReached(int position) {
        isBusy.set(true);
        FrameModel frame = FrameFactory.createReceptionFrame(getCurrentPositionN());
        sendFrame(frame);
        isBusy.set(false);
    }

    public boolean isBusy() {
        return isBusy.get();
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
}


