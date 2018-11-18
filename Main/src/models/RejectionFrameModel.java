package models;

import managers.ConversionManager;

public class RejectionFrameModel extends FrameModel {

    /**
     * Default constructor.
     * @param rejectedFrameId Identifier of the rejected Frame.
     */
    public RejectionFrameModel(int rejectedFrameId) {
        super(TypeModel.Type.REJECTED_FRAME, new ByteModel(rejectedFrameId));
    }

    /**
     * Default constructor (+ checksum).
     * @param rejectedFrameId Identifier of the rejected Frame.
     * @param checksum Frame's checkSum.
     */
    public RejectionFrameModel(int rejectedFrameId, String checksum) {
        super(TypeModel.Type.REJECTED_FRAME, new ByteModel(rejectedFrameId), "", checksum);
    }

    // ------------------------------------------------------------------------
    // Getters

    public int getRejectedFrameId() { return getMetadata().toInt(); }
}
