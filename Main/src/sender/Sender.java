package sender;

import models.FrameModel;
import networking.SocketMonitorThread;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Sender implements SocketMonitorThread.PacketReceptionListener {

    private AtomicInteger backN = new AtomicInteger();
    private AtomicInteger position = new AtomicInteger();

    private SocketMonitorThread SocketMonitor;
    private Socket socket;
    private DataOutputStream out;

    private ArrayList<FrameModel> unconfirmedPackets = new ArrayList<>();

    public Sender(String hostname, int port, int backN) {
        position.set(0);
        this.backN.set(backN);

        try
        {
            socket = new Socket(hostname, port);
            System.out.println("Connected");
            // sends output to the socket
            out    = new DataOutputStream(socket.getOutputStream());
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
                sendData(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFrameReceptionTimeOut() {

    }

    @Override
    public void onFrameReceived(FrameModel packet) {
        switch (packet.getType()) {
            case FRAME_RECEPTION: {
                confirmPackets(position.get(), packet.getMetadata());
                for (FrameModel p: unconfirmedPackets) {
//                    try {
//                        sendPacket(p);
//                    } catch (IOException e) {
//                        e.printStackTrace(); //TODO
//                    }
                }

                break;
            }
            case REJECTED_FRAME: {

                break;
            }
        }
    }

    private void sendData(String data) throws IOException {
        //FrameModel packet = new FrameModel((byte)'a', FrameModel.Type.INFORMATION, new PayloadModel(data)); //TODO byte[1] remplacer par num de trame
        //unconfirmedPackets.add(packet);
        //out.write(packet.toBinary().getBytes());
        out.writeUTF(data);
    }

    private void sendFrame(FrameModel p) throws IOException {
        unconfirmedPackets.add(p);
     //   out.write(p.toBinary().getBytes());
    }

    private void confirmPackets(int from, int to) {
        int confirmPosition = from;
        while (confirmPosition != to) {
            unconfirmedPackets = (ArrayList<FrameModel>) unconfirmedPackets.stream().filter(x -> x.getMetadata() != position.get()).collect(Collectors.toList());
            confirmPosition = nextPos(position.get());
        }
    }

    private int nextPos(int pos) {
        return pos > backN.get() ? 0 : pos + 1;
    }
}
