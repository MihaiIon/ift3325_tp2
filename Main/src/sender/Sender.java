package sender;

import models.Packet;
import networking.SocketMonitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Sender implements SocketMonitor.PacketReceptionListener {

    private String filepath;
    private int backN;
    private int position = 0;

    private SocketMonitor SocketMonitor;
    private Socket clientSocket;
    private OutputStream out;

    private ArrayList<Packet> unconfirmedPackets = new ArrayList<>();

    public Sender(String hostname, int port, String filePath, int backN) throws IOException {
        clientSocket = new Socket(hostname, port);
        out = clientSocket.getOutputStream();
        SocketMonitor = new SocketMonitor(clientSocket);
        SocketMonitor.setReceptionListener(this);

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
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPacketReceived(Packet packet) {
        switch (packet.getType()) {
            case PAQUET_RECEPTION: {

                break;
            }
            case REFECTED_PAQUET: {

                break;
            }
        }
    }

    private void sendData(String data) {
        Packet packet = new Packet(new byte[1], Packet.Type.INFORMATION, data); //TODO byte[1] remplacer par num de trame
        unconfirmedPackets.add(packet);
        out.write(packet.toBinary());
    }

    private void canSend(int packetPosition) {

    }
}
