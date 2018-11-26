package factories;

import models.FrameTypeModel;

public class TypeFactory {

    /**
     * Provides a FrameTypeModel corresponding to the provided <frameType>.
     */
    public static FrameTypeModel createTypeModel(FrameTypeModel.FrameType frameType) {
        switch (frameType) {
            case INFORMATION:
                return new FrameTypeModel(frameType, 'I');
            case CONNECTION_REQUEST:
                return new FrameTypeModel(frameType, 'C');
            case FRAME_RECEPTION:
                return new FrameTypeModel(frameType, 'A');
            case REJECTED_FRAME:
                return new FrameTypeModel(frameType, 'R');
            case TERMINATE_CONNECTION_REQUEST:
                return new FrameTypeModel(frameType, 'F');
            case P_BITS:
                return new FrameTypeModel(frameType, 'P');
            default:
                return new FrameTypeModel(frameType, 'X');
        }
    }
}
