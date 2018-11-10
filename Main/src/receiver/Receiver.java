package receiver;

import models.Packet;
import networking.SocketMonitor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements SocketMonitor.PacketReceptionListener {

    ServerSocket serverSocket;
    SocketMonitor socketMonitor;
    OutputStream out;

    public Receiver(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();
        out = clientSocket.getOutputStream();
        socketMonitor = new SocketMonitor(clientSocket);
        socketMonitor.setReceptionListener(this);
    }

    @Override
    public void onPacketReceived(Packet packet) {

    }
}


