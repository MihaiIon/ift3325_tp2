package networking;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import models.FrameModel;

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

    private int currentPositionN;

    private int goBackN;

    public void packetsReceived(ArrayList<FrameModel> packetsReceived) {
        frameNumber.incrementAndGet();
    };

    public abstract void timeOutReached(int position);

    private State state;

    //The possible statuses for a socket
    protected enum State {
        Waiting,
        Open,
        Closed;
    }
    /**
     * Do the initial configuration of the socket being controlled
     * @param socket the socket to configure and add a listener for incoming requests
     * @throws IOException if anything is wrong with the socket
     */
    void configureSocket(Socket socket) throws IOException {
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        socketMonitor = new SocketMonitorThread(socket);

        compositeDisposable.add(socketMonitor.getReceivedPacketsObservable()
                .observeOn(Schedulers.trampoline())
                .subscribe(this::packetsReceived));

        compositeDisposable.add(timeOutPublisher
                .observeOn(Schedulers.trampoline())
                .subscribe(this::timeOutReached));

        socketMonitor.start();
    }

    /**
     * Close all the remaining resources for the current socket controller
     */
    public void close() {
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
    void sendFrame(FrameModel frame) {
        try {
            System.out.println("Sending : \n" + frame);
            out.writeUTF(frame.toBinary());
            out.flush();
        } catch (Exception e) {
            System.out.println("Error sending data :");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    void sendFrames(ArrayList<FrameModel> frameModels) {
        StringBuilder sb = new StringBuilder();
        System.out.println("---Batch sending :");
        frameModels.forEach(frameModel -> {
            sb.append(frameModel.toBinary());
            System.out.println(frameModel);
            System.out.println(frameModel.getFrameContent());
        });
        System.out.println("---Batch sending end");

        try {
            out.writeUTF(sb.toString());
            out.flush();
        } catch (Exception e) {
            System.out.println("Error sending data :");
            e.printStackTrace();
            System.exit(-1);
        }
        addTimeOut();
    }

    private void addTimeOut() {
        new TimeOutMonitor(frameNumber.incrementAndGet()).run();
    }

    public int getCurrentPositionN() {
        return currentPositionN;
    }

    public void setCurrentPositionN(int currentPositionN) {
        this.currentPositionN = currentPositionN;
    }

    public void incrementCurrentPositionN() {
        currentPositionN = nextPos(currentPositionN);
    }

    public int getGoBackN() {
        return goBackN;
    }

    public void setGoBackN(int goBackN) {
        this.goBackN = goBackN;
    }

    int nextPos(int pos) {
        return pos > getGoBackN() ? 0 : pos + 1;
    }

    protected int prevPosN(int pos) {
        return pos == 0 ? getGoBackN() : pos - 1;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    /*
     * Attends 3 secondes et averti que le timeout de 3 secondes a été atteint
     */
    private class TimeOutMonitor implements Runnable {

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
}
