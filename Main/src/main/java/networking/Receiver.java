package networking;

import factories.FrameFactory;
import models.FrameModel;
import models.TypeModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Receiver extends SocketController {

    private ServerSocket server;

    private AtomicBoolean isBusy = new AtomicBoolean(false);

    private boolean isOpen = false;

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

    public void packetsReceived(ArrayList<FrameModel> packetModels) {
        isBusy.set(true);
        packetModels.forEach(stream -> {
            if(stream.getType() == TypeModel.Type.CONNECTION_REQUEST) {
                if (isOpen) {
                    System.out.println("Connection request but connection is already open.");
                } else {
                    isOpen = true;
                }
            } else {
                switch (stream.getType()) {
                    case TERMINATE_CONNECTION_REQUEST: {
                        if(isOpen) {
                            close();
                        } else {
                            System.out.println("Trying to close an non-open connection.");
                        }
                        break;
                    }
                    case INFORMATION: {
                        FrameModel frameModel = FrameFactory.createReceptionFrame(getCurrentPosition());
                        sendFrame(frameModel);
                        break;
                    }
                    case REJECTED_FRAME: {
                        //TODO
                        break;
                    }
                    case FRAME_RECEPTION: {
                        //TODO possible?
                        break;
                    }
                    case P_BITS: {
                        //TODO

                    }
                }
            }
        });
        isBusy.set(false);
    }

    @Override
    public void timeOutReached(int position) {
        isBusy.set(true);
        FrameModel frame = FrameFactory.createReceptionFrame(getCurrentPosition());
        sendFrame(frame);
        isBusy.set(false);
    }

    public boolean isBusy() {
        return isBusy.get();
    }

    @Override
    public void close() {
        if(server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.close();
    }
}


