package models;

public class Packet {
  
  /**
   * Used in the constructor to specifiy the type (role)
   * of the packet that is sent. 
   */
  public static enum Type {
    INFORMATION,
    CONNECTION_REQUEST,
    ACCEPTED_CONNECTION_REQUEST,
    REFECTED_PAQUET,
    ENDING_CONNECTION,
    P_BITS
  }
  
  // Used for the bits stuffing.
  private static byte FLAG = (byte) 126;
  
  // ------------------------------------------------------------------------
  // Static Methods

  public static Packet convertToPacket(byte[] data) {
    return new Packet(0, Type.I, "");
  }

  // ------------------------------------------------------------------------
  // Packet Object

  // Attributes
  private byte id;
  private Type type;
  private String data;

  /**
   * @param id Identifies the packet (0-7).
   * @param type Identifies the type of the packet (see class Type).
   */
  public Packet(byte id, Type type, String data) {
    this.id = id;
    this.type = type;
    this.data = data;
  }

  private Byte[] toBinary() {
    return new Byte[1];
  }

  private byte getTypeByte() {
    switch (this.type) {
      case Type.INFORMATION:
        return (byte) 'I';
      case Type.CONNECTION_REQUEST:
        return (byte) 'C';
      case Type.ACCEPTED_CONNECTION_REQUEST:
        return (byte) 'A';
      case Type.REFECTED_PAQUET:
        return (byte) 'R';
      case Type.ENDING_CONNECTION:
        return (byte) 'F';
      default:
        return (byte) 'P';
    }
  }
}
