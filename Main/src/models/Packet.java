package models;

public class Packet {

  // ------------------------------------------------------------------------
  // Static

  /**
   * Used in the constructor to specifiy the type (role)
   * of the packet that is sent. 
   */
  public enum Type {
    INFORMATION,
    CONNECTION_REQUEST,
    PACKET_RECEPTION,
    REJECTED_PACKET,
    ENDING_CONNECTION,
    P_BITS
  }
  
  // Used for the bits stuffing.
  private static byte FLAG = (byte) 126;

  /**
   * Converts the provided binary data to a Packet Object.
   * @param stream Stream of bites reprensenting the packet.
   * @return Packet Object.
   */
  public static Packet convertToPacket(String stream) {
    return new Packet((byte)0, Type.INFORMATION, "TODO");
  }

  /**
   * @param type The type of the packet.
   * @return Encodes the type of the packet on 8 bits (byte).
   */
  private static byte convertTypeToByte(Type type) {
    switch (type) {
      case INFORMATION:
        return (byte) 'I';
      case CONNECTION_REQUEST:
        return (byte) 'C';
      case PACKET_RECEPTION:
        return (byte) 'A';
      case REJECTED_PACKET:
        return (byte) 'R';
      case ENDING_CONNECTION:
        return (byte) 'F';
      default:
        return (byte) 'P';
    }
  }

  /**
   * @param b Byte to be converted to String.
   * @return Provides a String of bits representing the byte.
   */
  private static String convertByteToString(byte b) {
    return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
  }

  /**
   * @param bs Array of bytes.
   * @return Provides a String of bits representing each byte.
   */
  private static String convertBytesToString(byte[] bs) {
    String str = "";
    for (int i = 0; i < bs.length; i++){
      str += convertByteToString(bs[i]);
    }
    return str;
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

  /**
   * Converts Packet object to binary number.
   */
  public String toBinary() {
    byte[] bytes = new byte[4];
    bytes[0] = FLAG;
    bytes[1] = this.id;
    bytes[2] = this.type;
    bytes[3] = FLAG;
    return convertBytesToString(bytes);
  }

  // ------------------------------------------------------------------------
  // Getters

  /**
   * @return Provides the identifier of the packet.
   */
  public int getId() {
    return this.id;
  }

  /**
   * @return Provides the type of the packet.
   */
  public Type getType() {
    char type = (char) (this.type & 0xFF);
    switch (type) {
      case 'I':
        return Type.INFORMATION;
      case 'C':
        return Type.CONNECTION_REQUEST;
      case 'A':
        return Type.PACKET_RECEPTION;
      case 'R':
        return Type.REJECTED_PACKET;
      case 'F':
        return Type.ENDING_CONNECTION;
      default:
        return Type.P_BITS;
    }
  }

  /**
   * @return Provides the data contained in the packet.
   */
  public String getData() {
    return this.data;
  }
}
