package factories;

import models.*;

public class FrameFactory {

    public static FrameModel createConnexionFrame() {
        return new RequestFrameModel(RequestFrameModel.RequestType.OPEN_CONNEXION);
    }

    public static FrameModel createDeconnexionFrame() {
        return new RequestFrameModel(RequestFrameModel.RequestType.CLOSE_CONNEXION);
    }

    public static FrameModel createInformationFrame(int id, String data) {
        return new InformationFrameModel(id, data);
    }

    public static FrameModel createReceptionFrame(int receivedFrameId) {
        return new ReceptionFrameModel(receivedFrameId);
    }

    public static FrameModel createRejectionFrame(int rejectedFrameId) {
        return new ReceptionFrameModel(rejectedFrameId);
    }
}
