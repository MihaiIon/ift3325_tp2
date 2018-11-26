package factories;

import managers.ConversionManager;
import managers.DataManager;
import models.BadFrame;
import models.ByteModel;

import static models.TypeModel.Type.*;

public class BadFrameFactory {

    private static String flag = ConversionManager.convertByteToString(DataManager.FLAG);

    /**
     *
     * @param type
     */
    public static BadFrame createBadFrame(BadFrame.BadFrameType type) {
        switch (type) {
            case NO_FLAG:
                return createNoFlagFrame();
            case INVALID_FLAG:
                return createInvalidFlagFrame();
            case INVALID_TYPE:
                return createInvalidTypeFrame();
            case INVALID_FRAME_LENGTH:
                return createInvalidFrameLengthFrame();
            case NO_DATA_INTO_INFORMATION_FRAME:
                return createNoDataIntoInformationFrame();
            case INFORMATION_FRAME_ID_OUT_OF_RANGE:
                return createInvalidInformationFrame(8);
            case RECEPTION_FRAME_ID_OUT_OF_RANGE:
                return createInvalidReceptionFrame(8);
            case REJECTION_FRAME_ID_OUT_OF_RANGE:
                return createInvalidRejectionFrame(8);
            case NEGATIVE_INFORMATION_FRAME_ID:
                return createInvalidInformationFrame(-1);
            case NEGATIVE_RECEPTION_FRAME_ID:
                return createInvalidReceptionFrame(-1);
            case NEGATIVE_REJECTION_FRAME_ID:
                return createInvalidRejectionFrame(-1);
            default:
                return null;
        }
    }

    public static BadFrame createNoFlagFrame() {
        return new BadFrame("", BAD_FRAME, new ByteModel(0), flag+flag+flag+flag, flag);
    }

    public static BadFrame createInvalidFlagFrame() {
        return new BadFrame("01100110", BAD_FRAME, new ByteModel(0), "", flag);
    }

    public static BadFrame createInvalidTypeFrame() {
        return new BadFrame(flag, BAD_FRAME, new ByteModel(0), "", flag);
    }

    public static BadFrame createInvalidFrameLengthFrame() {
        return new BadFrame("", BAD_FRAME, new ByteModel(0), "", "");
    }

    public static BadFrame createNoDataIntoInformationFrame(){
        return new BadFrame(flag, INFORMATION, new ByteModel(0), "", flag);
    }

    public static BadFrame createInvalidInformationFrame(int id) {
        return new BadFrame(flag, INFORMATION, new ByteModel(id), flag, flag);
    }

    public static BadFrame createInvalidReceptionFrame(int id) {
        return new BadFrame(flag, INFORMATION, new ByteModel(id), flag, flag);
    }

    public static BadFrame createInvalidRejectionFrame(int id) {
        return new BadFrame(flag, INFORMATION, new ByteModel(id), flag, flag);
    }
}
