package models;

import managers.ConversionManager;

import static managers.DataManager.PAYLOAD_SIZE;

public class PayloadModel {

    // Attributes
    private byte[] payload;

    /**
     * Object wrapping the payload.
     * @param stream Stream of bits.
     */
    public PayloadModel(String stream) {
        // Splits each group of 8 digits (byteStream) into a byte.
        this.payload = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            String byteStream = stream.substring(i * 8, (i + 1) * 8);
            payload[i] = ConversionManager.convertStringToByte(byteStream);
        }
    }

    @Override
    public String toString(){
        return ConversionManager.convertBytesToString(payload);
    }
}
