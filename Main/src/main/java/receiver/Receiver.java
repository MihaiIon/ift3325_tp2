package receiver;

import models.PacketModel;
import networking.SocketController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver extends SocketController {

    private ServerSocket server;

    boolean isBusy;

    public Receiver(int port) throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server started");
    }

    public void listen() throws IOException {
        System.out.println("Waiting for a client ...");
        Socket client = server.accept();
        System.out.println("Client accepted");
        configureSocket(client);
    }

    public void packetsReceived(ArrayList<PacketModel> packetModels) {
        isBusy = true;

        isBusy = false;
    }

    @Override
    public void timeOutReached(int position) {
        isBusy = true;

        isBusy = false;
    }

    public boolean isBusy() {
        return isBusy;
    }
}


