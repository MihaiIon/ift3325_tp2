import managers.TestManager;
import receiver.Receiver;
import sender.Sender;
import utils.StringUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * La commande d’exécution de l’émetteur est :
 * % Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>
 * La dernière option fait référence à l’utilisation de Go-Back-N (0).
 * La commande d’exécution du récepteur est :
 * % Receiver <Numero_Port>
 */

public class Main {

    public static void main(String args[]) throws IOException {


        System.setProperty( "sun.security.ssl.allowUnsafeRenegotiation", "true" );
        //Vérifie si les arguments du programme correspondent à ceux nécessaire pour partir un receveur
        if(args.length == 2 && args[0].toUpperCase().equals("RECEIVER") && StringUtils.isNumeric(args[1])) {

            int port = Integer.valueOf(args[1]);
            Receiver receiver = new Receiver(port);
//            receiver.run();

            //Vérifie si les arguments du programme correspondent à ceux nécessaire pour partir un émetteur
        } else if (args.length == 5 && args[0].toUpperCase().equals("SENDER") && StringUtils.isNumeric(args[2])
                && StringUtils.isNumeric(args[4])) {

            String hostname = args[1];
            int port = Integer.valueOf(args[2]);
            String filePath = args[3];
            int backN = Integer.valueOf(args[4]);

            Sender sender = new Sender(hostname, port, filePath, backN);
            sender.run();

        } else {
            System.out.print("Arguments du programme invalides : " + Arrays.toString(args));
            TestManager.testData("mihai");
        }
    }
}
