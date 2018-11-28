package models;

public class BadFrame extends FrameModel {

    public enum BadFrameType {
        NO_FLAG,

        INVALID_FLAG,
        INVALID_TYPE,
        INVALID_FRAME_LENGTH,

        NO_DATA_IN_INFORMATION_FRAME,
        INVALID_DATA_IN_INFORMATION_FRAME,

        INFORMATION_FRAME_ID_OUT_OF_RANGE,
        RECEPTION_FRAME_ID_OUT_OF_RANGE,
        REJECTION_FRAME_ID_OUT_OF_RANGE,

        NEGATIVE_INFORMATION_FRAME_ID,
        NEGATIVE_RECEPTION_FRAME_ID,
        NEGATIVE_REJECTION_FRAME_ID
    }

    public BadFrame(String flag, FrameTypeModel.FrameType frameType, ByteModel metadata, String data, String checkSum) {
        super(flag, frameType, metadata, data, checkSum);
    }
}
