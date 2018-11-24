package utils;

/**
 * Classe dutilitaires permettant de simuler une communication sur couche physique
 * ou les erreurs de communication sont possibles
 */
public class BitFlipper {

    private BitFlipper() {}

    public static String flipRandomBits(String bits) {
        if(Math.random() > 0.95) {
            StringBuilder sb = new StringBuilder(bits);
            int pos = (int) Math.ceil(Math.random() * bits.length());
            int bit = (int) Math.round(Math.random());
            sb.setCharAt(pos, (""+bit).charAt(0));
            return sb.toString();
        }
        return bits;
    }




}
