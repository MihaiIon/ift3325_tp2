package models;

import managers.ConversionManager;
import managers.DataManager;

public class PacketModel {

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

  /**
   * Converts the provided binary data to a PacketModel Object.
   * @param stream Stream of bites reprensenting the packet.
   * @return PacketModel Object.
   */
  public static PacketModel convertToPacket(String stream) {
    return new PacketModel((byte)0, Type.INFORMATION, new PayloadModel("00000000000000000000000000000000"));
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

  // ------------------------------------------------------------------------
  // Packet Model

  // Attributes
  private byte id;
  private byte type;
  private PayloadModel payload;
  private CheckSumModal checkSum;

  /**
   * @param id Identifies the packet (0-7).
   * @param type Identifies the type of the packet (see class Type).
   */
  public PacketModel(byte id, Type type, PayloadModel payload) {
    this.id = id;
    this.type = PacketModel.convertTypeToByte(type);
    this.payload = payload;
    this.checkSum = new CheckSumModal(payload);
  }

  /**
   * Converts PacketModel object to binary number.
   */
  public String toBinary() {
    byte[] bytes = new byte[4];
    bytes[0] = DataManager.FLAG;
    bytes[1] = this.type;
    bytes[2] = this.id;
    bytes[3] = DataManager.FLAG;
    return ConversionManager.convertBytesToString(bytes);
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
  public PayloadModel getPayload() {
    return payload;
  }

  public CheckSumModal getCheckSum() {
    return checkSum;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Packet : ");
    sb.append("\n\tid : " + getId());
    sb.append("\n\ttype : " + getType());
    sb.append("\n\tpayload : " + ConversionManager.convertStreamToReadableStream(getPayload().toString()));
    sb.append("\n\tcheckSum : IN PROGRESS "/* + ConversionManager.convertStreamToReadableStream(getCheckSum().toString())*/);
    sb.append("\n\tbinary : IN PROGRESS "/* + ConversionManager.convertStreamToReadableStream(toBinary())*/);
    return sb.toString();
  }
}
