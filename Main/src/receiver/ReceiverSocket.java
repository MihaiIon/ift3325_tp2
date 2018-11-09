package receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverSocket {

    ServerSocket serverSocket;
    ReceptionListener receptionListener;

    public ReceiverSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();

        PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
    }


    public void setReceptionListener(ReceptionListener receptionListener) {
        this.receptionListener = receptionListener;
    }

    public interface ReceptionListener{

        void onPacketReceived(Packet packet);

    }
}
