package receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements ReceiverSocket.ReceptionListener {

    private ReceiverSocket receiverSocket;

    public Receiver(int port) throws IOException {
        receiverSocket = new ReceiverSocket(port);
        receiverSocket.setReceptionListener(this);
    }

    public void run() {

    }

    @Override
    public void onPacketReceived(Packet packet) {

    }
}


