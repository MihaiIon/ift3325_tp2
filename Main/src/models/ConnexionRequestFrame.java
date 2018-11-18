package models;

import managers.ConversionManager;

public class ConnexionRequestFrame extends FrameModel {

    // Attributes


    /**
     * Default constructor.
     */
    public ConnexionRequestFrame(byte metadata){
        super(TypeModel.Type.CONNECTION_REQUEST, metadata);
    }

    /**
     * Default constructor (+ checksum).
     * @param checksum Frame's checkSum.
     */
    public ConnexionRequestFrame(byte metadata, String checksum) {
        super(TypeModel.Type.CONNECTION_REQUEST, metadata, "", checksum);
    }

    // ----------------------------------------------------------------
    // Getters


}
