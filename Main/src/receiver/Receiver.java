package receiver;

import models.Packet;

import java.io.IOException;

public class Receiver implements ReceiverSocket.PacketReceptionListener {

    private ReceiverSocket receiverSocket;

    public Receiver(int port) throws IOException {
        receiverSocket = new ReceiverSocket(port);
        receiverSocket.setReceptionListener(this);
    }

    public void run() {

    }

    @Override
    public void onPacketReceived(Packet packet) {

    }
}


