package factories;

import models.*;

public class FrameFactory {

    public static FrameModel createConnectionFrame() {
        return new RequestFrameModel(RequestFrameModel.RequestType.OPEN_CONNEXION);
    }

    public static FrameModel createDisconnectionFrame() {
        return new RequestFrameModel(RequestFrameModel.RequestType.CLOSE_CONNEXION);
    }

    public static InformationFrameModel createInformationFrame(int id, String binaryData) {
        return new InformationFrameModel(id, binaryData);
    }

    public static FrameModel createReceptionFrame(int receivedFrameId) {
        return new ReceptionFrameModel(receivedFrameId);
    }

    public static FrameModel createRejectionFrame(int rejectedFrameId) {
        return new RejectionFrameModel(rejectedFrameId);
    }

    public static FrameModel pBitFrame(int pBitFrameId) {
        return new PBitFrameModel(pBitFrameId);
    }
}
