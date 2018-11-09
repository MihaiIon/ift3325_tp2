package models;

public class Packet {
  
  /**
   * Used in the constructor to specifiy the type (role)
   * of the packet that is sent. 
   */
  public static enum Type {
    INFORMATION,
    CONNECTION_REQUEST,
    PAQUET_RECEPTION,
    REFECTED_PAQUET,
    ENDING_CONNECTION,
    P_BITS
  }
  
  // Used for the bits stuffing.
  private static byte FLAG = (byte) 126;
  
  // ------------------------------------------------------------------------
  // Static Methods

  public static Packet convertToPacket(byte[] data) {
    return new Packet(0, Type.INFORMATION, "");
  }

  private static byte convertTypeToByte(Type type) {
    switch (type) {
      case Type.INFORMATION:
        return (byte) 'I';
      case Type.CONNECTION_REQUEST:
        return (byte) 'C';
      case Type.PAQUET_RECEPTION:
        return (byte) 'A';
      case Type.REFECTED_PAQUET:
        return (byte) 'R';
      case Type.ENDING_CONNECTION:
        return (byte) 'F';
      default:
        return (byte) 'P';
    }
  }

  // ------------------------------------------------------------------------
  // Packet Object

  // Attributes
  private byte id;
  private byte type;
  private String data;

  /**
   * @param id Identifies the packet (0-7).
   * @param type Identifies the type of the packet (see class Type).
   */
  public Packet(byte id, Type type, String data) {
    this.id = id;
    this.type = Packet.convertTypeToByte(type);
    this.data = data;
  }

  private Byte[] toBinary() {
    return new Byte[1];
  }

  public int getId() {
    return this.id;
  }

  public Type getType() {
    switch ((char) (this.type & 0xFF)) {
      case 'I':
        return Type.INFORMATION:
      case 'C':
        return Type.CONNECTION_REQUEST:
      case 'A':
        return Type.PAQUET_RECEPTION:
      case 'R':
        return Type.REFECTED_PAQUET:
      case 'F':
        return Type.ENDING_CONNECTION:
      default:
        return Type.P_BITS;
    }
  }

  public String getData() {
    return this.data;
  }
}
