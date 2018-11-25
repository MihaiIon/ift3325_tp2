package utils;

import models.FrameModel;

/**
 * Classe dutilitaires permettant de simuler une communication sur couche physique
 * ou les erreurs de communication sont possibles
 */
public class BitFlipper {

    private BitFlipper() {}

    public static String flipRandomBits(String bits) {
        if(Math.random() > 0.85) {
            StringBuilder sb = new StringBuilder(bits);
            int pos = (int) Math.ceil(Math.random() * bits.length());
            if(sb.charAt(pos) == '0') {
                sb.setCharAt(pos, '1');
            } else {
                sb.setCharAt(pos, '0');
            }
            System.out.println("Bit flipped : " + sb.toString());
            return sb.toString();
        }
        return bits;
    }
}
