package utils;

public class StringUtils {

    /*
     * Détermine si une string contient uniquement des caractères numériques
     * @return true si numeric et false sinon
     */
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
