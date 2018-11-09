package models;

public class Packet {

  /**
   * Used in the constructor to specifiy the type (role)
   * of the packet that is sent. 
   */
  public class Type {
    // Information.
    public static byte I = (byte) 'I';
    // Connection Request.
    public static byte C = (byte) 'C';
    // Confirmation. 
    public static byte A = (byte) 'A';
    // Rejection.
    public static byte R = (byte) 'R';
    // End Communications.
    public static byte F = (byte) 'F';
    // P bits.
    public static byte P = (byte) 'P';
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
  private byte type;
  private String data;

  /**
   * @param id Identifies the packet (0-7).
   * @param type Identifies the type of the packet (see class Type).
   */
  public Packet(byte id, byte type, String data) {
    this.id = id;
    this.type = type;
    this.data = data;
  }

  private Byte[] toBinary() {
    return new Byte[1];
  }
}
