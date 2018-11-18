package models;

import managers.ConversionManager;

public class ReceptionFrameModel extends FrameModel {

    /**
     * Default constructor.
     * @param receivedFrameId Identifier of the received Frame.
     */
    public ReceptionFrameModel(byte receivedFrameId) {
        super(TypeModel.Type.FRAME_RECEPTION, ConversionManager.convertByteToString(receivedFrameId));
    }

    /**
     * Default constructor (+ checksum).
     * @param receivedFrameId Identifier of the received Frame.
     * @param checksum Frame's checkSum.
     */
    public ReceptionFrameModel(byte receivedFrameId, String checksum) {
        super(TypeModel.Type.FRAME_RECEPTION, ConversionManager.convertByteToString(receivedFrameId), "", checksum);
    }

    // ------------------------------------------------------------------------
    // Getters

    public int getRecievedFrameId() { return (int) ConversionManager.convertStringToByte(getMetadata()); }
}
