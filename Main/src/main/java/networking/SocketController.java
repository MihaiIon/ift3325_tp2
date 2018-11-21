package networking;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import models.FrameModel;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public abstract class SocketController {

    private SocketMonitorThread socketMonitor;
    private DataOutputStream out;

    private Disposable packetsSuscription;
    private Disposable timeOutsSuscription;

    private int currentPositionN;

    private int goBackN;

    public abstract boolean isBusy();

    public abstract void packetsReceived(ArrayList<FrameModel> packetsReceived);

    public abstract void timeOutReached(int position);

    /**
     * Do the initial configuration of the socket being controlled
     * @param socket the socket to configure and add a listener for incoming requests
     * @throws IOException if anything is wrong with the socket
     */
    void configureSocket(Socket socket) throws IOException {
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        socketMonitor = new SocketMonitorThread(socket, this);
        packetsSuscription = socketMonitor.getReceivedPacketsObservable()
                .observeOn(Schedulers.trampoline())
                .subscribe(this::packetsReceived);

        timeOutsSuscription = socketMonitor.getTimeOutObservable()
                .observeOn(Schedulers.trampoline())
                .subscribe(this::timeOutReached);

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
        if(packetsSuscription != null) {
            packetsSuscription.dispose();
        }
        if(timeOutsSuscription != null) {
            timeOutsSuscription.dispose();
        }
    }

    /**
     * Sends a frame throught a socket
     * @param frame the frame to send throught the socket
     */
    void sendFrame(FrameModel frame) {
        try {
            out.writeUTF(frame.toBinary());
            out.flush();
        } catch (Exception e) {
            System.out.println("Error sending data :");
            e.printStackTrace();
            System.exit(-1);
        }
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
}
