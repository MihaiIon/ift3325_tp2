package receiver;

import models.Packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;

public class ReceiverSocket {

    ServerSocket serverSocket;
    ReceptionListener receptionListener;
    PrintWriter out;
    BufferedReader in;

    public ReceiverSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        new Runnable() {
            @Override
            public void run() {
                while (true) {

                }
            }
        }.run();
    }


    public void setReceptionListener(ReceptionListener receptionListener) {
        this.receptionListener = receptionListener;
    }

    public interface ReceptionListener{

        void onPacketReceived(Packet packet);

    }
}
