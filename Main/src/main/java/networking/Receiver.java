package networking;

import factories.FrameFactory;
import managers.DataManager;
import models.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver extends SocketController {

    // Attributes
    private ServerSocket server;
    private ArrayList<FrameWindowModel> frameWindows = new ArrayList<>();
    private byte framePoolSize = 0;
    private static byte MAX_FRAME_POOL_SIZE = 6;

    /**
     *
     * @param port
     * @throws IOException
     */
    public Receiver(int port) throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server started");
    }

    // ----------------------------------------------------------------------------------
    // Methods

    /**
     *
     * @throws IOException
     */
    public void listen() throws IOException {
        System.out.println("STANDBY for a client ...");
        setState(State.STANDBY);
        Socket client = server.accept();
        System.out.println("Client accepted");
        configureSocket(client);
    }


    @Override
    public void closeServer() {
        if(server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.closeServer();
    }

    /**
     *
     */
    private void sendReceptionFrame() {
        int lastId = getLatestFrameWindow().getSize();
        sendFrame(FrameFactory.createReceptionFrame(lastId == 0 ? 7 : lastId-1));
        framePoolSize = 0;
    }

    /**
     *
     */
    private void sendRejectionFrame() {
        int lastId = getLatestFrameWindow().getSize();
        sendFrame(FrameFactory.createRejectionFrame(lastId));
    }

    /**
     *
     */
    private void printReceivedMessage() {
        System.out.println("--------Received message :");
        System.out.println(DataManager.extractMessageFromFrames(frameWindows));
        System.out.println("--------Received message end");
    }

    // ----------------------------------------------------------------------------------
    // Handlers

    @Override
    public void onTimeOutReached(int position) {
        if (getState() == State.CONNECTION_OPENED) {
            if(frameWindows.size() == 1 && getLatestFrameWindow().getSize() == 0) return;
            sendReceptionFrame();
        }
    }

    /**
     * Check State and decide what action to take.
     * @param frame Received frame.
     * @return True if all went good.
     */
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

    /**
     * @param frame Received frame.
     * @return True if all went good.
     */
    @Override
    boolean handleOnStandbyState(FrameModel frame) {
        switch (frame.getType()) {
            case CONNECTION_REQUEST:
                setState(State.CONNECTION_OPENED);
                sendFrame(FrameFactory.createConnectionFrame());
                frameWindows.add(new FrameWindowModel());
                return true;
            default:
                logWrongRequestType("Connection request", frame.getType().toString());
                return false;
        }
    }

    /**
     * @param frame Received frame.
     * @return True if all went good.
     */
    @Override
    boolean handleOnConnectionOpenedState(FrameModel frame) {
        switch (frame.getType()) {
            case TERMINATE_CONNECTION_REQUEST:
                setState(State.CONNECTION_CLOSED);
                printReceivedMessage();
                closeServer();
                break;
            case INFORMATION:
                InformationFrameModel informationFrame = (InformationFrameModel) frame;
                FrameWindowModel fw = getLatestFrameWindow();
                // If push succeeds...send ARK.
                if(fw.addFrame(informationFrame)) {
                    framePoolSize++;
                    if (Math.random() > 0.75 || framePoolSize >= MAX_FRAME_POOL_SIZE) {
                        sendReceptionFrame();
                    }
                    break;
                }
                sendRejectionFrame();
                return false;
            case CONNECTION_REQUEST:
                // Sender may have not received the connection request.
                sendFrame(FrameFactory.createConnectionFrame());
                break;
            case P_BITS:
                sendReceptionFrame();
                break;
            case BAD_FRAME:
                sendRejectionFrame();
                return false;
            default:
                logWrongRequestType("terminate, information, connection request or pbit", frame.getType().toString());
                return false;
        }
        return true;
    }

    // ----------------------------------------------------------------------------------
    // Getters

    public FrameWindowModel getLatestFrameWindow() {
        return frameWindows.get(frameWindows.size()-1);
    }
}


