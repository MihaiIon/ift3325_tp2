package networking;

import factories.BadFrameFactory;
import factories.FrameFactory;
import managers.DataManager;
import models.*;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Sender extends SocketController {

    // Attributes
    private boolean didReceiveRejectionFrameId = false;
    private boolean isSendingInformationFrames = false;
    private AtomicInteger rejectedFrameId = new AtomicInteger(-1);
    private AtomicInteger savedFrameIndex = new AtomicInteger();
    private AtomicInteger currFrameIndex = new AtomicInteger();
    private AtomicInteger lastFrameIdReceived = new AtomicInteger();
    private AtomicInteger lastFrameIdSent = new AtomicInteger(-1);
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
        sendFrame(FrameFactory.pBitFrame(lastFrameIdSent.get()));
    }

    /**
     * Send Frames one by one until it fills the Receiver Frame pool.
     */
    private void sendNextFrames() {
        // If the Sender is not busy...
        if(!isSendingInformationFrames) {
            // Send frames.
            isSendingInformationFrames = true;
            while (isReceiverReady()) {

                // Retrieve next frame.
                FrameModel frame = getNextFrame();
                currFrameIndex.incrementAndGet();

                // Close connection if all frames were sent.
                if(frame == null) {
                    sendFrame(FrameFactory.createDisconnectionFrame());
                    setState(State.CONNECTION_CLOSED);
                    return;
                }

                // Send next Frame.
                try {
                    lastFrameIdSent.set(((InformationFrameModel) frame).getId());
                    System.out.println(
                            "Sending next frame " + currFrameIndex.get() + "/" + framesToBeSent.length
                                    + " | id : " + lastFrameIdSent.get() + "."
                    );
                } catch (Exception e) {
                    lastFrameIdSent.set((currFrameIndex.get()-1)%8);
                    int badFrameIndex = ((int)Math.floor(Math.random() * 8));
                    frame.setId(badFrameIndex);
                    System.out.println(
                            "Sending next frame (**BAD FRAME**) " + currFrameIndex.get() + "/" + framesToBeSent.length
                                    + " | id : " + badFrameIndex + "."
                    );
                }
                sendFrame(frame);
            }

            // Liberate Sender.
            isSendingInformationFrames = false;
        }
    }

    /**
     *
     * @param id
     * @return
     */
    private int fixIdRange(int id) {
        if(currFrameIndex.get() == 0 && id < 0) return -1;
        else if (id > 7) return id % 8;
        return id < 0 ? 7 : id;
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
                sendNextFrames();
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
                RejectionFrameModel rejectionFrame = (RejectionFrameModel)frame;
                handleOnFrameIdRejected(rejectionFrame.getRejectedFrameId());
                sendNextFrames();
                break;
            case FRAME_RECEPTION:
                ReceptionFrameModel receptionFrame = (ReceptionFrameModel)frame;
                handleOnFrameIdReceived(receptionFrame.getRecievedFrameId());
                sendNextFrames();
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
                if(isReceiverReady() && !didReceiveRejectionFrameId) {
                    sendNextFrames();
                } else sendPBitFrame();
                break;
            }
            case STANDBY: {
                sendConnectionFrame();
                break;
            }
        }
    }

    /**
     *
     * @param id Received Frame id.
     */
    private void handleOnFrameIdReceived(int id) {
        int rid = rejectedFrameId.get();
        int ridd = fixIdRange(rid-1);
        if(didReceiveRejectionFrameId && (rid == id || ridd == id)) {
            didReceiveRejectionFrameId = false;
            rejectedFrameId.set(-1);
            handleOnFrameIdReceived(id);
        } else {
            setSavedFrameIndex(id);
            lastFrameIdReceived.set(id);
        }
    }

    /**
     *
     * @param id Rejected Frame id.
     */
    private void handleOnFrameIdRejected(int id) {
        didReceiveRejectionFrameId = true;
        // Update rejected id
        rejectedFrameId.set(id);
        // Update indexes
        setSavedFrameIndex(id);
        currFrameIndex.set(savedFrameIndex.get());
    }

    // ----------------------------------------------------------------------------------
    // Getters & Setters

    /**
     *
     * @return
     */
    private boolean isReceiverReady() {
        return getReceiverFramePoolSize() < (didReceiveRejectionFrameId ? 1 : 8);
    }

    /**
     *
     * @return
     */
    private int getReceiverFramePoolSize() {
        System.out.println("********* " + currFrameIndex.get());
        System.out.println("********* " + savedFrameIndex.get());
        return currFrameIndex.get() - savedFrameIndex.get();
    }

    /**
     *
     * @return
     */
    private FrameModel getNextFrame() {
        if(Math.random() > 0.9) {
            return getBadFrame();
        }
        if(currFrameIndex.get() < framesToBeSent.length) {
            return framesToBeSent[currFrameIndex.get()];
        }
        return null;
    }

    /**
     * Provides a Bad Frame (for testing errors).
     */
    private FrameModel getBadFrame() {
        BadFrame.BadFrameType[] types = BadFrame.BadFrameType.values();
        return BadFrameFactory.createBadFrame(types[(int)Math.floor(Math.random() * types.length)]);
    }

    /**
     * Updates the saved Frame index.
     * @param id Received/Rejected Frame id.
     */
    private void setSavedFrameIndex(int id) {
        // Get difference
        boolean isAtFirstFrame = savedFrameIndex.get() == 0;
        int lastId = isAtFirstFrame ? -1 : lastFrameIdReceived.get();
        int newId = id <= lastId ? id + 8 : id;
        int diff = lastId == -1 ? id : newId - lastId;
        // Update atomic integer.
        if(didReceiveRejectionFrameId){
            savedFrameIndex.set(savedFrameIndex.get() + diff - (isAtFirstFrame ? 0 : 1));
            lastFrameIdReceived.set(fixIdRange(id-1));
        } else {
            savedFrameIndex.set(savedFrameIndex.get() + diff);
            lastFrameIdReceived.set(id);
        }
    }
}
