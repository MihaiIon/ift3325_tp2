package receiver;

import models.PacketModel;
import networking.SocketMonitorThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements SocketMonitorThread.PacketReceptionListener {

    ServerSocket serverSocket;
    SocketMonitorThread socketMonitor;
    OutputStream out;

    public Receiver(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();
        out = clientSocket.getOutputStream();
        try {
            String input;
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while ((input = in.readLine()) != null) {
                System.out.println(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //socketMonitor = new SocketMonitorThread(clientSocket, this);
    }

    @Override
    public void onPacketReceived(PacketModel packet) {

    }
}


