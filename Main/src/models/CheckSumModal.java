package models;

import managers.ConversionManager;

public class CheckSumModal {

    // Attributes
    private byte[] value;

    /**
     *
     * @param payload
     */
    public CheckSumModal (PayloadModel payload) {
        value = new byte[2];
    }

    @Override
    public String toString(){
        return ConversionManager.convertBytesToString(value);
    }
}
