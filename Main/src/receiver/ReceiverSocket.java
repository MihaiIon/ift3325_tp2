package receiver;

import models.Packet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;

public class ReceiverSocket {

    private ServerSocket serverSocket;
    private PacketReceptionListener receptionListener;
    private PrintWriter out;
    private InputStream in;

    public ReceiverSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = clientSocket.getInputStream();

    }

    private void listenSocket() {
        ((Runnable) () -> {
            byte[] bytes = new byte[0];
            try {
                bytes = new byte[in.available()];
                in.read(bytes);
                //receptionListener.onPacketReceived(new Packet(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).run();
    }

    public void setReceptionListener(PacketReceptionListener receptionListener) {
        this.receptionListener = receptionListener;
    }

    public interface PacketReceptionListener{

        void onPacketReceived(Packet packet);

    }
}
