package sender;

import models.Packet;
import networking.SocketMonitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Sender implements SocketMonitor.PacketReceptionListener {

    String filepath;
    int backN;
    int position;

    SocketMonitor SocketMonitor;
    Socket clientSocket;
    OutputStream out;

    public Sender(String hostname, int port, String filePath, int backN) throws IOException {
        clientSocket = new Socket(hostname, port);
        out = clientSocket.getOutputStream();
        SocketMonitor = new SocketMonitor(clientSocket);
        SocketMonitor.setReceptionListener(this);

        this.filepath = filePath;
        this.backN = backN;
    }

    //https://www.journaldev.com/709/java-read-file-line-by-line
    public void run() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filepath));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();


            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPacketReceived(Packet packet) {

    }
}
