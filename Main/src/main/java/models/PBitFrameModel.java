package models;

public class PBitFrameModel extends FrameModel {

    /**
     * Default constructor.
     * @param bitLength Identifier of the received Frame.
     */
    public PBitFrameModel(int bitLength) {
        super(FrameTypeModel.FrameType.P_BITS, new ByteModel(bitLength));
    }

    /**
     * Default constructor (+ checksum).
     * @param receivedFrameId Identifier of the received Frame.
     * @param checksum Frame's checkSum.
     */
    public PBitFrameModel(int receivedFrameId, String checksum) {
        super(FrameTypeModel.FrameType.P_BITS, new ByteModel(receivedFrameId), "", checksum);
    }

    // ------------------------------------------------------------------------
    // Getters

    public int getBitLengthId() { return getMetadata().toInt(); }
}