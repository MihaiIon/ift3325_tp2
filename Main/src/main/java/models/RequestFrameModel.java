package models;

import static models.TypeModel.Type.CONNECTION_REQUEST;
import static models.TypeModel.Type.TERMINATE_CONNECTION_REQUEST;

import static models.RequestFrameModel.RequestType.OPEN_CONNEXION;

public class RequestFrameModel extends FrameModel {

    public enum RequestType {
        OPEN_CONNEXION, CLOSE_CONNEXION,
    }

    /**
     * Default constructor.
     */
    public RequestFrameModel(RequestType requestType) {
        super(
            requestType == OPEN_CONNEXION
                ? CONNECTION_REQUEST
                : TERMINATE_CONNECTION_REQUEST,
            new ByteModel(0)
        );
    }

    /**
     * Default constructor (+ checksum).
     * @param checksum Frame's checkSum.
     */
    public RequestFrameModel(RequestType requestType, String checksum) {
        super(
            requestType == OPEN_CONNEXION
                ? CONNECTION_REQUEST
                : TERMINATE_CONNECTION_REQUEST,
            new ByteModel(0),
            "",
            checksum
        );
    }
}
