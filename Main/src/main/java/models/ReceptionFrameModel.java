package models;

public class ReceptionFrameModel extends FrameModel {

    /**
     * Default constructor.
     * @param receivedFrameId Identifier of the received Frame.
     */
    public ReceptionFrameModel(int receivedFrameId) {
        super(FrameTypeModel.FrameType.FRAME_RECEPTION, new ByteModel(receivedFrameId));
    }

    /**
     * Default constructor (+ checksum).
     * @param receivedFrameId Identifier of the received Frame.
     * @param checksum Frame's checkSum.
     */
    public ReceptionFrameModel(int receivedFrameId, String checksum) {
        super(FrameTypeModel.FrameType.FRAME_RECEPTION, new ByteModel(receivedFrameId), "", checksum);
    }

    // ------------------------------------------------------------------------
    // Getters

    public int getRecievedFrameId() { return getMetadata().toInt(); }
}
