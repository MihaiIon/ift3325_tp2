package networking;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import models.FrameModel;
import utils.BitFlipper;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SocketController {

    private SocketMonitorThread socketMonitor;
    private DataOutputStream out;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private PublishSubject<Integer> timeOutPublisher = PublishSubject.create();

    private AtomicInteger frameNumber = new AtomicInteger();


    /**
     * Increments the frameNumber to keep track of the number of frames receiver
     * for the timeout thread to know when the was a timeout
     * @param frame the received frame
     * @return true if all when well, false otherwise
     */
    private boolean onFrameReceived(FrameModel frame) {
        frameNumber.incrementAndGet();
        return processReceivedFrame(frame);
    };

    abstract boolean processReceivedFrame(FrameModel frameModel);

    public abstract void onTimeOutReached(int position);
    abstract boolean handleOnStandbyState(FrameModel frame);
    abstract boolean handleOnConnectionOpenedState(FrameModel frame);

    private State state;

    //The possible statuses for a socket
    protected enum State {
        STANDBY, CONNECTION_OPENED, CONNECTION_CLOSED
    }
    /**
     * Do the initial configuration of the socket being controlled
     * @param socket the socket to configure and add a listener for incoming requests
     * @throws IOException if anything is wrong with the socket
     */
    void configureSocket(Socket socket) throws IOException {
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        socketMonitor = new SocketMonitorThread(socket);

        compositeDisposable.add(socketMonitor.getReceivedFrameObservable()
                .observeOn(Schedulers.trampoline())
                .subscribe(this::onFrameReceived));

        compositeDisposable.add(timeOutPublisher
                .observeOn(Schedulers.trampoline())
                .subscribe(this::onTimeOutReached));

        socketMonitor.start();
    }

    /**
     * Close all the remaining resources for the current socket controller
     */
    public void closeServer() {
        if(out != null) {
            try {
                out.close();
            } catch (Exception e) {

            }
        }

        if(socketMonitor != null) {
            socketMonitor.interrupt();
        }
        compositeDisposable.dispose();
    }

    /**
     * Sends a frame throught a socket
     * @param frame the frame to send throught the socket
     */
    boolean sendFrame(FrameModel frame) {
        try {
            System.out.println("Sending : \n" + frame);
            String output = BitFlipper.flipRandomBits(frame.toBinary());
            out.writeUTF(output);
            out.flush();
        } catch (Exception e) {
            System.out.println("Error sending data :");
            return false;
        }
        addTimeOut();
        return true;
    }
    /**
     * Envoie une série de frame en un seul coup
     * @param frameModels the frames to send throught the socket
     */
    void sendFrames(ArrayList<FrameModel> frameModels) {
        System.out.println("---Batch sending :");
        for (FrameModel frameModel : frameModels) {
            if (!sendFrame(frameModel)) break;
        }
        System.out.println("---Batch sending end");
    }

    private void addTimeOut() {
        new TimeOutMonitor(frameNumber.incrementAndGet()).start();
    }

    State getState() {
        return state;
    }

    void setState(State state) {
        this.state = state;
    }

    /*
     * Attends 3 secondes et averti que le timeout de 3 secondes a été atteint
     */
    private class TimeOutMonitor extends Thread {

        final int packetNumber;

        TimeOutMonitor(final int packetNumber) {
            this.packetNumber = packetNumber;
        }

        public void run() {
            try {
                Thread.sleep(3000);
                if(frameNumber.get() <= packetNumber) {
                    timeOutPublisher.onNext(packetNumber);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void logWrongRequestType(String expected, String found) {
        System.out.println("-----------------------------");
        System.out.println("Wrong frame type received :");
        System.out.println("expected : " + expected);
        System.out.println("received : " + found);
        System.out.println("-----------------------------");
    }

}
