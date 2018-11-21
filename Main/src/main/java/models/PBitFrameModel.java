package models;

import managers.ConversionManager;

public class PBitFrameModel extends FrameModel {

    /**
     * Default constructor.
     * @param receivedFrameId Identifier of the received Frame.
     */
    public PBitFrameModel(int receivedFrameId) {
        super(TypeModel.Type.P_BITS, new ByteModel(receivedFrameId));
    }

    /**
     * Default constructor (+ checksum).
     * @param receivedFrameId Identifier of the received Frame.
     * @param checksum Frame's checkSum.
     */
    public PBitFrameModel(int receivedFrameId, String checksum) {
        super(TypeModel.Type.P_BITS, new ByteModel(receivedFrameId), "", checksum);
    }

    // ------------------------------------------------------------------------
    // Getters

    public int getRecievedFrameId() { return getMetadata().toInt(); }
}