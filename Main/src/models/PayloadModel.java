package models;

public class PayloadModel {

    // Attributes
    private String data;
    private String checksum;

    /**
     * Payload Object.
     * @param data Stream of bits representing the data contained in the Frame.
     * @param checksum Stream of bits representing the checksum.
     */
    public PayloadModel(String data, String checksum) {
        this.data = data;
        this.checksum = checksum;
    }

    public String getData() { return data; }
    public String getCheckSum() { return checksum; }

    @Override
    public String toString() { return data+checksum; }
}
