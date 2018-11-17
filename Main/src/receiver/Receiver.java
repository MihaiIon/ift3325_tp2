package receiver;

import models.PacketModel;
import networking.SocketMonitorThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements SocketMonitorThread.PacketReceptionListener {

    ServerSocket server;
    SocketMonitorThread socketMonitor;
    DataOutputStream out;
    Socket client;

    public Receiver(int port) throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server started");
        //socketMonitor = new SocketMonitorThread(clientSocket, this);
    }

    public void listen() throws IOException {
        System.out.println("Waiting for a client ...");

        client = server.accept();
        System.out.println("Client accepted");

        out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));

        socketMonitor = new SocketMonitorThread(client, this);
        socketMonitor.start();
        // takes input from the client socket
        //DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
        //try {
        //    String input;
        //    while ((input = in.readUTF()) != null) {
        //System.out.println(input);
        //    }
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }

    @Override
    public void onPacketReceptionTimeOut() {

    }

    @Override
    public void onPacketReceived(PacketModel packet) {

    }
}


