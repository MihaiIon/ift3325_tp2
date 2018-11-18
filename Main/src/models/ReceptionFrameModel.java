package models;

import managers.ConversionManager;

public class ReceptionFrameModel extends FrameModel {

    /**
     * Default constructor.
     * @param receivedFrameId Identifier of the received Frame.
     */
    public ReceptionFrameModel(int receivedFrameId) {
        super(TypeModel.Type.FRAME_RECEPTION, new ByteModel(receivedFrameId));
    }

    /**
     * Default constructor (+ checksum).
     * @param receivedFrameId Identifier of the received Frame.
     * @param checksum Frame's checkSum.
     */
    public ReceptionFrameModel(int receivedFrameId, String checksum) {
        super(TypeModel.Type.FRAME_RECEPTION, new ByteModel(receivedFrameId), "", checksum);
    }

    // ------------------------------------------------------------------------
    // Getters

    public int getRecievedFrameId() { return getMetadata().toInt(); }
}
