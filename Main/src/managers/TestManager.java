package managers;

import models.PacketModel;
import models.PayloadModel;

public class TestManager {

    /**
     *
     * @param data
     */
    public static void testData(String data) {
        //
        System.out.println("\n=========================\n  Testing Data\n=========================\nData : " + data);

        // Build payloads
        PayloadModel[] payloads = DataManager.splitDataToPayloads(data);

        // Create Packets
        PacketModel[] packetsSent = new PacketModel[payloads.length];
        for (int i = 0; i < packetsSent.length; i++) {
            byte id = (byte)i;
            packetsSent[i] = new PacketModel(id, PacketModel.Type.INFORMATION, payloads[i]);
            System.out.println("== Packet Created ==");
            System.out.println(packetsSent[i].toString());
        }

        //
    }
}
