package models;

import managers.ConversionManager;

public class RejectionFrameModel extends FrameModel {

    /**
     * Default constructor.
     * @param rejectedFrameId Identifier of the rejected Frame.
     */
    public RejectionFrameModel(byte rejectedFrameId) {
        super(TypeModel.Type.REJECTED_FRAME, ConversionManager.convertByteToString(rejectedFrameId));
    }

    /**
     * Default constructor (+ checksum).
     * @param rejectedFrameId Identifier of the rejected Frame.
     * @param checksum Frame's checkSum.
     */
    public RejectionFrameModel(byte rejectedFrameId, String checksum) {
        super(TypeModel.Type.REJECTED_FRAME, ConversionManager.convertByteToString(rejectedFrameId), "", checksum);
    }

    // ------------------------------------------------------------------------
    // Getters

    public int getRejectedFrameId() { return (int) ConversionManager.convertStringToByte(getMetadata()); }
}
