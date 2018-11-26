import managers.TestManager;
import networking.Receiver;
import networking.Sender;
import utils.StringUtils;
import org.fusesource.jansi.AnsiConsole;

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

    //
    public static final String TEST_DATA = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco";

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {

        // Install ansi into console.
        AnsiConsole.systemInstall();

        System.setProperty( "sun.security.ssl.allowUnsafeRenegotiation", "true" );
        //Vérifie si les arguments du programme correspondent à ceux nécessaire pour partir un receveur
        if(args.length == 2 && args[0].toUpperCase().equals("RECEIVER") && StringUtils.isNumeric(args[1])) {

            int port = Integer.valueOf(args[1]);
            Receiver receiver = new Receiver(port);
            receiver.listen();

            //Vérifie si les arguments du programme correspondent à ceux nécessaire pour partir un émetteur
        } else if (args.length == 5 && args[0].toUpperCase().equals("SENDER") && StringUtils.isNumeric(args[2])
                && StringUtils.isNumeric(args[4])) {

            String hostname = args[1];
            int port = Integer.valueOf(args[2]);
            String filePath = args[3];
            int protocol = Integer.valueOf(args[4]);

            if(protocol != 0) {
                System.out.println("only backn is supported : 0");
                System.exit(-1);
            }

            Sender sender = new Sender(hostname, port);
            sender.sendFile(filePath);

        } else {
            System.out.print("Invalid Arguments : " + Arrays.toString(args));
            TestManager.testChecksum();
//            TestManager.testMessageTransmission(TEST_DATA);
            TestManager.testErrors();
        }

        // Uninstall ansi into console.
        AnsiConsole.systemUninstall();
    }
}
