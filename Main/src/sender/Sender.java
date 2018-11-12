package sender;

import models.Packet;
import networking.SocketMonitorThread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Sender implements SocketMonitorThread.PacketReceptionListener {

    private String filepath;
    private int backN;
    private int position = 0;

    private SocketMonitorThread SocketMonitor;
    private Socket clientSocket;
    private OutputStream out;

    private ArrayList<Packet> unconfirmedPackets = new ArrayList<>();

    public Sender(String hostname, int port, String filePath, int backN) throws IOException {
        clientSocket = new Socket(hostname, port);
        out = clientSocket.getOutputStream();
        SocketMonitor = new SocketMonitorThread(clientSocket, this);

        this.filepath = filePath;
        this.backN = backN;
    }

    /*
     *   https://www.journaldev.com/709/java-read-file-line-by-line
     *
     *   Lit un fichier ligne par ligne et les envoie
     */
    public void run() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filepath));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                sendData(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPacketReceived(Packet packet) {
        switch (packet.getType()) {
            case PACKET_RECEPTION: {
                confirmPackets(position, packet.getId());
                for (Packet p: unconfirmedPackets) {
                    try {
                        sendPacket(p);
                    } catch (IOException e) {
                        e.printStackTrace(); //TODO
                    }
                }

                break;
            }
            case REJECTED_PACKET: {

                break;
            }
        }
    }

    private void sendData(String data) throws IOException {
        Packet packet = new Packet((byte)'a', Packet.Type.INFORMATION, data); //TODO byte[1] remplacer par num de trame
        unconfirmedPackets.add(packet);
        out.write(packet.toBinary().getBytes());
    }

    private void sendPacket(Packet p) throws IOException {
        unconfirmedPackets.add(p);
        out.write(p.toBinary().getBytes());
    }

    private void confirmPackets(int from, int to) {
        int confirmPosition = from;
        while (confirmPosition != to) {
            unconfirmedPackets = (ArrayList<Packet>) unconfirmedPackets.stream().filter(x -> x.getId() != position).collect(Collectors.toList());
            confirmPosition = nextPos(position);
        }
    }

    private int nextPos(int position) {
        return position > backN ? position - backN : position;
    }
}
