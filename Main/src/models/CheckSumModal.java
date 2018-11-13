package models;

import managers.ConversionManager;

public class CheckSumModal {

    // Attributes
    private byte[] value;

    /**
     * Computes the CRC and stores it. This will help in finding eventual errors in a payload.
     * @param payload Encoded portion of the data.
     */
    public CheckSumModal (PayloadModel payload) {
        value = new byte[2];
    }

    @Override
    public String toString(){
        return ConversionManager.convertBytesToString(value);
    }
}
