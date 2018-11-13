package sender;

import models.PacketModel;
import networking.SocketController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Sender extends SocketController {

    private AtomicInteger backN = new AtomicInteger();
    private AtomicInteger position = new AtomicInteger();

    private ArrayList<PacketModel> unconfirmedPackets = new ArrayList<PacketModel>();

    private boolean busy;

    public Sender(String hostname, int port, int backN) {
        position.set(0);
        this.backN.set(backN);

        try
        {
            Socket socket = new Socket(hostname, port);
            System.out.println("Connected");
            // sends output to the socket
            configureSocket(socket);
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /*
     *   https://www.journaldev.com/709/java-read-file-line-by-line
     *   Lit un fichier ligne par ligne et les envoie
     */
    public void sendFile(String filepath) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filepath));
            String line = reader.readLine();
            while (line != null) {
                line = "hello";//TODO lire le fichier reader.readLine();
                super.sendData(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void sendPacket(PacketModel p) {
        unconfirmedPackets.add(p);
        super.sendPacket(p);
    }

    private void confirmPackets(int from, int to) {
        int confirmPosition = from;
        while (confirmPosition != to) {
            unconfirmedPackets = (ArrayList<PacketModel>) unconfirmedPackets.stream().filter(x -> x.getId() != position.get()).collect(Collectors.toList());
            confirmPosition = nextPos(position.get());
        }
    }

    private int nextPos(int pos) {
        return pos > backN.get() ? 0 : pos + 1;
    }

    public boolean isBusy() {
        return false;
    }

    @Override
    public void packetsReceived(ArrayList<PacketModel> packetsReceived) {
        busy = true;


        busy = false;
    }

    @Override
    public void timeOutReached(int position) {
        busy = true;

        busy = false;
    }
}
