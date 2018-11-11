package models;

import managers.ConversionManager;

public class PayloadModel {

    /**
     * The length of a payload must be 32 bits (4 bytes).
     */
    public class PayloadLengthException extends Exception {
        public PayloadLengthException() { super("The payload length must be 4."); }
    }

    // ------------------------------------------------------------------------
    // Payload Model

    // Attributes
    private byte[] payload;

    /**
     * @param payload Array of bytes containing a portion of a larger data.
     * @throws PayloadLengthException See <PayloadLengthException>.
     */
    public PayloadModel(byte[] payload) throws PayloadLengthException {
        if(payload.length != 4) {
            throw new PayloadLengthException();
        }
        this.payload = payload;
    }

    @Override
    public String toString(){
        return ConversionManager.convertBytesToString(payload);
    }
}
